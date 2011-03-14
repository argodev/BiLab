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

package type;

/**
 * type/DateHelper.java . Generated by the IDL-to-Java compiler (portable),
 * version "3.1" from corba/types.idl Monday, August 23, 2004 12:02:36 PM BST
 */

/**
 * Date is a struct to describe a date, independent of any report format
 * <dl>
 * <dt>day
 * <dd>day of month as a number between 1-31 (inclusive)
 * <dt>month
 * <dd>month of the year as a number between 1-12 (inclusive)
 * <dt>year
 * <dd>year as a 4 digit number
 * </dl>
 */
abstract public class DateHelper {
    private static String _id = "IDL:type/Date:1.0";

    private static org.omg.CORBA.TypeCode __typeCode = null;

    private static boolean __active = false;

    public static type.Date extract(final org.omg.CORBA.Any a) {
        return read(a.create_input_stream());
    }

    public static String id() {
        return _id;
    }

    public static void insert(final org.omg.CORBA.Any a, final type.Date that) {
        final org.omg.CORBA.portable.OutputStream out = a
                .create_output_stream();
        a.type(type());
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }

    public static type.Date read(
            final org.omg.CORBA.portable.InputStream istream) {
        final type.Date value = new type.Date();
        value.day = istream.read_ushort();
        value.month = istream.read_ushort();
        value.year = istream.read_ushort();
        return value;
    }

    synchronized public static org.omg.CORBA.TypeCode type() {
        if (__typeCode == null) {
            synchronized (org.omg.CORBA.TypeCode.class) {
                if (__typeCode == null) {
                    if (__active) {
                        return org.omg.CORBA.ORB.init()
                                .create_recursive_tc(_id);
                    }
                    __active = true;
                    final org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember[3];
                    org.omg.CORBA.TypeCode _tcOf_members0 = null;
                    _tcOf_members0 = org.omg.CORBA.ORB.init().get_primitive_tc(
                            org.omg.CORBA.TCKind.tk_ushort);
                    _members0[0] = new org.omg.CORBA.StructMember("day",
                            _tcOf_members0, null);
                    _tcOf_members0 = org.omg.CORBA.ORB.init().get_primitive_tc(
                            org.omg.CORBA.TCKind.tk_ushort);
                    _members0[1] = new org.omg.CORBA.StructMember("month",
                            _tcOf_members0, null);
                    _tcOf_members0 = org.omg.CORBA.ORB.init().get_primitive_tc(
                            org.omg.CORBA.TCKind.tk_ushort);
                    _members0[2] = new org.omg.CORBA.StructMember("year",
                            _tcOf_members0, null);
                    __typeCode = org.omg.CORBA.ORB.init().create_struct_tc(
                            type.DateHelper.id(), "Date", _members0);
                    __active = false;
                }
            }
        }
        return __typeCode;
    }

    public static void write(final org.omg.CORBA.portable.OutputStream ostream,
            final type.Date value) {
        ostream.write_ushort(value.day);
        ostream.write_ushort(value.month);
        ostream.write_ushort(value.year);
    }

}
