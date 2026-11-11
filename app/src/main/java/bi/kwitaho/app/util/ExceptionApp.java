package bi.kwitaho.app.util;

public class ExceptionApp extends Exception {
    public final ErreurApp erreur;

    public ExceptionApp(ErreurApp erreur) {
        super(erreur.name());
        this.erreur = erreur;
    }

    public ExceptionApp(ErreurApp erreur, Throwable cause) {
        super(erreur.name(), cause);
        this.erreur = erreur;
    }
}
