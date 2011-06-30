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

package seqdb;

/**
 * seqdb/SeqInfoOperations.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/seqdb.idl Monday, August 23, 2004
 * 12:02:46 PM BST
 */

/**
 * Information associated with a sequence
 */
public interface SeqInfoOperations {

    /**
     * sequence of comments, describing the characteristics of the sequence
     */
    String[] getComments() throws type.NoResult;

    /**
     * cross references to other databases containing related or additional
     * information
     */
    type.DbXref[] getDbXrefs() throws type.NoResult;

    /**
     * short (one line) description
     */
    String getDescription() throws type.NoResult;

    /**
     * sequence of keywords, describing the characteristics of the sequence
     */
    String[] getKeywords() throws type.NoResult;

    /**
     * cross references to the EMBL publication database information
     */
    type.DbXref[] getReferences() throws type.NoResult;
} // interface SeqInfoOperations