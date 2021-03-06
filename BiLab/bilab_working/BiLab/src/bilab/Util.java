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
import java.lang.annotation.Annotation;
import java.net.URL;

import scigol.Any;
import scigol.TypeSpec;

public class Util {
    // gets the resource/file-name extension, if present, or the empty string
    public static String extension(final String resourceName) {
        final int dotIndex = resourceName.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }

        final String end = resourceName.substring(dotIndex + 1,
                resourceName.length());

        if ((end.indexOf('/') == -1) && (end.indexOf('\\') == -1)) {
            return end; // return as extension if doesn't contain '/' or '\'
        }

        return "";
    }

    public static scigol.Any help(final String command) {
        final StringBuilder sb = new StringBuilder();

        if (command.equals(".help")) {

            sb.append("Type '.show quickstart' for a quick-start guide\n");
            sb.append(".list                 - list functions and types in the current scope\n");
            sb.append(".help [<func>|<type>] - get help on a specific function or type\n");
            sb.append(".typeof <value>       - shows the actual type of the given value\n");
            sb.append(".run <resource>       - executes a Scigol source resource (e.g. a file)\n");
            sb.append(".using <namespace>    - make definitions in <namespace>'s scope available in the current scope\n");
            sb.append(".show <value>         - show <value> in the value view frame (i.e. default is lower right frame)\n");
            return new scigol.Any(sb.toString());

        } else if (command.startsWith(".help ")) {
            final String helpTarget = command.substring(6);

            final scigol.NamespaceScope globalScope = BilabPlugin.getDefault()
                    .getGlobalScope();

            final scigol.TypeSpec summaryAnnotationType = new scigol.TypeSpec(
                    Summary.class);
            final scigol.TypeSpec docAnnotationType = new scigol.TypeSpec(
                    Doc.class);

            final scigol.Entry entries[] = globalScope.getEntries(helpTarget,
                    null);

            if (entries.length > 0) {

                final scigol.Entry entry = entries[0]; // just take the first
                final String name = entry.name;

                final Annotation docAnnot = entry
                        .getAnnotation(docAnnotationType);
                if (docAnnot != null) {

                    String doc = null;
                    if (docAnnot instanceof scigol.ScigolAnnotation) {
                        final scigol.Value v = (scigol.Value) ((scigol.ScigolAnnotation) docAnnot)
                                .getMembers().get(0);
                        doc = (String) v.getValue();
                    } else {
                        doc = ((Doc) docAnnot).value();
                    }

                    if (!doc.startsWith("file:") && !doc.startsWith("http:")) {
                        sb.append(name + ":\n" + doc);
                    } else {
                        // return a URL (will be displayed by the registered
                        // HTML viewer)

                        final String URLString = doc;
                        try {
                            URL url = null;
                            if (URLString.startsWith("file:")) { // treat is as
                                                                 // a resource
                                url = BilabPlugin.findResource(URLString
                                        .substring(5));
                            } else {
                                url = new URL(URLString);
                            }

                            Notify.devInfo(Util.class, "Help URL " + URLString);

                            return new scigol.Any(url);

                        } catch (final java.net.MalformedURLException e) {
                            sb.append(doc);
                        } catch (final IOException e) {
                            throw new BilabException(
                                    "error reading help resource " + URLString
                                            + " - " + e);
                        }

                    }

                    return new scigol.Any(sb.toString());
                } else { // no Doc annotation, try for a Summary and use that
                         // instead
                    final String typeString = entry.type.toString();

                    final Annotation summaryAnnot = entry
                            .getAnnotation(summaryAnnotationType);
                    if (summaryAnnot != null) {
                        String summary = null;
                        if (summaryAnnot instanceof scigol.ScigolAnnotation) {
                            final scigol.Value v = (scigol.Value) ((scigol.ScigolAnnotation) summaryAnnot)
                                    .getMembers().get(0);
                            summary = (String) v.getValue();
                        } else {
                            summary = ((Summary) summaryAnnot).value();
                        }

                        sb.append(name + ": " + typeString + "\n" + summary);
                        return new scigol.Any(sb.toString());
                    } else { // no Summary either, at least report the name &
                             // type
                        sb.append(name + ": " + typeString + "\n");

                    }
                }

            }

        }

        sb.append("no help available.\n");
        return new scigol.Any(sb.toString());
    }

    // return a list of functions & classes in the current scope, along with doc
    // summary
    public static String list() {
        final scigol.NamespaceScope globalScope = BilabPlugin.getDefault()
                .getGlobalScope();

        final scigol.TypeSpec summaryAnnotationType = new scigol.TypeSpec(
                Summary.class);

        final scigol.Entry entries[] = globalScope.getEntries(null, null);

        final StringBuilder sb = new StringBuilder();

        for (final scigol.Entry entry : entries) {
            final String name = entry.name;

            final Annotation summaryAnnot = entry
                    .getAnnotation(summaryAnnotationType);
            if (summaryAnnot != null) {

                String summary = null;
                if (summaryAnnot instanceof scigol.ScigolAnnotation) {
                    final scigol.Value v = (scigol.Value) ((scigol.ScigolAnnotation) summaryAnnot)
                            .getMembers().get(0);
                    summary = (String) v.getValue();
                } else {
                    summary = ((Summary) summaryAnnot).value();
                }

                sb.append(entry.name + " - " + summary + "\n");

            }

        }

        return sb.toString();
    }

    /*
     * public static scigol.Matrix I(int d) {
     * 
     * scigol.Matrix m = new scigol.Matrix(); for(int r=0; r<d; r++) { //
     * construct row vector scigol.Vector z = new scigol.Vector(); for(int c=0;
     * c<d; c++) z.appendElement(new Integer((c==r)?1:0));
     * 
     * // append it m.appendRowVector(z); }
     * 
     * return m; }
     */

    public static Double mean(final scigol.Vector v) {
        final int d = v.get_size();

        double t = 0;
        for (int i = 0; i < d; i++) {
            t += ((Integer) v.get_Item(i).value).intValue();
        }
        return new Double(t / d);
    }

    @Summary("find and read in a file containing any type of supported data of the given type (which may be \"unknown\")")
    public static Any readResource(final String resourceName,
            final String resourceType) {
        try {
            final URL url = BilabPlugin.findResource(resourceName);

            final String fullResourceName = url.toString();

            Object obj = null;
            // special case for HTML as java.net.URL doesn't implement
            // IResourceIOProvider
            if (extension(fullResourceName).equals("html")
                    || extension(fullResourceName).equals("htm")) {
                obj = url;
            } else {
                obj = BilabPlugin.instantiateObjectFromResource(
                        fullResourceName, resourceType);
            }

            if (obj != null) {
                if (obj instanceof Any) {
                    return (Any) obj;
                } else {
                    return new Any(obj);
                }
            }

        } catch (final IOException e) {
            throw new BilabException("unable to find or open resource "
                    + resourceName + " - IO error " + e);
        } catch (final BilabException e) {
            throw e;
        } catch (final Exception e) {
            throw new BilabException("unable to read resource " + resourceName
                    + " - " + e);
        }
        throw new BilabException(
                "unrecognised or unsupported data format in resource "
                        + resourceName);
    }

    /*
     * // encode a url-like string to make it a valid URL // replaces invalid
     * chars with %HH, spaces with '+' and leaves '/' unchanged in the path part
     * public static String encodeURL(String urlString) { // not sure how to
     * best do this // need to encode anything between delimiters. Delimiters
     * are /, & ? etc. // see RFC2396 // Actually, I think the URI class can
     * probably do everything I want // (specifically the long form constructor
     * that takes the path // seperately)
     * 
     * 
     * if (urlString==null) return null;
     * 
     * URL url = new URL(urlString); String scheme = url.
     * 
     * StringBuffer sb = new StringBuffer(64); char c; for(int i=0;
     * i<url.length(); i++) { c = url.charAt(i);
     * 
     * } }
     * 
     * 
     * public static String decodeURL(String url) {
     * 
     * }
     */

    public static scigol.Map resourceTypesMap() {
        return BilabPlugin.resourceTypesMap();
    }

    // convert a path-like string containing platform native seperators to use
    // '/' separators
    public static String toForwardPathSeparator(final String path) {
        if (File.separatorChar == '/') {
            return path;
        }

        final StringBuffer sb = new StringBuffer();
        for (int c = 0; c < path.length(); c++) {
            if (path.charAt(c) != '\\') {
                sb.append(path.charAt(c));
            } else {
                sb.append('/');
            }
        }
        return sb.toString();
    }

    // convert a path-like string containing '/' seperators to use the platform
    // native separators
    public static String toNativePathSeparator(final String path) {
        if (File.separatorChar == '/') {
            return path;
        }

        final StringBuffer sb = new StringBuffer();
        for (int c = 0; c < path.length(); c++) {
            if (path.charAt(c) != '/') {
                sb.append(path.charAt(c));
            } else {
                sb.append('\\');
            }
        }

        final String npath = sb.toString();

        // if starts with '\C:' (or any drive letter), remove the \
        if ((npath.charAt(0) == '\\') && (npath.charAt(2) == ':')) {
            return npath.substring(1);
        }

        return npath;
    }

    public static String writeResource(final Any resource,
            final String resourceName, final String resourceType) {
        final Object r = TypeSpec.unwrapAnyOrNum(resource);

        if (r instanceof seq) {
            seq.exportResource((seq) r, resourceName, resourceType);
        } else {
            return "resource doesn't support export";
        }

        return "resource written.";
    }

}
