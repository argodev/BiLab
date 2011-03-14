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

package bilab;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.impl.SimpleSequence;
import org.biojava.bio.symbol.Alphabet;
import org.biojava.bio.symbol.IllegalAlphabetException;

import scigol.Any;
import scigol.Debug;
import scigol.Range;
import scigol.accessor;

@Summary("A RNA sequence molecule")
public class RNA extends seq {
    public static RNA op_Addition(final RNA rna1, final RNA rna2) {
        // do by string
        final String seq1 = rna1.get_rawsequence();
        final String seq2 = rna2.get_rawsequence();
        return new RNA(seq1 + seq2);
    }

    public static int op_Card(final RNA rna) {
        return rna.seq.length();
    }

    public static RNA op_UnaryNegation(final RNA rna) {
        return rna.get_Compliment();
    }

    public static protein translate(final RNA rna) {
        return rna.get_translation();
    }

    @Summary("The underlying biojava Sequence representing this RNA")
    @Sophistication(Sophistication.Advanced)
    public Sequence seq;

    public RNA() {
        try {
            seq = RNATools.createRNASequence("", "rna");
        } catch (final BioException e) {
            Debug.Assert(false, "biojava error");
        }
    }

    public RNA(final Sequence s) {
        seq = s;
    }

    public RNA(final String seq) {
        try {
            this.seq = RNATools.createRNASequence(seq, "rna");
        } catch (final BioException e) {
            throw new BilabException("biojava error", e);
        }
    }

    @Override
    public scigol.Map get_annotations() {
        // for now just copy annot into a new Map, later make a wrapper that
        // extends Map
        // so that the properties can be changed
        // NB: we use the seq.getAnnotation, not seq.annot
        final scigol.Map m = new scigol.Map();
        for (final java.util.Iterator i = seq.getAnnotation().keys().iterator(); i
                .hasNext();) {
            final Object key = i.next();
            m.add(key, seq.getAnnotation().getProperty(key));
        }
        return m;
    }

    @accessor
    public RNA get_Compliment() {
        try {
            return new RNA(new SimpleSequence(RNATools.reverseComplement(seq),
                    seq.getURN() + "|compliment",
                    seq.getName() + " compliment", seq.getAnnotation()));
        } catch (final IllegalAlphabetException e) {
            throw new BilabException(
                    "Cannot reverse compliment RNA with alphabet '"
                            + seq.getAlphabet().getName() + "'");
        }
    }

    @Override
    @accessor
    public String get_DetailText() {
        // !!! implement properly
        return get_ShortText();
    }

    @Override
    @accessor
    public Any get_Item(final int i) {
        try {
            // return a molecule representing the specific aminoacid
            final org.biojava.bio.symbol.Symbol sym = seq.symbolAt(i + 1);
            final Alphabet alpha = sym.getMatches();
            final scigol.List mols = new scigol.List();
            if (alpha.contains(RNATools.a())) {
                mols.add(MoleculeImpl.A);
            }
            if (alpha.contains(RNATools.c())) {
                mols.add(MoleculeImpl.C);
            }
            if (alpha.contains(RNATools.g())) {
                mols.add(MoleculeImpl.G);
            }
            if (alpha.contains(RNATools.u())) {
                mols.add(MoleculeImpl.U);
            }

            if (mols.get_size() == 1) {
                return mols.get_head();
            }
            return new Any(mols);
        } catch (final IndexOutOfBoundsException e) {
            throw new BilabException("index must be in range 0.."
                    + (seq.length() - 1) + ", not " + i);
        }
    }

    @Override
    @accessor
    public RNA get_Item(Range r) {
        r = r.normalize(seq.length());
        return new RNA(seq.subStr(r.start + 1, r.end + 1));
    }

    @Override
    @Summary("get sequence as a string of continous letters")
    public String get_rawsequence() {
        return seq.seqString().toUpperCase();
    }

    @Override
    @Summary("get sequence as a string of letters formatted into columns")
    public String get_sequence() {
        return formatSeqString(seq.seqString().toUpperCase());
    }

    @Override
    @accessor
    public String get_ShortText() {
        return seq.getName();
    }

    @accessor
    public protein get_translation() {
        try {
            return new protein(new SimpleSequence(RNATools.translate(seq),
                    seq.getURN() + "|translation", seq.getName()
                            + " translation", seq.getAnnotation()));
        } catch (final BioException e) {
            throw new BilabException("biojava error", e);
        }
    }

    public void importFrom(final String resourceName) {
        Debug.Unimplemented();
    }

    @Override
    @accessor
    public void set_Item(final int i, final Any value) {
        Debug.Unimplemented();
    }

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }

}
