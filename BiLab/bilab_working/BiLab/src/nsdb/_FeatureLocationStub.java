package nsdb;


/**
* nsdb/_FeatureLocationStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb.idl
* Monday, August 23, 2004 12:02:40 PM BST
*/


/**
       * Location of a NucFeature
       * This interface does not allow to change the nuc_feature
       * If a location is assigned to a nucfeature, the inverse relation
       * should be properly updated
       * @see NucFeature.getLocation
       */
public class _FeatureLocationStub extends org.omg.CORBA.portable.ObjectImpl implements nsdb.FeatureLocation
{


  /**
           * nucfeature to which the location is associated
           */
  public nsdb.NucFeature getNucFeature ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getNucFeature", true);
                $in = _invoke ($out);
                nsdb.NucFeature $result = nsdb.NucFeatureHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getNucFeature (        );
            } finally {
                _releaseReply ($in);
            }
  } // getNucFeature


  /**
  	 * retrieve 
  	 * <A href="http://www.ebi.ac.uk/ebi_docs/embl_db/ft/components.html#location">
  	 * string representation of location</A>
  	 */
  public String getLocationString ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getLocationString", true);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getLocationString (        );
            } finally {
                _releaseReply ($in);
            }
  } // getLocationString


  /**
  	 * retrieve tree representation of location
  	 */
  public nsdb.LocationPackage.LocationNode_u[] getNodes ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getNodes", true);
                $in = _invoke ($out);
                nsdb.LocationPackage.LocationNode_u $result[] = nsdb.LocationPackage.LocationNodeListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getNodes (        );
            } finally {
                _releaseReply ($in);
            }
  } // getNodes


  /**
  	 * Create nucleotide sequence defined by location. This can imply getting
  	 * fragments from multiple sequences and concatenating.
  	 * If it is not possible to resolve the location into a single sequence
  	 * (e.g. when it contains a 'group' operator, or gap nodes)
  	 * each fragment will be returned as a seperate string. No assumption should be
  	 * made on the order, if multiple fragments are returned
           * @raises InexactLocation if an exact sequence cannot be
  	 *   determined due to the location being inexact
  	 */
  public String getSeq () throws nsdb.InexactLocation
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getSeq", true);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:nsdb/InexactLocation:1.0"))
                    throw nsdb.InexactLocationHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getSeq (        );
            } finally {
                _releaseReply ($in);
            }
  } // getSeq

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:nsdb/FeatureLocation:1.0", 
    "IDL:nsdb/Location:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     String str = org.omg.CORBA.ORB.init (args, props).object_to_string (this);
     s.writeUTF (str);
  }
} // class _FeatureLocationStub
