package MCview;

import jalview.*;

import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;

public class PDBfile extends jalview.FileParse {

  public Vector chains = new Vector();

  public PDBfile(String inFile, String inType) throws IOException {

    super(inFile,inType);
    readLines();
    parse();
  }
  public void parse() {
 
    System.out.println("Parsing");
    
    for (int i = 0; i < lineArray.size(); i++) {
      StringTokenizer str = new StringTokenizer(lineArray.elementAt(i).toString());
      if (str.hasMoreTokens()) {
	String inStr = str.nextToken();
	
	if (inStr.indexOf("ATOM") != -1) {
          try {
            myAtom tmpatom = new myAtom(str);
	    if (findChain(tmpatom.chain) != null) {
	      //  System.out.println("Adding to chain " + tmpatom.chain);
	      findChain(tmpatom.chain).atoms.addElement(tmpatom);
	    } else {
	      // System.out.println("Making chain " + tmpatom.chain);
	      PDBChain tmpchain = new PDBChain(tmpatom.chain);
	      chains.addElement(tmpchain);
	      tmpchain.atoms.addElement(tmpatom);
	    }
          } catch(NumberFormatException e) {
            System.out.println("Caught" +  e);
            System.out.println("Atom not added");
          }
	}
      }
    }
    makeResidueList();
    makeCaBondList();
    //    for (int i=0; i < chains.size() ; i++) {
    //  String pog = ((PDBChain)chains.elementAt(i)).print();
    //  System.out.println(pog);
    // }
  }
  
  public void makeResidueList() {
    for (int i=0; i < chains.size() ; i++) {
      ((PDBChain)chains.elementAt(i)).makeResidueList();
    }
  }
  public void makeCaBondList() {
    for (int i=0; i < chains.size() ; i++) {
      ((PDBChain)chains.elementAt(i)).makeCaBondList();
    }
  }
  
  public PDBChain findChain(String id) {
    for (int i=0; i < chains.size(); i++) {
      // System.out.println("ID = " + id + " " +((PDBChain)chains.elementAt(i)).id);
      if (((PDBChain)chains.elementAt(i)).id.equals(id)) {
	return (PDBChain)chains.elementAt(i);
      }
    }
    return null;
  }


  public void setChargeColours() {
    for (int i=0; i < chains.size(); i++) {
      ((PDBChain)chains.elementAt(i)).setChargeColours();
    }
  }
  
  public void setHydrophobicityColours() {
    for (int i=0; i < chains.size(); i++) {
      ((PDBChain)chains.elementAt(i)).setHydrophobicityColours();
    }
  }

  public void colourBySequence(DrawableSequence seq) {
    int max = seq.maxchain;
    if (seq.maxchain != -1) {
      ((PDBChain)chains.elementAt(max)).colourBySequence(seq);
    }
  }
    
  public void setChainColours() {
    for (int i=0; i < chains.size(); i++) {
      ((PDBChain)chains.elementAt(i)).setChainColours();
    }
  }
  public static void main(String[] args) {
    try {
      PDBfile pdb = new PDBfile("enkp1.pdb","File");
    } catch(IOException e) {
      System.out.println(e);
      System.exit(0);
    }
  }
}



