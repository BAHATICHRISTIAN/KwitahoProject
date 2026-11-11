package bi.kwitaho.app.ml;

import android.graphics.Bitmap;

import java.util.List;
import java.util.Random;

import bi.kwitaho.app.data.model.CatalogueMaladies;

/**
 * Classifieur de démonstration : résultat simulé, stable pour une même photo.
 * Utilisé tant que kwitaho_model.tflite est absent du dossier assets.
 */
public final class ClassifieurDemo implements Classifieur {
    private final List<String> etiquettes = CatalogueMaladies.etiquettesParDefaut();

    @Override
    public List<String> etiquettes() {
        return etiquettes;
    }

    @Override
    public boolean estDemo() {
        return true;
    }

    @Override
    public ResultatClassification classer(Bitmap bitmap) {
        long seed = 17;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                seed = seed * 31 + bitmap.getPixel(x * (w - 1) / 7, y * (h - 1) / 7);
            }
        }
        Random r = new Random(seed);
        int n = etiquettes.size();
        int haut = r.nextInt(n);
        float conf = 0.52f + r.nextFloat() * 0.45f;
        float[] probas = new float[n];
        float reste = 1f - conf;
        float somme = 0f;
        for (int i = 0; i < n; i++) {
            if (i == haut) continue;
            probas[i] = r.nextFloat() + 0.05f;
            somme += probas[i];
        }
        for (int i = 0; i < n; i++) {
            probas[i] = (i == haut) ? conf : probas[i] / somme * reste;
        }
        return new ResultatClassification(probas);
    }
}
