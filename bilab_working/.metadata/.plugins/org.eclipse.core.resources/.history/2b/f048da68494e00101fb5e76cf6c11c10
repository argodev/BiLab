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

@Summary("A protein sequence molecule")
public class protein extends seq {
    public static protein op_Addition(final protein p1, final protein p2) {
        // do by string
        final String seq1 = p1.get_rawsequence();
        final String seq2 = p2.get_rawsequence();
        return new protein(seq1 + seq2);
    }

    public static int op_Card(final protein p) {
        return p.seq.length();
    }

    @Summary("The underlying biojava Sequence representing this protein")
    @Sophistication(Sophistication.Advanced)
    public Sequence seq;

    public protein() {
        seq = null;
    }

    public protein(final Sequence s) {
        seq = s;
    }

    public protein(final String seq) {
        try {
            this.seq = ProteinTools.createProteinSequence(seq, "protein");
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

    @accessor
    public double get_avgIsoMass() {
        try {
            final MassCalc mc = new MassCalc(SymbolPropertyTable.AVG_MASS,
                    false);
            return mc.getMass(seq);
        } catch (final BioException e) {
            throw new BilabException("biojava error", e);
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
            if (alpha.contains(ProteinTools.ala())) {
                mols.add(MoleculeImpl.Ala);
            }
            if (alpha.contains(ProteinTools.cys())) {
                mols.add(MoleculeImpl.Cys);
            }
            if (alpha.contains(ProteinTools.asp())) {
                mols.add(MoleculeImpl.Asp);
            }
            if (alpha.contains(ProteinTools.glu())) {
                mols.add(MoleculeImpl.Glu);
            }
            if (alpha.contains(ProteinTools.phe())) {
                mols.add(MoleculeImpl.Phe);
            }
            if (alpha.contains(ProteinTools.gly())) {
                mols.add(MoleculeImpl.Gly);
            }
            if (alpha.contains(ProteinTools.his())) {
                mols.add(MoleculeImpl.His);
            }
            if (alpha.contains(ProteinTools.ile())) {
                mols.add(MoleculeImpl.Ile);
            }
            if (alpha.contains(ProteinTools.lys())) {
                mols.add(MoleculeImpl.Lys);
            }
            if (alpha.contains(ProteinTools.leu())) {
                mols.add(MoleculeImpl.Leu);
            }
            if (alpha.contains(ProteinTools.met())) {
                mols.add(MoleculeImpl.Met);
            }
            if (alpha.contains(ProteinTools.asn())) {
                mols.add(MoleculeImpl.Asn);
            }
            if (alpha.contains(ProteinTools.pro())) {
                mols.add(MoleculeImpl.Pro);
            }
            if (alpha.contains(ProteinTools.gln())) {
                mols.add(MoleculeImpl.Gln);
            }
            if (alpha.contains(ProteinTools.arg())) {
                mols.add(MoleculeImpl.Arg);
            }
            if (alpha.contains(ProteinTools.ser())) {
                mols.add(MoleculeImpl.Ser);
            }
            if (alpha.contains(ProteinTools.thr())) {
                mols.add(MoleculeImpl.Thr);
            }
            if (alpha.contains(ProteinTools.val())) {
                mols.add(MoleculeImpl.Val);
            }
            if (alpha.contains(ProteinTools.trp())) {
                mols.add(MoleculeImpl.Trp);
            }
            if (alpha.contains(ProteinTools.tyr())) {
                mols.add(MoleculeImpl.Tyr);
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
    public protein get_Item(Range r) {
        r = r.normalize(seq.length());
        return new protein(seq.subStr(r.start + 1, r.end + 1));
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

                if (mol == MoleculeImpl.Ala) {
                    s = 'A';
                }
                if (mol == MoleculeImpl.Cys) {
                    s = 'C';
                }
                if (mol == MoleculeImpl.Asp) {
                    s = 'D';
                }
                if (mol == MoleculeImpl.Glu) {
                    s = 'E';
                }
                if (mol == MoleculeImpl.Phe) {
                    s = 'F';
                }
                if (mol == MoleculeImpl.Gly) {
                    s = 'G';
                }
                if (mol == MoleculeImpl.His) {
                    s = 'H';
                }
                if (mol == MoleculeImpl.Ile) {
                    s = 'I';
                }
                if (mol == MoleculeImpl.Lys) {
                    s = 'K';
                }
                if (mol == MoleculeImpl.Leu) {
                    s = 'L';
                }
                if (mol == MoleculeImpl.Met) {
                    s = 'M';
                }
                if (mol == MoleculeImpl.Asn) {
                    s = 'N';
                }
                if (mol == MoleculeImpl.Pro) {
                    s = 'P';
                }
                if (mol == MoleculeImpl.Gln) {
                    s = 'Q';
                }
                if (mol == MoleculeImpl.Arg) {
                    s = 'R';
                }
                if (mol == MoleculeImpl.Ser) {
                    s = 'S';
                }
                if (mol == MoleculeImpl.Thr) {
                    s = 'T';
                }
                if (mol == MoleculeImpl.Val) {
                    s = 'V';
                }
                if (mol == MoleculeImpl.Trp) {
                    s = 'W';
                }
                if (mol == MoleculeImpl.Tyr) {
                    s = 'Y';
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
                            "unable to add given molecule to proetin");
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
                        + TypeSpec.typeOf(item) + " to a protein sequence");
            }

            // indicate the editing in the annotations
            seq.getAnnotation().setProperty("edited",
                    "true; sequence edited from original");
        } catch (final IllegalSymbolException e) {
            throw new BilabException("illegal symbol");
        } catch (final BioError e) {
            throw new BilabException("biojava error adding an element of type "
                    + TypeSpec.typeOf(item) + " to a protein sequence");
        } catch (final Exception e) {
            throw new BilabException("exception adding an element of type "
                    + TypeSpec.typeOf(item) + " to a protein sequence");
        }

    }

}
