package bi.kwitaho.app.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bi.kwitaho.app.data.db.EntiteDiagnostic;
import bi.kwitaho.app.data.model.CatalogueMaladies;
import bi.kwitaho.app.ml.Classifieur;
import bi.kwitaho.app.ml.FournisseurClassifieur;
import bi.kwitaho.app.ml.ResultatClassification;
import bi.kwitaho.app.repository.Diagnostic;
import bi.kwitaho.app.util.ErreurApp;
import bi.kwitaho.app.util.ExceptionApp;
import bi.kwitaho.app.util.Reglages;
import bi.kwitaho.app.util.Evenement;
import bi.kwitaho.app.util.OutilsImage;
import bi.kwitaho.app.util.StockagePhotos;

/** Session de diagnostic : photos prises, analyse en arrière-plan, enregistrement du résultat. */
public class VueModeleDiagnostic extends AndroidViewModel {
    private final Diagnostic depot;
    private final ExecutorService executeur = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<String>> photos = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Evenement<ErreurApp>> erreur = new MutableLiveData<>();
    private final MutableLiveData<Evenement<Long>> enregistre = new MutableLiveData<>();
    private volatile boolean analyseEnCours = false;

    public VueModeleDiagnostic(@NonNull Application application) {
        super(application);
        depot = new Diagnostic(application);
    }

    public LiveData<List<String>> obtenirPhotos() {
        return photos;
    }

    public LiveData<Evenement<ErreurApp>> obtenirErreur() {
        return erreur;
    }

    public LiveData<Evenement<Long>> obtenirEnregistre() {
        return enregistre;
    }

    public int nombrePhotos() {
        List<String> l = photos.getValue();
        return l == null ? 0 : l.size();
    }

    /** À appeler sur le thread principal. Retourne false si le maximum est atteint. */
    public boolean ajouterPhoto(String chemin) {
        List<String> liste = new ArrayList<>(photos.getValue() == null ? new ArrayList<String>() : photos.getValue());
        if (liste.size() >= Reglages.NB_PHOTOS_MAX) return false;
        liste.add(chemin);
        photos.setValue(liste);
        return true;
    }

    public void retirerPhoto(int indice) {
        List<String> liste = new ArrayList<>(photos.getValue() == null ? new ArrayList<String>() : photos.getValue());
        if (indice < 0 || indice >= liste.size()) return;
        StockagePhotos.supprimer(liste.remove(indice));
        photos.setValue(liste);
    }

    /** Abandonne la session : supprime les photos non enregistrées. */
    public void abandonnerSession() {
        List<String> liste = photos.getValue();
        if (liste != null) {
            for (String p : liste) StockagePhotos.supprimer(p);
        }
        photos.setValue(new ArrayList<>());
    }

    public void analyser() {
        if (analyseEnCours) return;
        final List<String> chemins = new ArrayList<>(photos.getValue() == null ? new ArrayList<String>() : photos.getValue());
        if (chemins.isEmpty()) {
            erreur.setValue(new Evenement<>(ErreurApp.AUCUNE_PHOTO));
            return;
        }
        analyseEnCours = true;
        executeur.execute(() -> {
            long debut = SystemClock.uptimeMillis();
            try {
                Classifieur classifieur = FournisseurClassifieur.obtenir(getApplication());
                List<String> etiquettes = classifieur.etiquettes();
                float[] somme = new float[etiquettes.size()];
                for (String chemin : chemins) {
                    Bitmap bitmap = OutilsImage.decoderReduite(chemin, 512);
                    try {
                        ResultatClassification r = classifieur.classer(bitmap);
                        for (int i = 0; i < somme.length && i < r.probas.length; i++) somme[i] += r.probas[i];
                    } finally {
                        bitmap.recycle();
                    }
                }
                int meilleur = 0;
                for (int i = 1; i < somme.length; i++) {
                    if (somme[i] > somme[meilleur]) meilleur = i;
                }
                EntiteDiagnostic e = new EntiteDiagnostic();
                e.cheminsImages = TextUtils.join("|", chemins);
                e.codeMaladie = CatalogueMaladies.normaliser(etiquettes.get(meilleur));
                e.confiance = Math.min(1f, somme[meilleur] / chemins.size());
                e.timestamp = System.currentTimeMillis();
                e.demo = classifieur.estDemo();
                e.synchronise = false;

                long attente = Reglages.DUREE_MIN_ANALYSE_MS - (SystemClock.uptimeMillis() - debut);
                if (attente > 0) Thread.sleep(attente);

                long id = depot.insererBloquant(e);
                photos.postValue(new ArrayList<>());   // les photos appartiennent maintenant au diagnostic
                enregistre.postValue(new Evenement<>(id));
            } catch (ExceptionApp ex) {
                erreur.postValue(new Evenement<>(ex.erreur));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                erreur.postValue(new Evenement<>(ErreurApp.INCONNUE));
            } catch (RuntimeException | OutOfMemoryError ex) {
                erreur.postValue(new Evenement<>(ErreurApp.INCONNUE));
            } finally {
                analyseEnCours = false;
            }
        });
    }

    @Override
    protected void onCleared() {
        executeur.shutdown();
    }
}
