package nsdb;


/**
* nsdb/FeatureLocationOperations.java .
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
public interface FeatureLocationOperations  extends nsdb.LocationOperations
{

  /**
           * nucfeature to which the location is associated
           */
  nsdb.NucFeature getNucFeature ();
} // interface FeatureLocationOperations
