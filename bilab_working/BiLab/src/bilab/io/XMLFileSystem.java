package bilab.io;

//!!! change this to be a generic XML file system implementing IFileSystem

import java.util.*;

// import java.nio.*;
import java.io.*;
// import java.nio.channels.*;

// A file-system like store within an XML file
//  (the file must be a local memory-mappable file)
//
// There are two formats for notebook files.  Both are XML files with
//  a similar structure.  The first is a normal XML file containing all
//  the information contained in the notebook.
//  The second is a similar XML file, but with its bytes specially
//  laid-out (via padding with white-space).  It also contains some
//  extra attributed on some elements with file byte offset hints
//  to aid in quickly seeking to specific elements.
// These 'random access structured' notebook files can be directly
//  modified, in-place, efficiently by this implementation.  They
//  are marked as such via the RandomAccess="true" attribute of the
//  top-level notebook element.
// If this class is constructed with a File that is not of the RandomAccess
//  type, it will be re-written as such first.
public class XMLFileSystem
{
  // Create a file-system associated with the specified file
  //  The file will be created if is doesn't already exist)
  public XMLFileSystem(File file) throws IOException
  {
    // first see if this file is a map structured notebook file, and if not
    //  re-write it into a map structured one
    
    /*
    file.createNewFile();
    rafile = new RandomAccessFile(file, "rw");
    channel = rafile.getChannel();
    buff = channel.map(FileChannel.MapMode.READ_WRITE, 0, channel.size());
    */
    // if the size is 0, write a new notebook header
//    if (channel.size() == 0)
//      rewriteHeader();
  }
  
  // look for the mappable attribute of the notebook element and see if it is true
  boolean isMappable(File file) {
    return false;
  }

  // notebook structure information
  protected static class PageInfo {
    public PageInfo() {}
  }
  
  
  protected static class SectionInfo {
    public SectionInfo(String name)
    {
      this.name = name;
    }
    
    public String name;
    int fileOffset;
  };
  
  protected static class VersionInfo {
    public VersionInfo(String name)
    {
      versionName = name;
    }
    
    public String versionName;
  }

  ArrayList<VersionInfo> notebookInfo = new ArrayList<VersionInfo>(); 
  
  // private MappedByteBuffer buff;
  // private RandomAccessFile rafile;
  // private FileChannel channel;
}