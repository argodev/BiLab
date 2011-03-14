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