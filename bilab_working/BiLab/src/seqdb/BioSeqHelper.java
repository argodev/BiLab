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
 * seqdb/BioSeqHelper.java . Generated by the IDL-to-Java compiler (portable),
 * version "3.1" from corba/seqdb.idl Monday, August 23, 2004 12:02:46 PM BST
 */

/**
 * generic biosequence. Provides al functionality we would like to see on any
 * sequence.
 */
abstract public class BioSeqHelper {
    private static String _id = "IDL:seqdb/BioSeq:1.0";

    private static org.omg.CORBA.TypeCode __typeCode = null;

    public static seqdb.BioSeq extract(final org.omg.CORBA.Any a) {
        return read(a.create_input_stream());
    }

    public static String id() {
        return _id;
    }

    public static void insert(final org.omg.CORBA.Any a, final seqdb.BioSeq that) {
        final org.omg.CORBA.portable.OutputStream out = a
                .create_output_stream();
        a.type(type());
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }

    public static seqdb.BioSeq narrow(final org.omg.CORBA.Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof seqdb.BioSeq) {
            return (seqdb.BioSeq) obj;
        } else if (!obj._is_a(id())) {
            throw new org.omg.CORBA.BAD_PARAM();
        } else {
            final org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)
                    ._get_delegate();
            final seqdb._BioSeqStub stub = new seqdb._BioSeqStub();
            stub._set_delegate(delegate);
            return stub;
        }
    }

    public static seqdb.BioSeq read(
            final org.omg.CORBA.portable.InputStream istream) {
        return narrow(istream.read_Object(_BioSeqStub.class));
    }

    synchronized public static org.omg.CORBA.TypeCode type() {
        if (__typeCode == null) {
            __typeCode = org.omg.CORBA.ORB.init().create_interface_tc(
                    seqdb.BioSeqHelper.id(), "BioSeq");
        }
        return __typeCode;
    }

    public static void write(final org.omg.CORBA.portable.OutputStream ostream,
            final seqdb.BioSeq value) {
        ostream.write_Object(value);
    }

}
