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

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;

import scigol.TypeSpec;

public class ExternalApps
{
  public static String RNAll(seq sequence, boolean nonOverlapping, boolean terminatorPrediction, int windowSize,
                             double energyThreashold, double symMapThreashold, int loopSize,
                             double tweight, double hybridEnergy, boolean produceCTFile)
  {
    try {
      // first get a resource file containing the input
      boolean existingResourceAvailable = isInputResourceAvailable(sequence, "FASTA");
      String resourceName = getInputResource(sequence, "FASTA");
      
      
      String pluginRoot = BilabPlugin.getPluginFilesystemRoot();
      String RNAllRoot = pluginRoot+Path.SEPARATOR+"RNAll";

      String outputResourceName = BilabPlugin.uniqueTemporaryResourceName("TEXT");
      
      // build command line options
      LinkedList<String> args = new LinkedList<String>();
      args.add("-i"); args.add(Util.toNativePathSeparator(resourceName));
      args.add("-o"); args.add(Util.toNativePathSeparator(outputResourceName));
      args.add("-g");
      if (nonOverlapping) args.add("-f");
      if (terminatorPrediction) args.add("-t");
      args.add("-w"); args.add(""+windowSize);
      args.add("-e"); args.add(""+energyThreashold);
      args.add("-s"); args.add(""+symMapThreashold);
      args.add("-l"); args.add(""+loopSize);
      args.add("-u"); args.add(""+tweight);
      args.add("-h"); args.add(""+hybridEnergy);
      if (produceCTFile) args.add("-c");
      
      String result = exec("RNAll", RNAllRoot, args);
      
      
      // delete the any temporary resource file created
      try {
        if (!existingResourceAvailable)
          BilabPlugin.deleteResource(resourceName);
      } catch (IOException e) {} // ignore deletion failure
      
      
      // now read in the output files
      InputStream rnallStructFile = BilabPlugin.findResourceStream(outputResourceName);
      BufferedReader rnallStructReader = new BufferedReader(new InputStreamReader(rnallStructFile));
      StringBuilder structString = new StringBuilder();
      String line;
      do {
        line = rnallStructReader.readLine();
        if (line != null) structString.append(line+"\n");
      } while (line != null);
      rnallStructFile.close();
      
      // now delete the output file created
      try {
        BilabPlugin.deleteResource(outputResourceName);
      } catch (IOException e) {} // ignore deletion failure
      
      
      return structString.toString();
      
    } catch (IOException e) {
      throw new BilabException("IO error during RNAll invocation - "+e);
    }
    
  }
  
  
  
  public static Alignment clustalw(scigol.List sequences)
  {
    // check that the list contains either all DNA or all protein
    if (sequences.get_size() < 2)
      throw new BilabException("the sequences:list must contain at least 2 sequences (DNA or protein)");
    
    if (!(sequences.get_head().value instanceof DNA) && !(sequences.get_head().value instanceof protein))
      throw new BilabException("the sequences:list elements must must be either all DNA or all protein");
    
    scigol.TypeSpec eltType = scigol.TypeSpec.typeOf(sequences.get_head().value);
    for(int i=0; i<sequences.get_size();i++) {
      seq s = (seq)sequences.get_Item(i).value;
      scigol.TypeSpec sType = scigol.TypeSpec.typeOf(s);
      if (!sType.equals(eltType))
        throw new BilabException("the sequences:list elements must must be either all DNA or all protein");
    }

    boolean nucleotide = eltType.equals(new TypeSpec(DNA.class));
    
    
    // now construct convert all the sequences to a single multi-sequence FASTA file
    //  (we assume both DNA & protein support export to FASTA format)
    String inputResourceName = BilabPlugin.uniqueTemporaryResourceName("FASTA");
    
    OutputStream fastaOut = null;
    try {
      fastaOut = BilabPlugin.createResourceStream(inputResourceName);
      
      for(int i=0; i<sequences.get_size();i++) {
        seq s = (seq)sequences.get_Item(i).value;
        seq.exportResource(s,fastaOut, "FASTA");
      }
      
    } catch (Exception e) {
      throw new BilabException("unable to convert sequence list to multi-sequence FASTA format - "+e);
    } finally {
      try {
        if (fastaOut != null) {
          fastaOut.flush();
          fastaOut.close();
        }
      } catch (Exception e) {}
    }
    

    String result = "";
    try {
      String outputResourceName = BilabPlugin.uniqueTemporaryResourceName("CLUSTALW");
      String pluginRoot = BilabPlugin.getPluginFilesystemRoot();
      String clustalWRoot = pluginRoot+Path.SEPARATOR+"ClustalW";
      
      // now build command-line
      LinkedList<String> args = new LinkedList<String>();
      args.add("-INFILE="+Util.toNativePathSeparator(inputResourceName));
      //args.add("-ALIGN");
      args.add("-TYPE="+(nucleotide?"DNA":"PROTEIN"));
      args.add("-OUTFILE="+Util.toNativePathSeparator(outputResourceName));
      
      result = exec("clustalw", clustalWRoot, args);

      // delete the temporary input file created
      try {
        BilabPlugin.deleteResource(inputResourceName);
      } catch (IOException e) {} // ignore deletion failure
      
      
      // now read in the output file
      Alignment aln = (Alignment)Util.readResource(outputResourceName, "CLUSTALW").value;
      /*
      InputStream clustalwFile = BilabPlugin.findResourceStream(outputResourceName);
      BufferedReader clustalwFileReader = new BufferedReader(new InputStreamReader(clustalwFile));
      StringBuilder clustalwString = new StringBuilder();
      String line;
      do {
        line = clustalwFileReader.readLine();
        if (line != null) clustalwString.append(line+"\n");
      } while (line != null);
      
      clustalwFile.close();
      */
      // now delete the output file created
      try {
        BilabPlugin.deleteResource(outputResourceName);
      } catch (IOException e) {} // ignore deletion failure
      
      return aln;
      //return clustalwString.toString();
      
    } catch (IOException e) {
      throw new BilabException("IO error during clustalw execution - "+e);
    } catch (Exception e) {
      Notify.devInfo(ExternalApps.class,"clustalw:"+result);
      throw new BilabException("error during clustalw execution - "+e + ":"+result);
    }
    
  }
  
  
  
  
  // helpers
  
  
  protected static String exec(String commandName, String commandDir, List<String> args)
  {
    try {
     
      LinkedList<String> cmdline = new LinkedList<String>();
      cmdline.add( commandDir+Path.SEPARATOR+commandName+exeSuffix );
      cmdline.addAll( args );
      
      StringBuilder cmdlineStr = new StringBuilder();
      for(String s : cmdline) cmdlineStr.append(s+" ");
      Notify.logInfo(ExternalApps.class,"invoking external command:"+cmdlineStr.toString());
      
      ProcessBuilder pb = new ProcessBuilder(cmdline);
      pb.directory(new File(commandDir));
      pb.redirectErrorStream(true);
      
      Process process = pb.start();
      
      InputStream cmdResultStream = process.getInputStream();
      InputStreamReader cmdResultReader = new InputStreamReader(cmdResultStream);
      
      StringBuilder str = new StringBuilder();
      
      int c = cmdResultReader.read();
      while (c != -1) {
        str.append((char)c);
        c = cmdResultReader.read();
      }
      
      process.waitFor(); // wait until command completes
      
      cmdResultStream.close();
      
      return str.toString();
      
    } catch (Exception e) {
      throw new BilabException("unable to execute command (external command invocation failed) - "+e);
    }
  }
  
  
//check if a suitable format resource is already associated with sequence
  protected static boolean isInputResourceAvailable(seq sequence, String requiredResourceFormat)
  {
    String resourceName = sequence.get_AssociatedResource();
    if (resourceName == null) return false; // no resource associated with sequence
    List<String> typesForExtension = BilabPlugin.getResourceTypesWithExtension(Util.extension(resourceName));
    if (typesForExtension.size() == 0) return false; // unknown format of associated resource
    if (typesForExtension.size() > 1) return false; // associated resource type is ambigious based on extension (don't risk using it in-case it is the wrong type)
    
    return typesForExtension.contains(requiredResourceFormat);
  }
  
  
  // obtains the absolute path to a suitable format resource file for input to the command,
  //  or creates one if there isn't one associated with the sequence
  protected static String getInputResource(seq sequence, String requiredResourceType) 
  {
    try {
      if (isInputResourceAvailable(sequence,requiredResourceType)) // use the existing resource
        return (new URL(sequence.get_AssociatedResource())).getFile();
    } catch (MalformedURLException e) {}
   
    // create a temporary resource by exporting the sequence
    List<String> supportedResourceTypes = seq.getSupportedResourceTypes();
    if (!supportedResourceTypes.contains(requiredResourceType))
      throw new BilabException("the supplied sequence cannot be converted into a format suitable for input into the requested function");
    
    String resourceName = BilabPlugin.uniqueTemporaryResourceName(requiredResourceType);
    
    seq.exportResource(sequence, resourceName, requiredResourceType);

    return resourceName;
  }
  
  
  protected static final String exeSuffix = BilabPlugin.platformOS().equals("win")?".exe":"";

}
