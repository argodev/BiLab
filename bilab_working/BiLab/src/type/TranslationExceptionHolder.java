package type;

/**
 * type/TranslationExceptionHolder.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/types.idl Monday, August 23, 2004
 * 12:02:37 PM BST
 */

/**
 * Translation exception of a single triplet within a sequence.
 * <p>
 * <dl>
 * <dt>primary_acc
 * <dd>This attribute is likely to change in the future. It is very rarely used,
 * but it is needed for translation exceptions on CDS's spanning entries
 * <dt>start
 * <dd>startposition of exception in the sequence.
 * <dt>end
 * <dd>endposition of exception in the sequence
 * <dt>amino_acid
 * <dd>amino acid used in this exception.
 * <dd>No modified AA are allowed.
 * </dl>
 */
public final class TranslationExceptionHolder implements
        org.omg.CORBA.portable.Streamable {
    public type.TranslationException value = null;

    public TranslationExceptionHolder() {
    }

    public TranslationExceptionHolder(
            final type.TranslationException initialValue) {
        value = initialValue;
    }

    @Override
    public void _read(final org.omg.CORBA.portable.InputStream i) {
        value = type.TranslationExceptionHelper.read(i);
    }

    @Override
    public org.omg.CORBA.TypeCode _type() {
        return type.TranslationExceptionHelper.type();
    }

    @Override
    public void _write(final org.omg.CORBA.portable.OutputStream o) {
        type.TranslationExceptionHelper.write(o, value);
    }

}
