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

import jalview.FormatAdapter;
import jalview.ScoreSequence;
import jalview.Sequence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import scigol.List;

/**
 * A list of sequences with alignment information
 * @author ru7
 *
 */
public class alignment extends List implements IAnnotated, IResourceIOProvider {

    /**
     * 
     */
    @Sophistication(Sophistication.Advanced)
    public Sequence[] alignedSeqs;

    /**
     * 
     */
    @Sophistication(Sophistication.Advanced)
    public ScoreSequence[] seqScores;

    protected scigol.Map annotation;

    private static java.util.List<String> supportedResourceTypes;

    static {
        // list of supported resource name type (not extensions)
        supportedResourceTypes = new LinkedList<String>();
        supportedResourceTypes.add("MSF");
        supportedResourceTypes.add("CLUSTALW");
        supportedResourceTypes.add("FASTA");
        supportedResourceTypes.add("BLC");
        supportedResourceTypes.add("PFAM");
    }

    /**
     * Create a resource containing data in a supported format from a sequence.
     * @param a
     * @param resourceName
     * @param resourceType
     */
    public static void exportResource(final alignment a,
            final String resourceName, final String resourceType) {
        try {
            String jalViewType;
            if (resourceType.equals("CLUSTALW")) {
                jalViewType = "CLUSTAL";
            } else if (resourceType.equals("FASTA")) {
                jalViewType = "FASTA";
            } else if (resourceType.equals("MSF")) {
                jalViewType = "MSF";
            } else if (resourceType.equals("BLC")) {
                jalViewType = "BLC";
            } else if (resourceType.equals("PFAM")) {
                jalViewType = "PFAM";
            } else {
                throw new BilabException("unsupported alignment resource type:"
                        + resourceType);
            }

            final OutputStream outStream = BilabPlugin
                    .createResourceStream(resourceName);
            final OutputStreamWriter outStreamWriter = new OutputStreamWriter(
                    outStream);
            final BufferedWriter buffWriter = new BufferedWriter(
                    outStreamWriter);

            final String outString = FormatAdapter.get(jalViewType,
                    a.alignedSeqs);
            buffWriter.write(outString);
            buffWriter.flush();
            outStream.close();

        } catch (final Exception e) {
            throw new BilabException("unable to export sequence as resource: "
                    + resourceName);
        }
    }

    /**
     * @return
     */
    public static java.util.List<String> getSupportedResourceTypes() {
        return supportedResourceTypes;
    }

    /**
     * @param resourceName
     * @param resourceType
     * @return
     */
    public static Object importResource(final String resourceName,
            final String resourceType) {
        try {
            scigol.Debug.WL("asked to import " + resourceName + " of type "
                    + resourceType);

            final InputStreamReader inputStreamReader = new InputStreamReader(
                    BilabPlugin.findResourceStream(resourceName));
            if (inputStreamReader == null) {
                throw new BilabException("unable to open resource:"
                        + resourceName);
            }
            final BufferedReader buffReader = new BufferedReader(
                    inputStreamReader);

            // read the entire alignment into a string first
            final StringBuilder inputString = new StringBuilder();
            String line;
            do {
                line = buffReader.readLine();
                if (line != null) {
                    inputString.append(line + "\n");
                }
            } while (line != null);

            inputStreamReader.close();

            String jalViewType;
            if (resourceType.equals("CLUSTALW")) {
                jalViewType = "CLUSTAL";
            } else if (resourceType.equals("FASTA")) {
                jalViewType = "FASTA";
            } else if (resourceType.equals("MSF")) {
                jalViewType = "MSF";
            } else if (resourceType.equals("BLC")) {
                jalViewType = "BLC";
            } else if (resourceType.equals("PFAM")) {
                jalViewType = "PFAM";
            } else {
                throw new BilabException("unsupported alignment resource type:"
                        + resourceType);
            }

            final alignment aln = new alignment();

            aln.alignedSeqs = FormatAdapter.read(jalViewType,
                    inputString.toString());
            aln.seqScores = new ScoreSequence[aln.alignedSeqs.length];
            for (int i = 0; i < aln.alignedSeqs.length; i++) {
                aln.seqScores[i] = new ScoreSequence(aln.alignedSeqs[i]);
            }

            return aln;

        } catch (final BilabException e) {
            throw e;
        } catch (final Exception e) {
            throw new BilabException(
                    "unable to locate/import resource as alignment(s): "
                            + resourceName + " - " + e);
        }
    }

    public alignment() {
        alignedSeqs = new Sequence[0];
        seqScores = new ScoreSequence[0];
        annotation = new scigol.Map();
    }

    /** {@inheritDoc} */
    @Override
    public final scigol.Map get_annotations() {
        return annotation;
    }

    @Override
    public final String toString() {
        String s = "alignment(";
        for (int i = 0; i < alignedSeqs.length; i++) {
            s += alignedSeqs[i].name;
            if (i != alignedSeqs.length - 1) {
                s += ",";
            }
        }
        s += ")";
        return s;
    }
}