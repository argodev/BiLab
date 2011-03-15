package scigol;

public class Debug {

    public static void Assert(final boolean c) {
        if (!c) {
            throw new InternalScigolError("assertion failure");
        }
    }

    public static void Assert(final boolean c, final String message) {
        if (!c) {
            throw new InternalScigolError("assertion failure:" + message);
        }
    }

    public static void Depricated() {
        throw new InternalScigolError("Depricated");
    }

    public static void Depricated(final String s) {
        throw new InternalScigolError("Depricated:" + s);
    }

    public static String stackTraceString() {
        try {
            throw new Exception("call stack:");
        } catch (final Exception e) {
            return e.toString();// !!! check this actually includes the trace
                                // into
        }
    }

    public static void Unimplemented() {
        throw new InternalScigolError("Unimplemented");
    }

    public static void Unimplemented(final String s) {
        throw new InternalScigolError("Unimplemented:" + s);
    }

    public static void Warning(final String message) {
        System.out.println("Warning: " + message);
    }

    // shorthand
    public static void WL(final String message) {
        System.out.println(message);
    }

    public static void Write(final String message) {
        System.out.println(message);
    }

    public static void WriteLine(final String message) {
        System.out.println(message);
    }

    public static void WriteStack() {
        System.out.println(stackTraceString());
    }

}
