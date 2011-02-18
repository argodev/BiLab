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

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import scigol.accessor;

/// convenience singleton to access to system-wide default notifier
public class Notify
{
  public Notify()
  {
  }

  @accessor
  public static INotifier get_Notifier() 
  { 
    return notifier;
  }
  
  @accessor
  public static void set_Notifier(INotifier value)
  {
    notifier=value; 
  }

  // convenience pass-through to notifier
  public static void startProgress(Object from, String task) 
  {
    notifier.StartProgress(from, task);
  }

  public static void progress(Object from, double percent)
  {
    notifier.Progress(from, percent);
  }

  public static void endProgress(Object from)
  {
    notifier.EndProgress(from);
  }

  public static void userStatus(Object from, String message)
  {
    notifier.UserStatus(from, message);
  }

  public static void userInfo(Object from, String message)
  {
    notifier.UserInfo(from, message);
  }

  public static void userWarning(Object from, String message)
  {
    notifier.UserWarning(from, message);
  }

  public static void userError(Object from, String message)
  {
    notifier.UserError(from, message);
  }

  public static void devStatus(Object from, String message)
  {
    notifier.DevStatus(from, message);
  }

  public static void devInfo(Object from, String message)
  {
    notifier.DevInfo(from, message);
  }

  public static void devWarning(Object from, String message)
  {
    notifier.DevWarning(from, message);
  }

  public static void devError(Object from, String message)
  {
    notifier.DevError(from, message);
  }

  // aliases for DevInfo
  public static void debug(Object from, String message)
  {
    devInfo(from, message);
  }

  public static void debug(String message)
  {
    devInfo(Notify.class, message);
  }
  
  // alias for DevError & throw exception
  public static void unimplemented(Object from)
  {
    devError(from, "unimplemented");
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();

    throw new BilabException("method of "+className+" invoked unimplemented");
  }

  public static void logInfo(Object from, String message)
  {
    notifier.LogInfo(from, message);
  }

  public static void logError(Object from, String message)
  {
    notifier.LogError(from, message);
  }

  private static INotifier notifier = null;
  
  static {
    if (notifier==null)
      notifier = new WriterNotifier(new BufferedWriter(new OutputStreamWriter(System.out)));
  } 
}