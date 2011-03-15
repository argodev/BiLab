package scigol;

import antlr.CommonAST;
import antlr.Token;
import antlr.collections.AST;

public class CommonASTWithLocation extends CommonAST {
    public Location loc;

    // protected CommonASTWithLocation(CommonASTWithLocation another) :
    // base(another)
    // {
    // _location = another._location;
    // }

    public CommonASTWithLocation() {
        loc = new Location(0, 0, "?");
    }

    public CommonASTWithLocation(final Token tok) {
        if (!(tok instanceof CommonTokenWithLocation)) {
            initialize(new CommonTokenWithLocation(tok));
        } else {
            initialize(tok);
        }
    }

    @Override
    public void initialize(final AST t) {
        super.initialize(t);
        loc = new Location(0, 0, "?");
    }

    public void initialize(final CommonTokenWithLocation tok) {
        super.initialize(tok);
        loc = new Location(tok.getLine(), tok.getColumn(), tok.getFilename());
    }

    @Override
    public void initialize(final int t, final String txt) {
        super.initialize(t, txt);
        loc = new Location(0, 0, "?");
    }

    public void initialize(final int t, final String txt,
            final Location location) {
        initialize(t, txt);
        loc = location;
    }

    // override public object Clone()
    // {
    // return new CommonASTWithLocation(this);
    // }

    @Override
    public void initialize(final Token tok) {
        if (tok instanceof CommonTokenWithLocation) {
            initialize((CommonTokenWithLocation) tok);
        } else {
            super.initialize(tok);
            loc = new Location(tok.getLine(), tok.getColumn(),
                    tok.getFilename());
        }
    }
}
