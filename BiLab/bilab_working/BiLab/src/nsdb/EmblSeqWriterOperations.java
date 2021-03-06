package nsdb;


/**
* nsdb/EmblSeqWriterOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb_write.idl
* Monday, August 23, 2004 12:02:44 PM BST
*/

public interface EmblSeqWriterOperations  extends nsdb.EmblSeqOperations
{

  /**
       * Create a new feature in this EmblSeq object.
       * @parm key Type of the feature to be created
       * @parm location_string The location of the new NucFeature
       * @raises LocationParse If the location string is not a valid location.
       * @raises type::IndexOutOfRange If any part of the location is beyond the
       *   end of the sequence
       * @raises InvalidKey if the given key is not a possible EMBL key.
       */
  nsdb.NucFeatureWriter createNucFeature (String key, String location) throws nsdb.LocationParse, type.IndexOutOfRange, nsdb.InvalidKey, nsdb.ReadOnlyException;

  /**
       * Remove the given feature.
       */
  void remove (nsdb.NucFeature nuc_feature) throws nsdb.ReadOnlyException;

  /**
       * retrieve sequence of NucFeatureList associated with
       * the nucleotide sequence that are within the given range of bases.
       * @raises type::NoResult if no features are owned by the sequence
       * @raises type::IndexOutOfRange if either of start_base or end_base is
       *  less than 1 or greater than the length of the sequence.
       */
  nsdb.NucFeature[] getNucFeaturesInRange (int start_base, int end_base) throws type.NoResult, type.IndexOutOfRange;

  /**
       *  Return the number of features
       */
  int getNucFeatureCount ();

  /**
       * Return the ith NucFeature from this Entry.  The feature are returned in
       * a consistent order, sorted by the first base of each Feature.
       * @raises type::IndexOutOfRange if the index is less than 0 or greater
       *   than the number of features.
       **/
  nsdb.NucFeature getFeatureAtIndex (int i) throws type.IndexOutOfRange;

  /**
       * Return the index of the given Feature.  This does the reverse of
       * getFeatureAtIndex ().  Returns -1 if the given NucFeature is not in
       * this EmblSeq object.
       **/
  int indexOf (nsdb.NucFeature feature);

  /**
       *  commit any pending changes to the database immediately.
       */
  void commit () throws nsdb.CommitFailed;

  /**
       * Return a Datestamp that will be passed to the set methods on the
       * EmblSeqWriter methods and NucFeatureWriter methods.  The object that is
       * returned represents the time when the entry was last changed (the last
       * time a feature was added, removed or changed location).
       */
  nsdb.Datestamp getDatestamp ();
} // interface EmblSeqWriterOperations
