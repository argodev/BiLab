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
 * type/PersonListHelper.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/types.idl Monday, August 23, 2004
 * 12:02:37 PM BST
 */

abstract public class PersonListHelper {
    private static String _id = "IDL:type/PersonList:1.0";

    private static org.omg.CORBA.TypeCode __typeCode = null;

    public static type.Person[] extract(final org.omg.CORBA.Any a) {
        return read(a.create_input_stream());
    }

    public static String id() {
        return _id;
    }

    public static void insert(final org.omg.CORBA.Any a,
            final type.Person[] that) {
        final org.omg.CORBA.portable.OutputStream out = a
                .create_output_stream();
        a.type(type());
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }

    public static type.Person[] read(
            final org.omg.CORBA.portable.InputStream istream) {
        type.Person value[] = null;
        final int _len0 = istream.read_long();
        value = new type.Person[_len0];
        for (int _o1 = 0; _o1 < value.length; ++_o1) {
            value[_o1] = type.PersonHelper.read(istream);
        }
        return value;
    }

    synchronized public static org.omg.CORBA.TypeCode type() {
        if (__typeCode == null) {
            __typeCode = type.PersonHelper.type();
            __typeCode = org.omg.CORBA.ORB.init().create_sequence_tc(0,
                    __typeCode);
            __typeCode = org.omg.CORBA.ORB.init().create_alias_tc(
                    type.PersonListHelper.id(), "PersonList", __typeCode);
        }
        return __typeCode;
    }

    public static void write(final org.omg.CORBA.portable.OutputStream ostream,
            final type.Person[] value) {
        ostream.write_long(value.length);
        for (int _i0 = 0; _i0 < value.length; ++_i0) {
            type.PersonHelper.write(ostream, value[_i0]);
        }
    }

}
