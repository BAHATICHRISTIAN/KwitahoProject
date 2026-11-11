package bi.kwitaho.app.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bi.kwitaho.app.data.db.BaseDeDonnees;
import bi.kwitaho.app.data.db.DaoDiagnostic;
import bi.kwitaho.app.data.db.EntiteDiagnostic;
import bi.kwitaho.app.util.StockagePhotos;

/** Point d'accès unique aux diagnostics (base Room + photos privées). */
public class Diagnostic {
    private static final ExecutorService ARRIERE_PLAN = Executors.newSingleThreadExecutor();

    private final Context app;
    private final DaoDiagnostic dao;
    private final Handler principal = new Handler(Looper.getMainLooper());

    public Diagnostic(Context contexte) {
        this.app = contexte.getApplicationContext();
        this.dao = BaseDeDonnees.obtenir(app).daoDiagnostic();
    }

    public LiveData<List<EntiteDiagnostic>> observerTout() {
        return dao.observerTout();
    }

    public LiveData<EntiteDiagnostic> observerDiagnostic(long id) {
        return dao.observerParId(id);
    }

    /** À appeler depuis un thread d'arrière-plan uniquement. */
    public long insererBloquant(EntiteDiagnostic entite) {
        return dao.inserer(entite);
    }

    public void supprimer(EntiteDiagnostic entite) {
        ARRIERE_PLAN.execute(() -> {
            dao.supprimerParId(entite.id);
            for (String p : entite.chemins()) StockagePhotos.supprimer(p);
        });
    }

    public void toutSupprimer(Runnable auTermine) {
        ARRIERE_PLAN.execute(() -> {
            dao.toutSupprimer();
            StockagePhotos.toutSupprimer(app);
            if (auTermine != null) principal.post(auTermine);
        });
    }
}
