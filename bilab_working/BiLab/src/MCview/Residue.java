package MCview;

import java.util.*;


public class Residue {
  Vector atoms = new Vector();
  int number;
  int count;
  int seqnumber;

  public Residue(Vector atoms, int number, int count) {
    this.atoms = atoms;
    this.number = number;
    this.count = count;
  }

  public myAtom findAtom(String name) {
     for (int i = 0; i < atoms.size(); i++) {
	if (((myAtom)atoms.elementAt(i)).name.equals(name)) {
           return (myAtom)atoms.elementAt(i);
        }
     }
     return null;
  }
}
