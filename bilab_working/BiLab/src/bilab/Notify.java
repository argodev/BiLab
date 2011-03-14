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

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import scigol.accessor;

// / convenience singleton to access to system-wide default notifier
public class Notify {
    private static INotifier notifier = null;

    static {
        if (notifier == null) {
            notifier = new WriterNotifier(new BufferedWriter(
                    new OutputStreamWriter(System.out)));
        }
    }

    // aliases for DevInfo
    public static void debug(final Object from, final String message) {
        devInfo(from, message);
    }

    public static void debug(final String message) {
        devInfo(Notify.class, message);
    }

    public static void devError(final Object from, final String message) {
        notifier.DevError(from, message);
    }

    public static void devInfo(final Object from, final String message) {
        notifier.DevInfo(from, message);
    }

    public static void devStatus(final Object from, final String message) {
        notifier.DevStatus(from, message);
    }

    public static void devWarning(final Object from, final String message) {
        notifier.DevWarning(from, message);
    }

    public static void endProgress(final Object from) {
        notifier.EndProgress(from);
    }

    @accessor
    public static INotifier get_Notifier() {
        return notifier;
    }

    public static void logError(final Object from, final String message) {
        notifier.LogError(from, message);
    }

    public static void logInfo(final Object from, final String message) {
        notifier.LogInfo(from, message);
    }

    public static void progress(final Object from, final double percent) {
        notifier.Progress(from, percent);
    }

    @accessor
    public static void set_Notifier(final INotifier value) {
        notifier = value;
    }

    // convenience pass-through to notifier
    public static void startProgress(final Object from, final String task) {
        notifier.StartProgress(from, task);
    }

    // alias for DevError & throw exception
    public static void unimplemented(final Object from) {
        devError(from, "unimplemented");
        final String className = (from instanceof java.lang.Class) ? ((java.lang.Class) from)
                .toString() : from.getClass().toString();

        throw new BilabException("method of " + className
                + " invoked unimplemented");
    }

    public static void userError(final Object from, final String message) {
        notifier.UserError(from, message);
    }

    public static void userInfo(final Object from, final String message) {
        notifier.UserInfo(from, message);
    }

    public static void userStatus(final Object from, final String message) {
        notifier.UserStatus(from, message);
    }

    public static void userWarning(final Object from, final String message) {
        notifier.UserWarning(from, message);
    }

    public Notify() {
    }
}