package bi.kwitaho.app.repository;

import android.content.Context;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bi.kwitaho.app.data.model.CatalogueMaladies;

public class Maladierpt {
    private final Context contexte;

    public Maladierpt(Context contexte) {
        this.contexte = contexte.getApplicationContext();
    }

    public List<bi.kwitaho.app.data.model.Maladie> toutes() {
        return CatalogueMaladies.toutes();
    }

    /** Recherche dans le nom (langue courante) et dans le code, sans tenir compte des accents. */
    public List<bi.kwitaho.app.data.model.Maladie> rechercher(Context contexteLocalise, String requete) {
        String q = normaliserTexte(requete);
        if (q.isEmpty()) return toutes();
        List<bi.kwitaho.app.data.model.Maladie> sortieForme = new ArrayList<>();
        for (bi.kwitaho.app.data.model.Maladie d : CatalogueMaladies.toutes()) {
            String nom = normaliserTexte(contexteLocalise.getString(d.nomRes));
            if (nom.contains(q) || normaliserTexte(d.code).contains(q)) sortieForme.add(d);
        }
        return sortieForme;
    }

    private static String normaliserTexte(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}+", "");
    }
}
