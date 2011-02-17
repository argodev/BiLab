package MCview;

import java.util.*;

public class Zsort {
   
   public static void Zsort(Vector bonds) {
     
    sort(bonds,0,bonds.size()-1);
   }

 
  
 public static void sort(Vector bonds,int p, int r) {
    int q;

    if (p < r) {
      q = partition(bonds,p,r);
      sort(bonds,p,q);
      sort(bonds,q+1,r);
    }
  }

  private static int partition(Vector bonds, int p, int r) {
    float x = ((Bond)bonds.elementAt(p)).start[2];
    int i = p-1;
    int j = r+1;

    while(true) {
      do {
	j = j-1;
      }	while (j >= 0  && ((Bond)bonds.elementAt(j)).start[2] > x);
      
      do {
	i = i+1;
      } while (i < bonds.size() && ((Bond)bonds.elementAt(i)).start[2] < x);
      
      if ( i < j) {
	Bond tmp = (Bond)bonds.elementAt(i);
	bonds.setElementAt(bonds.elementAt(j),i);
	bonds.setElementAt(tmp,j);
      } else {
	return j;
      }
    }
  }
}



