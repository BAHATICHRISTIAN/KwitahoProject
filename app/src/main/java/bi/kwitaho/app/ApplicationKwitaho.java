package bi.kwitaho.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import bi.kwitaho.app.repository.Profil;
import bi.kwitaho.app.util.AideLangue;

public class ApplicationKwitaho extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Thème clair uniquement (usage en plein soleil, contraste maîtrisé)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        String langue = new Profil(this).obtenirLangue();
        if (!langue.isEmpty()) AideLangue.appliquer(langue);
    }
}
