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

import java.io.IOException;
import java.net.URISyntaxException;

// An interface to a notebook store
//
// Conceptual notebook definition:
// A notebook is referenced by a unique URI (which includes a name and
// optionally a version name)
// A notebook has a name and one or more sections
// Sections are named and identified via a URI (which includes the containing
// notebook and the name)
// Sections are ordered, but the section URI is not affected by the ordering
// A Section consists of zero or more pages, also identified by a URI
// Pages are ordered and their containing notebook, section and position are
// part of their URI.
// Pages can have associated named resources (arbitrary byte arrays with simple
// names), which are also versioned.
// Metadata annotations, in the form of property name,value pairs can be applied
// to
// notebooks, sections and pages (there are some standard properties, defined
// below)
// A notebook can be versioned (including its properties), and associated with a
// version name.
// Only the current version can be modified. Some special properties can be
// modified for
// versions other than the current version - such as NOTARIZATIONS,
// LAST_ACCESS_DATETIME etc.
public interface INotebookStore {
    // some standard property names
    public static final String NOTEBOOK_TITLE = "NotebookTitle"; // a read-only
                                                                 // property

    public static final String NOTEBOOK_VERSION = "NotebookVersion"; // a
                                                                     // read-only
                                                                     // property

    public static final String SECTION_NAME = "SectionName"; // a read-only
                                                             // property

    public static final String CREATION_DATE = "CreationDate"; // a read-only
                                                               // property

    public static final String LAST_MODIFIED_DATETIME = "LastModDateTime"; // read-only
                                                                           // -
                                                                           // last
                                                                           // time
                                                                           // component
                                                                           // was
                                                                           // modified

    public static final String LAST_ACCESS_DATETIME = "LastAccessDateTime"; // read-only

    public static final String AUTHOR_NAME = "AuthorName"; // human readable
                                                           // author name

    public static final String AUTHOR_ID = "AuthorID"; // e.g. author principle
                                                       // string

    public static final String NOTARIZATIONS = "Notarizations";

    public static final String EDIT_HISTORY = "EditHistory";

    public static final String CURRENT = "current"; // value of the
                                                    // NOTEBOOK_VERSION property
                                                    // for the current version

    // pages can have resources associated with them (also versioned)
    // If a resource with the speficied name apready exists, it will be
    // replaced)
    // (NB: only the current version can have resources added)
    public void addPageResource(String pageURI, String resourceName,
            byte[] content) throws URISyntaxException, IOException;

    // create a new notebook at the specificed location (e.g. a file-system
    // directory path) and return its
    // unique URI (e.g. file://My Documents\MyNotebook.bln, or
    // http://notebookserver.company.com/path/to/notebook.bln))
    // The name is recorded an an annotation with property name NOTEBOOK_NAME
    // The NOTEBOOK_VERSION property will be CURRENT
    public String createNotebook(String locationURI, String name)
            throws URISyntaxException, IOException;

    // create a new page within the specified notebook & section after the given
    // page
    // If insertAfterPageURI is null, the page is inserted after the last page
    // of the section.
    // The insertAfterPageURI page must be in the same section as sectionURI.
    // Returns the new page's URI (which also identifies the notebook & section)
    // (NB: new pages can only be created in the current version, not in
    // snapshots)
    public String createPage(String sectionURI, String insertAfterPageURI)
            throws URISyntaxException, IOException;

    // creates a new section within the specified notebook
    // The section is inserted after the specified section, or as the last
    // section if insertAfterSectionURI is null.
    // The section name is recorded as an annotation with propertyName
    // SECTION_NAME
    // Returns the URI of the new section (which also identifies the notebook
    // itself)
    // (NB: new sections can only be create in the current version, not in
    // snapshots)
    public String createSection(String notebookURI, String newSectionName,
            String insertAfterSectionURI) throws URISyntaxException,
            IOException;

    // delete an entire notebook (all versions)
    public void deleteNotebookAll(String notebookURI)
            throws URISyntaxException, IOException;

    // deletes the specified page
    // (NB: pages can only be deleted from the current version, not from
    // snapshots)
    public void deletePage(String pageURI) throws URISyntaxException,
            IOException;

    // delete the specified section
    // (NB: sections can only be deleted from the current notebook, not from
    // snapshots)
    public void deleteSection(String sectionURI) throws URISyntaxException,
            IOException;

    // return true if the specified notebook refers to an existing notebook
    public boolean existsNotebook(String notebookURI);

    public String getContainingSection(String pageURI)
            throws URISyntaxException, IOException;// returns the section URI to
                                                   // which this page belongs

    // given the URI of any version of a notebook, return the URI of the current
    // version
    public String getCurrentNotebookVersion(String anyNotebookVersionURI)
            throws URISyntaxException;

    public byte[] getPageContent(String pageURI) throws URISyntaxException,
            IOException;

    public int getPageCount(String sectionURI) throws URISyntaxException,
            IOException; // returns the number of pages in the given section

    public byte[] getPageResource(String pageURI, String resourceName)
            throws URISyntaxException, IOException;

    public String getPageURI(String sectionURI, int pageIndex)
            throws URISyntaxException, IOException; // returns page URI of page
                                                    // with specified index
                                                    // (1-based)

    // fetch all properties on a given component
    public String[] getProperties(String componentURI)
            throws URISyntaxException, IOException;

    // lookup an annotation property on a given component (rturning the value,
    // or null is no such property exists)
    public String getProperty(String componentURI, String propertyName)
            throws URISyntaxException, IOException;

    public int getSectionCount(String notebookURI) throws URISyntaxException,
            IOException; // returns the number of sections in the given notebook

    public String getSectionURI(String notebookURI, int sectionIndex)
            throws URISyntaxException, IOException; // returns section URI of
                                                    // section with specified
                                                    // index (1-based)
    // return a list of the URIs for every section of the specified notebook -
    // either the current
    // version or the version specified.

    public String[] listNotebookSectionNames(String notebookURI)
            throws URISyntaxException, IOException;

    // return a list of the URIs for every section of the specified notebook -
    // either the current
    // version or the version specified.
    public String[] listNotebookSectionURIs(String notebookURI)
            throws URISyntaxException, IOException;

    // return a list of the name for every version of the specified notebook,
    // including the
    // current one (as the last element)
    public String[] listNotebookVersionNames(String notebookURI)
            throws URISyntaxException, IOException;

    // return a list of the URIs for every version of the specified notebook,
    // including the
    // current one (as the last element)
    public String[] listNotebookVersionURIs(String notebookURI)
            throws URISyntaxException, IOException;

    // rename the specified section to the new given name (and return tne new
    // URI)
    // (NB: sections can only be renamed in the current notebook, not in
    // snapshots)
    public String renameSection(String sectionURI, String newSectionName)
            throws URISyntaxException, IOException;

    // re-position the specified page after the given page and return its new
    // URI
    // (NB: The afterPageURI may specify a page in a different section)
    // (NB: This will change the page's URI - hence the client will need to
    // update any URI references held)
    public String repositionPage(String sectionURI, String pageURI,
            String afterPageURI) throws URISyntaxException, IOException;

    // re-position the specified section after the given section and return its
    // new position index
    // (NB: this will not effect the URI of the section)
    public int repositionSection(String notebookURI, String sectionURI,
            String afterSectionURI) throws URISyntaxException, IOException;

    // set the content of the specified page (overwriting the previous content
    // of the page - only in the current version)
    // (NB: only the content of the current version can be changed)
    public void setPageContent(String pageURI, byte[] content)
            throws URISyntaxException, IOException;

    // annotate a notebook component with a property (e.g. a notebook, notebook
    // section or page)
    public void setProperty(String componentURI, String propertyName,
            String value) throws URISyntaxException, IOException;

    // permanently store the current state of the specified notebook as a
    // version with the given
    // name. All subsequent operations will apply to the new current version
    // unless
    // this version is specified explicitly.
    // The NOTEBOOK_VERSION property of the returned version will be versioName.
    // Returns a URI for the notebook version 'versionName'
    public String snapshotNotebookAsVersion(String notebookURI,
            String versionName) throws URISyntaxException, IOException;
}
