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

import org.biojava.bio.Annotation;
import org.biojava.bio.BioError;
import org.biojava.bio.BioException;
import org.biojava.bio.proteomics.MassCalc;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.Alphabet;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolPropertyTable;
import org.biojava.utils.ChangeVetoException;

import scigol.Any;
import scigol.Range;
import scigol.TypeSpec;
import scigol.accessor;

@Summary("A carbohydrate sequence molecule")
public class carbohydrate extends seq {
    public static carbohydrate op_Addition(final carbohydrate p1, final carbohydrate p2) {
        // do by string
        final String seq1 = p1.get_rawsequence();
        final String seq2 = p2.get_rawsequence();
        return new carbohydrate(seq1 + seq2);
    }

    public static int op_Card(final carbohydrate p) {
        return p.seq.length();
    }

    @Summary("The underlying biojava Sequence representing this carbohydrate")
    @Sophistication(Sophistication.Advanced)
    public Sequence seq;

    public carbohydrate() {
        seq = null;
    }

    public carbohydrate(final Sequence s) {
        seq = s;
    }

    public carbohydrate(final String seq) {
        try {
            this.seq = ProteinTools.createProteinSequence(seq, "carbohydrate");
        } catch (final BioException e) {
            throw new BilabException("biojava error:" + e.getMessage(), e);
        }
    }

    protected void copyAnnotation(final Annotation from, final Annotation to) {
        try {
            for (final Object key : from.keys()) {
                to.setProperty(key, from.getProperty(key));
            }
        } catch (final ChangeVetoException e) {
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
            m.add(((String) key).toLowerCase(), seq.getAnnotation()
                    .getProperty(key));
        }
        return m;
    }

//    @accessor
//    public double get_avgIsoMass() {
//        try {
//            final MassCalc mc = new MassCalc(SymbolPropertyTable.AVG_MASS,
//                    false);
//            return mc.getMass(seq);
//        } catch (final BioException e) {
//           throw new BilabException("biojava error", e);
//        }
//    }

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
            if (alpha.contains(ProteinTools.o())) {
                mols.add(MoleculeImpl.GLUCOSE);
            }
            if (alpha.contains(ProteinTools.n())) {
                mols.add(MoleculeImpl.GALACTOSE);
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
    public carbohydrate get_Item(Range r) {
        r = r.normalize(seq.length());
        return new carbohydrate(seq.subStr(r.start + 1, r.end + 1));
    }

    @accessor
    public double get_MonoIsotopicMass() {
        try {
            return MassCalc
                    .getMass(seq, "SymbolPropertyTable.MONO_MASS", false);
        } catch (final BioException e) {
            throw new BilabException("biojava error", e);
        }
    }

    @Override
    @Summary("get sequence as a string of continous letters")
    public String get_rawsequence() {
        return seq.seqString().toUpperCase();
    }

    @Override
    @Summary("get sequence as a string of letters formatted into columns")
    public String get_sequence() {
        return formatSeqString(seq.seqString());
    }

    @Override
    @accessor
    public String get_ShortText() {
        // !!! implement properly
        return seq.getName();
    }

    @Override
    @accessor
    public void set_Item(final int i, final Any value) {
        final Object item = TypeSpec.unwrapAny(value);

        try {
            if (item instanceof MoleculeImpl) {
                final MoleculeImpl mol = (MoleculeImpl) item;

                // do subst by string for now
                char s = '?';

                if (mol == MoleculeImpl.GLUCOSE) {
                    s = 'O';
                }

                if (mol == MoleculeImpl.GALACTOSE) {
                    s = 'N';
                }

                if (s != '?') {
                    final String seqStr = seq.seqString();
                    final char[] seqChars = seqStr.toCharArray();

                    seqChars[i] = s;
                    final Sequence newseq = ProteinTools.createProteinSequence(
                            new String(seqChars), seq.getAlphabet().getName());
                    copyAnnotation(seq.getAnnotation(), newseq.getAnnotation());
                    seq = newseq;
                } else {
                    throw new BilabException(
                            "unable to add given molecule to carbohydrate");
                }
            } else if (TypeSpec.typeOf(item).isChar()) { // why doesn't
                                                         // instanceof char
                                                         // work?
                final String seqStr = seq.seqString();
                final char[] seqChars = seqStr.toCharArray();

                seqChars[i] = ((Character) item).charValue();
                final Sequence newseq = ProteinTools.createProteinSequence(
                        new String(seqChars), seq.getAlphabet().getName());
                copyAnnotation(seq.getAnnotation(), newseq.getAnnotation());
                seq = newseq;
            } else if (item instanceof String) {
                final String seqStr = seq.seqString();
                final String newStr = seqStr.substring(0, i) + (String) item
                        + seqStr.substring(i + 1, seqStr.length());
                final Sequence newseq = ProteinTools.createProteinSequence(
                        newStr, seq.getAlphabet().getName());
                copyAnnotation(seq.getAnnotation(), newseq.getAnnotation());
                seq = newseq;
            } else {
                throw new BilabException("unable to add an element of type "
                        + TypeSpec.typeOf(item) + " to a carbohydrate sequence");
            }

            // indicate the editing in the annotations
            seq.getAnnotation().setProperty("edited",
                    "true; sequence edited from original");
        } catch (final IllegalSymbolException e) {
            throw new BilabException("illegal symbol");
        } catch (final BioError e) {
            throw new BilabException("biojava error adding an element of type "
                    + TypeSpec.typeOf(item) + " to a carbohydrate sequence");
        } catch (final Exception e) {
            throw new BilabException("exception adding an element of type "
                    + TypeSpec.typeOf(item) + " to a carbohydrate sequence");
        }

    }

}
