package bi.kwitaho.app.util;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;

import bi.kwitaho.app.R;

/** Clés d'arguments et raccourcis de navigation. */
public final class OutilsNavigation {
    public static final String ARG_ID = "diagnostic_id";
    public static final String ARG_CODE = "disease_code";
    public static final String ARG_CONFIANCE = "confidence";
    public static final String ARG_DEMO = "demo";
    public static final String ARG_FOCUS_RECHERCHE = "focus_search";

    private OutilsNavigation() {}

    /** Navigation vers un onglet principal : l'accueil reste en bas de la pile. */
    public static NavOptions optionsOnglet() {
        return new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.accueilFragment, false)
                .build();
    }

    public static void ouvrirResultat(NavController navigateur, long id) {
        Bundle a = new Bundle();
        a.putLong(ARG_ID, id);
        navigateur.navigate(R.id.resultatFragment, a);
    }

    public static void ouvrirMaladie(NavController navigateur, String code, float confiance, boolean demo) {
        Bundle a = new Bundle();
        a.putString(ARG_CODE, code);
        a.putFloat(ARG_CONFIANCE, confiance);
        a.putBoolean(ARG_DEMO, demo);
        navigateur.navigate(R.id.detailMaladieFragment, a);
    }

    public static void ouvrirScan(NavController navigateur) {
        if (navigateur.getCurrentDestination() != null && navigateur.getCurrentDestination().getId() == R.id.cameraFragment) return;
        navigateur.navigate(R.id.cameraFragment);
    }
}
