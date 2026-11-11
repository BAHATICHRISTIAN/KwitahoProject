package bi.kwitaho.app.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import bi.kwitaho.app.repository.Maladierpt;

public class VueModeleMaladie extends AndroidViewModel {
    private final Maladierpt depot;
    private final MutableLiveData<List<bi.kwitaho.app.data.model.Maladie>> resultats = new MutableLiveData<>();

    public VueModeleMaladie(@NonNull Application application) {
        super(application);
        depot = new Maladierpt(application);
        resultats.setValue(depot.toutes());
    }

    public LiveData<List<bi.kwitaho.app.data.model.Maladie>> obtenirResultats() {
        return resultats;
    }

    /** Le contexte fourni doit être celui de l'activité (langue courante). */
    public void rechercher(Context contexteLocalise, String requete) {
        resultats.setValue(depot.rechercher(contexteLocalise, requete));
    }
}
