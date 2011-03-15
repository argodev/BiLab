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

import scigol.Any;
import scigol.Map;
import scigol.TypeSpec;

import org.biojava.bio.seq.*;
import org.biojava.bio.seq.db.*;
import org.biojava.bio.seq.io.GenbankFormat;
import org.biojava.bio.seq.io.FastaFormat;
import org.biojava.bio.BioException;
import org.biojava.utils.ChangeVetoException;

import scigol.accessor;



// a sequence database (a map of seq keyed by id)
@Summary("a sequence database")
public class seqdb extends Map
{
  // this implementation wraps the biojava SequenceDBLite interface.
  //  as a map it appears to be a map if string id -> DNA/RNA/protein
  
  @Summary("create an empty seq database")
  public seqdb()
  {
    db = new HashSequenceDB();
  }
  
  
  @Summary("create a seq database from a given database source & database (e.g. \"NCBI\",\"nucleotide\" ")
  @Sophistication(Sophistication.Advanced)
  public seqdb(String sourceName, String databaseName)
  {
    if (sourceName.equals("NCBI")) {
      if (databaseName.equals("nucleotide"))
        db = new NCBISequenceDB(NCBISequenceDB.DB_NUCLEOTIDE, new GenbankFormat());
      else if (databaseName.equals("protein"))
        db = new NCBISequenceDB(NCBISequenceDB.DB_PROTEIN, new GenbankFormat());
    }
    else
      throw new BilabException("unsupported database source:"+sourceName);
  }

  
  @Summary("add a seq to the database (generating a new unique id)")
  public void add(seq s)
  {
    add(null, s);
  }
  
  
  @Summary("add an id -> seq mapping to the database")
  public void add(String id, seq s)
  {
    add(id,s);
  }

  
  
  // scigol.Map methods
  
  @Sophistication(Sophistication.Advanced)
  public void add(Object key, Object value)
  {
    try {
      key = TypeSpec.unwrapAnyOrNum(key);
      value = TypeSpec.unwrapAnyOrNum(value);
      if ((key != null) && !(key instanceof String))
        throw new BilabException("seqdb key must be a string (id)");
      
      if (!(value instanceof seq))
        throw new BilabException("can only add seq to a seqdb");
      
      if (db instanceof HashSequenceDB)
        ((HashSequenceDB)db).addSequence((String)key,getSeq((seq)value));
      else {
        if (key == null)
          db.addSequence(getSeq((seq)value));
        else
          throw new BilabException("this kind of seqdb doesn't support seq addition");
      }
      
    } catch (BilabException e) { throw e; }
    catch (ChangeVetoException e) {
      throw new BilabException("change to underlying biojava Sequence vetoed:"+e.getMessage(),e);
    }
    catch (BioException e) {
      throw new BilabException("biojava error:"+e.getMessage(),e);
    }
  }
  
  
  
  @Sophistication(Sophistication.Advanced)
  public Object put(Object key, Object value)
  {
    add(key,value);
    return value;
  }
  
  
  @Sophistication(Sophistication.Advanced)
  public Object get(Object key)
  {
    // we handle a list of GI numbers, or a single one
    
    key = TypeSpec.unwrapAnyOrNum(key);
    scigol.List seqList = new scigol.List();
    
    if (!(key instanceof scigol.List)) { // not a list, so make a list of 1
      scigol.List list = new scigol.List();
      list.add(key);
      key = list;
    }
    
    scigol.List giList = (scigol.List)key;
    
    for(int i=0; i<giList.get_size();i++) { // for each GI number
    
      Object giObj = TypeSpec.unwrapAnyOrNum( giList.get(i) ); 
      String gi;
      
      if (giObj instanceof String)
        gi = (String)giObj;
      else if (giObj instanceof Integer)
        gi = ""+((Integer)giObj).intValue();
      else
        throw new BilabException("seqdb keys must be a string or int GI number (or a list of GI numbers)");
      
      try {
        
        Sequence sequence = db.getSequence(gi);
        
        seqList.add( seq.seqFromSequence( sequence ) );
        
      } catch (IllegalIDException e) {
        throw new BilabException("seqdb doesn't contain a seq with key '"+key+"'");
      } catch (BioException e) {
        throw new BilabException("biojava error:"+e.getMessage(),e);
      }
      
    } // for
    
    if (seqList.get_size() == 1) // just one, don't return a list
      return TypeSpec.unwrapAnyOrNum( seqList.get(0) );
    
    return seqList; 
  }
  

  @Sophistication(Sophistication.Advanced)
  public int size()
  {
    if (db instanceof SequenceDB)
      return ((SequenceDB)db).ids().size();
    throw new BilabException("unable to determine size of this kind of seqdb");
  }
  
  
  public boolean contains(Object key)  
  {
    if (!(key instanceof String))
      throw new BilabException("seqdb key must be a string (id)");
    
    try {
      
      key = TypeSpec.unwrapAnyOrNum(key);
      db.getSequence((String)key);
      return true;
      
    } catch (IllegalIDException e) {}
    catch (BioException e) { 
      throw new BilabException("biojava error:"+e.getMessage(),e);
    }
    return false;
  }

  
  @Sophistication(Sophistication.Advanced)
  public Object remove(Object key)  
  {
    if (!(key instanceof String))
      throw new BilabException("seqdb key must be a string (id)");
    
    Sequence sequence = null;
    try {
      
      key = TypeSpec.unwrapAnyOrNum(key);
      sequence = db.getSequence((String)key);
      db.removeSequence((String)key);
      
    } catch (IllegalIDException e) {
      throw new BilabException("seqdb doesn't contain a seq with key '"+key+"'");
    } catch (ChangeVetoException e) {
      throw new BilabException("change to underlying biojava Sequence vetoed:"+e.getMessage(),e);
    } catch (BioException e) {
      throw new BilabException("biojava error:"+e.getMessage(),e);
    }
    
    return seq.seqFromSequence(sequence);
  }

  
  
  @accessor
  public Any get_Item(Object key)
  {
    key = TypeSpec.unwrapAnyOrNum(key);
    return new Any(get(key));
  }
    
  @accessor
  public void set_Item(Object key, Any value)
  {
    key = TypeSpec.unwrapAnyOrNum(key);
    put(key, TypeSpec.unwrapAnyOrNum(value));
  }
  


  public static int op_Card(seqdb db)
  {
    return db.size();
  }
  
  
  
  
  public String toString()
  {
    if (db instanceof SequenceDB) {
      SequenceDB sdb = (SequenceDB)db;
      String s = "[\n";

      java.util.Set ids = sdb.ids();
      for(Object id : ids) {
        String key = (String)id;
        Object value = get(key);
        s += " "+key.toString()+" -> "+((value!=null)?value.toString():"null")+"\n";
      }
      s += "]";
      return s;
    }
    else {
      if ((db.getName() != null) && (db.getName().length()>0))
        return "<seqdb "+db.getName()+"> [...]";
      return "<seqdb> [...]";
    }
  }
  


  // convenience
  protected Sequence getSeq(seq s)
  {
    if (s instanceof DNA)
      return ((DNA)s).seq;
    else if (s instanceof RNA)
      return ((RNA)s).seq;
    else if (s instanceof protein)
      return ((protein)s).seq;
    
    throw new BilabException("expecting DNA, RNA or protein");
  }

  
  protected SequenceDBLite db;
}
