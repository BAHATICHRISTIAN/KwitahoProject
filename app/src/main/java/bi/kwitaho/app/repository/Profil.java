package bi.kwitaho.app.repository;

import android.content.Context;
import android.content.SharedPreferences;

/** Profil local  */
public class Profil {
    private static final String NOM_FICHIER = "kwitaho_prefs";
    private static final String CLE_LANGUE = "language";
    private static final String CLE_PRESENTATION = "onboarding_done";
    private static final String CLE_NOM = "name";
    private static final String CLE_FLASH = "flash_default";

    private final SharedPreferences preferences;

    public Profil(Context contexte) {
        preferences = contexte.getApplicationContext().getSharedPreferences(NOM_FICHIER, Context.MODE_PRIVATE);
    }

    public String obtenirLangue() {
        return preferences.getString(CLE_LANGUE, "");
    }

    public void definirLangue(String code) {
        preferences.edit().putString(CLE_LANGUE, code).apply();
    }

    public boolean presentationTerminee() {
        return preferences.getBoolean(CLE_PRESENTATION, false);
    }

    public void definirPresentationTerminee(boolean termine) {
        preferences.edit().putBoolean(CLE_PRESENTATION, termine).apply();
    }

    public String obtenirNom() {
        return preferences.getString(CLE_NOM, "");
    }

    public void definirNom(String nom) {
        preferences.edit().putString(CLE_NOM, nom == null ? "" : nom.trim()).apply();
    }

    public boolean flashParDefaut() {
        return preferences.getBoolean(CLE_FLASH, false);
    }

    public void definirFlashParDefaut(boolean on) {
        preferences.edit().putBoolean(CLE_FLASH, on).apply();
    }

    public void vider() {
        preferences.edit().clear().apply();
    }
}
