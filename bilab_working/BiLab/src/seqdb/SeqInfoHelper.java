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

package seqdb;

/**
 * seqdb/SeqInfoHelper.java . Generated by the IDL-to-Java compiler (portable),
 * version "3.1" from corba/seqdb.idl Monday, August 23, 2004 12:02:46 PM BST
 */

/**
 * Information associated with a sequence
 */
abstract public class SeqInfoHelper {
    private static String _id = "IDL:seqdb/SeqInfo:1.0";

    private static org.omg.CORBA.TypeCode __typeCode = null;

    public static seqdb.SeqInfo extract(final org.omg.CORBA.Any a) {
        return read(a.create_input_stream());
    }

    public static String id() {
        return _id;
    }

    public static void insert(final org.omg.CORBA.Any a,
            final seqdb.SeqInfo that) {
        final org.omg.CORBA.portable.OutputStream out = a
                .create_output_stream();
        a.type(type());
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }

    public static seqdb.SeqInfo narrow(final org.omg.CORBA.Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof seqdb.SeqInfo) {
            return (seqdb.SeqInfo) obj;
        } else if (!obj._is_a(id())) {
            throw new org.omg.CORBA.BAD_PARAM();
        } else {
            final org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)
                    ._get_delegate();
            final seqdb._SeqInfoStub stub = new seqdb._SeqInfoStub();
            stub._set_delegate(delegate);
            return stub;
        }
    }

    public static seqdb.SeqInfo read(
            final org.omg.CORBA.portable.InputStream istream) {
        return narrow(istream.read_Object(_SeqInfoStub.class));
    }

    synchronized public static org.omg.CORBA.TypeCode type() {
        if (__typeCode == null) {
            __typeCode = org.omg.CORBA.ORB.init().create_interface_tc(
                    seqdb.SeqInfoHelper.id(), "SeqInfo");
        }
        return __typeCode;
    }

    public static void write(final org.omg.CORBA.portable.OutputStream ostream,
            final seqdb.SeqInfo value) {
        ostream.write_Object(value);
    }

}
