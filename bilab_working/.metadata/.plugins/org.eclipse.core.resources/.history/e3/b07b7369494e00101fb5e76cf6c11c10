/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The license is also available at: http://www.gnu.org/copyleft/lgpl.html
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
