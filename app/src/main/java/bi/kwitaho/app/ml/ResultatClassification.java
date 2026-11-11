package bi.kwitaho.app.ml;

/** Probabilités par classe, dans l'ordre de labels.txt. */
public final class ResultatClassification {
    public final float[] probas;

    public ResultatClassification(float[] probas) {
        this.probas = probas;
    }
}
