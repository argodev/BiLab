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
 * type/TranslationException.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/types.idl Monday, August 23, 2004
 * 12:02:37 PM BST
 */

/**
 * Translation exception of a single triplet within a sequence.
 * <p>
 * <dl>
 * <dt>primary_acc
 * <dd>This attribute is likely to change in the future. It is very rarely used,
 * but it is needed for translation exceptions on CDS's spanning entries
 * <dt>start
 * <dd>startposition of exception in the sequence.
 * <dt>end
 * <dd>endposition of exception in the sequence
 * <dt>amino_acid
 * <dd>amino acid used in this exception.
 * <dd>No modified AA are allowed.
 * </dl>
 */
public final class TranslationException implements
        org.omg.CORBA.portable.IDLEntity {
    public String primary_acc = null;
    public int start = 0;
    public int end = 0;
    public type.AminoAcid amino_acid = null;

    public TranslationException() {
    } // ctor

    public TranslationException(final String _primary_acc, final int _start,
            final int _end, final type.AminoAcid _amino_acid) {
        primary_acc = _primary_acc;
        start = _start;
        end = _end;
        amino_acid = _amino_acid;
    } // ctor

} // class TranslationException