package bi.kwitaho.app.ml;

import android.content.Context;

import java.io.IOException;

import bi.kwitaho.app.util.ExceptionApp;
import bi.kwitaho.app.util.Reglages;

/** Choisit le vrai modèle s'il est présent, sinon le mode démonstration. */
public final class FournisseurClassifieur {
    private static Classifieur instance;

    private FournisseurClassifieur() {}

    public static synchronized Classifieur obtenir(Context contexte) throws ExceptionApp {
        if (instance == null) {
            Context app = contexte.getApplicationContext();
            instance = possedeModele(app) ? ClassifieurTflite.creer(app) : new ClassifieurDemo();
        }
        return instance;
    }

    public static boolean possedeModele(Context contexte) {
        try {
            String[] fichiers = contexte.getAssets().list("");
            if (fichiers == null) return false;
            for (String f : fichiers) {
                if (Reglages.FICHIER_MODELE.equals(f)) return true;
            }
        } catch (IOException ignore) {
            // considéré comme absent
        }
        return false;
    }
}
