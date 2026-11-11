package bi.kwitaho.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

import bi.kwitaho.app.data.model.NiveauConfiance;
import bi.kwitaho.app.data.model.CatalogueMaladies;

@Entity(tableName = "diagnostics")
public class EntiteDiagnostic {
    @PrimaryKey(autoGenerate = true)
    public long id;

    /** Chemins des photos, séparés par « | ». */
    @NonNull
    public String cheminsImages = "";

    @NonNull
    public String codeMaladie = "";

    public float confiance;
    public long timestamp;

    /** Vrai si le résultat vient du mode démonstration (modèle absent). */
    public boolean demo;

    /**  la synchronisation  */
    public boolean synchronise;

    public List<String> chemins() {
        List<String> liste = new ArrayList<>();
        if (cheminsImages.isEmpty()) return liste;
        for (String p : cheminsImages.split("\\|")) {
            if (!p.isEmpty()) liste.add(p);
        }
        return liste;
    }

    public String premierChemin() {
        List<String> p = chemins();
        return p.isEmpty() ? null : p.get(0);
    }

    /** 0 = incertain, 1 = saine, 2 = malade. */
    public int categorie() {
        if (NiveauConfiance.depuis(confiance) == NiveauConfiance.INCERTAIN) return 0;
        return CatalogueMaladies.SAINE.equals(codeMaladie) ? 1 : 2;
    }
}
