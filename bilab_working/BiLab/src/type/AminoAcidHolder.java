package type;

/**
* type/AminoAcidHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/types.idl
* Monday, August 23, 2004 12:02:37 PM BST
*/


/**
   * Amino Acid. This can be any amino acid, including modified or unusual ones.
   * <P>
   * <dl>
   * <dt> code
   * <dd> <A href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/aa_abbrevs.html">
   *     IUPAC-IUB</A> one-letter code if the amino acid has one assigned
   * <dd> othwerwise the code will be a blank character
   * <dt> name
   * <dd> descriptive name of the
   *     <A href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/aa_abbrevs.html">
   *      Amino Acid</A> or 
   *      <A href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/modified_aa.html">
   *      modified or unusual Amino Acid</A>
   * <dt> abbreviation
   * <dd> abbreviated name of the
   *     <A href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/aa_abbrevs.html">
   *      Amino Acid</A> or 
   *      <A href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/modified_aa.html">
   *      modified or unusual Amino Acid</A>
   *</dl>
   */
public final class AminoAcidHolder implements org.omg.CORBA.portable.Streamable
{
  public type.AminoAcid value = null;

  public AminoAcidHolder ()
  {
  }

  public AminoAcidHolder (type.AminoAcid initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = type.AminoAcidHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    type.AminoAcidHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return type.AminoAcidHelper.type ();
  }

}