package bilab.io;

import java.io.*;

//import bilab.Notify;
import bilab.Util;

// simple interface to the local OS file-system 
public class LocalFileSystem implements IFileSystem
{
  // new file-system rooted at the local root ("/")
  public LocalFileSystem()
  {
    localRoot = new File("/");
  }
  
  // new file-system rooted at specified local directory
  public LocalFileSystem(String rootDir) throws IOException
  {
    localRoot = new File(Util.toNativePathSeparator(rootDir));
    if (!localRoot.isDirectory())
      throw new IOException("local rootDir '"+rootDir+"' must be a directory");
  }
  
  protected File localPath(String path) 
  {
    if (path.equals("/")) return localRoot;
    
    boolean containsDriveLetter =     ((path.length() >= 3) && path.substring(1,3).equals(":/"))  // e.g. C:/
    						      || ( (path.length() >= 4) && (path.charAt(0)=='/') && (path.substring(2,4).equals(":/")) ) ; // e.g. /C:/
    boolean isAbsolute = (path.charAt(0) == '/') || containsDriveLetter;
    
    File local = null;
    if (isAbsolute) { 
      // form path from local directory that represents the root of this filesystem and the absolute path
      //  (note that under windows, an absolute path prefixed with a drive letter is supported.  For example,
      //   if the localRoot is "\" and the path is /C:/subdir, the local path will be C:\subdir.  It is
      //   an error is the current drive isn't C (in this example).
      //  
      
      if (!containsDriveLetter)
        local = new File(localRoot.getAbsolutePath(),Util.toNativePathSeparator(path.substring(1))); // remove leading '/'
      else { // special case when path contains a drive letter
        // check the letter is the same drive as the localRoot's drive
        // \TODO !!!
        
        // it is an error to include the drive letter is the localRoot isn't the root of the drive
        //  (i.e. can't accept a drive absolute path if this filesystem's root is a sub-directory)
        if (!localRoot.equals(new File(File.separator)))
          throw new IllegalArgumentException("invalid path "+path+" for filesystem rooted in the local filesystem directory '"+localRoot+"'");
        
        String newPath = (path.charAt(0)=='/')?path.substring(1):path; // first, remove leading '/'
        if (newPath.substring(1,3).equals(":/")) newPath = newPath.substring(3); // remove ?:/
        
        local = new File(localRoot.getAbsolutePath(),Util.toNativePathSeparator(newPath)); 
      }
    }
    else {
      // relative to current dir
      String fullPath = (currentDirectory.equals("."))?path:currentDirectory+"/"+path;
      
      local = new File(localRoot.getAbsolutePath(),Util.toNativePathSeparator(fullPath));
    }

    return local;
  }
  
  public String getLocalPath(String path) {
    return localPath(path).getAbsolutePath().toString();
  }
  
  public boolean exists(String path) {
    return localPath(path).exists();
  }
  
  public boolean isDirectory(String path) {
    return localPath(path).isDirectory();
  }
  
  public String currentDir() {
    String cwd = null;
    
    if (currentDirectory.equals(".")) {
      cwd = Util.toForwardPathSeparator(localRoot.getAbsolutePath().toString());
    }
    else {
      if (localRoot.equals(new File(File.separator)))
        cwd = Util.toForwardPathSeparator(localRoot.getAbsolutePath().toString())+currentDirectory;
      else
        cwd = Util.toForwardPathSeparator(localRoot.getAbsolutePath().toString())+"/"+currentDirectory;
    }

    if (cwd.charAt(0)!='/')
      cwd="/"+cwd;
    return cwd;
  }
  
  public boolean changeDir(String subdirName)
  {
    if (subdirName.equals(".")) return true;
    
    if (subdirName.equals("/")) currentDirectory = ".";
    
    if (subdirName.equals("..")) {
      
    	if (currentDirectory.equals(".")) return true;
    	
    	File parent = (new File(Util.toNativePathSeparator(currentDirectory))).getParentFile();
      
    	if (parent != null)
    		currentDirectory = Util.toForwardPathSeparator(parent.getPath());
    	else
    		return false;
    }
    else {
      String newDir = subdirName;

      if (!subdirName.startsWith("/")) { // if not absolute prepend currentDirectory
        newDir = (currentDirectory.equals("."))?"/"+subdirName:currentDirectory+"/"+subdirName;
      }
      
      File local = localPath(newDir);
      
      if (local.exists()) 
        currentDirectory = newDir;
      else
        return false;
    }

    // the current directory doesn't need a drive letter as it is relative to the localRoot, so remove
    //  it if present
    if (currentDirectory.charAt(0)=='/') currentDirectory=currentDirectory.substring(1);
    if ((currentDirectory.length() >= 3) && currentDirectory.substring(1,3).equals(":/"))
      currentDirectory = currentDirectory.substring(3);
    
    return true;
  }

  
  public String[] listDirectoryContents(String directory) throws IOException
  {
    if (!exists(directory)) throw new IOException("directory '"+directory+"' not found");
    
    File localDir = localPath(directory);
    return localDir.list();
  }
  
  public InputStream readFile(String file) throws FileNotFoundException
  {
    try {
      if (exists(file))
        return new FileInputStream(localPath(file));
    }
    catch (FileNotFoundException e) {}
    
    throw new FileNotFoundException("not found '"+file+"'");
  }

  
  public void makeDirectory(String dirName) throws IOException
  {
    if (exists(dirName)) return; // already exists
    File local = localPath(dirName);
    if (!local.mkdir())
      throw new IOException("unable to create new directory '"+dirName+"'.");
  }
  
  public OutputStream writeFile(String file, boolean append) throws IOException
  {
    if (append && !exists(file)) throw new FileNotFoundException("can't append to non-existent file '"+file+"'.");
    try {
      return new FileOutputStream(localPath(file), append);
    } catch (IOException e) {
      throw new IOException("unable to write to file '"+file+"' - "+e.getMessage());
    }
  }
  
  
  public void delete(String path, boolean recursive) throws IOException
  {
    if (exists(path)) {
      if ((!recursive) || !isDirectory(path))
        localPath(path).delete();
      else {
        String[] contents = listDirectoryContents(path);
        for(String item : contents)
          delete(path+"/"+item,true);
        delete(path,false);
      }
    }
  }
  
  private File localRoot;
  private String currentDirectory = ".";
}