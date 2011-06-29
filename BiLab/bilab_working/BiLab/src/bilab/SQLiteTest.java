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

/*
 * Created on Jan 27, 2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package bilab;

import SQLite.Constants;
import SQLite.FunctionContext;
import SQLite.Vm;

public class SQLiteTest implements SQLite.Callback, SQLite.Function,
        SQLite.Authorizer, SQLite.Trace, SQLite.ProgressHandler {

    public static void test() {
        System.out.println("LIB version: " + SQLite.Database.version());
        final SQLite.Database db = new SQLite.Database();
        try {
            db.open("U:\\dev\\GTL\\win\\BiLabRCP\\BiLab\\resources\\test.db",
                    0666);
            System.out.println("DB version: " + db.dbversion());
            db.interrupt();
            db.busy_timeout(1000);
            db.busy_handler(null);
            db.exec("create table TEST (firstname char(32), lastname char(32) )",
                    new SQLiteTest()); // -DJ
            db.create_function("myregfunc", -1, new SQLiteTest());
            db.function_type("myregfunc", Constants.SQLITE_TEXT);
            db.create_aggregate("myaggfunc", 1, new SQLiteTest());
            db.function_type("myaggfunc", Constants.SQLITE_TEXT);
            db.exec("PRAGMA show_datatypes = on", null);
            System.out.println("==== local callback ====");
            db.exec("select * from sqlite_master", new SQLiteTest());
            System.out.println("==== get_table ====");
            System.out.print(db.get_table("select * from TEST"));
            System.out.println("==== get_table w/ args ====");
            final String qargs[] = new String[1];
            qargs[0] = "tab%";
            System.out.print(db.get_table(
                    "select * from sqlite_master where type like '%q'", qargs));
            System.out.println("==== call regular function ====");
            db.exec("select myregfunc(TEST.firstname) from TEST",
                    new SQLiteTest());
            System.out.println("==== call aggregate function ====");
            db.exec("select myaggfunc(TEST.firstname) from TEST",
                    new SQLiteTest());
            System.out.println("==== compile/step interface ====");
            final SQLiteTest cb = new SQLiteTest();
            db.set_authorizer(cb);
            db.trace(cb);
            final Vm vm = db.compile("select * from TEST; "
                    + "delete from TEST where id = 5; "
                    + "insert into TEST values(5, 'Speedy', 'Gonzales'); "
                    + "select * from TEST");
            int stmt = 0;
            do {
                ++stmt;
                if (stmt > 3) {
                    System.out.println("setting progress handler");
                    db.progress_handler(3, cb);
                }
                System.out.println("---- STMT #" + stmt + " ----");
                while (vm.step(cb)) {
                }
            } while (vm.compile());
            db.close();
            System.out.println("An exception is expected from now on.");
            System.out.println("==== local callback ====");
            db.exec("select * from sqlite_master", new SQLiteTest());
        } catch (final java.lang.Exception e) {
            System.err.println("error: " + e);
        } finally {
            try {
                System.err.println("cleaning up ...");
                db.close();
            } catch (final java.lang.Exception e) {
                System.err.println("error: " + e);
            } finally {
                System.err.println("done.");
            }
        }

    }

    private final StringBuffer acc = new StringBuffer();

    @Override
    public int authorize(final int what, final String arg1, final String arg2,
            final String arg3, final String arg4) {
        System.out.println("AUTH: " + what + "," + arg1 + "," + arg2 + ","
                + arg3 + "," + arg4);
        return Constants.SQLITE_OK;
    }

    @Override
    public void columns(final String col[]) {
        System.out.println("#cols = " + col.length);
        for (int i = 0; i < col.length; i++) {
            System.out.println("col" + i + ": " + col[i]);
        }
        // throw new java.lang.RuntimeException("boom");
    }

    @Override
    public void function(final FunctionContext fc, final String args[]) {
        System.out.println("function:");
        for (int i = 0; i < args.length; i++) {
            System.out.println("arg[" + i + "]=" + args[i]);
        }
        if (args.length > 0) {
            fc.set_result(args[0].toLowerCase());
        }
    }

    @Override
    public void last_step(final FunctionContext fc) {
        System.out.println("last_step");
        fc.set_result(acc.toString());
        acc.setLength(0);
    }

    @Override
    public boolean newrow(final String data[]) {
        for (int i = 0; i < data.length; i++) {
            System.out.println("data" + i + ": " + data[i]);
        }
        return false;
    }

    @Override
    public boolean progress() {
        System.out.println("PROGRESS");
        return true;
    }

    @Override
    public void step(final FunctionContext fc, final String args[]) {
        System.out.println("step:");
        for (int i = 0; i < args.length; i++) {
            acc.append(args[i]);
            acc.append(" ");
        }
    }

    @Override
    public void trace(final String stmt) {
        System.out.println("TRACE: " + stmt);
    }

    @Override
    public void types(final String types[]) {
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                System.out.println("coltype" + i + ": " + types[i]);
            }
        }
    }
}
