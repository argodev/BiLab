/* Jalview - a java multiple alignment editor
 * Copyright (C) 1998  Michele Clamp
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jalview;
import java.util.*;
import java.io.*;

public class FormatAdapter {
  
  public static String get(String format,Sequence[] s) {
    if (FormatProperties.contains(format)) {
      if (FormatProperties.indexOf(format) == FormatProperties.MSF) {
        return MSFfile.print(s);
      } else  if (FormatProperties.indexOf(format) == FormatProperties.FASTA) {
        return FastaFile.print(s,72);
      } else  if (FormatProperties.indexOf(format) == FormatProperties.CLUSTAL) {
        return ClustalFile.print(s);
      } else  if (FormatProperties.indexOf(format) == FormatProperties.PIR) {
        return PIRFile.print(s,72);
      } else  if (FormatProperties.indexOf(format) == FormatProperties.BLC) {
        return BLCFile.print(s);
      } else  if (FormatProperties.indexOf(format) == FormatProperties.MSP) {
        return MSPFile.print(s);
      } else  if (FormatProperties.indexOf(format) == FormatProperties.PFAM) {
        return PfamFile.print(s);
      } else  if (FormatProperties.indexOf(format) == FormatProperties.POSTAL){
        return PostalFile.print(s);
      } else  if (FormatProperties.indexOf(format) == FormatProperties.JNET){
        return JnetFile.print(s);
      }
    } else {
      // Should throw exception here
      return null;
    }
    return null;
  }
  public static Sequence[] read(String format,String inStr) {
    if (FormatProperties.contains(format)) {
      Sequence[] s;
      if (FormatProperties.indexOf(format) == FormatProperties.MSF) {
        
        MSFfile msf = new MSFfile(inStr);
        s = new Sequence[msf.seqs.size()];
        for (int i=0;i < msf.seqs.size();i++) {
          s[i] = (Sequence)msf.seqs.elementAt(i);
        }
        return s;
      } else  if (FormatProperties.indexOf(format) == FormatProperties.FASTA) {
        FastaFile msf = new FastaFile(inStr);
        s = new Sequence[msf.seqs.size()];
        for (int i=0;i < msf.seqs.size();i++) {
          s[i] = (Sequence)msf.seqs.elementAt(i);
        }
        return s;
        
      } else  if (FormatProperties.indexOf(format) == FormatProperties.CLUSTAL) {
        ClustalFile msf = new ClustalFile(inStr);
        s = new Sequence[msf.seqs.size()];
        for (int i=0;i < msf.seqs.size();i++) {
          s[i] = (Sequence)msf.seqs.elementAt(i);
        }
        return s;
        
      } else  if (FormatProperties.indexOf(format) == FormatProperties.PIR) {
        PIRFile msf = new PIRFile(inStr);
        s = new Sequence[msf.seqs.size()];
        for (int i=0;i < msf.seqs.size();i++) {
          s[i] = (Sequence)msf.seqs.elementAt(i);
        }
        return s;
        
      } else  if (FormatProperties.indexOf(format) == FormatProperties.BLC) {
        BLCFile msf = new BLCFile(inStr);
        s = new Sequence[msf.seqs.size()];
        for (int i=0;i < msf.seqs.size();i++) {
          s[i] = (Sequence)msf.seqs.elementAt(i);
        }
        return s;
      } else  if (FormatProperties.indexOf(format) == FormatProperties.MSP) {
        MSPFile msf = new MSPFile(inStr);
        s = new Sequence[msf.seqs.size()];
        for (int i=0;i < msf.seqs.size();i++) {
          s[i] = (Sequence)msf.seqs.elementAt(i);
        }
        return s;
      } else  if (FormatProperties.indexOf(format) == FormatProperties.PFAM) {
        PfamFile msf = new PfamFile(inStr);
        s = new Sequence[msf.seqs.size()];
        for (int i=0;i < msf.seqs.size();i++) {
          s[i] = (Sequence)msf.seqs.elementAt(i);
        }
        return s;
      } else  if (FormatProperties.indexOf(format) == FormatProperties.POSTAL) {
        PostalFile msf = new PostalFile(inStr);
        s = new Sequence[msf.seqs.size()];
        for (int i=0;i < msf.seqs.size();i++) {
          s[i] = (Sequence)msf.seqs.elementAt(i);
        }
        return s;
      } else  if (FormatProperties.indexOf(format) == FormatProperties.JNET) {
        JnetFile msf = new JnetFile(inStr);
        s = new Sequence[msf.seqs.size()];
        for (int i=0;i < msf.seqs.size();i++) {
          s[i] = (Sequence)msf.seqs.elementAt(i);
        }
        return s;
        
      }
    } else {
      // Should throw exception
      return null;
    }
    return null;
  }
  public static Sequence[] read(String infile, String type, String format) {
    System.out.println("In FormatAdapter");
    if (FormatProperties.contains(format)) {
      try {
        Sequence[] s;
        if (FormatProperties.indexOf(format) == FormatProperties.MSF) {
          
          MSFfile msf = new MSFfile(infile,type);
          s = new Sequence[msf.seqs.size()];
          for (int i=0;i < msf.seqs.size();i++) {
            s[i] = (Sequence)msf.seqs.elementAt(i);
          }
          return s;
        } else  if (FormatProperties.indexOf(format) == FormatProperties.FASTA) {
          System.out.println("In fasta");
          FastaFile msf = new FastaFile(infile,type);
          System.out.println("Size = " + msf.seqs.size());
          s = new Sequence[msf.seqs.size()];
          for (int i=0;i < msf.seqs.size();i++) {
            s[i] = (Sequence)msf.seqs.elementAt(i);
          }
          return s;
          
        } else  if (FormatProperties.indexOf(format) == FormatProperties.CLUSTAL) {
          ClustalFile msf = new ClustalFile(infile,type);
          s = new Sequence[msf.seqs.size()];
          for (int i=0;i < msf.seqs.size();i++) {
            s[i] = (Sequence)msf.seqs.elementAt(i);
          }
          return s;
          
        } else  if (FormatProperties.indexOf(format) == FormatProperties.PIR) {
          PIRFile msf = new PIRFile(infile,type);
          s = new Sequence[msf.seqs.size()];
          for (int i=0;i < msf.seqs.size();i++) {
            s[i] = (Sequence)msf.seqs.elementAt(i);
          }
          return s;
          
        } else  if (FormatProperties.indexOf(format) == FormatProperties.BLC) {
          BLCFile msf = new BLCFile(infile,type);
          s = new Sequence[msf.seqs.size()];
          for (int i=0;i < msf.seqs.size();i++) {
            s[i] = (Sequence)msf.seqs.elementAt(i);
          }
          return s;
        } else  if (FormatProperties.indexOf(format) == FormatProperties.MSP) {
          MSPFile msf = new MSPFile(infile,type);
          s = new Sequence[msf.seqs.size()];
          for (int i=0;i < msf.seqs.size();i++) {
            s[i] = (Sequence)msf.seqs.elementAt(i);
          }
          return s;
        } else  if (FormatProperties.indexOf(format) == FormatProperties.PFAM) {
          PfamFile msf = new PfamFile(infile,type);
          s = new Sequence[msf.seqs.size()];
          for (int i=0;i < msf.seqs.size();i++) {
            s[i] = (Sequence)msf.seqs.elementAt(i);
          }
          return s;
        } else  if (FormatProperties.indexOf(format) == FormatProperties.POSTAL) {
          PostalFile msf = new PostalFile(infile,type);
          s = new Sequence[msf.seqs.size()];
          for (int i=0;i < msf.seqs.size();i++) {
            s[i] = (Sequence)msf.seqs.elementAt(i);
          }
          return s;
        } else  if (FormatProperties.indexOf(format) == FormatProperties.JNET) {
          JnetFile msf = new JnetFile(infile,type);
          s = new Sequence[msf.seqs.size()];
          for (int i=0;i < msf.seqs.size();i++) {
            s[i] = (Sequence)msf.seqs.elementAt(i);
          }
          return s;
          
        }
      } catch (IOException e) {
        System.out.println("IOException " + e + " in FormatAdapter");
      }
    } else {
      // Should throw exception
      return null;
    }
    return null;
  }
  
  public static DrawableSequence[] toDrawableSequence(Sequence[] s) {
    System.out.println("In FormatAdapter " + s.length);
    DrawableSequence[] ds = new DrawableSequence[s.length];
    int i=0;
    while (i < ds.length && s[i] != null) {
      ds[i] = new DrawableSequence(s[i]);
      i++;
    }
    return ds;
  }
  
  public static void main(String[] args) {
    Sequence[] s  = FormatAdapter.read(args[0],"File",args[1]);
    
    System.out.println(FormatAdapter.get(args[2],s));
  }
}






