package scigol;

import java.io.InputStream;
import java.io.Reader;

public class LexerSharedInputStateWrapper extends antlr.LexerSharedInputState {
    public CombinedSharedInputState state;

    public LexerSharedInputStateWrapper(final CombinedSharedInputState istate,
            final antlr.InputBuffer inbuf) {
        super(inbuf);
        initialize(istate);
    }

    public LexerSharedInputStateWrapper(final CombinedSharedInputState istate,
            final InputStream inStream) {
        super(inStream);
        initialize(istate);
    }

    public LexerSharedInputStateWrapper(final CombinedSharedInputState istate,
            final Reader inReader) {
        super(inReader);
        initialize(istate);
    }

    protected void initialize(final CombinedSharedInputState istate) {
        state = istate;
    }
}
