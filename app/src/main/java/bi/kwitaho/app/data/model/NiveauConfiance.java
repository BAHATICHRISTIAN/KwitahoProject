package bi.kwitaho.app.data.model;

import bi.kwitaho.app.util.Reglages;

/** Niveau de fiabilité d'un résultat . */
public enum NiveauConfiance {
    FIABLE, PROBABLE, INCERTAIN;

    public static NiveauConfiance depuis(float confiance) {
        if (confiance >= Reglages.SEUIL_FIABLE) return FIABLE;
        if (confiance >= Reglages.SEUIL_PROBABLE) return PROBABLE;
        return INCERTAIN;
    }
}
