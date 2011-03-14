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

package type;

/**
 * type/DbXref.java . Generated by the IDL-to-Java compiler (portable), version
 * "3.1" from corba/types.idl Monday, August 23, 2004 12:02:37 PM BST
 */

/**
 * Database cross-reference. The list of valid database identifiers used by the
 * collaboration is defined at the <A
 * href="http://www.ncbi.nlm.nih.gov/collab/db_xref.html">NCBI collaborative web
 * server</A>
 * <dl>
 * <dt>db
 * <dd>database identifier
 * <dt>primary_id
 * <dd>object identifier in the database
 * <dt>version
 * <dd>if the referenced database supports versioning, version refers to the
 * version of the object, when the cross-reference was generated. If can be used
 * to verify that a cross-reference is up to date.
 * <dt>label
 * <dd>secondary identifier, possibly used to indicate a sub/super-part of the
 * primary object or an alternative name to the primary id. The label can
 * contain information for the convenience of the user, but no assumptions
 * should be made on the long-term stability of it.
 * </dl>
 */
public final class DbXref implements org.omg.CORBA.portable.IDLEntity {
    public String db = null;
    public String primary_id = null;
    public int version = 0;
    public String label = null;

    public DbXref() {
    } // ctor

    public DbXref(final String _db, final String _primary_id,
            final int _version, final String _label) {
        db = _db;
        primary_id = _primary_id;
        version = _version;
        label = _label;
    } // ctor

} // class DbXref
