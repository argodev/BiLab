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



import java.io.*;



public interface OutputGenerator {



  public MailProperties getMailProperties();

  public PostscriptProperties getPostscriptProperties();

  public FileProperties getFileProperties();



  public void setMailProperties(MailProperties mp);

  public void setPostscriptProperties(PostscriptProperties pp);

  public void setFileProperties(FileProperties fp);



  public String getText(String format);

  public void getPostscript(BufferedWriter bw);

  public void getPostscript(PrintStream ps);

  public StringBuffer getPostscript();

}

  

