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
 * type/CodonTranslationHolder.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/types.idl Monday, August 23, 2004
 * 12:02:37 PM BST
 */

/**
 * Translation rule specifying the amino acid encoded by a codon. Standard rules
 * are defined by the genetic code of an organism. If a CDS uses non-standard
 * rules, this can be annotated with qualifier codon (value type
 * CodonTranslation_s).
 * <p>
 * <dt>codon
 * <dd>literal sequence of the codon.
 * <dt>amino_acid
 * <dd>amino acid used.
 * <dd>No modified AA are allowed.
 */
public final class CodonTranslationHolder implements
        org.omg.CORBA.portable.Streamable {
    public type.CodonTranslation value = null;

    public CodonTranslationHolder() {
    }

    public CodonTranslationHolder(final type.CodonTranslation initialValue) {
        value = initialValue;
    }

    @Override
    public void _read(final org.omg.CORBA.portable.InputStream i) {
        value = type.CodonTranslationHelper.read(i);
    }

    @Override
    public org.omg.CORBA.TypeCode _type() {
        return type.CodonTranslationHelper.type();
    }

    @Override
    public void _write(final org.omg.CORBA.portable.OutputStream o) {
        type.CodonTranslationHelper.write(o, value);
    }

}