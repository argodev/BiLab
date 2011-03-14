/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The license is also available at: http://www.gnu.org/copyleft/lgpl.html
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
