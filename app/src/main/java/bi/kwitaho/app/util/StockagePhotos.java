package bi.kwitaho.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

/** Stockage privé des photos (aucun accès depuis les autres applications). */
public final class StockagePhotos {
    private static final AtomicInteger COMPTEUR = new AtomicInteger();

    private StockagePhotos() {}

    public static File dossier(Context contexte) throws ExceptionApp {
        File d = new File(contexte.getFilesDir(), "photos");
        if (!d.exists() && !d.mkdirs()) throw new ExceptionApp(ErreurApp.INCONNUE);
        if (d.getUsableSpace() < Reglages.OCTETS_LIBRES_MIN) throw new ExceptionApp(ErreurApp.STOCKAGE_PLEIN);
        return d;
    }

    public static File nouveauFichier(Context contexte) throws ExceptionApp {
        return new File(dossier(contexte), "p_" + System.currentTimeMillis() + "_" + COMPTEUR.incrementAndGet() + ".jpg");
    }

    /** Copie une image choisie dans la galerie vers le stockage privé, redimensionnée. */
    public static String importerUri(Context contexte, Uri uri) throws ExceptionApp {
        File sortieForme = nouveauFichier(contexte);
        try (InputStream in = contexte.getContentResolver().openInputStream(uri);
             FileOutputStream os = new FileOutputStream(sortieForme)) {
            if (in == null) throw new ExceptionApp(ErreurApp.IMAGE_INVALIDE);
            byte[] tampon = new byte[16 * 1024];
            int n;
            while ((n = in.read(tampon)) > 0) os.write(tampon, 0, n);
        } catch (IOException | SecurityException e) {
            sortieForme.delete();
            String m = String.valueOf(e.getMessage());
            if (m.contains("ENOSPC") || m.toLowerCase().contains("no space")) {
                throw new ExceptionApp(ErreurApp.STOCKAGE_PLEIN, e);
            }
            throw new ExceptionApp(ErreurApp.IMAGE_INVALIDE, e);
        }
        try {
            normaliser(sortieForme.getPath());
        } catch (ExceptionApp e) {
            sortieForme.delete();
            throw e;
        }
        return sortieForme.getPath();
    }

    /** Réduit la photo, applique l'orientation et la réécrit en JPEG. */
    public static void normaliser(String chemin) throws ExceptionApp {
        Bitmap bitmap = OutilsImage.decoderReduite(chemin, Reglages.COTE_MAX_PHOTO);
        try {
            OutilsImage.enregistrerJpeg(bitmap, new File(chemin), 90);
        } finally {
            bitmap.recycle();
        }
    }

    public static void supprimer(String chemin) {
        if (chemin == null || chemin.isEmpty()) return;
        //noinspection ResultOfMethodCallIgnored
        new File(chemin).delete();
    }

    public static void toutSupprimer(Context contexte) {
        File d = new File(contexte.getFilesDir(), "photos");
        File[] fichiers = d.listFiles();
        if (fichiers != null) {
            for (File f : fichiers) //noinspection ResultOfMethodCallIgnored
                f.delete();
        }
    }
}
