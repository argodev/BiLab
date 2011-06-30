/*
 *
 * created: Wed Aug 3 2004
 *
 * This file is part of Artemis
 *
 * Copyright(C) 2000  Genome Research Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package uk.ac.sanger.artemis.editor;

import java.awt.Point;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.StringReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.MalformedURLException;

import uk.ac.sanger.artemis.util.WorkingGZIPInputStream;

public class FastaTextPane extends JScrollPane
{
  private JTextArea textArea;
  private Vector hitInfoCollection = null;
  private String format = null;
  private String dataFile;
  private int qlen;
  private Vector listerners = new Vector();
  private Vector threads = new Vector();

  public FastaTextPane(String dataFile)
  {
    super();
    //read fasta file

    this.dataFile = dataFile;
    format = getResultsFormat();
    StringBuffer contents = null;

    if(format.equals("fasta"))
      contents = readFASTAFile(format);
    else if(format.equals("blastp"))
      contents = readBLASTPFile(format);

    textArea = new JTextArea(contents.toString());
    setTextAreaFont(BigPane.font);
    textArea.setEditable(false);

    setViewportView(textArea);
    setPreferredSize(new Dimension(500,300));
  }

  protected void addFastaListener(FastaListener obj)
  {
    listerners.add(obj);
  }

  protected void reRead()
  {
    StringBuffer contents = null;

    format = getResultsFormat();
    if(format.equals("fasta"))
      contents = readFASTAFile(format);
    else if(format.equals("blastp"))
      contents = readBLASTPFile(format);

    textArea.setText(contents.toString());
    setViewportView(textArea);

    Enumeration enumListeners = listerners.elements();
    while(enumListeners.hasMoreElements())
      ((FastaListener)enumListeners.nextElement()).update();
  }

  /**
  *
  * Get the format of the results (e.g. FASTA or BLASTP).
  * @return format
  *
  */
  protected String getFormat()
  {
    return format;
  }

  protected void setTextAreaFont(Font f)
  {
    textArea.setFont(f);
  }
  
  protected InputStream getInputStream()
            throws IOException
  {
    FileInputStream inStream = new FileInputStream(dataFile);
    if(dataFile.endsWith(".gz"))
      return new WorkingGZIPInputStream(inStream);
    else
      return inStream;
  }

  /**
  *
  * Get the format of the results in a file. FASTA and
  * BLASTP are supported.
  *
  */
  protected String getResultsFormat()
  {
    File fn = new File(dataFile);

    if(!fn.exists())
    {
      dataFile = dataFile+".gz";
      fn = new File(dataFile);
    }

    InputStreamReader streamReader = null;
    BufferedReader buffReader = null;
    String line = null;
    String format = null;

    try
    {
      streamReader = new InputStreamReader(getInputStream());
      buffReader = new BufferedReader(streamReader);
      while( (line = buffReader.readLine()) != null)
      {
        if(line.startsWith("BLASTP"))
        {
          format = "blastp";
          break;
        }
        else if(line.indexOf("FASTA") > -1)
        {
          format = "fasta";
          break;
        }
      }
      streamReader.close();
      buffReader.close();
    }
    catch(IOException ioe)
    {
      System.out.println("Cannot read file: " + fn.getAbsolutePath() );
    }
    
    return format;
  }


  protected StringBuffer readBLASTPFile(String format)
  {
    File fn = new File(dataFile);
    StringBuffer sbuff = new StringBuffer();

    InputStreamReader streamReader = null;
    BufferedReader buffReader = null;

    hitInfoCollection = new Vector();
    try
    {
      streamReader = new InputStreamReader(getInputStream());
      buffReader = new BufferedReader(streamReader);

      String line = null;
      int textPosition = 0;
      int len     = 0;
      HitInfo hit = null;
      int ind1 = 0;
      int endQuery = 0;

      while( (line = buffReader.readLine()) != null)
      {
        len = line.length()+1;
        sbuff.append(line+"\n");
        if(line.startsWith("Sequences producing significant alignments:"))
        {
          buffReader.readLine();
          while( !(line = buffReader.readLine()).equals("") )
          {
            textPosition += line.length()+1;
            sbuff.append(line+"\n");
  
            hit = new HitInfo(line,format);

            hitInfoCollection.add(hit);
          }
        }
        else if(line.indexOf(" ----") > -1 ||
                line.indexOf(" --- ") > -1 ||
                line.indexOf(" -- ") > -1 )
        {
        }
        else if(line.startsWith(">"))  // start of alignment
        {
          String currentID = line;

          int ind = line.indexOf(" ");
          if(ind > -1)
          {
            currentID = line.substring(1,ind);
            int indDot;
            if( (indDot = currentID.indexOf(".")) > 5)
            {
              String version = currentID.substring(indDot+1);
              try
              {
                int version_no = Integer.parseInt(version);
                // version number looks like uniprot so strip this
                if(version_no < 50)
                  currentID= currentID.substring(0,indDot);
              }
              catch(NumberFormatException nfe){}
            }
          }

          int ind2 = currentID.indexOf(":");
          if(ind2 > -1)
          {
            currentID = currentID.substring(ind2+1); 
          }

          if(hit != null)
            hit.setEndPosition(textPosition);

          hit = getHitInfo(currentID,hitInfoCollection);
          hit.setStartPosition(textPosition);

          String going = "";
          ind = line.indexOf("GO:");
          if(ind > -1)
            going = line.substring(ind+3);
          
          String nextLine = null;
//        buffReader.mark(210);

// get GO numbers
          while((nextLine = buffReader.readLine()).indexOf("Length") == -1)
          {
            len += nextLine.length()+1;
            sbuff.append(nextLine+"\n");
            if(going.equals("") && ((ind = nextLine.indexOf("GO:")) > -1))
              going = nextLine.substring(ind+3);
            else if(!going.equals(""))
              going = going.concat(nextLine);
          }
          
          if(!going.equals(""))
            hit.setGO(going);

          if(nextLine != null)
          {
            len += nextLine.length()+1;
            sbuff.append(nextLine+"\n");
            if( (ind1 = nextLine.indexOf("  Length = ")) > -1)
              hit.setLength(nextLine.substring(ind1+11));
          }

// get query start
          int start = 999999;
          int end   = 0;
          boolean seen = false;

          while((nextLine = buffReader.readLine()) != null &&
                !nextLine.startsWith(">"))
          {
            len += nextLine.length()+1;
            sbuff.append(nextLine+"\n");

            if(nextLine.startsWith(" Score ="))
            {
//            System.out.println("start:: "+start+"  end:: "+end);
              if(end != 0)
  //              hit.setQueryPosition(start,end);
              start = 999999;
              end   = 0;
              seen  = true;
            }
            else if(nextLine.startsWith("Query:"))
            {
              ind1 = nextLine.indexOf(" ",8);
              int nstart = Integer.parseInt(nextLine.substring(7,ind1).trim());
              if(nstart < start)
                start = nstart;
              end  = Integer.parseInt(nextLine.substring(nextLine.lastIndexOf(" ")).trim());
              seen = false;
            }
            buffReader.mark(100); 
          }
          if(!seen && end != 0)
//            hit.setQueryPosition(start,end);

          buffReader.reset();
        }
        else if( (ind1 = line.indexOf("Identities = ")) > -1)
        {
          ind1 = line.indexOf("(",ind1)+1;
          if(ind1 > -1)
            hit.setIdentity(line.substring(ind1,line.indexOf(")",ind1)).trim());
        }
        else if( (ind1 = line.indexOf("  Length = ")) > -1)
          hit.setLength(line.substring(ind1+11));
//      else if(line.startsWith("Query: "))
//      {
//        hit.setQueryEnd(Integer.parseInt(line.substring(line.lastIndexOf(" ")).trim()));
//      }
        else if(line.startsWith("Query="))
        {
          int ind2 = 0;
          ind1 = line.indexOf(" letters)");
          if(ind1 == -1)
          {
            String nextLine = null;
            while((nextLine = buffReader.readLine()).indexOf(" letters)") < 0)
            {
              len += nextLine.length()+1;
              sbuff.append(nextLine+"\n");
            }
            line = nextLine;
            ind1 = nextLine.indexOf(" letters)");
            ind2 = nextLine.indexOf("(");
          }
          else
            ind2 = line.indexOf("(");

          qlen = Integer.parseInt(line.substring(ind2+1,ind1).trim());
        }

        textPosition += len;
      }

      if(hit != null)
        hit.setEndPosition(textPosition);

      streamReader.close();
      buffReader.close();

      GetzThread getz = new GetzThread(hitInfoCollection);
      getz.start();
      threads.add(getz);
    }
    catch (IOException ioe)
    {
      System.out.println("Cannot read file: " + dataFile);
    }
    return sbuff;
  }


  protected StringBuffer readFASTAFile(String format)
  {
    File fn = new File(dataFile);
    StringBuffer sbuff = new StringBuffer();

    InputStreamReader streamReader = null;
    BufferedReader buffReader = null;

    hitInfoCollection = new Vector();
    try
    {
      streamReader = new InputStreamReader(getInputStream());
      buffReader = new BufferedReader(streamReader);

      String line = null;
      int textPosition = 0;
      int len    = 0;
      HitInfo hi = null;

      while( (line = buffReader.readLine()) != null)
      {
        len = line.length()+1;
        sbuff.append(line+"\n");  

        int ind1;

        if(line.endsWith(" aa"))
        {
          String tmp = line.substring(0,line.length()-3).trim();
          int in1 = tmp.lastIndexOf(" ");
          if(in1 > -1)
            qlen = Integer.parseInt(tmp.substring(in1).trim());
        }
        else if(line.startsWith("The best scores are:"))
        {
          while( !(line = buffReader.readLine()).equals("") )
          {
            textPosition += line.length()+1;
            sbuff.append(line+"\n");
            hitInfoCollection.add(new HitInfo(line,format)); 
          }
        }
        else if(line.startsWith(">>"))  // start of alignment
        {
          int ind = line.indexOf(" ");
          String currentID = line.substring(2,ind);
 
          int indDot;
          if( (indDot = currentID.indexOf(".")) > 5)
          {
            String version = currentID.substring(indDot+1);
            try
            {
              int version_no = Integer.parseInt(version);
              // version number looks like uniprot so strip this
              if(version_no < 50)
                currentID= currentID.substring(0,indDot);
            }
            catch(NumberFormatException nfe){}

            // HERE currentID= currentID.substring(0,indDot);
          }

          if(hi != null)
            hi.setEndPosition(textPosition);

          hi = getHitInfo(currentID,hitInfoCollection);
          hi.setStartPosition(textPosition);
        }
        else if(line.startsWith("Smith-Waterman")) // Smith-Waterman
        {
          ind1 = line.indexOf("score:");
          int ind2;
          if(ind1 > -1)
          {
            ind2 = line.indexOf(";",ind1);

            hi.setScore(line.substring(ind1+6,ind2));
     
            ind1 = ind2+1;
            ind2 = line.indexOf("identity");
            if(ind2 > -1)
              hi.setIdentity(line.substring(ind1,ind2).trim());
          
            ind1 = line.indexOf("(",ind2);
            if(ind1 > -1)
            {
              ind2 = line.indexOf("ungapped)",ind1);
              if(ind2 > -1)
                hi.setUngapped(line.substring(ind1+1,ind2).trim());
            }

            ind1 = line.indexOf(" in ",ind2);
            ind2 = line.indexOf("(",ind1);
            if(ind1 > -1 && ind2 > -1)
              hi.setOverlap(line.substring(ind1+4,ind2).trim());
           
            ind1 = ind2+1;
            ind2 = line.indexOf(":",ind1);
            if(ind2 > -1)
            {
              String range = line.substring(ind1,ind2);
              hi.setQueryRange(range);
              int split = range.indexOf("-");
              if(split > -1)
              {   
//              hi.setQueryStart(Integer.parseInt(range.substring(0,split)));
//              hi.setQueryEnd(Integer.parseInt(range.substring(split+1)));
  //              hi.setQueryPosition(Integer.parseInt(range.substring(0,split)),
  //                                  Integer.parseInt(range.substring(split+1)));
              }
            }

            ind1 = ind2+1;
            ind2 = line.indexOf(")",ind1);
            if(ind2 > -1)
              hi.setSubjectRange(line.substring(ind1,ind2)); 
          }
        }
        else if( (ind1 = line.indexOf(" E():")) > -1)
        {
          StringTokenizer tok = new StringTokenizer(line.substring(ind1+5));
          hi.setEValue(tok.nextToken().trim());
        }
 
        textPosition += len;
      }
  
      if(hi != null)
        hi.setEndPosition(textPosition);
   
      streamReader.close();
      buffReader.close();

      GetzThread getz = new GetzThread(hitInfoCollection);
      getz.start();
      threads.add(getz);
    }
    catch (IOException ioe)
    {
      System.out.println("Cannot read file: " + dataFile);
    }
    return sbuff;
  }

  /**
  *
  * Get the query sequence length.
  * @return query sequence length.
  *
  */
  protected int getQueryLength()
  {
    return qlen;
  }

  protected Vector getHitCollection()
  {
    return hitInfoCollection;
  }


  private static HitInfo getHitInfo(String acc, Vector hits)
  {
    int ind = 0;
    acc     = acc.trim();

    if((ind = acc.indexOf(";")) > -1)
      acc = acc.substring(0,ind);

    Enumeration ehits = hits.elements();
    HitInfo hit = null;
    while(ehits.hasMoreElements())
    {
      hit = (HitInfo)ehits.nextElement();
      if(hit.getAcc().equals(acc) ||
         hit.getID().equals(acc))
        return hit;
    }

    return null;
  }

   
  /**
  *
  * Stop all getz processes
  *
  */
  protected void stopGetz()
  {
    Enumeration threadEnum = threads.elements();

    while(threadEnum.hasMoreElements())
    {
      GetzThread gthread = (GetzThread)threadEnum.nextElement();
      if(gthread.isAlive())
        gthread.stopMe();
    }
  }

  class GetzThread extends Thread
  {
    private Vector hitInfoCollection;
    private boolean keepRunning = true;

    protected GetzThread(Vector hitInfoCollection)
    {
      this.hitInfoCollection = hitInfoCollection;
    }

    public void run()
    {
//    int max = hitInfoCollection.size();
//    if(max > 10)
//      max = 10;

//    for(int i=0; i<max; i++)
//      DataCollectionPane.getzCall((HitInfo)hitInfoCollection.get(i),false);

//    DataCollectionPane.getzCall(hitInfoCollection,hitInfoCollection.size());
      getzCall(hitInfoCollection,hitInfoCollection.size());
    }

    protected void stopMe()
    {
      keepRunning = false;
    }

    /**
    *
    * Creates and executes an SRS query for all the hits in the
    * collection.
    * @param hit          HitInfo for a single hit.
    * @param ortholog     true if ortholog is selected.
    *
    */
    private void getzCall(final Vector hits, final int nretrieve)
    {
      final String env[] = { "PATH=/usr/local/pubseq/bin/" };

      StringBuffer query = new StringBuffer();

      int n = 0;

      Enumeration ehits = hits.elements();
      while(ehits.hasMoreElements() && keepRunning)
      {
        if(n>nretrieve)
          break;
        HitInfo hit = (HitInfo)ehits.nextElement();
        if(n > 0)
          query.append("|");

        query.append(hit.getAcc());
        n++;
      }
       
      BufferedReader strbuff = null;
      File fgetz = new File("/usr/local/pubseq/bin/getz");
      if(!fgetz.exists())
      {
        try
        {
          URL wgetz = new URL(DataCollectionPane.srs_url+
                             "/wgetz?-f+acc%20org%20description%20gen+[uniprot-acc:"+
                             query.toString()+"]+-lv+500");
          InputStream in = wgetz.openStream();

          strbuff = new BufferedReader(new InputStreamReader(in));
          StringBuffer resBuff = new StringBuffer();
          String line;
          while((line = strbuff.readLine()) != null)
            resBuff.append(line);
   
          strbuff.close();
          in.close();

          String res = resBuff.toString();
          res= insertNewline(res, "OS ");
          res= insertNewline(res, "DE ");
          res= insertNewline(res, "GN ");
          res= insertNewline(res, "AC ");

          StringReader strread   = new StringReader(res);
          strbuff = new BufferedReader(strread);

//        System.out.println(DataCollectionPane.srs_url+
//                           "/wgetz?-f+acc%20org%20description%20gen+[uniprot-acc:"+
//                           query.toString()+"]+-lv+500");
//        System.out.println("HERE\n"+res);
        }
        catch(MalformedURLException e) {System.err.println(e);}
        catch(IOException e) {System.err.println(e);} 
      }
      else
      {
        String cmd[]   = { "getz", "-f", "acc org description gen",
                           "[uniprot-acc:"+query.toString()+"]" };
                      
        ExternalApplication app = new ExternalApplication(cmd,
                                                   env,null);
        String res = app.getProcessStdout();
        StringReader strread   = new StringReader(res);
        strbuff = new BufferedReader(strread);
      }             

      HitInfo hit = null;
      String line = null;
      String lineStrip = null;

      try             
      { 
        while((line = strbuff.readLine()) != null)
        { 
          line = line.trim();
          if(line.equals(""))
            continue; 
           
          if(line.length() < 3)  // empty description line
            continue;
        
          lineStrip = line.substring(3).trim();
          if(line.startsWith("AC "))
          {           
            hit = getHitInfo(lineStrip,hits);
                      
            if(hit == null)
            {         
              System.out.println("HIT NOT FOUND "+line);
              continue;
            }         
                      
            hit.setOrganism("");
            hit.setGeneName("");
          }           
                      
          if(hit == null)
            continue; 
                      
          if(line.startsWith("OS "))
            hit.setOrganism(lineStrip);
          else if(line.startsWith("DE "))
            hit.appendDescription(lineStrip);
          else if(line.startsWith("GN "))
          {           
            StringTokenizer tokGN = new StringTokenizer(lineStrip,";");
            while(tokGN.hasMoreTokens())
            {         
              line = tokGN.nextToken();
              if(line.startsWith("Name="))
                hit.setGeneName(line.substring(5));
//            else    
//              hit.appendDescription(line);
            }         
          }           
        }

        strbuff.close();   
      }   
      catch(IOException ioe){}

      String res = null;
      ehits = hits.elements();
      while(ehits.hasMoreElements() && keepRunning)
      {               
        hit = (HitInfo)ehits.nextElement();
        res = getUniprotLinkToDatabase(fgetz, hit, env, "EMBL");
              
        int ind1 = res.indexOf("ID ");
        if(ind1 > -1) 
        {             
          StringTokenizer tok = new StringTokenizer(res);
          tok.nextToken();
          hit.setEMBL(tok.nextToken());
        }             
        else          
          hit.setEMBL("");
                      
        // EC_number  
  //      if(hit.getEC_number() != null)
          continue;   

   //     res = getUniprotLinkToDatabase(fgetz, hit, env, "enzyme");

  //      ind1 = res.indexOf("ID ");
  //      if(ind1 > -1) 
  //      {             
 //         StringTokenizer tok = new StringTokenizer(res);
 //         tok.nextToken();
 //         hit.setEC_number(tok.nextToken());
        }             
      }               
    }              

 // }

  protected static String insertNewline(String s1, String s2)
  {
    int index = 0;
    while((index = s1.indexOf(s2, index)) > -1)
    {
      if(index > 0)
        s1 = s1.substring(0,index-1)+"\n"+s1.substring(index);
      index++;
    }
    return s1;
  }

  /**
  *
  * Link Uniprot to the another database (e.g. EMBL or ENZYME)
  *
  */
  protected static String getUniprotLinkToDatabase(File fgetz, HitInfo hit,
                                                  String env[], String DB)
  {
    String res = null;
    if(!fgetz.exists())
    {
      try
      {
        URL wgetz = new URL(DataCollectionPane.srs_url+
                         "/wgetz?-f+id+[uniprot-acc:"+hit.getAcc()+"]%3E"+DB);

        InputStream in = wgetz.openStream();
        BufferedReader strbuff = new BufferedReader(new InputStreamReader(in));
        StringBuffer resBuff = new StringBuffer();
        String line;
        while((line = strbuff.readLine()) != null)
          resBuff.append(line);
   
        strbuff.close();
        in.close();

        res = resBuff.toString();

        if(res.indexOf("SRS error") > -1)
          return "";
 
//      System.out.println(DataCollectionPane.srs_url+
//                         "/wgetz?-f+id+[uniprot-acc:"+hit.getAcc()+"]%3E"+DB);
      }
      catch(MalformedURLException e) {System.err.println(e);}
      catch(IOException e) {System.err.println(e);}

    }
    else
    {
      String cmd3[] = { "getz", "-f", "id",
             "[libs={uniprot}-acc:"+hit.getID()+"]>"+DB };
      ExternalApplication app = new ExternalApplication(cmd3,env,null);
      res = app.getProcessStdout();
    }

    return res;
  }

  public void show(Object obj)
  {
    if(obj instanceof HitInfo)
    {
      HitInfo hit = (HitInfo)obj;

      int start = hit.getStartPosition();
      int end   = hit.getEndPosition();
//    textArea.moveCaretPosition(end);
      textArea.moveCaretPosition(start);

      Point pos  = getViewport().getViewPosition();
      Dimension rect = getViewport().getViewSize();
      double hgt = rect.getHeight()+pos.getY();
      pos.setLocation(pos.getX(),hgt);
      getViewport().setViewPosition(pos);
    }
  }
 

}

