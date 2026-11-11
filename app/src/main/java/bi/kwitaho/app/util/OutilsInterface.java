package bi.kwitaho.app.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import bi.kwitaho.app.R;

public final class OutilsInterface {
    private OutilsInterface() {}

    /** Ajoute aux marges intérieures la place des barres système (mode bord à bord). */
    public static void ajouterMargesSysteme(View vue, boolean haut, boolean bas) {
        final int l = vue.getPaddingLeft();
        final int t = vue.getPaddingTop();
        final int r = vue.getPaddingRight();
        final int b = vue.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(vue, (v, marges) -> {
            Insets barres = marges.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(l, haut ? t + barres.top : t, r, bas ? b + barres.bottom : b);
            return marges;
        });
        ViewCompat.requestApplyInsets(vue);
    }

    public static int dp(Context contexte, int dp) {
        return Math.round(dp * contexte.getResources().getDisplayMetrics().density);
    }

    public static void barreMessage(View ancre, @StringRes int message) {
        if (ancre == null) return;
        Snackbar.make(ancre, message, Snackbar.LENGTH_LONG).show();
    }

    public static void afficherErreur(Context contexte, ErreurApp erreur) {
        if (contexte == null) return;
        new MaterialAlertDialogBuilder(contexte)
                .setTitle(R.string.err_titre)
                .setMessage(erreur.messageRes)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public static void afficherInfo(Context contexte, @StringRes int titre, CharSequence message) {
        new MaterialAlertDialogBuilder(contexte)
                .setTitle(titre)
                .setMessage(message)
                .setPositiveButton(R.string.fermer, null)
                .show();
    }

    /** Remplit un conteneur avec des lignes « coche + texte ». */
    public static void remplirCoches(LinearLayout conteneur, List<String> elements) {
        conteneur.removeAllViews();
        LayoutInflater gonfleur = LayoutInflater.from(conteneur.getContext());
        for (String s : elements) {
            View ligne = gonfleur.inflate(R.layout.element_ligne_coche, conteneur, false);
            ((TextView) ligne.findViewById(R.id.txt_ligne)).setText(s);
            conteneur.addView(ligne);
        }
    }

    /** Remplace un squelette par le vrai contenu, après un délai minimal. */
    public static void reveler(final View squelette, final View contenu, long debutMesure) {
        long attente = Math.max(0L, Reglages.DUREE_MIN_SQUELETTE_MS - (SystemClock.uptimeMillis() - debutMesure));
        contenu.postDelayed(() -> {
            if (!contenu.isAttachedToWindow()) return;
            contenu.setAlpha(0f);
            contenu.setVisibility(View.VISIBLE);
            contenu.animate().alpha(1f).setDuration(250).start();
            squelette.animate().alpha(0f).setDuration(250)
                    .withEndAction(() -> squelette.setVisibility(View.GONE)).start();
        }, attente);
    }

    public static String nomVersion(Context contexte) {
        try {
            return contexte.getPackageManager().getPackageInfo(contexte.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0";
        }
    }
}
