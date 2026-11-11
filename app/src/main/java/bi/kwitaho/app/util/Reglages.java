package bi.kwitaho.app.util;

/** Réglages centralisés de l'application. */
public final class Reglages {
    private Reglages() {}

    /** Nombre maximal de photos par diagnostic (haut, milieu, bas de la plante). */
    public static final int NB_PHOTOS_MAX = 3;
    /** Nombre minimal de photos pour lancer l'analyse. Mettre 3 pour imposer les trois photos. */
    public static final int NB_PHOTOS_MIN = 1;

    /** Seuils de confiance. */
    public static final float SEUIL_FIABLE = 0.80f;
    public static final float SEUIL_PROBABLE = 0.60f;

    public static final String FICHIER_MODELE = "kwitaho_model.tflite";
    public static final String FICHIER_ETIQUETTES = "labels.txt";

    /**
     * Normalisation des pixels pour un modèle à entrée FLOAT32 :
     * 0 = valeurs brutes 0..255 (MobileNetV3 Keras intègre déjà son prétraitement),
     * 1 = valeurs entre 0 et 1,
     * 2 = valeurs entre -1 et 1.
     * Doit être identique à ce que le modèle exporté attend.
     */
    public static final int MODE_ENTREE_FLOTTANTE = 0;

    /** Durée minimale d'affichage de l'écran d'analyse (confort visuel). */
    public static final long DUREE_MIN_ANALYSE_MS = 1500L;
    /** Durée minimale d'affichage des squelettes, pour éviter un clignotement. */
    public static final long DUREE_MIN_SQUELETTE_MS = 450L;

    /** Côté maximal des photos enregistrées. */
    public static final int COTE_MAX_PHOTO = 1280;
    /** Espace libre minimal exigé avant d'enregistrer une photo. */
    public static final long OCTETS_LIBRES_MIN = 5L * 1024 * 1024;

    /** Affiche « illustration à remplacer » sur les fiches dont l'image n'est pas une vraie photo. */
    public static final boolean AFFICHER_NOTE_ILLUSTRATION = true;
}
