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
