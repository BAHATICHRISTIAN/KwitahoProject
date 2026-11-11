package bi.kwitaho.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import bi.kwitaho.app.repository.Diagnostic;
import bi.kwitaho.app.repository.Profil;

public class VueModeleProfil extends AndroidViewModel {
    private final Profil profil;
    private final Diagnostic diagnostics;
    private final MutableLiveData<String> nom = new MutableLiveData<>();

    public VueModeleProfil(@NonNull Application application) {
        super(application);
        profil = new Profil(application);
        diagnostics = new Diagnostic(application);
        nom.setValue(profil.obtenirNom());
    }

    public LiveData<String> obtenirNom() {
        return nom;
    }

    public void definirNom(String valeur) {
        profil.definirNom(valeur);
        nom.setValue(profil.obtenirNom());
    }

    public String obtenirLangue() {
        return profil.obtenirLangue();
    }

    public void definirLangue(String code) {
        profil.definirLangue(code);
    }

    public boolean flashParDefaut() {
        return profil.flashParDefaut();
    }

    public void definirFlashParDefaut(boolean on) {
        profil.definirFlashParDefaut(on);
    }

    /** Efface l'historique, les photos et les réglages de ce téléphone. */
    public void toutEffacer(Runnable auTermine) {
        diagnostics.toutSupprimer(() -> {
            profil.vider();
            nom.setValue("");
            if (auTermine != null) auTermine.run();
        });
    }
}
