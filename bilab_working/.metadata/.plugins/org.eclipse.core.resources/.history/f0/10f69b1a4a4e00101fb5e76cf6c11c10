package scigol;

import antlr.CommonToken;
import antlr.Token;

public class CommonTokenWithLocation extends CommonToken {
    public Location loc;

    public CommonTokenWithLocation() {
        loc = new Location();
    }

    public CommonTokenWithLocation(final int t, final String txt) {
        super(t, txt);
        loc = new Location();
    }

    public CommonTokenWithLocation(final String s) {
        super(s);
        loc = new Location();
    }

    public CommonTokenWithLocation(final Token tok) {
        super(tok.getType(), tok.getText());
        setLine(tok.getLine());
        setColumn(tok.getColumn());
        if (tok.getFilename() != null) {
            setFilename(tok.getFilename());
        } else {
            setFilename("?");
        }

        if (!(tok instanceof CommonTokenWithLocation)) {
            loc = new Location();
        } else {
            loc = ((CommonTokenWithLocation) tok).loc;
        }
    }

    @Override
    public String getFilename() {
        if ((loc != null) && (loc.filename != null)) {
            return loc.filename;
        } else {
            return "?";
        }
    }

    @Override
    public void setColumn(final int c) {
        if (loc == null) {
            loc = new Location();
        }
        loc.column = c;
        super.setColumn(c);
    }

    @Override
    public void setFilename(final String name) {
        Debug.Assert(name != null);
        if (loc == null) {
            loc = new Location();
        }
        loc.filename = name;
    }

    @Override
    public void setLine(final int l) {
        if (loc == null) {
            loc = new Location();
        }
        loc.line = l;
        super.setLine(l);
    }
}
