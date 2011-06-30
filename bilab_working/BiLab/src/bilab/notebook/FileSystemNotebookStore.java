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

package bilab.notebook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import bilab.BilabException;
import bilab.Util;
import bilab.io.IFileSystem;
import bilab.io.LocalFileSystem;

// Implements a notebook store via a file-system interface
// Hence, this implementation only accepts file: URLs for notebooks.
// They take the form:
// file:/home/user/mynotebook.xml?version=<version_name>&section=<section_name>&page=<page#>
// where mynotebook may be an XML notebook file or a notebook directory.
// The version parameter is optional; and if omitted the current version is
// assumed.
//
// NB: this is a singelton and is not threadsafe
// (concurrent access to the same notebook will give undefined results)
public class FileSystemNotebookStore implements INotebookStore {
    // info extracted from the notebook URI
    protected static class NotebookItemInfo {
        String notebookName;

        String notebookPath;

        String versionName;
        String sectionName;
        int page;

        public NotebookItemInfo() {
            versionName = CURRENT;
            sectionName = "";
            page = -1;
        }

        public NotebookItemInfo(final NotebookItemInfo copy) {
            notebookName = copy.notebookName;
            notebookPath = copy.notebookPath;
            versionName = copy.versionName;
            sectionName = copy.sectionName;
            page = copy.page;
        }

        public String toURI() {
            try {
                final String absPath = (new File(notebookPath + "/"
                        + notebookName).toURI()).getPath();
                String query = "";
                if (!versionName.equals(CURRENT)) {
                    query += "version=" + versionName;
                }
                if (sectionName.length() > 0) {
                    if (query.length() > 0) {
                        query += "&";
                    }
                    query += "section=" + sectionName;
                }
                if (page != -1) {
                    if (query.length() > 0) {
                        query += "&";
                    }
                    query += "page=" + page;
                }

                return new URI("file", null, absPath,
                        (query.length() > 0) ? query : null, null).toString();
            } catch (final URISyntaxException e) {
                throw new RuntimeException(
                        "interal error: can't form URI string - "
                                + e.getMessage());
            }

            /*
             * String uri = (new
             * File(notebookPath+"/"+notebookName).toURI()).toString(); boolean
             * addedQuestionMark = false; if (!versionName.equals(CURRENT)) {
             * String encodedVersion = null; try { encodedVersion =
             * URLEncoder.encode(versionName, "UTF-8"); } catch
             * (UnsupportedEncodingException e) { throw new
             * BilabException("unable to encode notebook version name:"
             * +versionName); } uri = uri+"?version="+encodedVersion;
             * addedQuestionMark = true; } if (sectionName.length()>0) { String
             * encodedSectionName = null; try { encodedSectionName =
             * URLEncoder.encode(sectionName, "UTF-8"); } catch
             * (UnsupportedEncodingException e) { throw new
             * BilabException("unable to encode notebook section name:"
             * +sectionName); } uri = uri+(addedQuestionMark?"&":"?"); uri =
             * uri+"section="+encodedSectionName; addedQuestionMark = true; } if
             * (page != -1) { uri = uri+(addedQuestionMark?"&":"?"); uri =
             * uri+"page="+page; addedQuestionMark = true; } return uri;
             */
        }

    }

    // section information for a version
    protected static class Sections {
        public LinkedList<String> sections = new LinkedList<String>(); // stored
                                                                       // in
                                                                       // sectionIndexFile
        // global (versioned) annotations (stored in annotationsFile in each
        // version dir)
        // (not to be confused with the per-section annotationsFile in each
        // section dir)
        public HashMap<String, String> annotations = new HashMap<String, String>();
    }

    // version information for a notebook
    protected static class Versions {
        LinkedList<String> versions = new LinkedList<String>(); // stored in
                                                                // versionIndexFile
        // global (non-versioned) annotations (e.g. notebook creation date)
        HashMap<String, String> globalAnnotations = new HashMap<String, String>(); // stored
                                                                                   // in
                                                                                   // versionIndexFile
    }

    protected static FileSystemNotebookStore instance = null;

    // find the next Element with the given name; optionally recursively
    // searching children
    protected static Element findNextElement(Node n, final String tagName,
            final boolean searchChildren) {
        if (n == null) {
            return null;
        }

        do {
            if ((n instanceof Element) && n.getNodeName().equals(tagName)) {
                return (Element) n;
            }

            if (searchChildren && n.hasChildNodes()) {
                // find first child element
                Node cn = n.getFirstChild();
                while ((cn != null) && (!(cn instanceof Element))) {
                    cn = cn.getNextSibling();
                }
                if (cn != null) {
                    final Element c = findNextElement(cn, tagName,
                            searchChildren);
                    if (c != null) {
                        return c;
                    }
                }
            }

            // get next element
            n = n.getNextSibling();
            while ((n != null) && (!(n instanceof Element))) {
                n = n.getNextSibling();
            }

        } while (n != null);

        return null;
    }

    public static FileSystemNotebookStore getInstance() {
        if (instance == null) {
            instance = new FileSystemNotebookStore();
        }
        return instance;
    }

    // test if path starts with '/', 'C:/', '\' or 'C:\', (with any drive
    // letter)
    public static boolean isAbsolutePath(final String path) {
        if (path.length() < 1) {
            return false;
        }

        if (path.startsWith("/") || path.startsWith("\\")) {
            return true;
        }

        if (path.length() < 3) {
            return false;
        }

        return path.substring(1, 3).equals(":/")
                || path.substring(1, 3).equals(":\\");
    }

    protected HashMap<String, IFileSystem> fsCache = new HashMap<String, IFileSystem>();

    protected static final String formatVersion = "Bilab 1.1";

    protected static final String versionIndexFile = "versions.xml";

    protected static final String sectionIndexFile = "sections.xml";

    protected static final String annotationsFile = "annotations.xml";

    protected DocumentBuilderFactory builderFactory;

    protected TransformerFactory transFactory;

    protected static final Calendar calendar = new GregorianCalendar(
            TimeZone.getTimeZone("UTC"));

    protected static DateFormat dateTimeFormatter = DateFormat
            .getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

    static {
        dateTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    protected FileSystemNotebookStore() {
        builderFactory = DocumentBuilderFactory.newInstance();
        transFactory = TransformerFactory.newInstance();
    }

    public void addPageResource(final String pageURI,
            final String resourceName, final byte[] content) {
        // TODO Auto-generated method stub

    }

    public String createNotebook(final String locationURI, final String name)
            throws URISyntaxException, IOException {
        // first check that the location is a directory
        URI locURI = null;
        if (isAbsolutePath(locationURI)) {
            // locationURI is actually a path, not a URI. Hence it may not
            // be properly escaped
            locURI = new URI("file", null, locationURI, null, null);
        } else {
            locURI = new URI(locationURI);
        }

        if (!locURI.getScheme().equals("file")) {
            throw new BilabException(
                    "only file: URIs are valid for the location of a local notebook");
        }

        String notebookURI = null;

        IFileSystem fs = null;
        try {
            // append notebook name to location URI path
            final String locPath = locURI.getPath();
            notebookURI = (new File(locPath + "/" + name)).toURI().toString();

            if (notebookURI.endsWith(".xml")) {
                // create appropriate empty XML file
                bilab.Notify.unimplemented(this);
            } else {
                // create empty directory
                final IFileSystem localRootFS = new LocalFileSystem("/");

                if (!localRootFS.changeDir(locURI.getPath())) {
                    throw new BilabException("location '" + locURI
                            + "' doesn't exist");
                }
                localRootFS.makeDirectory(name);

                final NotebookItemInfo info = getNotebookItemInfo(notebookURI);
                fs = getNotebookFilesystem(info);
            }

            // create initial versions index
            final Versions versions = new Versions();
            final Date now = new Date();
            final String nowDateTime = dateTimeFormatter.format(now);

            versions.globalAnnotations.put(CREATION_DATE, nowDateTime); // notebook
                                                                        // creation
                                                                        // date
            versions.versions.add(CURRENT);
            writeVersionIndex(fs, versions);

            // now create top level current version 'dir' & a main section
            fs.changeDir("/");
            fs.makeDirectory(CURRENT);
            fs.changeDir(CURRENT);

            final Sections sections = new Sections();
            sections.annotations.put(CREATION_DATE, nowDateTime); // version
                                                                  // creation
                                                                  // date

            sections.sections.add("main"); // default initial name for single
                                           // initial section
            writeSectionIndex(fs, CURRENT, sections);

            fs.makeDirectory("main");
            final HashMap<String, String> sectionAnnotations = new HashMap<String, String>();
            sectionAnnotations.put(CREATION_DATE, nowDateTime); // section
                                                                // creation date
            writeAnnotationFile(fs, CURRENT, "main", sectionAnnotations);

        } catch (final Exception e) {
            final String msg = (e.getMessage() == null) ? "" : " - "
                    + e.getMessage();
            throw new BilabException("unable to create notebook '" + name
                    + "' at location '" + locationURI + "' - " + msg, e);
        }

        return notebookURI;
    }

    public String createPage(final String sectionURI,
            final String insertAfterPageURI) throws URISyntaxException,
            IOException {
        if (!existsNotebook(sectionURI)) {
            throw new IOException("notebook not found:" + sectionURI);
        }

        // TODO Auto-generated method stub
        return null;
    }

    public String createSection(String notebookURI,
            final String newSectionName, String insertAfterSectionURI)
            throws URISyntaxException, IOException {
        if (!existsNotebook(notebookURI)) {
            throw new IOException("notebook not found:" + notebookURI);
        }

        if (newSectionName.equals("last")) {
            throw new BilabException(
                    "'last' is not a valid notebook section name (it is reserved)");
        }

        notebookURI = getCurrentNotebookVersion(notebookURI); // can only create
                                                              // sections in the
                                                              // current version
        final NotebookItemInfo info = getNotebookItemInfo(notebookURI);
        final IFileSystem fs = getNotebookFilesystem(info);

        // check if it already exists
        final NotebookItemInfo physicalSecInfo = locateSection(info);
        if (physicalSecInfo != null) {
            throw new BilabException("a notebook section named '"
                    + newSectionName
                    + "' already exists in the current version");
        }

        NotebookItemInfo afterSectionInfo = null;

        if (insertAfterSectionURI != null) {
            afterSectionInfo = locateSection(getNotebookItemInfo(insertAfterSectionURI));
            if (afterSectionInfo == null) {
                throw new BilabException(
                        "unable to create new section after non-existant section:"
                                + insertAfterSectionURI);
            }
        } else {
            // get last section (which may not exist if the current version is
            // empty)
            insertAfterSectionURI = notebookURI;
            afterSectionInfo = getNotebookItemInfo(insertAfterSectionURI);
            afterSectionInfo.sectionName = "last";
            afterSectionInfo = locateSection(afterSectionInfo); // null if no
                                                                // sections
        }

        // create section dir and initial content
        fs.changeDir("/" + CURRENT);
        fs.makeDirectory(newSectionName);
        final HashMap<String, String> sectionAnnotations = new HashMap<String, String>();
        final Date now = new Date();
        final String nowDateTime = dateTimeFormatter.format(now);
        sectionAnnotations.put(CREATION_DATE, nowDateTime); // section creation
                                                            // date
        writeAnnotationFile(fs, CURRENT, newSectionName, sectionAnnotations);

        // add section to current section index
        final Sections sections = readSectionIndex(fs, CURRENT);
        if (afterSectionInfo == null) {
            sections.sections.addLast(newSectionName);
        } else {
            final int afterSectionIndex = sections.sections
                    .indexOf(afterSectionInfo.sectionName);
            sections.sections.add(afterSectionIndex + 1, newSectionName);
        }
        writeSectionIndex(fs, CURRENT, sections);

        final NotebookItemInfo newSectionInfo = getNotebookItemInfo(notebookURI);
        newSectionInfo.sectionName = newSectionName;
        newSectionInfo.page = -1;

        return newSectionInfo.toURI();
    }

    public void deleteNotebookAll(final String notebookURI)
            throws URISyntaxException, IOException {
        if (!existsNotebook(notebookURI)) {
            throw new IOException("notebook not found:" + notebookURI);
        }

        final NotebookItemInfo info = getNotebookItemInfo(notebookURI);
        final IFileSystem fs = getNotebookFilesystem(info);

        // first delete all the content
        final String[] contents = fs.listDirectoryContents("/");
        for (final String item : contents) {
            fs.delete(item, true);
        }

        // now delete the actual notebook dir/file on the local filesystem
        final IFileSystem localRootFS = new LocalFileSystem("/");
        localRootFS.changeDir("/" + info.notebookPath);
        localRootFS.delete(info.notebookName, true);

        // also remove the fs from the cache
        removeNotebookFilesystem(info);
    }

    public void deletePage(final String pageURI) {
        // TODO Auto-generated method stub

    }

    public void deleteSection(final String sectionURI)
            throws URISyntaxException, IOException {
        if (!existsNotebook(sectionURI)) {
            throw new IOException("notebook not found:" + sectionURI);
        }

        // TODO Auto-generated method stub

    }

    public boolean existsNotebook(final String notebookURI) {
        try {
            final NotebookItemInfo info = getNotebookItemInfo(notebookURI);
            if (getNotebookFilesystem(info) == null) {
                return false;
            }
        } catch (final Exception e) {
            e.printStackTrace();// !!!
            return false;
        }
        return true;
    }

    public String getContainingSection(final String pageURI) throws IOException {
        if (!existsNotebook(pageURI)) {
            throw new IOException("notebook not found:" + pageURI);
        }

        // TODO Auto-generated method stub
        return null;
    }

    public String getCurrentNotebookVersion(final String anyNotebookVersionURI)
            throws URISyntaxException {
        final NotebookItemInfo info = getNotebookItemInfo(anyNotebookVersionURI);
        info.versionName = CURRENT;
        return info.toURI();
    }

    // gets a suitable IFileSystem via which this notebook can be accessed
    // (NB: this keeps a cache of IFileSystems keyed on the URI path for
    // efficiency)
    protected IFileSystem getNotebookFilesystem(final NotebookItemInfo itemInfo)
            throws IOException {
        // check if we have an IFileSystem cached for this notebook already. If
        // not, create one
        final String fullPath = itemInfo.notebookPath + "/"
                + itemInfo.notebookName;
        if (fsCache.containsKey(fullPath)) {
            return fsCache.get(fullPath);
        }

        // for now, we assume anything with an .xml extension is an xml file and
        // any extensionless name is a directory
        IFileSystem fs = null;
        if (itemInfo.notebookName.endsWith(".xml")) {
            fs = null; // !!new XMLFileSystem();
            bilab.Notify.unimplemented(this);
        } else {
            fs = new LocalFileSystem(fullPath);
        }

        if (fs != null) {
            // before we add the new entry to the cache, remove an entry if the
            // cache is
            // too big to ensure it doesn't grow indefinitely
            if (fsCache.size() > 10) {
                fsCache.remove(fsCache.keySet().toArray()[0]); // remov an
                                                               // element (an
                                                               // easier way?)
            }

            fsCache.put(fullPath, fs);
        } else {
            throw new bilab.BilabException("unsuported notebook format");
        }

        return fs;
    }

    protected NotebookItemInfo getNotebookItemInfo(final String notebookURI)
            throws URISyntaxException {
        // accept either a 'file:' URL or a raw absolute file path

        URI uri = null;
        if (isAbsolutePath(notebookURI)) { // abs path, turn into file URI
            // locationURI is actually a path, not a URI. Hence it may not
            // be properly escaped
            uri = new URI("file", null, notebookURI, null, null);
        } else {
            uri = new URI(notebookURI);
        }

        final NotebookItemInfo itemInfo = new NotebookItemInfo();
        final String fullPath = uri.getPath();
        final File path = new File(Util.toNativePathSeparator(fullPath));

        itemInfo.notebookPath = bilab.Util.toForwardPathSeparator(path
                .getParent());
        itemInfo.notebookName = path.getName();

        final String query = uri.getQuery();
        // !!! getQuery decodes, no need to use decoder below.
        // extract from query part of URI (if present)
        if (query != null) {
            final String[] args = query.split("&");
            for (final String arg : args) {
                final String[] parts = arg.split("=");
                if (parts.length != 2) {
                    throw new IllegalArgumentException(
                            "syntax error in notebook URI parameters: "
                                    + notebookURI);
                }
                if (parts[0].equals("version")) {
                    final String decodedVersionName = null;
                    itemInfo.versionName = parts[1];
                } else if (parts[0].equals("section")) {
                    itemInfo.sectionName = parts[1];
                } else if (parts[0].equals("page")) {
                    itemInfo.page = Integer.parseInt(parts[1]);
                } else {
                    throw new IllegalArgumentException(
                            "unrecognized parameter in notebook URI: "
                                    + notebookURI);
                }
            }
        }

        return itemInfo;
    }

    public byte[] getPageContent(final String pageURI) {
        // TODO Auto-generated method stub
        return null;
    }

    public int getPageCount(final String sectionURI) throws IOException {
        if (!existsNotebook(sectionURI)) {
            throw new IOException("notebook not found:" + sectionURI);
        }

        // TODO Auto-generated method stub
        return 0;
    }

    public byte[] getPageResource(final String pageURI,
            final String resourceName) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPageURI(final String sectionURI, final int pageIndex)
            throws IOException {
        if (!existsNotebook(sectionURI)) {
            throw new IOException("notebook not found:" + sectionURI);
        }

        // TODO Auto-generated method stub
        return null;
    }

    public String[] getProperties(final String componentURI) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getProperty(final String componentURI,
            final String propertyName) {
        // TODO Auto-generated method stub
        return null;
    }

    public int getSectionCount(final String notebookURI)
            throws URISyntaxException, IOException {
        if (!existsNotebook(notebookURI)) {
            throw new IOException("notebook not found:" + notebookURI);
        }

        final NotebookItemInfo info = getNotebookItemInfo(notebookURI);
        final IFileSystem fs = getNotebookFilesystem(info);

        final Sections sections = readSectionIndex(fs, info.versionName);

        return sections.sections.size();
    }

    public String getSectionURI(final String notebookURI, final int sectionIndex)
            throws URISyntaxException, IOException {
        if (!existsNotebook(notebookURI)) {
            throw new IOException("notebook not found:" + notebookURI);
        }

        final NotebookItemInfo info = getNotebookItemInfo(notebookURI);
        final IFileSystem fs = getNotebookFilesystem(info);

        final Sections sections = readSectionIndex(fs, info.versionName);

        if ((sectionIndex < 1) || (sectionIndex > sections.sections.size())) {
            throw new BilabException("section index " + sectionIndex
                    + " out of range [1.." + sections.sections.size() + "]");
        }

        info.page = -1;
        info.sectionName = sections.sections.get(sectionIndex - 1);
        return info.toURI();
    }

    //
    // File/dir layout of a notebook:
    //
    // Root
    // versions.xml - contains list of version names (same as version dir names)
    // current - current version dir
    // annotations.xml - contains notenook wide annotations (versioned)
    // sections.xml - contains list of section names (same as section dir names)
    // main - a section dir
    // annotations.xml - contains section wide annotations (versioned)
    // page0.xml - page content
    // page1.xml - page content
    // ..
    // page0-annotations.xml - contains annotations for page0 (versioned)
    // ..
    // intro - a section dir
    // annotations.xml
    // .. pages..
    // appendix - a section dir
    // ..pages..
    // oldversion - previous version dir
    // main - a section dir
    // ..pages..
    // appendix - a section dir
    // ..pages..
    // olderversion - older previous version
    // main - a section dir
    // ..pages..

    //
    // The notebook version/section structure is represented in-memory and
    // read/written from/to XML files.

    protected HashMap<String, String> inputAnnotations(final Document xmlDoc,
            Element e) {
        if (e == null) {
            e = xmlDoc.getDocumentElement();
        }

        final HashMap<String, String> props = new HashMap<String, String>();

        final Element annotElement = findNextElement(e, "annotations", true);
        if ((annotElement == null) || !annotElement.hasChildNodes()) {
            return props; // none
        }

        Element propElement = findNextElement(annotElement.getFirstChild(),
                "property", false);
        if (propElement == null) {
            return props; // none
        }

        while (propElement != null) {
            final String propName = propElement.getAttribute("name");
            if (propName.length() > 0) {
                final String propValue = propElement.getTextContent();
                props.put(propName, propValue);
            }
            propElement = findNextElement(propElement.getNextSibling(),
                    "property", false);
        }

        return props;
    }

    protected Document inputXMLFromStream(final InputStream in)
            throws IOException {
        try {
            final DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.parse(in);

        } catch (final Exception e) {
            throw new IOException("error reading XML content from notebook - "
                    + e.getMessage());
        }

    }

    public String[] listNotebookSectionNames(final String notebookURI)
            throws URISyntaxException, IOException {
        return null;
    }

    public String[] listNotebookSectionURIs(final String notebookURI)
            throws URISyntaxException, IOException {
        return null;
    }

    // annotationsFile stores a HashMap<String,String> propertName -> value

    public String[] listNotebookVersionNames(final String notebookURI)
            throws URISyntaxException, IOException {
        if (!existsNotebook(notebookURI)) {
            throw new IOException("notebook not found:" + notebookURI);
        }

        // return the names of the top-level version directories
        final NotebookItemInfo info = getNotebookItemInfo(notebookURI);
        final IFileSystem fs = getNotebookFilesystem(info);

        final Versions versions = readVersionIndex(fs);

        return versions.versions.toArray(new String[0]);
    }

    public String[] listNotebookVersionURIs(final String notebookURI)
            throws URISyntaxException, IOException {
        final String[] names = listNotebookVersionNames(notebookURI);
        final String[] URIs = new String[names.length];
        final NotebookItemInfo notebookInfo = getNotebookItemInfo(notebookURI);
        for (int nameIndex = 0; nameIndex < names.length; nameIndex++) {
            notebookInfo.versionName = names[nameIndex];
            URIs[nameIndex] = notebookInfo.toURI();
        }
        return URIs;
    }

    // locate the physical section from the logical section URI (or null if not
    // found)
    // Also accepts the special section name 'last' (with current version only),
    // which locates the last section
    // (highest index, or null if empty)
    protected NotebookItemInfo locateSection(final NotebookItemInfo secInfo)
            throws URISyntaxException, IOException {
        // start from specified and look at each version back in time until
        // either the section directory is found or the section no longer
        // appears in the index
        // (indicating that is was deleted)
        final IFileSystem fs = getNotebookFilesystem(secInfo);

        final Versions versions = readVersionIndex(fs);

        // handle special section name 'last'
        if (secInfo.versionName.equals(CURRENT)
                && secInfo.sectionName.equals("last")) {
            final NotebookItemInfo lastInfo = new NotebookItemInfo(secInfo);
            final Sections sections = readSectionIndex(fs, CURRENT);
            try {
                lastInfo.sectionName = sections.sections.getLast();
                return lastInfo;
            } catch (final NoSuchElementException e) {
                return null;
            }
        }

        // check version name is valid
        if (!versions.versions.contains(secInfo.versionName)) {
            throw new BilabException("Unknown version '" + secInfo.versionName
                    + "' in notebook: " + secInfo.toURI());
        }

        boolean found = false; // found the section
        boolean foundDeleted = false; // found version in which it was deleted
                                      // (don't look further back)
        int versionIndex = versions.versions.indexOf(secInfo.versionName) + 1; // 1-based
        while (!found && !foundDeleted && (versionIndex >= 1)) {

            final Sections sections = readSectionIndex(fs,
                    versions.versions.get(versionIndex - 1));

            if (sections.sections.contains(secInfo.sectionName)) {
                // it is in the index, check if there is a directory for it in
                // this version.
                final String sectionDir = "/"
                        + versions.versions.get(versionIndex - 1) + "/"
                        + secInfo.sectionName;
                if (fs.exists(sectionDir) && fs.isDirectory(sectionDir)) {
                    found = true;
                } else {
                    versionIndex--; // look back further
                }
            } else {
                // wasn't in the index, so doesn't exist or has been deleted
                foundDeleted = true;
            }
        } // while

        if (found) {
            final NotebookItemInfo foundInfo = new NotebookItemInfo(secInfo);
            foundInfo.versionName = versions.versions.get(versionIndex - 1);
            return foundInfo;
        }

        return null;
    }

    protected void outputAnnotations(final Document xmlDoc, final Element e,
            final HashMap<String, String> annotations) {
        // TODO: if prop value is more that a simple string (e.g. is
        // XML/HTML/binary) then is should be encoded
        final Element annotElement = xmlDoc.createElement("annotations");
        if (e != null) {
            e.appendChild(annotElement);
        } else {
            xmlDoc.appendChild(annotElement);
        }

        for (final String propName : annotations.keySet()) {
            final Element propElement = xmlDoc.createElement("property");
            propElement.setAttribute("name", propName);
            propElement.setTextContent(annotations.get(propName));
            annotElement.appendChild(propElement);
        }
    }

    protected void outputXMLToStream(final Document xmlDoc,
            final OutputStream out) throws IOException {
        try {
            final Transformer transformer = transFactory.newTransformer();
            final DOMSource source = new DOMSource(xmlDoc);
            final StreamResult result = new StreamResult(out);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");// needed?
            transformer.transform(source, result);
        } catch (final Exception e) {
            throw new IOException("error writing XML content to notebook - "
                    + e.getMessage());
        }

    }

    protected HashMap<String, String> readAnnotationFile(final IFileSystem fs,
            final String version, final String section) throws IOException {
        try {
            // get file stream
            fs.changeDir("/" + version + "/" + section);
            final InputStream in = fs.readFile(annotationsFile);

            final DocumentBuilder builder = builderFactory.newDocumentBuilder();
            final Document xmlDoc = builder.parse(in);
            in.close();

            return inputAnnotations(xmlDoc, null);

        } catch (final Exception e) {
            final IOException ioe = new IOException(
                    "error reading notebook annotation information - "
                            + e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

    protected Sections readSectionIndex(final IFileSystem fs,
            final String version) throws IOException {
        try {
            fs.changeDir("/" + version);
            final InputStream in = fs.readFile(sectionIndexFile);

            final DocumentBuilder builder = builderFactory.newDocumentBuilder();
            final Document xmlDoc = builder.parse(in);
            in.close();

            // now interpret DOM XML as section index
            final Sections sections = new Sections();

            final Element secsElement = findNextElement(xmlDoc, "sections",
                    true);
            if (secsElement == null) {
                throw new IOException("required element 'sections' not present");
            }

            Element secElement = findNextElement(secsElement.getFirstChild(),
                    "section", false);
            while (secElement != null) {
                final String section = secElement.getTextContent();
                sections.sections.add(section);
                secElement = findNextElement(secElement.getNextSibling(),
                        "section", false);
            }

            /*
             * Element dsecsElement = findNextElement(xmlDoc, "deletedsections",
             * true); // not required if (dsecsElement != null) {
             * 
             * secElement =
             * findNextElement(secsElement.getFirstChild(),"section",false);
             * while (secElement != null) { String section =
             * secElement.getTextContent();
             * sections.deletedSections.add(section); secElement =
             * findNextElement(secElement.getNextSibling(),"section",false); } }
             */

            return sections;
        } catch (final Exception e) {
            final IOException ioe = new IOException(
                    "error reading notebook section information - "
                            + e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

    protected Versions readVersionIndex(final IFileSystem fs)
            throws IOException {
        try {
            // get file stream
            fs.changeDir("/");
            final InputStream in = fs.readFile(versionIndexFile);

            final DocumentBuilder builder = builderFactory.newDocumentBuilder();
            final Document xmlDoc = builder.parse(in);
            in.close();

            // now interpret the document & construct Versions
            final Element nbElement = findNextElement(xmlDoc, "notebook", true);
            if (nbElement == null) {
                throw new IOException("required element 'notebook' not found");
            }
            final String inFormatVersion = nbElement.getAttribute("format");
            if (inFormatVersion.length() > 0) {
                if (!inFormatVersion.equals(formatVersion)) {
                    throw new BilabException("unsupported notebook format '"
                            + inFormatVersion + "'");
                }
            }

            final Versions versions = new Versions();
            versions.globalAnnotations = inputAnnotations(xmlDoc, nbElement);

            final Element versElement = findNextElement(nbElement, "versions",
                    true);
            Element verElement = findNextElement(versElement.getFirstChild(),
                    "version", false);
            while (verElement != null) {
                final String version = verElement.getTextContent();
                versions.versions.add(version);
                verElement = findNextElement(verElement.getNextSibling(),
                        "version", false);
            }

            return versions;
        } catch (final Exception e) {
            final IOException ioe = new IOException(
                    "error reading notebook version information - "
                            + e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

    protected void removeNotebookFilesystem(final NotebookItemInfo itemInfo) {
        final String fullPath = itemInfo.notebookPath + "/"
                + itemInfo.notebookName;
        fsCache.remove(fullPath);
    }

    public String renameSection(final String sectionURI,
            final String newSectionName) throws URISyntaxException, IOException {
        if (!existsNotebook(sectionURI)) {
            throw new IOException("notebook not found:" + sectionURI);
        }

        // TODO Auto-generated method stub
        return null;
    }

    public String repositionPage(final String sectionURI, final String pageURI,
            final String afterPageURI) {
        // TODO Auto-generated method stub
        return null;
    }

    public int repositionSection(final String notebookURI,
            final String sectionURI, final String afterSectionURI)
            throws URISyntaxException, IOException {
        if (!existsNotebook(notebookURI)) {
            throw new IOException("notebook not found:" + notebookURI);
        }

        // TODO Auto-generated method stub
        bilab.Notify.unimplemented(this);
        return 0;
    }

    public void setPageContent(final String pageURI, final byte[] content) {
        // TODO Auto-generated method stub

    }

    public void setProperty(final String componentURI,
            final String propertyName, final String value) {
        // TODO Auto-generated method stub

    }

    public String snapshotNotebookAsVersion(final String notebookURI,
            final String versionName) {
        // TODO Auto-generated method stub
        return null;
    }

    protected void writeAnnotationFile(final IFileSystem fs,
            final String version, final String section,
            final HashMap<String, String> annotations) throws IOException {
        try {
            // construct XML DOM from versions

            final DocumentBuilder builder = builderFactory.newDocumentBuilder();
            final Document xmlDoc = builder.newDocument();
            xmlDoc.setXmlStandalone(true);

            outputAnnotations(xmlDoc, null, annotations);

            // write it to the file
            fs.changeDir("/" + version + "/" + section);
            final OutputStream out = fs.writeFile(annotationsFile, false);
            outputXMLToStream(xmlDoc, out);
            out.flush();
            out.close();

        } catch (final Exception e) {
            final IOException ioe = new IOException(
                    "error writing notebook annotation information - "
                            + e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

    protected void writeSectionIndex(final IFileSystem fs,
            final String version, final Sections sections) throws IOException {
        try {

            // construct XML DOM from sections
            final DocumentBuilder builder = builderFactory.newDocumentBuilder();
            final Document xmlDoc = builder.newDocument();
            xmlDoc.setXmlStandalone(true);

            final Element secsElement = xmlDoc.createElement("sections");
            xmlDoc.appendChild(secsElement);

            for (final String sectionName : sections.sections) {
                final Element secElement = xmlDoc.createElement("section");
                secElement.setTextContent(sectionName);
                secsElement.appendChild(secElement);
            }

            /*
             * if (sections.deletedSections.size()>0) { Element dsecsElement =
             * xmlDoc.createElement("deletedsections");
             * xmlDoc.appendChild(dsecsElement);
             * 
             * for(String sectionName : sections.deletedSections) { Element
             * secElement = xmlDoc.createElement("section");
             * secElement.setTextContent(sectionName);
             * secsElement.appendChild(secElement); } }
             */

            // write it to the file
            fs.changeDir("/" + version);
            final OutputStream out = fs.writeFile(sectionIndexFile, false);
            outputXMLToStream(xmlDoc, out);
            out.flush();
            out.close();

        } catch (final Exception e) {
            final IOException ioe = new IOException(
                    "error writing notebook section information - "
                            + e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

    protected void writeVersionIndex(final IFileSystem fs,
            final Versions versions) throws IOException {
        try {

            // construct XML DOM from versions

            final DocumentBuilder builder = builderFactory.newDocumentBuilder();
            final Document xmlDoc = builder.newDocument();
            xmlDoc.setXmlStandalone(true);

            final Element nbElement = xmlDoc.createElement("notebook");
            nbElement.setAttribute("format", formatVersion);
            xmlDoc.appendChild(nbElement);
            outputAnnotations(xmlDoc, nbElement, versions.globalAnnotations);

            final Element versElement = xmlDoc.createElement("versions");

            for (final String versionName : versions.versions) {
                final Element verElement = xmlDoc.createElement("version");
                verElement.setTextContent(versionName);
                versElement.appendChild(verElement);
            }

            nbElement.appendChild(versElement);

            // write it to the file
            fs.changeDir("/");
            final OutputStream out = fs.writeFile(versionIndexFile, false);
            outputXMLToStream(xmlDoc, out);
            out.flush();
            out.close();

        } catch (final Exception e) {
            final IOException ioe = new IOException(
                    "error writing notebook version information - "
                            + e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

}
