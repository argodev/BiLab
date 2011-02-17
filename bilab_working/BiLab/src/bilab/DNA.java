package bilab;

import java.io.FileNotFoundException;

import scigol.*;
  
import org.biojava.bio.BioException;
import org.biojava.bio.seq.*;
import org.biojava.bio.symbol.*;
import org.biojava.bio.seq.io.*;
  
import org.biojava.bio.seq.impl.SimpleSequence;

@Summary("A DNA sequence molecule")
public class DNA extends seq
{
  public DNA()
  {
    try {
      seq = DNATools.createDNASequence("","dna");
    } catch (BioException e) {
      Debug.Assert(false,"biojava error");
    }
  }
  
  @Sophistication(Sophistication.Advanced)
  public DNA(Sequence s)
  {
    seq = s;
  }
  
  @Summary("create a DNA sequence from a string.  For example \"atggccat\".")
  public DNA(String seq)
  {
    try {
      this.seq = DNATools.createDNASequence(seq,"dna");
    } catch (BioException e) {
      throw new BilabException("biojava error",e);
    }
  }
  
  @accessor
  public DNA get_Item(Range r)
  {
    r = r.normalize(seq.length());
    return new DNA(seq.subStr(r.start+1, r.end+1));
  }

  @accessor
  public Any get_Item(int i)
  {
    try {
      // return a molecule representing the specific aminoacid
      org.biojava.bio.symbol.Symbol sym = seq.symbolAt(i+1);
      Alphabet alpha = sym.getMatches();
      scigol.List mols = new scigol.List();
      if (alpha.contains(DNATools.a())) mols.add(MoleculeImpl.A);
      if (alpha.contains(DNATools.c())) mols.add(MoleculeImpl.C);
      if (alpha.contains(DNATools.g())) mols.add(MoleculeImpl.G);
      if (alpha.contains(DNATools.t())) mols.add(MoleculeImpl.T);
      
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

  @Summary("Transcribe DNA into RNA")
  public static RNA transcribe(DNA dna)
  {
    return dna.get_transcription();
  }

  @accessor
  @Summary("Transcription into RNA")
  public RNA get_transcription()
  {
    try {
      //!!! after Biojava 1.4, do we need to use DNATools.toRNA() instead? (see cookbook)
      return new RNA( new SimpleSequence( RNATools.transcribe(seq),
                                          seq.getURN()+"|transcription",
                                          seq.getName()+" transcription",
                                          seq.getAnnotation() )
                       );
    } catch (IllegalAlphabetException e) {
      throw new BilabException("Cannot transcribe DNA with alphabet '"+seq.getAlphabet().getName()+"'");
    }
  }

  @Summary("Reverse compliment of DNA")
  public static DNA op_UnaryNegation(DNA dna)
  {
    return dna.get_Compliment();
  }
  
  @accessor
  @Summary("Reverse compliment")
  public DNA get_Compliment()
  {
    try {
      return new DNA( new SimpleSequence( DNATools.reverseComplement(seq),
                                          seq.getURN()+"|compliment",
                                          seq.getName()+" compliment",
                                          seq.getAnnotation() )
                    );
    } catch (IllegalAlphabetException e) {
      throw new BilabException("Cannot reverse compliment DNA with alphabet '"+seq.getAlphabet().getName()+"'");
    }
  }
  
  public static DNA op_Addition(DNA dna1, DNA dna2)
  {
    // do by string
    String seq1 = dna1.get_rawsequence();
    String seq2 = dna2.get_rawsequence();
    return new DNA(seq1+seq2);
  }
  
  public static int op_Card(DNA dna)
  {
    return dna.seq.length();
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
  
  public scigol.Map get_annotations()
  {
    // for now just copy annotation into a new Map, later make a wrapper that extends Map
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

  @Summary("The underlying biojava Sequence representing this DNA")
  @Sophistication(Sophistication.Advanced)
  public Sequence seq; 
}