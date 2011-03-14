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
 * seqdb/_SeqInfoStub.java . Generated by the IDL-to-Java compiler (portable),
 * version "3.1" from corba/seqdb.idl Monday, August 23, 2004 12:02:46 PM BST
 */

/**
 * Information associated with a sequence
 */
public class _SeqInfoStub extends org.omg.CORBA.portable.ObjectImpl implements
        seqdb.SeqInfo {

    // Type-specific CORBA::Object operations
    private static String[] __ids = { "IDL:seqdb/SeqInfo:1.0" };

    @Override
    public String[] _ids() {
        return __ids.clone();
    }

    /**
     * sequence of comments, describing the characteristics of the sequence
     */
    @Override
    public String[] getComments() throws type.NoResult {
        org.omg.CORBA.portable.InputStream $in = null;
        try {
            final org.omg.CORBA.portable.OutputStream $out = _request(
                    "getComments", true);
            $in = _invoke($out);
            final String $result[] = type.stringListHelper.read($in);
            return $result;
        } catch (final org.omg.CORBA.portable.ApplicationException $ex) {
            $in = $ex.getInputStream();
            final String _id = $ex.getId();
            if (_id.equals("IDL:type/NoResult:1.0")) {
                throw type.NoResultHelper.read($in);
            } else {
                throw new org.omg.CORBA.MARSHAL(_id);
            }
        } catch (final org.omg.CORBA.portable.RemarshalException $rm) {
            return getComments();
        } finally {
            _releaseReply($in);
        }
    } // getComments

    /**
     * cross references to other databases containing related or additional
     * information
     */
    @Override
    public type.DbXref[] getDbXrefs() throws type.NoResult {
        org.omg.CORBA.portable.InputStream $in = null;
        try {
            final org.omg.CORBA.portable.OutputStream $out = _request(
                    "getDbXrefs", true);
            $in = _invoke($out);
            final type.DbXref $result[] = type.DbXrefListHelper.read($in);
            return $result;
        } catch (final org.omg.CORBA.portable.ApplicationException $ex) {
            $in = $ex.getInputStream();
            final String _id = $ex.getId();
            if (_id.equals("IDL:type/NoResult:1.0")) {
                throw type.NoResultHelper.read($in);
            } else {
                throw new org.omg.CORBA.MARSHAL(_id);
            }
        } catch (final org.omg.CORBA.portable.RemarshalException $rm) {
            return getDbXrefs();
        } finally {
            _releaseReply($in);
        }
    } // getDbXrefs

    /**
     * short (one line) description
     */
    @Override
    public String getDescription() throws type.NoResult {
        org.omg.CORBA.portable.InputStream $in = null;
        try {
            final org.omg.CORBA.portable.OutputStream $out = _request(
                    "getDescription", true);
            $in = _invoke($out);
            final String $result = $in.read_string();
            return $result;
        } catch (final org.omg.CORBA.portable.ApplicationException $ex) {
            $in = $ex.getInputStream();
            final String _id = $ex.getId();
            if (_id.equals("IDL:type/NoResult:1.0")) {
                throw type.NoResultHelper.read($in);
            } else {
                throw new org.omg.CORBA.MARSHAL(_id);
            }
        } catch (final org.omg.CORBA.portable.RemarshalException $rm) {
            return getDescription();
        } finally {
            _releaseReply($in);
        }
    } // getDescription

    /**
     * sequence of keywords, describing the characteristics of the sequence
     */
    @Override
    public String[] getKeywords() throws type.NoResult {
        org.omg.CORBA.portable.InputStream $in = null;
        try {
            final org.omg.CORBA.portable.OutputStream $out = _request(
                    "getKeywords", true);
            $in = _invoke($out);
            final String $result[] = type.stringListHelper.read($in);
            return $result;
        } catch (final org.omg.CORBA.portable.ApplicationException $ex) {
            $in = $ex.getInputStream();
            final String _id = $ex.getId();
            if (_id.equals("IDL:type/NoResult:1.0")) {
                throw type.NoResultHelper.read($in);
            } else {
                throw new org.omg.CORBA.MARSHAL(_id);
            }
        } catch (final org.omg.CORBA.portable.RemarshalException $rm) {
            return getKeywords();
        } finally {
            _releaseReply($in);
        }
    } // getKeywords

    /**
     * cross references to the EMBL publication database information
     */
    @Override
    public type.DbXref[] getReferences() throws type.NoResult {
        org.omg.CORBA.portable.InputStream $in = null;
        try {
            final org.omg.CORBA.portable.OutputStream $out = _request(
                    "getReferences", true);
            $in = _invoke($out);
            final type.DbXref $result[] = type.DbXrefListHelper.read($in);
            return $result;
        } catch (final org.omg.CORBA.portable.ApplicationException $ex) {
            $in = $ex.getInputStream();
            final String _id = $ex.getId();
            if (_id.equals("IDL:type/NoResult:1.0")) {
                throw type.NoResultHelper.read($in);
            } else {
                throw new org.omg.CORBA.MARSHAL(_id);
            }
        } catch (final org.omg.CORBA.portable.RemarshalException $rm) {
            return getReferences();
        } finally {
            _releaseReply($in);
        }
    } // getReferences

    private void readObject(final java.io.ObjectInputStream s)
            throws java.io.IOException {
        final String str = s.readUTF();
        final String[] args = null;
        final java.util.Properties props = null;
        final org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init(args, props)
                .string_to_object(str);
        final org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)
                ._get_delegate();
        _set_delegate(delegate);
    }

    private void writeObject(final java.io.ObjectOutputStream s)
            throws java.io.IOException {
        final String[] args = null;
        final java.util.Properties props = null;
        final String str = org.omg.CORBA.ORB.init(args, props)
                .object_to_string(this);
        s.writeUTF(str);
    }
} // class _SeqInfoStub
