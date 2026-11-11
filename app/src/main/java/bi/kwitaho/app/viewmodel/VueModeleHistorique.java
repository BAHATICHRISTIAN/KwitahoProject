package bi.kwitaho.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import bi.kwitaho.app.data.db.EntiteDiagnostic;
import bi.kwitaho.app.repository.Diagnostic;

public class VueModeleHistorique extends AndroidViewModel {
    public static final int FILTRE_TOUT = 0;
    public static final int FILTRE_SAINES = 1;
    public static final int FILTRE_MALADES = 2;

    private final Diagnostic depot;
    private final LiveData<List<EntiteDiagnostic>> toutes;
    private final MutableLiveData<Integer> filtre = new MutableLiveData<>(FILTRE_TOUT);
    private final MediatorLiveData<List<EntiteDiagnostic>> filtres = new MediatorLiveData<>();

    public VueModeleHistorique(@NonNull Application application) {
        super(application);
        depot = new Diagnostic(application);
        toutes = depot.observerTout();
        filtres.addSource(toutes, liste -> mettreAJour());
        filtres.addSource(filtre, f -> mettreAJour());
    }

    private void mettreAJour() {
        List<EntiteDiagnostic> source = toutes.getValue();
        if (source == null) return;
        Integer f = filtre.getValue();
        int mode = f == null ? FILTRE_TOUT : f;
        List<EntiteDiagnostic> sortieForme = new ArrayList<>();
        for (EntiteDiagnostic e : source) {
            if (mode == FILTRE_TOUT
                    || (mode == FILTRE_SAINES && e.categorie() == 1)
                    || (mode == FILTRE_MALADES && e.categorie() == 2)) {
                sortieForme.add(e);
            }
        }
        filtres.setValue(sortieForme);
    }

    public LiveData<List<EntiteDiagnostic>> obtenirTout() {
        return toutes;
    }

    public LiveData<List<EntiteDiagnostic>> obtenirFiltres() {
        return filtres;
    }

    public void definirFiltre(int mode) {
        filtre.setValue(mode);
    }

    public LiveData<EntiteDiagnostic> obtenirParId(long id) {
        return depot.observerDiagnostic(id);
    }

    public void supprimer(EntiteDiagnostic entite) {
        depot.supprimer(entite);
    }
}
