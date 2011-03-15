package scigol;

// !!! consider supporting a stride as well

// / A range of element indices
// ranges are inclusive of both the start and end indices
// e.g. if l = (0,1,2,3) then l(0..2) == (0,1,2)
// -ve indices count from the sequence length being accessed, backward
// e.g -1 is the last element
public class Range {
    // public static int End = 2147483647; // 2^31-1

    public static int op_Card(final Range r) {
        if (sign(r.start) == sign(r.end)) {
            return r.end - r.start + 1;
        }
        return -1;
    }

    protected static int sign(final int i) {
        return (i >= 0) ? 1 : -1;
    }

    public int start;

    public int end;

    public Range() {
        start = 0;
        end = -1;
    }

    public Range(final int start, final int end) {
        this.start = start;
        this.end = end;
    }

    public Range normalize(final int count) {
        final Range r = new Range(start, end);
        if (start < 0) {
            r.start = count + start;
        }
        if (end < 0) {
            r.end = count + end;
        }
        return r;
    }

    @Override
    public String toString() {
        return start + ".." + end;
    }

}
