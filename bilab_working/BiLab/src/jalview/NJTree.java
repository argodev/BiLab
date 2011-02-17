package jalview;



import java.util.*;

import java.awt.*;



public class NJTree {

  TreeFile tf;

  Sequence[] sequence;

  float distance[][];

  int noClus;

  Vector cluster;

  int done[];

  int noseqs;



  int mini;

  int minj;

  float ri;

  float rj;

  Vector node;

  String type;

  String pwtype;



 



  public NJTree(Sequence[] sequence,String type,String pwtype ) {

    this.sequence = sequence;

    this.node = new Vector();

    

    this.type = type;

    this.pwtype = pwtype;



    if (!(type.equals("NJ"))) {

      type = "AV";

    }



    if (!(pwtype.equals("PID"))) {

      type = "SW";

    }

    

    int i=0;

    done = new int[sequence.length];



    while (i < sequence.length && sequence[i] != null) {

      done[i] = 0;

      i++;

    }

    noseqs = i++; 



    distance = findDistances();

    makeLeaves();

    noClus = cluster.size();



    cluster();

  }



  public void cluster() {

    while (noClus > 2) {

      if (type.equals("NJ")) {

	float mind = findMinNJDistance();

      } else {

	float mind = findMinDistance();

      }

      Cluster c = joinClusters(mini,minj);



      done[minj] = 1;



      cluster.setElementAt(null,minj);

      cluster.setElementAt(c,mini);

      

      noClus--;

    }



    boolean onefound = false;



    int one = -1;

    int two = -1;



    for (int i=0; i < noseqs; i++) {

      if (done[i] != 1) {

        if (onefound == false) {

          two = i;

          onefound = true;

        } else {

          one = i;

        }

      }

    }



    Cluster c = joinClusters(one,two);

    tf = new TreeFile((SequenceNode)(node.elementAt(one)));



  }



  public Cluster joinClusters(int i, int j) {

    float dist = distance[i][j];

    //    System.out.println("Dist = " + dist);



    int noi = ((Cluster)cluster.elementAt(i)).value.length;

    int noj = ((Cluster)cluster.elementAt(j)).value.length;



    int[] value = new int[noi + noj];



    for (int ii = 0; ii < noi;ii++) {

      value[ii] =  ((Cluster)cluster.elementAt(i)).value[ii];

    }

    for (int ii = noi; ii < noi+ noj;ii++) {

      value[ii] =  ((Cluster)cluster.elementAt(j)).value[ii-noi];

    }



    Cluster c = new Cluster(value);

    ri = findr(i,j);

    rj = findr(j,i);



    if (type.equals("NJ")) {

      findClusterNJDistance(i,j);

    } else {

      findClusterDistance(i,j);

    }



    // Make a new node

    SequenceNode sn = new SequenceNode();



    sn.left = (SequenceNode)(node.elementAt(i));

    sn.right =(SequenceNode)(node.elementAt(j));

    

    SequenceNode tmpi = (SequenceNode)(node.elementAt(i));

    SequenceNode tmpj = (SequenceNode)(node.elementAt(j));



    if (type.equals("NJ")) {

      findNewNJDistances(tmpi,tmpj,dist);

    } else {

      findNewDistances(tmpi,tmpj,dist);

    }

    tmpi.parent = sn;

    tmpj.parent = sn;



    node.setElementAt(sn,i);



    return c;

  }



  public void findNewNJDistances(SequenceNode tmpi, SequenceNode tmpj, float dist) {

    float ih = 0;

    float jh = 0;

    

    SequenceNode sni = tmpi;

    SequenceNode snj = tmpj;



    tmpi.dist = (dist + ri - rj)/2;

    tmpj.dist = (dist - tmpi.dist);



    if (tmpi.dist < 0) {tmpi.dist = 0;}

    if (tmpj.dist < 0) {tmpj.dist = 0;}



  }

    

  public void findNewDistances(SequenceNode tmpi,SequenceNode tmpj,float dist) {

    float ih = 0;

    float jh = 0;

    

    SequenceNode sni = tmpi;

    SequenceNode snj = tmpj;



    while (sni != null) {

      ih = ih + sni.dist;

      sni = (SequenceNode)sni.left;

     }



    while (snj != null) {

      jh = jh + snj.dist;

      snj = (SequenceNode)snj.left;

     }



    //    System.out.println("Dist = " + dist);

    tmpi.dist = (dist/2 - ih);

    tmpj.dist = (dist/2 - jh);



  }



  public void findClusterDistance(int i, int j) {



    int noi = ((Cluster)cluster.elementAt(i)).value.length;

    int noj = ((Cluster)cluster.elementAt(j)).value.length;

    

    // New distances from cluster to others

    float[] newdist = new float[noseqs];

    

    for (int l = 0; l < noseqs; l++) {

      if ( l != i && l != j) {

	newdist[l] = (distance[i][l] * noi + distance[j][l] * noj)/(noi + noj);

      } else {

	newdist[l] = 0;

      }

    }



    for (int ii=0; ii < noseqs;ii++) {

      distance[i][ii] = newdist[ii];

      distance[ii][i] = newdist[ii];

    }

  }

  public void findClusterNJDistance(int i, int j) {



    int noi = ((Cluster)cluster.elementAt(i)).value.length;

    int noj = ((Cluster)cluster.elementAt(j)).value.length;

    

    // New distances from cluster to others

    float[] newdist = new float[noseqs];

    

    for (int l = 0; l < noseqs; l++) {

      if ( l != i && l != j) {

	newdist[l] = (distance[i][l] + distance[j][l] - distance[i][j])/2;

      } else {

	newdist[l] = 0;

      }

    }



    for (int ii=0; ii < noseqs;ii++) {

      distance[i][ii] = newdist[ii];

      distance[ii][i] = newdist[ii];

    }

  }



  public float findr(int i, int j) {

    float tmp = 1;

    for (int k=0; k < noseqs;k++) {

      if (k!= i && k!= j && done[k] != 1) {

	tmp = tmp + distance[i][k];

      }

    }

    if (noClus > 2) {

      tmp = tmp/(noClus - 2);

    } 



    

    return tmp;

  }

				  

  public float findMinNJDistance() {

    float min = 100000;



    for (int i=0; i < noseqs-1; i++) {

      for (int j=i+1;j < noseqs;j++) {

	if (done[i] != 1 && done[j] != 1) {

	  float tmp = distance[i][j] - (findr(i,j) + findr(j,i));

	  if (tmp < min) {

	    mini = i;

	    minj = j;

	    

	    min = tmp;

	  }

	}

      }

    }

    return min;

  }

  public float findMinDistance() {

    float min = 100000;



    for (int i=0; i < noseqs-1;i++) {

      for (int j = i+1; j < noseqs;j++) {

	if (done[i] != 1 && done[j] != 1) {

	  if (distance[i][j] < min) {

	    mini = i;

	    minj = j;

	    

	    min = distance[i][j];

	  }

	}

      }

    }

    return min;

  }



  public float[][] findDistances() {

    float[][] distance = new float[noseqs][noseqs];



    if (pwtype.equals("PID")) {

      for (int i = 0; i < noseqs-1; i++) {

	for (int j = i; j < noseqs; j++) {

	  if (j==i) {

	    distance[i][i] = 0;

	  } else {

	    //	    distance[i][j] = 100-Alignment.PID(sequence[i],sequence[j]);

	    distance[i][j] = 100-Alignment.compare(sequence[i],sequence[j]);

	    distance[j][i] = distance[i][j];

	  }

	}

      }

    } else {

      float max = -1;

      for (int i = 0; i < noseqs-1; i++) {

	for (int j = i; j < noseqs; j++) {

	  AlignSeq as = new AlignSeq(sequence[i],sequence[j],"pep");

	  as.calcScoreMatrix();

	  as.traceAlignment();

	  as.printAlignment();

	  distance[i][j] = (float)as.maxscore;

	  if (max < distance[i][j]) { max = distance[i][j];}

	  //	    distance[i][j] = 100-Alignment.PID(sequence[i],sequence[j]);

	  //distance[j][i] = distance[i][j];

	  //	  System.out.println("Score = " + distance[i][j] + " " + i + " " + j);

	  }

	}

      for (int i = 0; i < noseqs-1; i++) {

	for (int j = i; j < noseqs; j++) {

	  distance[i][j] =  max - distance[i][j];

	  distance[j][i] = distance[i][j];

	  //	  System.out.println("Distance = " + distance[i][j] + " " + i + " " + j);

	}

      }

    }

      

    return distance;

  }

  

  public void makeLeaves() {

    cluster = new Vector();



    for (int i=0; i < noseqs; i++) {

      SequenceNode sn = new SequenceNode();



      sn.element = sequence[i];

      node.addElement(sn);



      int[] value = new int[1];

      value[0] = i;



      Cluster c = new Cluster(value);

      cluster.addElement(c);

    }

  }



  public static void main(String[] args) {

    try {



      MSFfile msf = new MSFfile(args[0],"File");

      Sequence[] s = new Sequence[msf.seqs.size()];

      for (int i=0; i < msf.seqs.size();i++) {

	s[i] = (Sequence)msf.seqs.elementAt(i);

      }

      NJTree njt = new NJTree(s,args[1],args[2]);





      Frame f = new Frame();

      f.setLayout(new BorderLayout());

      Panel p = new Panel();

      p.setLayout(new BorderLayout());

      MyCanvas mc = new MyCanvas(njt.tf);

      p.add("Center",mc);



      f.resize(600,600);

      f.add("Center",p);

   

      f.show();



      njt.tf.reCount(njt.tf.top);

      njt.tf.findHeight(njt.tf.top);



      //      System.out.println("Preorder");

      njt.tf.printNode(njt.tf.top);

      njt.tf.draw(p.getGraphics(),500,500);



      njt.tf.groupNodes(njt.tf.top,Float.valueOf(args[3]).floatValue());





    } catch (java.io.IOException e) {

      System.out.println("Exception : " + e);

    }

  }

}

class Cluster {

  int[] value;



  public Cluster(int[] value) {

    this.value = value;

  }

}

      



class MyCanvas extends Canvas {

  TreeFile tf;



  public MyCanvas(TreeFile tf) {

    super();

    this.tf = tf;

  }



  public void paint(Graphics g) {

    if (size() != null) {

      tf.draw(g,size().width,size().height);

    } else {

      tf.draw(g,500,500);

    }

  }



  

}

  

