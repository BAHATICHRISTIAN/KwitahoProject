package bi.kwitaho.app.util;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bi.kwitaho.app.R;

public final class FormatTemps {
    private FormatTemps() {}
    public static String formater(Context contexte, long horodatage) {
        Date date = new Date(horodatage);
        String heure = new SimpleDateFormat("HH:mm", Locale.ROOT).format(date);
        long jours = Math.round((debutDeJournee(System.currentTimeMillis()) - debutDeJournee(horodatage)) / 86400000.0);
        if (jours == 0) return contexte.getString(R.string.aujourdhui) + " • " + heure;
        if (jours == 1) return contexte.getString(R.string.hier) + " • " + heure;
        return new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(date) + " • " + heure;
    }

    private static long debutDeJournee(long millisecondes) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millisecondes);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
