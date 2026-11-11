package bi.kwitaho.app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class OutilsImage {
    private OutilsImage() {}

    /** Décode une image réduite (côté max) en appliquant la rotation EXIF. */
    public static Bitmap decoderReduite(String chemin, int coteMax) throws ExceptionApp {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(chemin, o);
            if (o.outWidth <= 0 || o.outHeight <= 0) throw new ExceptionApp(ErreurApp.IMAGE_INVALIDE);

            int echantillon = 1;
            int plusLong = Math.max(o.outWidth, o.outHeight);
            while (plusLong / (echantillon * 2) >= coteMax) echantillon *= 2;

            o = new BitmapFactory.Options();
            o.inSampleSize = echantillon;
            Bitmap bitmap = BitmapFactory.decodeFile(chemin, o);
            if (bitmap == null) throw new ExceptionApp(ErreurApp.IMAGE_INVALIDE);

            int degres = lireRotation(chemin);
            if (degres != 0) {
                Bitmap pivotee = pivoter(bitmap, degres);
                if (pivotee != bitmap) bitmap.recycle();
                bitmap = pivotee;
            }
            // Réduction finale si l'image dépasse encore la taille demandée
            int cote = Math.max(bitmap.getWidth(), bitmap.getHeight());
            if (cote > coteMax) {
                float f = coteMax / (float) cote;
                Bitmap redimensionnee = Bitmap.createScaledBitmap(bitmap, Math.max(1, Math.round(bitmap.getWidth() * f)),
                        Math.max(1, Math.round(bitmap.getHeight() * f)), true);
                if (redimensionnee != bitmap) bitmap.recycle();
                bitmap = redimensionnee;
            }
            return bitmap;
        } catch (OutOfMemoryError e) {
            throw new ExceptionApp(ErreurApp.IMAGE_INVALIDE);
        }
    }

    private static int lireRotation(String chemin) {
        try {
            ExifInterface exif = new ExifInterface(chemin);
            int o = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (o) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            return 0;
        }
    }

    public static Bitmap pivoter(Bitmap source, int degres) {
        Matrix m = new Matrix();
        m.postRotate(degres);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, true);
    }

    /** Recadre au centre en carré puis redimensionne à la taille demandée. */
    public static Bitmap recadrerCarreCentre(Bitmap source, int taille) {
        int cote = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - cote) / 2;
        int y = (source.getHeight() - cote) / 2;
        Bitmap carre = Bitmap.createBitmap(source, x, y, cote, cote);
        Bitmap redimensionnee = Bitmap.createScaledBitmap(carre, taille, taille, true);
        if (carre != source && carre != redimensionnee) carre.recycle();
        return redimensionnee;
    }

    public static void enregistrerJpeg(Bitmap bitmap, File fichier, int qualite) throws ExceptionApp {
        try (FileOutputStream sortieForme = new FileOutputStream(fichier)) {
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, qualite, sortieForme)) {
                throw new ExceptionApp(ErreurApp.IMAGE_INVALIDE);
            }
        } catch (IOException e) {
            String m = String.valueOf(e.getMessage());
            if (m.contains("ENOSPC") || m.toLowerCase().contains("no space")) {
                throw new ExceptionApp(ErreurApp.STOCKAGE_PLEIN, e);
            }
            throw new ExceptionApp(ErreurApp.INCONNUE, e);
        }
    }
}
