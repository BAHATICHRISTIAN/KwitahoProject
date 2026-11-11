package bi.kwitaho.app.util;

import androidx.annotation.StringRes;

import bi.kwitaho.app.R;

/** Erreurs typées, chacune associée à un message traduit. */
public enum ErreurApp {
    CAMERA_REFUSEE(R.string.err_camera_refusee),
    CAMERA_INDISPONIBLE(R.string.err_camera_indisponible),
    STOCKAGE_PLEIN(R.string.err_stockage_plein),
    IMAGE_INVALIDE(R.string.err_image_invalide),
    CHARGEMENT_MODELE(R.string.err_chargement_modele),
    AUCUNE_PHOTO(R.string.err_aucune_photo),
    INCONNUE(R.string.err_inconnue);

    @StringRes
    public final int messageRes;

    ErreurApp(@StringRes int messageRes) {
        this.messageRes = messageRes;
    }
}
