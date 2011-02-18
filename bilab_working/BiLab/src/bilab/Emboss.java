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

import java.lang.Runtime;
import java.lang.Process;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;

import org.eclipse.core.runtime.Path;


// API wrappers for EMBOSS command-line tools
public class Emboss
{
  
  //
  // Alignment consensus group
  
  
  public static seq cons(Alignment a)
  {
    Notify.unimplemented(Emboss.class);
    return null;
  }
  
  //
  // Alignment differences
  
  
  //
  // Alignment dot plots
  
  @Summary("thresholded dotplot of two sequences")
  public static picture dotmatcher(seq sa, seq sb, int windowSize, int threshold)
  {
    String result = "";
    try {
      // first convert the two sequences to FASTA format
      boolean existingResourceAAvailable = ExternalApps.isInputResourceAvailable(sa, "FASTA");
      String resourceNameA = ExternalApps.getInputResource(sa, "FASTA");
      
      boolean existingResourceBAvailable = ExternalApps.isInputResourceAvailable(sb, "FASTA");
      String resourceNameB = ExternalApps.getInputResource(sb, "FASTA");

      String outputResourceName = BilabPlugin.uniqueTemporaryResourceName("PNG");

      
      // construct command line and execute
      List<String> args = new LinkedList<String>();
      args.add( "fasta::"+Util.toNativePathSeparator(resourceNameA) );
      args.add( "fasta::"+Util.toNativePathSeparator(resourceNameB) );
      args.add("-auto");
      args.add("-goutfile"); args.add(Util.toNativePathSeparator(outputResourceName));
      args.add("-graph"); args.add("win3"); // win3 for now, until png works
      //args.add("-xygraph"); args.add("png");
      args.add("-windowsize"); args.add(""+windowSize);
      args.add("-threshold"); args.add(""+threshold);
      
      result = exec("dotmatcher",args);
      
      // delete the any temporary resource file created
      try {
        if (!existingResourceAAvailable)
          BilabPlugin.deleteResource(resourceNameA);
      } catch (IOException e) {} // ignore deletion failure
      
      // delete the any temporary resource file created
      try {
        if (!existingResourceBAvailable)
          BilabPlugin.deleteResource(resourceNameB);
      } catch (IOException e) {} // ignore deletion failure
      
      
      //  now read in the output file
      //picture plot = (picture)Util.readResource(outputResourceName, "PNG").value;
      
      //return plot;
      return null;
    } catch (Exception e) {
      Notify.logError(Emboss.class,"dotmatcher:"+result);
      throw new BilabException("error executing dotmatcher - "+e);
    }
  }
  
  
  
  
  
  
  
  //
  //
  
  @Summary("predicts potentially antigenic regions of a protein sequence, using the method of Kolaskar and Tongaonkar")
  @Doc("file:EMBOSS/doc/html/antigenic.html")
  public static scigol.Any antigenic(seq sequence, int minLength, String format)
  {
    // call the command-line executable and get the result
   
    // first get a resource file containing the input
    boolean existingResourceAvailable = ExternalApps.isInputResourceAvailable(sequence, "FASTA");
    String resourceName = ExternalApps.getInputResource(sequence, "FASTA");

    // construct command line and execute
    List<String> args = new LinkedList<String>();
    args.add("-auto");
    args.add("-minlen"); args.add(""+minLength);
    args.add("-rformat"); args.add(format);
    args.add( "fasta::"+Util.toNativePathSeparator(resourceName) );

    String result = exec("antigenic",args);

    // delete the any temporary resource file created
    try {
      if (!existingResourceAvailable)
        BilabPlugin.deleteResource(resourceName);
    } catch (IOException e) {} // ignore deletion failure
   

    if (format.equalsIgnoreCase("gff")) {
      //!!! this should make a copy of the original sequence first
      
      sequence.addGFFFeatures(result);
      return new scigol.Any(sequence);
    }
    else
      return new scigol.Any(filterEMBOSSReportOutput(result));
  }
  
  
  
  
  @Summary("Protein proteolytic enzyme or reagent cleavage digest")
  @Doc("file:EMBOSS/doc/html/digest.html")
  public static String digest(seq sequence, int reagent, boolean unfavoured, boolean overlap, boolean allpartials)
  {
    // call the command-line executable and get the result
   
    // first get a resource file containing the input
    boolean existingResourceAvailable = ExternalApps.isInputResourceAvailable(sequence, "FASTA");
    String resourceName = ExternalApps.getInputResource(sequence, "FASTA");

    // construct command line and execute
    List<String> args = new LinkedList<String>();
    args.add("-auto");
    args.add("-menu"); args.add(""+reagent);
    if (unfavoured) args.add("-unfavoured");
    if (overlap) args.add("-overlap");
    if (allpartials) args.add("-allpartials");
    args.add( "fasta::"+Util.toNativePathSeparator(resourceName) );

    String result = exec("digest",args);
    
    // delete the any temporary resource file created
    try {
      if (!existingResourceAvailable)
        BilabPlugin.deleteResource(resourceName);
    } catch (IOException e) {} // ignore deletion failure
    
    return filterEMBOSSReportOutput(result);
  }
  
  
  
  @Summary("Protein pattern search (PROSITE-style)")
  @Doc("file:EMBOSS/doc/html/fuzzpro.html")
  public static String fuzzpro(seq sequence, String pattern, int mismatch)
  {
    // call the command-line executable and get the result
   
    // first get a resource file containing the input
    boolean existingResourceAvailable = ExternalApps.isInputResourceAvailable(sequence, "FASTA");
    String resourceName = ExternalApps.getInputResource(sequence, "FASTA");

    // construct command line and execute
    List<String> args = new LinkedList<String>();
    args.add("-auto");
    args.add("-mismatch"); args.add(""+mismatch);
    args.add("-pattern"); args.add(pattern);
    args.add( "fasta::"+Util.toNativePathSeparator(resourceName) );

    String result = exec("fuzzpro",args);
    
    // delete the any temporary resource file created
    try {
      if (!existingResourceAvailable)
        BilabPlugin.deleteResource(resourceName);
    } catch (IOException e) {} // ignore deletion failure
    
    return filterEMBOSSReportOutput(result);
  }
  
  
  
  @Summary("Protein pattern search after translation (PROSITE-style)")
  @Doc("file:EMBOSS/doc/html/fuzztran.html")
  public static String fuzztran(seq sequence, String pattern, String frame, int code, int mismatch)
  {
    // call the command-line executable and get the result
   
    // first get a resource file containing the input
    boolean existingResourceAvailable = ExternalApps.isInputResourceAvailable(sequence, "FASTA");
    String resourceName = ExternalApps.getInputResource(sequence, "FASTA");

    // construct command line and execute
    List<String> args = new LinkedList<String>();
    args.add("-auto");
    args.add("-mismatch"); args.add(""+mismatch);
    args.add("-pattern"); args.add(pattern);
    args.add("-frame"); args.add(frame);
    args.add("-table"); args.add(""+code);
    if (sequence instanceof protein) args.add("-sprotein1");
    args.add( "fasta::"+Util.toNativePathSeparator(resourceName) );

    String result = exec("fuzztran",args);
    
    // delete the any temporary resource file created
    try {
      if (!existingResourceAvailable)
        BilabPlugin.deleteResource(resourceName);
    } catch (IOException e) {} // ignore deletion failure
    
    return filterEMBOSSReportOutput(result);
  }
  
  
  
  @Summary("Report nucleic acid binding motifs")
  @Doc("file:EMBOSS/doc/html/helixturnhelix.html")
  public static String helixturnhelix(seq sequence, double mean, double sd, double minsd)
  {
    // call the command-line executable and get the result
   
    // first get a resource file containing the input
    boolean existingResourceAvailable = ExternalApps.isInputResourceAvailable(sequence, "FASTA");
    String resourceName = ExternalApps.getInputResource(sequence, "FASTA");

    // construct command line and execute
    List<String> args = new LinkedList<String>();
    args.add("-auto");
    args.add("-mean"); args.add(""+mean);
    args.add("-sd"); args.add(""+sd);
    args.add("-minsd"); args.add(""+minsd);
    if (sequence instanceof protein) args.add("-sprotein1");
    args.add( "fasta::"+Util.toNativePathSeparator(resourceName) );

    String result = exec("helixturnhelix",args);
    
    // delete the any temporary resource file created
    try {
      if (!existingResourceAvailable)
        BilabPlugin.deleteResource(resourceName);
    } catch (IOException e) {} // ignore deletion failure
    
    return filterEMBOSSReportOutput(result);
  }
  
  
  @Summary("Reports protein signal cleavage sites")
  @Doc("file:EMBOSS/doc/html/sigcleave.html")
  public static String sigcleave(seq sequence, double minWeight, boolean prokaryote)
  {
    if (!(sequence instanceof protein))
      throw new BilabException("a protein sequence is required");
    
    // call the command-line executable and get the result
   
    // first get a resource file containing the input
    boolean existingResourceAvailable = ExternalApps.isInputResourceAvailable(sequence, "FASTA");
    String resourceName = ExternalApps.getInputResource(sequence, "FASTA");

    // construct command line and execute
    List<String> args = new LinkedList<String>();
    args.add("-auto");
    args.add("-minweight"); args.add(""+minWeight);
    if (prokaryote) args.add("-prokaryote");
    args.add("-sprotein1");
    args.add( "fasta::"+Util.toNativePathSeparator(resourceName) );

    String result = exec("sigcleave",args);
    
    // delete the any temporary resource file created
    try {
      if (!existingResourceAvailable)
        BilabPlugin.deleteResource(resourceName);
    } catch (IOException e) {} // ignore deletion failure
    
    return filterEMBOSSReportOutput(result);
  }
  
  
 
  
  

  
  
  
  
  //
  // helper methods
  
  
  
  
  
  
  protected static String exec(String commandName, List<String> args)
  {
    try {
      String pluginRoot = BilabPlugin.getPluginFilesystemRoot();
      
      String EMBOSSRoot = pluginRoot+Path.SEPARATOR+"EMBOSS";
      
      LinkedList<String> cmdline = new LinkedList<String>();
      cmdline.add( EMBOSSRoot+Path.SEPARATOR+commandName+ExternalApps.exeSuffix );
      cmdline.addAll( args );
      
      StringBuilder cmdlineStr = new StringBuilder();
      for(String s : cmdline) cmdlineStr.append(s+" ");
      Notify.logInfo(Emboss.class,"invoking external command:"+cmdlineStr.toString());
      
      ProcessBuilder pb = new ProcessBuilder(cmdline);
      Map<String, String> env = pb.environment();
      env.put("EMBOSSWIN", EMBOSSRoot);
      pb.directory(new File(EMBOSSRoot));
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
      
      return str.toString();
      
    } catch (Exception e) {
      throw new BilabException("unable to execute command (external EMBOSS invocation failed) - "+e);
    }
  }
  
  
  // filter out some of the unnecessary header lines from an EMBOSS report
  protected static String filterEMBOSSReportOutput(String report)
  {
    String terminator = BilabPlugin.platformOS().equals("win")?"\r\n":"\n";
    
    String[] lines = report.split(terminator);
    StringBuilder filtered = new StringBuilder();
    for(String line : lines) {
      if ((line.length() == 0) || line.charAt(0)!='#')
        filtered.append(line+terminator);
      else {
        if (   !line.startsWith("#####") 
            && !line.startsWith("# Program")
            && !line.startsWith("# Rundate")
            && !line.startsWith("# Report")
           )
          filtered.append(line+terminator);
      }
    }
    return filtered.toString();
  }
  
  
  
  
  static {
    runtime = Runtime.getRuntime();
  }
  
  protected static Runtime runtime;
  
}
