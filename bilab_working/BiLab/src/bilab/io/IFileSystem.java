package bilab.io;

import java.io.*;

// represent a simple file system 
//  (e.g. the OS file system, a zip 'file-system', etc.)
public interface IFileSystem
{
  public boolean      exists(String path);
  public boolean      isDirectory(String path);
  
  public String       currentDir();
  public boolean      changeDir(String subdirName) throws IOException;
  
  public String[]     listDirectoryContents(String directory) throws IOException;
  public InputStream  readFile(String file) throws IOException;

  public void         makeDirectory(String dirName) throws IOException;
  public OutputStream writeFile(String file, boolean append) throws IOException;
  
  public void         delete(String path, boolean recursive) throws IOException;
}