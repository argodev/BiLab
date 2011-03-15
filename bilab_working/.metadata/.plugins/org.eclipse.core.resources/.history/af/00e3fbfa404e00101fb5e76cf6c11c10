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

import java.io.IOException;
import java.io.Writer;


/// a INotifier that outputs to a Writer
public class WriterNotifier implements INotifier
{
  public WriterNotifier(Writer w)
  {  
    this.w = w; 
  }
  
  
  public void StartProgress(Object from, String task) 
  {
    try {
      String s = task+" [";
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
    }
  }
  
  public void Progress(Object from, double percent)
  {
    try {
      String s = ".."+percent+"%%";
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
    }
  }
  
  public void EndProgress(Object from)
  {
    try {
      String s = "..] done."+EOL;
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
    }
  }
  
  
  public void PushLevel(Object from)
  {
  }
  
  public void PopLevel(Object from)
  {
  }
  
  
  public void UserStatus(Object from, String message)
  {
    String s = "status: "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void UserInfo(Object from, String message)
  {
    String s = "info: "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void UserWarning(Object from, String message)
  {
    String s = "warning: "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void UserError(Object from, String message)
  {
    String s = "error: "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  
  public void DevStatus(Object from, String message)
  {
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();
    String s = "dev status: "+className+" - "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void DevInfo(Object from, String message)
  {
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();
    String s = "dev info: "+className+" - "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void DevWarning(Object from, String message)
  {
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();
    String s = "dev warning: "+className+" - "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void DevError(Object from, String message)
  {
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();
    String s = "dev error: "+className+" - "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  
  public void LogInfo(Object from, String message)
  {
    String s = "log info: "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void LogError(Object from, String message)
  {
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();
    String s = "log error: "+className+" - "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public static final String EOL = "\n";
  
  protected Writer w;
}
