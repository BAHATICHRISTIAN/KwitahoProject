package bi.kwitaho.app.ml;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import bi.kwitaho.app.util.ErreurApp;
import bi.kwitaho.app.util.ExceptionApp;
import bi.kwitaho.app.util.Reglages;
import bi.kwitaho.app.util.OutilsImage;

/** Inférence locale avec le modèle TensorFlow Lite (LiteRT). */
public final class ClassifieurTflite implements Classifieur {
    private final Interpreter interpreteur;
    private final List<String> etiquettes;
    private final int largeur;
    private final int hauteur;
    private final boolean entreeFlottante;
    private final int nombreClasses;

    private ClassifieurTflite(Interpreter interpreteur, List<String> etiquettes) throws ExceptionApp {
        this.interpreteur = interpreteur;
        this.etiquettes = etiquettes;
        int[] in = interpreteur.getInputTensor(0).shape();   // [1, hauteur, largeur, 3]
        if (in.length != 4 || in[3] != 3) throw new ExceptionApp(ErreurApp.CHARGEMENT_MODELE);
        this.hauteur = in[1];
        this.largeur = in[2];
        DataType type = interpreteur.getInputTensor(0).dataType();
        if (type != DataType.FLOAT32 && type != DataType.UINT8) throw new ExceptionApp(ErreurApp.CHARGEMENT_MODELE);
        this.entreeFlottante = type == DataType.FLOAT32;
        if (interpreteur.getOutputTensor(0).dataType() != DataType.FLOAT32) {
            throw new ExceptionApp(ErreurApp.CHARGEMENT_MODELE);
        }
        int[] sortieForme = interpreteur.getOutputTensor(0).shape();
        this.nombreClasses = sortieForme[sortieForme.length - 1];
        if (nombreClasses != etiquettes.size()) throw new ExceptionApp(ErreurApp.CHARGEMENT_MODELE);
    }

    public static ClassifieurTflite creer(Context contexte) throws ExceptionApp {
        try {
            MappedByteBuffer modele = chargerModele(contexte);
            List<String> etiquettes = chargerEtiquettes(contexte);
            Interpreter.Options options = new Interpreter.Options();
            options.setNumThreads(2);
            return new ClassifieurTflite(new Interpreter(modele, options), etiquettes);
        } catch (ExceptionApp e) {
            throw e;
        } catch (IOException | RuntimeException | LinkageError e) {
            throw new ExceptionApp(ErreurApp.CHARGEMENT_MODELE, e);
        }
    }

    private static MappedByteBuffer chargerModele(Context contexte) throws IOException {
        try (AssetFileDescriptor fd = contexte.getAssets().openFd(Reglages.FICHIER_MODELE);
             FileInputStream is = new FileInputStream(fd.getFileDescriptor());
             FileChannel channel = is.getChannel()) {
            return channel.map(FileChannel.MapMode.READ_ONLY, fd.getStartOffset(), fd.getDeclaredLength());
        }
    }

    private static List<String> chargerEtiquettes(Context contexte) throws IOException {
        List<String> liste = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(
                contexte.getAssets().open(Reglages.FICHIER_ETIQUETTES), "UTF-8"))) {
            String ligne;
            while ((ligne = r.readLine()) != null) {
                ligne = ligne.trim();
                if (!ligne.isEmpty()) liste.add(ligne);
            }
        }
        return liste;
    }

    @Override
    public List<String> etiquettes() {
        return etiquettes;
    }

    @Override
    public boolean estDemo() {
        return false;
    }

    @Override
    public synchronized ResultatClassification classer(Bitmap bitmap) throws ExceptionApp {
        Bitmap carre = OutilsImage.recadrerCarreCentre(bitmap, Math.max(largeur, hauteur));
        Bitmap entree = (largeur == hauteur) ? carre : Bitmap.createScaledBitmap(carre, largeur, hauteur, true);
        int[] pixels = new int[largeur * hauteur];
        entree.getPixels(pixels, 0, largeur, 0, 0, largeur, hauteur);

        ByteBuffer tampon = ByteBuffer.allocateDirect(largeur * hauteur * 3 * (entreeFlottante ? 4 : 1));
        tampon.order(ByteOrder.nativeOrder());
        for (int p : pixels) {
            int r = (p >> 16) & 0xFF;
            int g = (p >> 8) & 0xFF;
            int b = p & 0xFF;
            if (entreeFlottante) {
                tampon.putFloat(normaliser(r));
                tampon.putFloat(normaliser(g));
                tampon.putFloat(normaliser(b));
            } else {
                tampon.put((byte) r);
                tampon.put((byte) g);
                tampon.put((byte) b);
            }
        }
        tampon.rewind();

        float[][] sortie = new float[1][nombreClasses];
        try {
            interpreteur.run(tampon, sortie);
        } catch (RuntimeException e) {
            throw new ExceptionApp(ErreurApp.CHARGEMENT_MODELE, e);
        }
        float[] probas = sortie[0];

        float somme = 0f;
        for (float v : probas) somme += v;
        if (somme < 0.98f || somme > 1.02f) probas = softmax(probas);
        return new ResultatClassification(probas);
    }

    private static float normaliser(int v) {
        switch (Reglages.MODE_ENTREE_FLOTTANTE) {
            case 1:
                return v / 255f;
            case 2:
                return (v - 127.5f) / 127.5f;
            default:
                return (float) v;
        }
    }

    private static float[] softmax(float[] x) {
        float max = Float.NEGATIVE_INFINITY;
        for (float v : x) max = Math.max(max, v);
        float somme = 0f;
        float[] e = new float[x.length];
        for (int i = 0; i < x.length; i++) {
            e[i] = (float) Math.exp(x[i] - max);
            somme += e[i];
        }
        for (int i = 0; i < x.length; i++) e[i] /= somme;
        return e;
    }
}
