package bi.kwitaho.app.util;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

public final class AideLangue {
    public static final String FR = "fr";
    public static final String RN = "rn";
    public static final String SW = "sw";

    private AideLangue() {}

    /**  la langue de l'application (l'activité est recréée si nécessaire). */
    public static void appliquer(String code) {
        if (code == null || code.isEmpty()) return;
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code));
    }
}
