package bi.kwitaho.app.ml;

import android.graphics.Bitmap;

import java.util.List;

import bi.kwitaho.app.util.ExceptionApp;

public interface Classifieur {
    /** Étiquettes dans l'ordre des sorties du modèle. */
    List<String> etiquettes();

    /** Vrai si les résultats sont simulés (modèle absent). */
    boolean estDemo();

    ResultatClassification classer(Bitmap bitmap) throws ExceptionApp;
}
