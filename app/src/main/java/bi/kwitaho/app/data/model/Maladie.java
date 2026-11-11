package bi.kwitaho.app.data.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/** Fiche d'une maladie (ou de la feuille saine). */
public final class Maladie {
    public final String code;
    public final boolean saine;
    @StringRes public final int nomRes;
    @StringRes public final int natureRes;
    @StringRes public final int descRes;
    @StringRes public final int symptomesRes;
    @StringRes public final int causeRes;
    @DrawableRes public final int imageRes;
    public final int[] preventionRes;
    public final int[] conseilsRes;

    public Maladie(String code, boolean saine, int nomRes, int natureRes, int descRes, int symptomesRes,
                   int causeRes, int imageRes, int[] preventionRes, int[] conseilsRes) {
        this.code = code;
        this.saine = saine;
        this.nomRes = nomRes;
        this.natureRes = natureRes;
        this.descRes = descRes;
        this.symptomesRes = symptomesRes;
        this.causeRes = causeRes;
        this.imageRes = imageRes;
        this.preventionRes = preventionRes;
        this.conseilsRes = conseilsRes;
    }
}
