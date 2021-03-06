package nsdb;


/**
* nsdb/ServerInfo.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb_write.idl
* Monday, August 23, 2004 12:02:44 PM BST
*/

public final class ServerInfo implements org.omg.CORBA.portable.IDLEntity
{

  // Information about the loaded entries
  public nsdb.EntryStats entry_stats_list[] = null;

  // Information about the files in the server directory
  public nsdb.EntryStats file_stats_list[] = null;

  public ServerInfo ()
  {
  } // ctor

  public ServerInfo (nsdb.EntryStats[] _entry_stats_list, nsdb.EntryStats[] _file_stats_list)
  {
    entry_stats_list = _entry_stats_list;
    file_stats_list = _file_stats_list;
  } // ctor

} // class ServerInfo
