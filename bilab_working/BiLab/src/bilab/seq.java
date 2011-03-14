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

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.biojava.bio.Annotation;
import org.biojava.bio.program.gff.GFFEntrySet;
import org.biojava.bio.program.gff.GFFTools;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;
import org.biojava.bio.seq.io.SeqIOTools;
import org.biojava.bio.symbol.Alphabet;

import scigol.Any;
import scigol.Debug;
import scigol.accessor;

// Sequence - a seq is a list of molecules (and is itself also a molecule)
public abstract class seq extends scigol.List implements molecule,
        IResourceIOProvider {
    // maximum length of a seq string for display
    // (longer sequences will have ... embedded)
    static final int maxStringLen = 40;

    private static List<String> supportedResourceTypes;

    static {
        // list of supported resource name type (not extensions)
        supportedResourceTypes = new LinkedList<String>();
        supportedResourceTypes.add("EMBL");
        supportedResourceTypes.add("SwissProt");
        supportedResourceTypes.add("GenBank");
        supportedResourceTypes.add("GenPept");
        supportedResourceTypes.add("FASTA");
    }

    @Summary("write the resource to the stream in a supported format")
    @Sophistication(Sophistication.Advanced)
    public static void exportResource(final seq s,
            final OutputStream outStream, final String resourceType) {
        try {
            if (resourceType.equals("EMBL")) {
                SeqIOTools.writeEmbl(outStream, sequenceFromSeq(s));
            } else if (resourceType.equals("SwissProt")) {
                SeqIOTools.writeSwissprot(outStream, sequenceFromSeq(s));
            } else if (resourceType.equals("GenBank")) {
                SeqIOTools.writeGenbank(outStream, sequenceFromSeq(s));
            } else if (resourceType.equals("GenPept")) {
                SeqIOTools.writeGenpept(outStream, sequenceFromSeq(s));
            } else if (resourceType.equals("FASTA")) {
                SeqIOTools.writeFasta(outStream, sequenceFromSeq(s));
            } else {
                throw new BilabException("unsupported resource type:"
                        + resourceType);
            }

        } catch (final Exception e) {
            throw new BilabException(
                    "unable to export sequence as resource type: "
                            + resourceType);
        }
    }

    @Summary("create a resource containing data in a supported format from a seq")
    public static void exportResource(final seq s, final String resourceName,
            final String resourceType) {
        try {
            final OutputStream outStream = BilabPlugin
                    .createResourceStream(resourceName);

            exportResource(s, outStream, resourceType);

            outStream.flush();
            outStream.close();

        } catch (final Exception e) {
            throw new BilabException("unable to export sequence as resource: "
                    + resourceName);
        }
    }

    @Summary("split into lines of 6 cols of 10 chars each")
    @Sophistication(Sophistication.Developer)
    public static String formatSeqString(final String seqString) {
        final StringBuilder fs = new StringBuilder();

        for (int c = 0; c < seqString.length(); c++) {
            fs.append(seqString.charAt(c));
            if (((c + 1) % 10) == 0) {
                if (((c + 1) % 60) == 0) {
                    fs.append("\n");
                } else {
                    fs.append(' ');
                }
            }
        }
        return fs.toString();
    }

    public static List<String> getSupportedResourceTypes() {
        return supportedResourceTypes;
    }

    // this will create a DNA, RNA or protien (or possibly a list thereof)
    public static Object importResource(final String resourceName,
            final String resourceType) {
        try {

            final java.io.InputStreamReader streamReader = new java.io.InputStreamReader(
                    BilabPlugin.findResourceStream(resourceName));
            if (streamReader == null) {
                throw new BilabException("unable to open resource:"
                        + resourceName);
            }

            final scigol.List seqList = new scigol.List();

            SequenceIterator sequences = null;

            final BufferedReader br = new BufferedReader(streamReader);

            if (resourceType.equals("EMBL")) {
                sequences = SeqIOTools.readEmbl(br);
            } else if (resourceType.equals("SwissProt")) {
                sequences = SeqIOTools.readSwissprot(br);
            } else if (resourceType.equals("GenBank")) {
                sequences = SeqIOTools.readGenbank(br);
            } else if (resourceType.equals("GenPept")) {
                sequences = SeqIOTools.readGenpept(br);
            } else if (resourceType.equals("FASTA")) {
                sequences = SeqIOTools.readFastaDNA(br);
                Notify.logInfo(seq.class, "Assuming FASTA DNA");
            } else {
                throw new BilabException("unsupported resource type:"
                        + resourceType);
            }

            while (sequences.hasNext()) {
                final Sequence bjs = sequences.nextSequence();
                final seq s = seqFromSequence(bjs);
                final Any a = s.get_annotation("ID");
                if (a != null) {
                    if ((a.value != null) && (a.value instanceof String)) {
                        s._name = "ID:" + (String) (a.value);
                    }
                }
                if (s._name.equals("untitled")) {
                    final Any a2 = s.get_annotation("GI");
                    if (a2 != null) {
                        if ((a2.value != null) && (a2.value instanceof String)) {
                            s._name = "GI:" + (String) (a2.value);
                        }
                    }

                }
                if (s._name.equals("untitled")) {
                    final Any a2 = s.get_annotation("gi");
                    if (a2 != null) {
                        if ((a2.value != null) && (a2.value instanceof String)) {
                            s._name = "gi:" + (String) (a2.value);
                        }
                    }

                }
                seqList.add(s);
            }

            // now return the list (or just the element if there is only one)
            if (seqList.get_size() > 1) {
                return seqList;
            }

            // single seq
            // associate the resource imported with the seq before returning it
            final seq s = (seq) seqList.get_head().value;
            s.set_AssociatedResource(resourceName);
            return seqList.get_head();

        } catch (final Exception e) {
            throw new BilabException(
                    "unable to locate/import resource as sequence(s): "
                            + resourceName);
        }
    }

    // convenience
    @Sophistication(Sophistication.Advanced)
    public static seq seqFromSequence(final Sequence seq) {
        if (seq == null) {
            return null;
        }

        // determine type via Alphabet
        final Alphabet alpha = seq.getAlphabet();

        if (alpha.equals(DNATools.getDNA())) {
            return new DNA(seq);
        } else if (alpha.equals(RNATools.getRNA())) {
            return new RNA(seq);
        } else if (alpha.equals(ProteinTools.getAlphabet())
                || alpha.equals(ProteinTools.getTAlphabet())) {
            return new protein(seq);
        } else {
            throw new BilabException("unsupported sequence alphabet:" + alpha);
        }

    }

    // convenience
    @Sophistication(Sophistication.Advanced)
    public static Sequence sequenceFromSeq(final seq s) {
        if (s == null) {
            return null;
        }

        if (s instanceof DNA) {
            return ((DNA) s).seq;
        } else if (s instanceof RNA) {
            return ((RNA) s).seq;
        } else if (s instanceof protein) {
            return ((protein) s).seq;
        }

        Debug.Assert(false, "unknown/unhandled seq subclass");
        return null;
    }

    protected String _name;
    protected Annotation annot;

    // force concrete sequences to implement required List methods
    // !!!!public abstract override Any head { get; set; }

    protected String associatedResourceName = null;

    public seq() {
        _name = "untitled";
    }

    // IResourceImporter

    public seq(final String name) {
        _name = name;
    }

    @Summary("add the features expressed in GFF format to this sequence")
    @Sophistication(Sophistication.Advanced)
    public void addGFFFeatures(final String GFFString) {
        try {
            final Reader stringReader = new StringReader(GFFString);
            final BufferedReader bufferedReader = new BufferedReader(
                    stringReader);
            final GFFEntrySet gffEntrySet = GFFTools.readGFF(bufferedReader);
            final Sequence thisSequence = seq.sequenceFromSeq(this); // get
                                                                     // upderlying
                                                                     // biojava
                                                                     // Sequence

            final Sequence thisGFFAnnotatedSequence = GFFTools
                    .annotateSequence(thisSequence, gffEntrySet);

            updateSequence(thisGFFAnnotatedSequence);

        } catch (final Exception e) {
            throw new BilabException("unable to parse GFF feature string:" + e);
        }

    }

    public Any get_annotation(final String key) {
        return get_annotations().get_Item(key);
    }

    @Override
    public scigol.Map get_annotations() {
        // for now just copy annot into a new Map, later make a wrapper that
        // extends Map
        // so that the properties can be changed !!!
        final scigol.Map m = new scigol.Map();
        for (final java.util.Iterator i = annot.keys().iterator(); i.hasNext();) {
            final Object key = i.next();
            m.add(key, annot.getProperty(key));
        }
        return m;
    }

    @Override
    @accessor
    public String get_AssociatedResource() {
        return associatedResourceName;
    }

    @Override
    public abstract String get_DetailText();

    @Override
    public String get_name() {
        return _name;
    }

    public abstract String get_rawsequence();

    public abstract String get_sequence();

    @Override
    public abstract String get_ShortText();

    @Override
    public boolean get_StructureKnown() {
        return false; // !!! for now
    }

    @Sophistication(Sophistication.Advanced)
    @accessor
    public void set_AssociatedResource(final String resourceName) {
        associatedResourceName = resourceName;
    }

    @Override
    public String ToMDL() {
        Debug.Unimplemented();
        return null;
    }

    @Override
    public String toString() {
        final String s = get_rawsequence();
        if (s.length() <= maxStringLen) {
            return s;
        }
        final int half = (maxStringLen - 5) / 2;
        final String start = s.substring(0, half);
        final String end = s.substring(s.length() - half - 1);
        return start + " ... " + end;
    }

    @Sophistication(Sophistication.Advanced)
    public void updateSequence(final Sequence seq) {
        if (seq == null) {
            return;
        }

        // determine type via Alphabet
        final Alphabet alpha = seq.getAlphabet();

        if (this instanceof DNA) {
            ((DNA) this).seq = seq;
        } else if (this instanceof RNA) {
            ((RNA) this).seq = seq;
        } else if (this instanceof protein) {
            ((protein) this).seq = seq;
        } else {
            throw new BilabException("unsupported seq subclass:"
                    + this.getClass());
        }

    }

}
