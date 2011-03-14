package scigol;

public class ScigolException extends java.lang.RuntimeException {
    public ScigolException() {
    }

    public ScigolException(final String s) {
        super(s);
    }

    public ScigolException(final String s, final Exception inner) {
        super(s, inner);
    }
}
