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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

// API wrappers for EMBOSS command-line tools
public class Emboss {

    //
    // Alignment consensus group

    static {
        runtime = Runtime.getRuntime();
    }

    //
    // Alignment differences

    //
    // Alignment dot plots

    protected static Runtime runtime;

    //
    //

    @Summary("predicts potentially antigenic regions of a protein sequence, using the method of Kolaskar and Tongaonkar")
    @Doc("file:EMBOSS/doc/html/antigenic.html")
    public static scigol.Any antigenic(final seq sequence, final int minLength,
            final String format) {
        // call the command-line executable and get the result

        // first get a resource file containing the input
        final boolean existingResourceAvailable = ExternalApps
                .isInputResourceAvailable(sequence, "FASTA");
        final String resourceName = ExternalApps.getInputResource(sequence,
                "FASTA");

        // construct command line and execute
        final List<String> args = new LinkedList<String>();
        args.add("-auto");
        args.add("-minlen");
        args.add("" + minLength);
        args.add("-rformat");
        args.add(format);
        args.add("fasta::" + Util.toNativePathSeparator(resourceName));

        final String result = exec("antigenic", args);

        // delete the any temporary resource file created
        try {
            if (!existingResourceAvailable) {
                BilabPlugin.deleteResource(resourceName);
            }
        } catch (final IOException e) {
        } // ignore deletion failure

        if (format.equalsIgnoreCase("gff")) {
            // !!! this should make a copy of the original sequence first

            sequence.addGFFFeatures(result);
            return new scigol.Any(sequence);
        } else {
            return new scigol.Any(filterEMBOSSReportOutput(result));
        }
    }

    public static seq cons(final alignment a) {
        Notify.unimplemented(Emboss.class);
        return null;
    }

    @Summary("Protein proteolytic enzyme or reagent cleavage digest")
    @Doc("file:EMBOSS/doc/html/digest.html")
    public static String digest(final seq sequence, final int reagent,
            final boolean unfavoured, final boolean overlap,
            final boolean allpartials) {
        // call the command-line executable and get the result

        // first get a resource file containing the input
        final boolean existingResourceAvailable = ExternalApps
                .isInputResourceAvailable(sequence, "FASTA");
        final String resourceName = ExternalApps.getInputResource(sequence,
                "FASTA");

        // construct command line and execute
        final List<String> args = new LinkedList<String>();
        args.add("-auto");
        args.add("-menu");
        args.add("" + reagent);
        if (unfavoured) {
            args.add("-unfavoured");
        }
        if (overlap) {
            args.add("-overlap");
        }
        if (allpartials) {
            args.add("-allpartials");
        }
        args.add("fasta::" + Util.toNativePathSeparator(resourceName));

        final String result = exec("digest", args);

        // delete the any temporary resource file created
        try {
            if (!existingResourceAvailable) {
                BilabPlugin.deleteResource(resourceName);
            }
        } catch (final IOException e) {
        } // ignore deletion failure

        return filterEMBOSSReportOutput(result);
    }

    @Summary("thresholded dotplot of two sequences")
    public static picture dotmatcher(final seq sa, final seq sb,
            final int windowSize, final int threshold) {
        String result = "";
        try {
            // first convert the two sequences to FASTA format
            final boolean existingResourceAAvailable = ExternalApps
                    .isInputResourceAvailable(sa, "FASTA");
            final String resourceNameA = ExternalApps.getInputResource(sa,
                    "FASTA");

            final boolean existingResourceBAvailable = ExternalApps
                    .isInputResourceAvailable(sb, "FASTA");
            final String resourceNameB = ExternalApps.getInputResource(sb,
                    "FASTA");

            final String outputResourceName = BilabPlugin
                    .uniqueTemporaryResourceName("PNG");

            // construct command line and execute
            final List<String> args = new LinkedList<String>();
            args.add("fasta::" + Util.toNativePathSeparator(resourceNameA));
            args.add("fasta::" + Util.toNativePathSeparator(resourceNameB));
            args.add("-auto");
            args.add("-goutfile");
            args.add(Util.toNativePathSeparator(outputResourceName));
            args.add("-graph");
            args.add("win3"); // win3 for now, until png works
            // args.add("-xygraph"); args.add("png");
            args.add("-windowsize");
            args.add("" + windowSize);
            args.add("-threshold");
            args.add("" + threshold);

            result = exec("dotmatcher", args);

            // delete the any temporary resource file created
            try {
                if (!existingResourceAAvailable) {
                    BilabPlugin.deleteResource(resourceNameA);
                }
            } catch (final IOException e) {
            } // ignore deletion failure

            // delete the any temporary resource file created
            try {
                if (!existingResourceBAvailable) {
                    BilabPlugin.deleteResource(resourceNameB);
                }
            } catch (final IOException e) {
            } // ignore deletion failure

            // now read in the output file
            // picture plot = (picture)Util.readResource(outputResourceName,
            // "PNG").value;

            // return plot;
            return null;
        } catch (final Exception e) {
            Notify.logError(Emboss.class, "dotmatcher:" + result);
            throw new BilabException("error executing dotmatcher - " + e);
        }
    }

    protected static String exec(final String commandName,
            final List<String> args) {
        try {
            final String pluginRoot = BilabPlugin.getPluginFilesystemRoot();

            final String EMBOSSRoot = pluginRoot + IPath.SEPARATOR + "EMBOSS";

            final LinkedList<String> cmdline = new LinkedList<String>();
            cmdline.add(EMBOSSRoot + IPath.SEPARATOR + commandName
                    + ExternalApps.exeSuffix);
            cmdline.addAll(args);

            final StringBuilder cmdlineStr = new StringBuilder();
            for (final String s : cmdline) {
                cmdlineStr.append(s + " ");
            }
            Notify.logInfo(Emboss.class, "invoking external command:"
                    + cmdlineStr.toString());

            final ProcessBuilder pb = new ProcessBuilder(cmdline);
            final Map<String, String> env = pb.environment();
            env.put("EMBOSSWIN", EMBOSSRoot);
            pb.directory(new File(EMBOSSRoot));
            pb.redirectErrorStream(true);

            final Process process = pb.start();

            final InputStream cmdResultStream = process.getInputStream();
            final InputStreamReader cmdResultReader = new InputStreamReader(
                    cmdResultStream);

            final StringBuilder str = new StringBuilder();

            int c = cmdResultReader.read();
            while (c != -1) {
                str.append((char) c);
                c = cmdResultReader.read();
            }

            process.waitFor(); // wait until command completes

            return str.toString();

        } catch (final Exception e) {
            throw new BilabException(
                    "unable to execute command (external EMBOSS invocation failed) - "
                            + e);
        }
    }

    // filter out some of the unnecessary header lines from an EMBOSS report
    protected static String filterEMBOSSReportOutput(final String report) {
        final String terminator = BilabPlugin.platformOS().equals("win") ? "\r\n"
                : "\n";

        final String[] lines = report.split(terminator);
        final StringBuilder filtered = new StringBuilder();
        for (final String line : lines) {
            if ((line.length() == 0) || line.charAt(0) != '#') {
                filtered.append(line + terminator);
            } else {
                if (!line.startsWith("#####") && !line.startsWith("# Program")
                        && !line.startsWith("# Rundate")
                        && !line.startsWith("# Report")) {
                    filtered.append(line + terminator);
                }
            }
        }
        return filtered.toString();
    }

    //
    // helper methods

    @Summary("Protein pattern search (PROSITE-style)")
    @Doc("file:EMBOSS/doc/html/fuzzpro.html")
    public static String fuzzpro(final seq sequence, final String pattern,
            final int mismatch) {
        // call the command-line executable and get the result

        // first get a resource file containing the input
        final boolean existingResourceAvailable = ExternalApps
                .isInputResourceAvailable(sequence, "FASTA");
        final String resourceName = ExternalApps.getInputResource(sequence,
                "FASTA");

        // construct command line and execute
        final List<String> args = new LinkedList<String>();
        args.add("-auto");
        args.add("-mismatch");
        args.add("" + mismatch);
        args.add("-pattern");
        args.add(pattern);
        args.add("fasta::" + Util.toNativePathSeparator(resourceName));

        final String result = exec("fuzzpro", args);

        // delete the any temporary resource file created
        try {
            if (!existingResourceAvailable) {
                BilabPlugin.deleteResource(resourceName);
            }
        } catch (final IOException e) {
        } // ignore deletion failure

        return filterEMBOSSReportOutput(result);
    }

    @Summary("Protein pattern search after translation (PROSITE-style)")
    @Doc("file:EMBOSS/doc/html/fuzztran.html")
    public static String fuzztran(final seq sequence, final String pattern,
            final String frame, final int code, final int mismatch) {
        // call the command-line executable and get the result

        // first get a resource file containing the input
        final boolean existingResourceAvailable = ExternalApps
                .isInputResourceAvailable(sequence, "FASTA");
        final String resourceName = ExternalApps.getInputResource(sequence,
                "FASTA");

        // construct command line and execute
        final List<String> args = new LinkedList<String>();
        args.add("-auto");
        args.add("-mismatch");
        args.add("" + mismatch);
        args.add("-pattern");
        args.add(pattern);
        args.add("-frame");
        args.add(frame);
        args.add("-table");
        args.add("" + code);
        if (sequence instanceof protein) {
            args.add("-sprotein1");
        }
        args.add("fasta::" + Util.toNativePathSeparator(resourceName));

        final String result = exec("fuzztran", args);

        // delete the any temporary resource file created
        try {
            if (!existingResourceAvailable) {
                BilabPlugin.deleteResource(resourceName);
            }
        } catch (final IOException e) {
        } // ignore deletion failure

        return filterEMBOSSReportOutput(result);
    }

    @Summary("Report nucleic acid binding motifs")
    @Doc("file:EMBOSS/doc/html/helixturnhelix.html")
    public static String helixturnhelix(final seq sequence, final double mean,
            final double sd, final double minsd) {
        // call the command-line executable and get the result

        // first get a resource file containing the input
        final boolean existingResourceAvailable = ExternalApps
                .isInputResourceAvailable(sequence, "FASTA");
        final String resourceName = ExternalApps.getInputResource(sequence,
                "FASTA");

        // construct command line and execute
        final List<String> args = new LinkedList<String>();
        args.add("-auto");
        args.add("-mean");
        args.add("" + mean);
        args.add("-sd");
        args.add("" + sd);
        args.add("-minsd");
        args.add("" + minsd);
        if (sequence instanceof protein) {
            args.add("-sprotein1");
        }
        args.add("fasta::" + Util.toNativePathSeparator(resourceName));

        final String result = exec("helixturnhelix", args);

        // delete the any temporary resource file created
        try {
            if (!existingResourceAvailable) {
                BilabPlugin.deleteResource(resourceName);
            }
        } catch (final IOException e) {
        } // ignore deletion failure

        return filterEMBOSSReportOutput(result);
    }

    @Summary("Reports protein signal cleavage sites")
    @Doc("file:EMBOSS/doc/html/sigcleave.html")
    public static String sigcleave(final seq sequence, final double minWeight,
            final boolean prokaryote) {
        if (!(sequence instanceof protein)) {
            throw new BilabException("a protein sequence is required");
        }

        // call the command-line executable and get the result

        // first get a resource file containing the input
        final boolean existingResourceAvailable = ExternalApps
                .isInputResourceAvailable(sequence, "FASTA");
        final String resourceName = ExternalApps.getInputResource(sequence,
                "FASTA");

        // construct command line and execute
        final List<String> args = new LinkedList<String>();
        args.add("-auto");
        args.add("-minweight");
        args.add("" + minWeight);
        if (prokaryote) {
            args.add("-prokaryote");
        }
        args.add("-sprotein1");
        args.add("fasta::" + Util.toNativePathSeparator(resourceName));

        final String result = exec("sigcleave", args);

        // delete the any temporary resource file created
        try {
            if (!existingResourceAvailable) {
                BilabPlugin.deleteResource(resourceName);
            }
        } catch (final IOException e) {
        } // ignore deletion failure

        return filterEMBOSSReportOutput(result);
    }

}
