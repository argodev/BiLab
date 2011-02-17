package bilab;

import scigol.*;
  
import org.biojava.bio.BioException;
import org.biojava.bio.seq.*;
import org.biojava.bio.symbol.*;
import org.biojava.bio.seq.io.*;
  
import org.biojava.bio.seq.impl.SimpleSequence;



@Summary("A RNA sequence molecule")
public class RNA extends seq
{
  public RNA()
  {
    try {
      seq = RNATools.createRNASequence("","rna");
    } catch (BioException e) {
      Debug.Assert(false,"biojava error");
    }
  }

  
  public RNA(Sequence s)
  {
    seq = s;
  }
  
  
  public RNA(String seq)
  {
    try {
      this.seq = RNATools.createRNASequence(seq,"rna");
    } catch (BioException e) {
      throw new BilabException("biojava error",e);
    }
  }

  

  @accessor
  public RNA get_Item(Range r)
  {
    r = r.normalize(seq.length());
    return new RNA(seq.subStr(r.start+1, r.end+1));
  }
  
  
  @accessor
  public Any get_Item(int i)
  {
    try {
      // return a molecule representing the specific aminoacid
      org.biojava.bio.symbol.Symbol sym = seq.symbolAt(i+1);
      Alphabet alpha = sym.getMatches();
      scigol.List mols = new scigol.List();
      if (alpha.contains(RNATools.a())) mols.add(MoleculeImpl.A);
      if (alpha.contains(RNATools.c())) mols.add(MoleculeImpl.C);
      if (alpha.contains(RNATools.g())) mols.add(MoleculeImpl.G);
      if (alpha.contains(RNATools.u())) mols.add(MoleculeImpl.U);
      
      if (mols.get_size() == 1)
        return mols.get_head();
      return new Any(mols);
    } catch (IndexOutOfBoundsException e) {
      throw new BilabException("index must be in range 0.."+(seq.length()-1)+", not "+i);
    }
  }
  
  @accessor
  public void set_Item(int i, Any value) 
  {
    Debug.Unimplemented();
  }

  
  
  
  
  public static RNA op_UnaryNegation(RNA rna)
  {
    return rna.get_Compliment();
  }
  
  
  @accessor
  public RNA get_Compliment()
  {
    try {
      return new RNA(new SimpleSequence( RNATools.reverseComplement(seq), 
                                         seq.getURN()+"|compliment", 
                                         seq.getName()+" compliment", 
                                         seq.getAnnotation() )
                     );
    } catch (IllegalAlphabetException e) {
      throw new BilabException("Cannot reverse compliment RNA with alphabet '"+seq.getAlphabet().getName()+"'");
    }
  }
  
  
  public static protein translate(RNA rna)
  {
    return rna.get_translation(); 
  }
  
  
  @accessor
  public protein get_translation()
  {
    try {
      return new protein( new SimpleSequence( RNATools.translate(seq),
                          seq.getURN()+"|translation",
                          seq.getName()+" translation",
                          seq.getAnnotation() )
                        );
    } catch (BioException e) {
      throw new BilabException("biojava error",e);
    }
  }
  
  
  
  public static RNA op_Addition(RNA rna1, RNA rna2)
  {
    // do by string
    String seq1 = rna1.get_rawsequence();
    String seq2 = rna2.get_rawsequence();
    return new RNA(seq1+seq2);
  }
  

  public static int op_Card(RNA rna)
  {
    return rna.seq.length();
  }
  
  
  
  

  @accessor
  public String get_ShortText()
  {
    return seq.getName();
  }

  
  @accessor
  public String get_DetailText()
  {
    //!!! implement properly
    return get_ShortText();
  }

  
  @Summary("get sequence as a string of letters formatted into columns")
  public String get_sequence()
  {
    return formatSeqString(seq.seqString().toUpperCase());
  }

  
  @Summary("get sequence as a string of continous letters")
  public String get_rawsequence()
  {
    return seq.seqString().toUpperCase();
  }
  
  
  public void importFrom(String resourceName)
  {
    Debug.Unimplemented();
  }
  
  

  public scigol.Map get_annotations()
  {
    // for now just copy annot into a new Map, later make a wrapper that extends Map
    //  so that the properties can be changed
    // NB: we use the seq.getAnnotation, not seq.annot
    scigol.Map m = new scigol.Map();
    for (java.util.Iterator i = seq.getAnnotation().keys().iterator(); i.hasNext(); ) {
      Object key = i.next();
      m.add(key, seq.getAnnotation().getProperty(key));
    }
    return m;
  }
  

  public String toString()
  {
    return super.toString().toUpperCase();
  }



  @Summary("The underlying biojava Sequence representing this RNA")
  @Sophistication(Sophistication.Advanced)
  public Sequence seq;
  
}


