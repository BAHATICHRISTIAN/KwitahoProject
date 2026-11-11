package bi.kwitaho.app.util;

/** Événement à consommer une seule fois (messages, navigation). */
public class Evenement<T> {
    private final T contenu;
    private boolean traite = false;

    public Evenement(T contenu) {
        this.contenu = contenu;
    }

    public T obtenirContenuSiNonTraite() {
        if (traite) return null;
        traite = true;
        return contenu;
    }

    public T apercu() {
        return contenu;
    }
}
