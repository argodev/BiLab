/**
* This document is a part of the source code and related artifacts for BiLab, an open source interactive workbench for 
* computational biologists.
*
* http://computing.ornl.gov/
*
* Copyright © 2011 Oak Ridge National Laboratory
*
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General 
* Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any 
* later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
* details.
*
* You should have received a copy of the GNU Lesser General Public License along with this program; if not, write to 
* the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
* The license is also available at: http://www.gnu.org/copyleft/lgpl.html
*/
package bilab;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.util.LinkedList;
import java.util.List;

import scigol.*;
  
import org.biojava.bio.*;
import org.biojava.bio.seq.*;
import org.biojava.bio.symbol.*;
import org.biojava.bio.seq.io.*;
import org.biojava.bio.program.gff.*;
  
// Sequence - a seq is a list of molecules (and is itself also a molecule)
public abstract class seq extends scigol.List implements molecule, IResourceIOProvider
{
  public seq()
  {
    _name = "untitled";
  }

  
  public seq(String name)
  {
    _name = name;
  }

  
  public boolean get_StructureKnown()
  {
    return false; // !!! for now
  }


  public String get_name()
  {
    return _name;
  }


  // maximum length of a seq string for display 
  //  (longer sequences will have ... embedded)
  static final int maxStringLen = 40;
  
  
  @accessor
  public String get_AssociatedResource()
  {
    return associatedResourceName;
  }

  @Sophistication(Sophistication.Advanced)
  @accessor
  public void set_AssociatedResource(String resourceName)
  {
    associatedResourceName = resourceName;
  }



  public String ToMDL()
  {
    Debug.Unimplemented(); return null;
  }


  public abstract String get_ShortText();
  public abstract String get_DetailText();

  
  public abstract String get_sequence();
  public abstract String get_rawsequence();


  // force concrete sequences to implement required List methods
  //!!!!public abstract override Any head { get; set; }



  public scigol.Map get_annotations()
  {
    // for now just copy annot into a new Map, later make a wrapper that extends Map
    //  so that the properties can be changed !!!
    scigol.Map m = new scigol.Map();
    for (java.util.Iterator i = annot.keys().iterator(); i.hasNext(); ) {
      Object key = i.next();
      m.add(key, annot.getProperty(key));
    }
    return m;
  }

  
  public Any get_annotation(String key)
  {
    return get_annotations().get_Item(key);
  }


  
  
  // IResourceImporter
  
  
  // this will create a DNA, RNA or protien (or possibly a list thereof)
  public static Object importResource(String resourceName, String resourceType)
  {
    try {
    
      java.io.InputStreamReader streamReader = new java.io.InputStreamReader(BilabPlugin.findResourceStream(resourceName));
      if (streamReader == null)
        throw new BilabException("unable to open resource:"+resourceName);

      scigol.List seqList = new scigol.List();
        
      SequenceIterator sequences = null;
      
      BufferedReader br = new BufferedReader(streamReader);
      
      if (resourceType.equals("EMBL")) 
        sequences = SeqIOTools.readEmbl(br);
      else if (resourceType.equals("SwissProt")) 
        sequences = SeqIOTools.readSwissprot(br);
      else if (resourceType.equals("GenBank")) 
        sequences = SeqIOTools.readGenbank(br);
      else if (resourceType.equals("GenPept"))
        sequences = SeqIOTools.readGenpept(br);
      else if (resourceType.equals("FASTA")) {
        sequences = SeqIOTools.readFastaDNA(br);
        Notify.logInfo(seq.class,"Assuming FASTA DNA");
      }
      else
        throw new BilabException("unsupported resource type:"+resourceType);
      
      while (sequences.hasNext()) {
        Sequence bjs = sequences.nextSequence();
        seq s = seqFromSequence(bjs);
        Any a = s.get_annotation("ID");
        if (a != null) {
          if ((a.value != null) && (a.value instanceof String))
            s._name = "ID:"+(String)(a.value);
        }
        if (s._name.equals("untitled")) {
          Any a2 = s.get_annotation("GI");
          if (a2 != null) {
            if ((a2.value != null) && (a2.value instanceof String))
              s._name = "GI:"+(String)(a2.value);
          }
          
        }
        if (s._name.equals("untitled")) {
          Any a2 = s.get_annotation("gi");
          if (a2 != null) {
            if ((a2.value != null) && (a2.value instanceof String))
              s._name = "gi:"+(String)(a2.value);
          }
          
        }
        seqList.add( s );
      }
    
      // now return the list (or just the element if there is only one)
      if (seqList.get_size() > 1) 
        return seqList;
      
      
      // single seq
      //  associate the resource imported with the seq before returning it
      seq s = (seq)seqList.get_head().value;
      s.set_AssociatedResource(resourceName);
      return seqList.get_head();
    
    } catch (Exception e) {
      throw new BilabException("unable to locate/import resource as sequence(s): "+resourceName);
    }
  }
  
  
  
  @Summary("create a resource containing data in a supported format from a seq")
  public static void exportResource(seq s, String resourceName, String resourceType)
  {
    try {
      OutputStream outStream = BilabPlugin.createResourceStream(resourceName);
      
      exportResource(s,outStream,resourceType);
      
      outStream.flush();
      outStream.close();
      
    } catch (Exception e) {
      throw new BilabException("unable to export sequence as resource: "+resourceName);
    }
  }

  
  @Summary("write the resource to the stream in a supported format")
  @Sophistication(Sophistication.Advanced)
  public static void exportResource(seq s, OutputStream outStream, String resourceType)
  {
    try {
      if (resourceType.equals("EMBL")) 
        SeqIOTools.writeEmbl(outStream, sequenceFromSeq(s));
      else if (resourceType.equals("SwissProt")) 
        SeqIOTools.writeSwissprot(outStream, sequenceFromSeq(s));
      else if (resourceType.equals("GenBank")) 
        SeqIOTools.writeGenbank(outStream, sequenceFromSeq(s));
      else if (resourceType.equals("GenPept"))
        SeqIOTools.writeGenpept(outStream, sequenceFromSeq(s));
      else if (resourceType.equals("FASTA"))
        SeqIOTools.writeFasta(outStream, sequenceFromSeq(s));
      else
        throw new BilabException("unsupported resource type:"+resourceType);
      
    } catch (Exception e) {
      throw new BilabException("unable to export sequence as resource type: "+resourceType);
    }
  }
  
  
  
  
  
  @Summary("add the features expressed in GFF format to this sequence")
  @Sophistication(Sophistication.Advanced)
  public void addGFFFeatures(String GFFString)
  {
    try {
      Reader stringReader = new StringReader(GFFString);
      BufferedReader bufferedReader = new BufferedReader(stringReader);
      GFFEntrySet gffEntrySet = GFFTools.readGFF(bufferedReader);
      Sequence thisSequence = seq.sequenceFromSeq(this); // get upderlying biojava Sequence
      
      Sequence thisGFFAnnotatedSequence = GFFTools.annotateSequence(thisSequence,gffEntrySet);
     
      updateSequence(thisGFFAnnotatedSequence);
      
    } catch (Exception e) {
      throw new BilabException("unable to parse GFF feature string:"+e); 
    }

  }
  
  
  
  
  // convenience
  @Sophistication(Sophistication.Advanced)
  public static seq seqFromSequence(Sequence seq)
  {
    if (seq==null) return null;
    
    // determine type via Alphabet
    Alphabet alpha = seq.getAlphabet();
    
    if (alpha.equals(DNATools.getDNA()))
      return new DNA(seq);
    else if (alpha.equals(RNATools.getRNA()))
      return new RNA(seq);
    else if (alpha.equals(ProteinTools.getAlphabet()) || alpha.equals(ProteinTools.getTAlphabet()))
      return new protein(seq);
    else 
      throw new BilabException("unsupported sequence alphabet:"+alpha);
    
  }
  
  
  
  @Sophistication(Sophistication.Advanced)
  public void updateSequence(Sequence seq)
  {
    if (seq==null) return;
    
    // determine type via Alphabet
    Alphabet alpha = seq.getAlphabet();
    
    if (this instanceof DNA)
      ((DNA)this).seq = seq;
    else if (this instanceof RNA)
      ((RNA)this).seq = seq;
    else if (this instanceof protein)
      ((protein)this).seq = seq;
    else 
      throw new BilabException("unsupported seq subclass:"+this.getClass());
    
  }
  
  
  

  // convenience
  @Sophistication(Sophistication.Advanced)
  public static Sequence sequenceFromSeq(seq s)
  {
    if (s==null) return null;
    
    if (s instanceof DNA)
      return ((DNA)s).seq;
    else if (s instanceof RNA)
      return ((RNA)s).seq;
    else if (s instanceof protein)
      return ((protein)s).seq;

    Debug.Assert(false, "unknown/unhandled seq subclass");
    return null;
  }

  
  
  
  
  
  private static List<String> supportedResourceTypes;
  
  static {
    // list of supported resource name type (not extensions)
    supportedResourceTypes = new LinkedList<String>();
    supportedResourceTypes.add("EMBL");
    supportedResourceTypes.add("SwissProt");
    supportedResourceTypes.add("GenBank");
    supportedResourceTypes.add("GenPept");
    supportedResourceTypes.add("FASTA");
  }
  
  
  public static List<String> getSupportedResourceTypes()
  {
    return supportedResourceTypes;
  }
  
  
  @Summary("split into lines of 6 cols of 10 chars each")
  @Sophistication(Sophistication.Developer)
  public static String formatSeqString(String seqString)
  {
    StringBuilder fs = new StringBuilder();
    
    for(int c=0; c<seqString.length(); c++) {
      fs.append(seqString.charAt(c));
      if (((c+1)%10)==0) {
        if (((c+1)%60)==0)
          fs.append("\n");
        else
          fs.append(' ');
      }
    }
    return fs.toString();
  }


  
  
  public String toString()
  {
    String s = get_rawsequence();
    if (s.length() <= maxStringLen)
      return s;
    int half = (maxStringLen-5)/2;
    String start = s.substring(0,half);
    String end = s.substring(s.length()-half-1);
    return start+" ... "+end;
  }

  
  protected String _name;
  protected Annotation annot;
  
  protected String associatedResourceName = null;

}


