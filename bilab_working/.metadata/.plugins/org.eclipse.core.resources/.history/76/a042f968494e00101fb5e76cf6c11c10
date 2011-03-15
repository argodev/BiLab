/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The license is also available at: http://www.gnu.org/copyleft/lgpl.html
 */

package bilab;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.db.HashSequenceDB;
import org.biojava.bio.seq.db.IllegalIDException;
import org.biojava.bio.seq.db.NCBISequenceDB;
import org.biojava.bio.seq.db.SequenceDB;
import org.biojava.bio.seq.db.SequenceDBLite;
import org.biojava.bio.seq.io.GenbankFormat;
import org.biojava.utils.ChangeVetoException;

import scigol.Any;
import scigol.Map;
import scigol.TypeSpec;
import scigol.accessor;

// a sequence database (a map of seq keyed by id)
@Summary("a sequence database")
public class seqdb extends Map {
    // this implementation wraps the biojava SequenceDBLite interface.
    // as a map it appears to be a map if string id -> DNA/RNA/protein

    public static int op_Card(final seqdb db) {
        return db.size();
    }

    protected SequenceDBLite db;

    @Summary("create an empty seq database")
    public seqdb() {
        db = new HashSequenceDB();
    }

    @Summary("create a seq database from a given database source & database (e.g. \"NCBI\",\"nucleotide\" ")
    @Sophistication(Sophistication.Advanced)
    public seqdb(final String sourceName, final String databaseName) {
        if (sourceName.equals("NCBI")) {
            if (databaseName.equals("nucleotide")) {
                db = new NCBISequenceDB(NCBISequenceDB.DB_NUCLEOTIDE,
                        new GenbankFormat());
            } else if (databaseName.equals("protein")) {
                db = new NCBISequenceDB(NCBISequenceDB.DB_PROTEIN,
                        new GenbankFormat());
            }
        } else {
            throw new BilabException("unsupported database source:"
                    + sourceName);
        }
    }

    // scigol.Map methods

    @Override
    @Sophistication(Sophistication.Advanced)
    public void add(Object key, Object value) {
        try {
            key = TypeSpec.unwrapAnyOrNum(key);
            value = TypeSpec.unwrapAnyOrNum(value);
            if ((key != null) && !(key instanceof String)) {
                throw new BilabException("seqdb key must be a string (id)");
            }

            if (!(value instanceof seq)) {
                throw new BilabException("can only add seq to a seqdb");
            }

            if (db instanceof HashSequenceDB) {
                ((HashSequenceDB) db).addSequence((String) key,
                        getSeq((seq) value));
            } else {
                if (key == null) {
                    db.addSequence(getSeq((seq) value));
                } else {
                    throw new BilabException(
                            "this kind of seqdb doesn't support seq addition");
                }
            }

        } catch (final BilabException e) {
            throw e;
        } catch (final ChangeVetoException e) {
            throw new BilabException(
                    "change to underlying biojava Sequence vetoed:"
                            + e.getMessage(), e);
        } catch (final BioException e) {
            throw new BilabException("biojava error:" + e.getMessage(), e);
        }
    }

    @Summary("add a seq to the database (generating a new unique id)")
    public void add(final seq s) {
        add(null, s);
    }

    @Summary("add an id -> seq mapping to the database")
    public void add(final String id, final seq s) {
        add(id, s);
    }

    @Override
    public boolean contains(Object key) {
        if (!(key instanceof String)) {
            throw new BilabException("seqdb key must be a string (id)");
        }

        try {

            key = TypeSpec.unwrapAnyOrNum(key);
            db.getSequence((String) key);
            return true;

        } catch (final IllegalIDException e) {
        } catch (final BioException e) {
            throw new BilabException("biojava error:" + e.getMessage(), e);
        }
        return false;
    }

    @Override
    @Sophistication(Sophistication.Advanced)
    public Object get(Object key) {
        // we handle a list of GI numbers, or a single one

        key = TypeSpec.unwrapAnyOrNum(key);
        final scigol.List seqList = new scigol.List();

        if (!(key instanceof scigol.List)) { // not a list, so make a list of 1
            final scigol.List list = new scigol.List();
            list.add(key);
            key = list;
        }

        final scigol.List giList = (scigol.List) key;

        for (int i = 0; i < giList.get_size(); i++) { // for each GI number

            final Object giObj = TypeSpec.unwrapAnyOrNum(giList.get(i));
            String gi;

            if (giObj instanceof String) {
                gi = (String) giObj;
            } else if (giObj instanceof Integer) {
                gi = "" + ((Integer) giObj).intValue();
            } else {
                throw new BilabException(
                        "seqdb keys must be a string or int GI number (or a list of GI numbers)");
            }

            try {

                final Sequence sequence = db.getSequence(gi);

                seqList.add(seq.seqFromSequence(sequence));

            } catch (final IllegalIDException e) {
                throw new BilabException(
                        "seqdb doesn't contain a seq with key '" + key + "'");
            } catch (final BioException e) {
                throw new BilabException("biojava error:" + e.getMessage(), e);
            }

        } // for

        if (seqList.get_size() == 1) {
            return TypeSpec.unwrapAnyOrNum(seqList.get(0));
        }

        return seqList;
    }

    @Override
    @accessor
    public Any get_Item(Object key) {
        key = TypeSpec.unwrapAnyOrNum(key);
        return new Any(get(key));
    }

    // convenience
    protected Sequence getSeq(final seq s) {
        if (s instanceof DNA) {
            return ((DNA) s).seq;
        } else if (s instanceof RNA) {
            return ((RNA) s).seq;
        } else if (s instanceof protein) {
            return ((protein) s).seq;
        }

        throw new BilabException("expecting DNA, RNA or protein");
    }

    @Override
    @Sophistication(Sophistication.Advanced)
    public Object put(final Object key, final Object value) {
        add(key, value);
        return value;
    }

    @Override
    @Sophistication(Sophistication.Advanced)
    public Object remove(Object key) {
        if (!(key instanceof String)) {
            throw new BilabException("seqdb key must be a string (id)");
        }

        Sequence sequence = null;
        try {

            key = TypeSpec.unwrapAnyOrNum(key);
            sequence = db.getSequence((String) key);
            db.removeSequence((String) key);

        } catch (final IllegalIDException e) {
            throw new BilabException("seqdb doesn't contain a seq with key '"
                    + key + "'");
        } catch (final ChangeVetoException e) {
            throw new BilabException(
                    "change to underlying biojava Sequence vetoed:"
                            + e.getMessage(), e);
        } catch (final BioException e) {
            throw new BilabException("biojava error:" + e.getMessage(), e);
        }

        return seq.seqFromSequence(sequence);
    }

    @Override
    @accessor
    public void set_Item(Object key, final Any value) {
        key = TypeSpec.unwrapAnyOrNum(key);
        put(key, TypeSpec.unwrapAnyOrNum(value));
    }

    @Override
    @Sophistication(Sophistication.Advanced)
    public int size() {
        if (db instanceof SequenceDB) {
            return ((SequenceDB) db).ids().size();
        }
        throw new BilabException(
                "unable to determine size of this kind of seqdb");
    }

    @Override
    public String toString() {
        if (db instanceof SequenceDB) {
            final SequenceDB sdb = (SequenceDB) db;
            String s = "[\n";

            final java.util.Set ids = sdb.ids();
            for (final Object id : ids) {
                final String key = (String) id;
                final Object value = get(key);
                s += " " + key.toString() + " -> "
                        + ((value != null) ? value.toString() : "null") + "\n";
            }
            s += "]";
            return s;
        } else {
            if ((db.getName() != null) && (db.getName().length() > 0)) {
                return "<seqdb " + db.getName() + "> [...]";
            }
            return "<seqdb> [...]";
        }
    }
}
