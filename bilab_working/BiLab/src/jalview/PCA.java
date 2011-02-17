package jalview;
import java.awt.*;
import java.io.*;

public class PCA implements Runnable {
  Matrix m;
  Matrix symm;
  Matrix m2;

  double[] eigenvalue;
  Matrix eigenvector;

  public PCA(Matrix m) {
    this.m = m;
  }

  public PCA(Sequence[] s) {
    Runtime rt = Runtime.getRuntime();
    
    BinarySequence[] bs = new BinarySequence[s.length];
    int ii = 0;
    while (ii < s.length && s[ii] != null) {
  
      bs[ii] = new BinarySequence(s[ii]);
      bs[ii].encode();
      ii++;
    }
   
    BinarySequence[] bs2 = new BinarySequence[s.length];
    ii = 0;
    while (ii < s.length && s[ii] != null) {
  
      bs2[ii] = new BinarySequence(s[ii]);
      bs2[ii].blosumEncode();
      ii++;
    }
   
    
    //System.out.println("Created binary encoding");
    //printMemory(rt);
    
    int count=0;
      while (count < bs.length && bs[count] != null) {
     count++;
    }
    double[][] seqmat = new double[count][bs[0].dbinary.length];
    double[][] seqmat2 = new double[count][bs2[0].dbinary.length];
    int i=0;
    while (i < count) {
      seqmat[i] = bs[i].dbinary;
      seqmat2[i] = bs2[i].dbinary;
      i++;
    }
    //System.out.println("Created array");
    //printMemory(rt);
    //    System.out.println(" --- Original matrix ---- ");
    m = new Matrix(seqmat,count,bs[0].dbinary.length);
    m2 = new Matrix(seqmat2,count,bs2[0].dbinary.length);
    
    //System.out.println("Created matrix");
    printMemory(rt);
  }
  
  public static void printMemory(Runtime rt) {
    System.out.println("Free memory = " + rt.freeMemory());
  }
  
  public double[] getEigenvector(int i) {
    return eigenvector.getColumn(i);
  }

  public double getEigenvalue(int i) {
    return eigenvector.d[i];
  }
  public float[][] getComponents(int l, int n, int mm) {
    return getComponents(l,n,mm,1);
  }
  public float[][] getComponents(int l, int n, int mm, float factor) {
    float[][] out = new float[m.rows][3];

    for (int i = 0; i < m.rows;i++) {
      out[i][0] = (float)component(i,l)*factor; 
      out[i][1] = (float)component(i,n)*factor; 
      out[i][2] = (float)component(i,mm)*factor; 
    }
    return out;
  }

  public double[] component(int n) {
    // n = index of eigenvector
    double[] out = new double[m.rows];

    for (int i=0; i < m.rows; i++) {
      out[i] = component(i,n);
    }
    return out;
  }
  public double component(int row, int n) {
    double out = 0.0;
    
    for (int i = 0; i < symm.cols; i++) {
      out += symm.value[row][i] * eigenvector.value[i][n];
    }
    return out/eigenvector.d[n];
  }

  public void checkEigenvector(int n,PrintStream ps) {
    ps.println(" --- Eigenvector " + n  + " --- ");
    
    double[] eigenv = eigenvector.getColumn(n);

    for (int i=0; i < eigenv.length;i++) {
      Format.print(ps,"%15.4f",eigenv[i]);
    }

    System.out.println();      
    
    double[] neigenv = symm.vectorPostMultiply(eigenv);
    System.out.println(" --- symmat * eigenv / lambda --- ");
    if (eigenvector.d[n] > 1e-4) {
      for (int i=0; i < neigenv.length;i++) {
	Format.print(System.out,"%15.4f",neigenv[i]/eigenvector.d[n]);
      }
    }
      System.out.println();      
  }

  public void run() {
    Matrix mt = m.transpose();
    //    System.out.println(" --- OrigT * Orig ---- ");
    eigenvector = mt.preMultiply(m2);
    //  eigenvector.print(System.out);
    symm = eigenvector.copy();
    
    //TextArea ta = new TextArea(25,72);
    //TextAreaPrintStream taps = new TextAreaPrintStream(System.out,ta);
    //Frame f = new Frame("PCA output");
    //f.resize(500,500);
    //f.setLayout(new BorderLayout());
    //f.add("Center",ta);
    //f.show();
    //symm.print(taps);
    long tstart = System.currentTimeMillis();
    eigenvector.tred();
    long tend = System.currentTimeMillis();
    //taps.println("Time take for tred = " + (tend-tstart) + "ms");
    //taps.println(" ---Tridiag transform matrix ---");
    
    //taps.println(" --- D vector ---");
    //eigenvector.printD(taps);
    //taps.println();   
    //taps.println(" --- E vector ---");
    //    eigenvector.printE(taps);
    //taps.println();   
    
    // Now produce the diagonalization matrix
    tstart = System.currentTimeMillis();
    eigenvector.tqli();
    tend = System.currentTimeMillis();
    //System.out.println("Time take for tqli = " + (tend-tstart) + " ms");
    
    //System.out.println(" --- New diagonalization matrix ---");
    
    //System.out.println(" --- Eigenvalues ---");
    //eigenvector.printD(taps);

    //System.out.println();  
 
     // for (int i=0; i < eigenvector.cols; i++) {
     // checkEigenvector(i,taps);
     // taps.println();
    // }
    
    //  taps.println();
    //  taps.println("Transformed sequences = ");
    // Matrix trans =  m.preMultiply(eigenvector);
    //  trans.print(System.out);
  }
}
