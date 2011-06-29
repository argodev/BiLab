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
 * type/AminoAcid.java . Generated by the IDL-to-Java compiler (portable),
 * version "3.1" from corba/types.idl Monday, August 23, 2004 12:02:37 PM BST
 */

/**
 * Amino Acid. This can be any amino acid, including modified or unusual ones.
 * <P>
 * <dl>
 * <dt>code
 * <dd><A href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/aa_abbrevs.html">
 * IUPAC-IUB</A> one-letter code if the amino acid has one assigned
 * <dd>othwerwise the code will be a blank character
 * <dt>name
 * <dd>descriptive name of the <A
 * href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/aa_abbrevs.html"> Amino
 * Acid</A> or <A
 * href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/modified_aa.html"> modified or
 * unusual Amino Acid</A>
 * <dt>abbreviation
 * <dd>abbreviated name of the <A
 * href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/aa_abbrevs.html"> Amino
 * Acid</A> or <A
 * href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/modified_aa.html"> modified or
 * unusual Amino Acid</A>
 * </dl>
 */
public final class AminoAcid implements org.omg.CORBA.portable.IDLEntity {
    public char code = (char) 0;
    public String name = null;
    public String abbreviation = null;

    public AminoAcid() {
    } // ctor

    public AminoAcid(final char _code, final String _name,
            final String _abbreviation) {
        code = _code;
        name = _name;
        abbreviation = _abbreviation;
    } // ctor

} // class AminoAcid