package scigol;

public class Location {
    public int line;

    public int column;

    public String filename;

    public Location() {
        line = column = -1;
        filename = "?";
    }

    public Location(final int line, final int col, final String filename) {
        if ((filename == null) || (filename == "")) {
            Debug.WriteLine("got filename:"
                    + ((filename == null) ? "null" : filename));
        }
        Debug.Assert((filename != null) && (filename != ""));
        this.line = line;
        column = col;
        this.filename = filename;
    }

    public boolean isKnown() {
        return (line != -1);
    }

    @Override
    public String toString() {
        if (!isKnown() || ((line == 0) && (column == 0))) {
            return filename;
        } else {
            return filename + ":" + line + ":" + column;
        }
    }

}
