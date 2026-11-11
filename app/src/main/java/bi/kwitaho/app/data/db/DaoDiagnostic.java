package bi.kwitaho.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoDiagnostic {
    @Insert
    long inserer(EntiteDiagnostic entite);

    @Query("SELECT * FROM diagnostics ORDER BY timestamp DESC")
    LiveData<List<EntiteDiagnostic>> observerTout();

    @Query("SELECT * FROM diagnostics WHERE id = :id")
    LiveData<EntiteDiagnostic> observerParId(long id);

    @Query("DELETE FROM diagnostics WHERE id = :id")
    void supprimerParId(long id);

    @Query("SELECT * FROM diagnostics")
    List<EntiteDiagnostic> obtenirToutMaintenant();

    @Query("DELETE FROM diagnostics")
    void toutSupprimer();
}
