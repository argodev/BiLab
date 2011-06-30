/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bilab;

import java.io.IOException;
import java.io.Writer;

// / a INotifier that outputs to a Writer
public class WriterNotifier implements INotifier {
    public static final String EOL = "\n";

    protected Writer w;

    public WriterNotifier(final Writer w) {
        this.w = w;
    }

    @Override
    public void DevError(final Object from, final String message) {
        final String className = (from instanceof java.lang.Class) ? ((java.lang.Class) from)
                .toString() : from.getClass().toString();
        final String s = "dev error: " + className + " - " + message + EOL;
        try {
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
            System.err.println(s);
        }
    }

    @Override
    public void DevInfo(final Object from, final String message) {
        final String className = (from instanceof java.lang.Class) ? ((java.lang.Class) from)
                .toString() : from.getClass().toString();
        final String s = "dev info: " + className + " - " + message + EOL;
        try {
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
            System.err.println(s);
        }
    }

    @Override
    public void DevStatus(final Object from, final String message) {
        final String className = (from instanceof java.lang.Class) ? ((java.lang.Class) from)
                .toString() : from.getClass().toString();
        final String s = "dev status: " + className + " - " + message + EOL;
        try {
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
            System.err.println(s);
        }
    }

    @Override
    public void DevWarning(final Object from, final String message) {
        final String className = (from instanceof java.lang.Class) ? ((java.lang.Class) from)
                .toString() : from.getClass().toString();
        final String s = "dev warning: " + className + " - " + message + EOL;
        try {
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
            System.err.println(s);
        }
    }

    @Override
    public void EndProgress(final Object from) {
        try {
            final String s = "..] done." + EOL;
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
        }
    }

    @Override
    public void LogError(final Object from, final String message) {
        final String className = (from instanceof java.lang.Class) ? ((java.lang.Class) from)
                .toString() : from.getClass().toString();
        final String s = "log error: " + className + " - " + message + EOL;
        try {
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
            System.err.println(s);
        }
    }

    @Override
    public void LogInfo(final Object from, final String message) {
        final String s = "log info: " + message + EOL;
        try {
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
            System.err.println(s);
        }
    }

    @Override
    public void PopLevel(final Object from) {
    }

    @Override
    public void Progress(final Object from, final double percent) {
        try {
            final String s = ".." + percent + "%%";
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
        }
    }

    @Override
    public void PushLevel(final Object from) {
    }

    @Override
    public void StartProgress(final Object from, final String task) {
        try {
            final String s = task + " [";
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
        }
    }

    @Override
    public void UserError(final Object from, final String message) {
        final String s = "error: " + message + EOL;
        try {
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
            System.err.println(s);
        }
    }

    @Override
    public void UserInfo(final Object from, final String message) {
        final String s = "info: " + message + EOL;
        try {
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
            System.err.println(s);
        }
    }

    @Override
    public void UserStatus(final Object from, final String message) {
        final String s = "status: " + message + EOL;
        try {
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
            System.err.println(s);
        }
    }

    @Override
    public void UserWarning(final Object from, final String message) {
        final String s = "warning: " + message + EOL;
        try {
            w.write(s, 0, s.length());
            w.flush();
        } catch (final IOException e) {
            System.err.println(s);
        }
    }
}
