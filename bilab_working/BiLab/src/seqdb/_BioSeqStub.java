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
 * seqdb/_BioSeqStub.java . Generated by the IDL-to-Java compiler (portable),
 * version "3.1" from corba/seqdb.idl Monday, August 23, 2004 12:02:46 PM BST
 */

/**
 * generic biosequence. Provides al functionality we would like to see on any
 * sequence.
 */
public class _BioSeqStub extends org.omg.CORBA.portable.ObjectImpl implements
        seqdb.BioSeq {

    // Type-specific CORBA::Object operations
    private static String[] __ids = { "IDL:seqdb/BioSeq:1.0" };

    @Override
    public String[] _ids() {
        return __ids.clone();
    }

    /**
     * sequence of objects describing the elements in the biosequence This is a
     * generic description. Most subclasses will define more convenient methods
     * for accessing the biosequence.
     * 
     * @returns any containing an set of objects. The any should have a typecode
     *          tk_array.
     */
    @Override
    public org.omg.CORBA.Any getAnySeq() {
        org.omg.CORBA.portable.InputStream $in = null;
        try {
            final org.omg.CORBA.portable.OutputStream $out = _request(
                    "getAnySeq", true);
            $in = _invoke($out);
            final org.omg.CORBA.Any $result = $in.read_any();
            return $result;
        } catch (final org.omg.CORBA.portable.ApplicationException $ex) {
            $in = $ex.getInputStream();
            final String _id = $ex.getId();
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (final org.omg.CORBA.portable.RemarshalException $rm) {
            return getAnySeq();
        } finally {
            _releaseReply($in);
        }
    } // getAnySeq

    /**
     * retrieve unique identifier
     */
    @Override
    public String getBioSeqId() {
        org.omg.CORBA.portable.InputStream $in = null;
        try {
            final org.omg.CORBA.portable.OutputStream $out = _request(
                    "getBioSeqId", true);
            $in = _invoke($out);
            final String $result = $in.read_string();
            return $result;
        } catch (final org.omg.CORBA.portable.ApplicationException $ex) {
            $in = $ex.getInputStream();
            final String _id = $ex.getId();
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (final org.omg.CORBA.portable.RemarshalException $rm) {
            return getBioSeqId();
        } finally {
            _releaseReply($in);
        }
    } // getBioSeqId

    /**
     * Return current version of the BioSeq. returns 0 if versioning is not
     * implemented on the bioseq.
     */
    @Override
    public int getBioSeqVersion() {
        org.omg.CORBA.portable.InputStream $in = null;
        try {
            final org.omg.CORBA.portable.OutputStream $out = _request(
                    "getBioSeqVersion", true);
            $in = _invoke($out);
            final int $result = $in.read_ulong();
            return $result;
        } catch (final org.omg.CORBA.portable.ApplicationException $ex) {
            $in = $ex.getInputStream();
            final String _id = $ex.getId();
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (final org.omg.CORBA.portable.RemarshalException $rm) {
            return getBioSeqVersion();
        } finally {
            _releaseReply($in);
        }
    } // getBioSeqVersion

    /**
     * length (nr of elements) of the biosequence
     */
    @Override
    public int getLength() {
        org.omg.CORBA.portable.InputStream $in = null;
        try {
            final org.omg.CORBA.portable.OutputStream $out = _request(
                    "getLength", true);
            $in = _invoke($out);
            final int $result = $in.read_ulong();
            return $result;
        } catch (final org.omg.CORBA.portable.ApplicationException $ex) {
            $in = $ex.getInputStream();
            final String _id = $ex.getId();
            throw new org.omg.CORBA.MARSHAL(_id);
        } catch (final org.omg.CORBA.portable.RemarshalException $rm) {
            return getLength();
        } finally {
            _releaseReply($in);
        }
    } // getLength

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
} // class _BioSeqStub
