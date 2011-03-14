package scigol;

public class ParserSharedInputStateWrapper extends antlr.ParserSharedInputState {
    public CombinedSharedInputState state;

    public ParserSharedInputStateWrapper(final CombinedSharedInputState istate) {
        state = istate;
    }
}
