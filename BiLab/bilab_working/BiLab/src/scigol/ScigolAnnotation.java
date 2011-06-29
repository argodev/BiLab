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

package scigol;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

public class ScigolAnnotation implements Annotation {
    private final TypeSpec annotationInterface;

    private ArrayList members;

    public ScigolAnnotation(final TypeSpec annotationInterface) {
        Debug.Assert(annotationInterface.isInterface());
        Debug.Assert(((java.lang.Class) annotationInterface.getSysType())
                .isAnnotation());

        this.annotationInterface = annotationInterface;

        members = new ArrayList();
    }

    @Override
    public java.lang.Class<? extends Annotation> annotationType() {
        return (java.lang.Class<Annotation>) annotationInterface.getSysType();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass().isAssignableFrom(
                (java.lang.Class) annotationInterface.getSysType());
    }

    public ArrayList getMembers() {
        return members;
    }

    @Override
    public int hashCode() {
        // !!! might want to match the Java algorithm here...
        int code = toString().hashCode();
        for (final Object o : members) {
            code += o.hashCode();
        }
        return code;
    }

    public void setMembers(final ArrayList members) {
        this.members = members;

        // check members against number & type of annotation

    }

    @Override
    public String toString() {
        return ((java.lang.Class) annotationInterface.getSysType()).toString();
    }
}
