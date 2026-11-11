package bi.kwitaho.app.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {EntiteDiagnostic.class}, version = 1, exportSchema = false)
public abstract class BaseDeDonnees extends RoomDatabase {
    private static volatile BaseDeDonnees instance;

    public abstract DaoDiagnostic daoDiagnostic();

    public static BaseDeDonnees obtenir(Context contexte) {
        if (instance == null) {
            synchronized (BaseDeDonnees.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(contexte.getApplicationContext(), BaseDeDonnees.class, "kwitaho.db")
                            .build();
                }
            }
        }
        return instance;
    }
}
