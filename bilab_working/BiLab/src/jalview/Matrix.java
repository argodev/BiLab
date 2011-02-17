package jalview;


import java.io.*;

public class Matrix {

  double[][] value;
  int rows;
  int cols;
  double[] d;  // Diagonal
  double[] e;  // off diagonal

  public Matrix(double[][] value, int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.value = value;
  }

  public Matrix transpose() {
    double[][] out = new double[cols][rows];

    for (int i = 0; i < cols; i++) {
      for (int j = 0; j < rows ; j++) {
	out[i][j] = value[j][i];
      }
    }
    return new Matrix(out,cols,rows);
  }

  public void print(PrintStream ps) {

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
	Format.print(ps,"%8.2f",value[i][j]);
      }
      ps.println();
    }
  }


  public Matrix  preMultiply(Matrix in) {
    System.out.println("new matrix size " + in.rows + " " + this.cols);
    double[][] tmp = new double[in.rows][this.cols];

    for (int i = 0; i < in.rows; i++) {
      for (int j = 0; j < this.cols; j++ ) {
	tmp[i][j] = 0.0;

	for (int k = 0; k < in.cols; k++) {
	  tmp[i][j] += in.value[i][k]*this.value[k][j];
	}

      }
    }
    
    return new Matrix(tmp,in.rows,this.cols);
  }

  public double[] vectorPostMultiply(double[] in) {
    double[] out = new double[in.length];
    for (int i = 0; i < in.length; i++) {
      out[i] = 0.0;
      for (int k=0; k < in.length; k++) {
	out[i] += value[i][k] * in[k];
      }
    }
    return out;
  }
  public Matrix postMultiply(Matrix in) {

    double[][] out = new double[this.rows][in.cols];
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < in.cols; j++ ) {

	out[i][j] = 0.0;

	for (int k = 0; k < rows; k++) {
	  out[i][j] = out[i][j] + value[i][k]*in.value[k][j];
	}

      }
    }
    return new Matrix(out,this.cols,in.rows);
  }

  public Matrix copy() {
    double[][] newmat = new double[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
	newmat[i][j] = value[i][j];
      }
    }
    return new Matrix(newmat,rows,cols);
  }

  public void tred() {
    int n = rows;
    int l;
    int k;
    int j;
    int i;

    double scale;
    double hh;
    double h;
    double g;
    double f;

    this.d = new double[rows];
    this.e = new double[rows];

    for (i=n; i >= 2;i--) {
      l=i-1;
      h = 0.0;
      scale = 0.0;
      
      if (l > 1) {
	for (k=1;k<=l;k++) {
	  scale += Math.abs(value[i-1][k-1]);
	}
	if (scale == 0.0) {
	  e[i-1] = value[i-1][l-1];
	} else {
	  for (k=1; k <= l; k++) {
	    value[i-1][k-1] /= scale;
	    h += value[i-1][k-1]*value[i-1][k-1];
	  }
	  f = value[i-1][l-1];
	  if (f>0) {
	    g = -1.0*Math.sqrt(h);
	  } else {
	    g = Math.sqrt(h);
	  }
	  e[i-1] = scale*g;
	  h -= f*g;
	  value[i-1][l-1] = f-g;
	  f=0.0;
	  for (j=1; j <= l; j++) {
	    value[j-1][i-1] = value[i-1][j-1]/h;
	    g=0.0;
	    for (k= 1; k <= j; k++) {
	      g += value[j-1][k-1]*value[i-1][k-1];
	    }
	    for (k=j+1; k<=l;k++) {
	      g+= value[k-1][j-1]*value[i-1][k-1];
	    }
	    e[j-1] = g/h;
	    f+=e[j-1]*value[i-1][j-1];
	  }
	  hh=f/(h+h);
	  for (j=1;j<=l;j++) {
	    f=value[i-1][j-1];
	    g = e[j-1] - hh*f;
	    e[j-1] = g;
	    for (k=1;k<=j;k++) {
	      value[j-1][k-1] -= (f*e[k-1]+g*value[i-1][k-1]);
	    }
	  }
	}
      } else {
	e[i-1] = value[i-1][l-1];
      }
      d[i-1] = h;
    }
    d[0] = 0.0;
    e[0] = 0.0;
    for (i=1;i<=n;i++) {
      l=i-1;
      if (d[i-1] != 0.0) {
	for (j=1;j<=l;j++) {
	  g=0.0;
	  for (k=1;k<=l;k++) {
	    g+= value[i-1][k-1]*value[k-1][j-1];
	  }
	  for (k=1;k<=l;k++) {
	    value[k-1][j-1] -= g*value[k-1][i-1];
	  }
	}
      }
      d[i-1] = value[i-1][i-1];
      value[i-1][i-1] = 1.0;
      for (j=1;j<=l;j++) {
	value[j-1][i-1] = 0.0;
	value[i-1][j-1] = 0.0;
      }
    }
  }

  public void tqli() {
    int n = rows;

    int m;
    int l;
    int iter;
    int i;
    int k;
    double s;
    double r;
    double p;;
    double g;
    double f;
    double dd;
    double c;
    double b;
    
    for (i=2;i<=n;i++) {
      e[i-2] = e[i-1];
    }
    e[n-1] = 0.0;
    for (l=1;l<=n;l++) {
      iter=0;
      do {
	for (m=l;m<=(n-1);m++) {
	  dd=Math.abs(d[m-1]) + Math.abs(d[m]);
	  if (Math.abs(e[m-1]) + dd == dd) break;
	}
	if (m != l) {
	  iter++;
	  if (iter == 30) {
	    System.out.print("Too many iterations in tqli");
	    System.exit(0);
	  } else {
	    //	    System.out.println("Iteration " + iter);
	  }
	  g=(d[l]-d[l-1])/(2.0*e[l-1]);
	  r = Math.sqrt((g*g) + 1.0);
	  g=d[m-1]-d[l-1]+e[l-1]/(g + sign(r,g));
	  c = 1.0;
	  s = c;
	  p=0.0;
	  for (i=m-1;i>=l;i--) {
	    f = s*e[i-1];
	    b = c*e[i-1];
	    if (Math.abs(f) >= Math.abs(g)) {
	      c=g/f;
	      r = Math.sqrt((c*c)+1.0);
	      e[i] = f*r;
	      s = 1.0/r;
	      c *= s;
	    } else {
	      s=f/g;
	      r = Math.sqrt((s*s)+1.0);
	      e[i] = g*r;
	      c = 1.0/r;
	      s *= c;
	    }
	    g=d[i] -p;
	    r=(d[i-1]-g)*s + 2.0*c*b;
	    p=s*r;
	    d[i] = g + p;
	    g = c * r - b;
	    for (k=1; k <= n; k++) {
	      f=value[k-1][i];
	      value[k-1][i] = s*value[k-1][i-1] + c*f;
	      value[k-1][i-1] = c*value[k-1][i-1] - s*f;
	    }
	  }
	  d[l-1] = d[l-1] - p;
	  e[l-1] = g;
	  e[m-1] = 0.0;
	}
      } while ( m != l);
    }
  }
  public void tred2() {
    int n = rows;
    int l;
    int k;
    int j;
    int i;

    double scale;
    double hh;
    double h;
    double g;
    double f;

    this.d = new double[rows];
    this.e = new double[rows];

    for (i=n-1; i >= 1;i--) {
      l=i-1;
      h = 0.0;
      scale = 0.0;
      
      if (l > 0) {
	for (k=0;k<l;k++) {
	  scale += Math.abs(value[i][k]);
	}
	if (scale == 0.0) {
	  e[i] = value[i][l];
	} else {
	  for (k=0; k < l; k++) {
	    value[i][k] /= scale;
	    h += value[i][k]*value[i][k];
	  }
	  f = value[i][l];
	  if (f>0) {
	    g = -1.0*Math.sqrt(h);
	  } else {
	    g = Math.sqrt(h);
	  }
	  e[i] = scale*g;
	  h -= f*g;
	  value[i][l] = f-g;
	  f=0.0;
	  for (j=0; j < l; j++) {
	    value[j][i] = value[i][j]/h;
	    g=0.0;
	    for (k= 0; k < j; k++) {
	      g += value[j][k]*value[i][k];
	    }
	    for (k=j; k<l;k++) {
	      g+= value[k][j]*value[i][k];
	    }
	    e[j] = g/h;
	    f+=e[j]*value[i][j];
	  }
	  hh=f/(h+h);
	  for (j=0;j<l;j++) {
	    f=value[i][j];
	    g = e[j] - hh*f;
	    e[j] = g;
	    for (k=0;k<j;k++) {
	      value[j][k] -= (f*e[k]+g*value[i][k]);
	    }
	  }
	}
      } else {
	e[i] = value[i][l];
      }
      d[i] = h;
    }
    d[0] = 0.0;
    e[0] = 0.0;
    for (i=0;i<n;i++) {
      l=i-1;
      if (d[i] != 0.0) {
	for (j=0;j<l;j++) {
	  g=0.0;
	  for (k=0;k<l;k++) {
	    g+= value[i][k]*value[k][j];
	  }
	  for (k=0;k<l;k++) {
	    value[k][j] -= g*value[k][i];
	  }
	}
      }
      d[i] = value[i][i];
      value[i][i] = 1.0;
      for (j=0;j<l;j++) {
	value[j][i] = 0.0;
	value[i][j] = 0.0;
      }
    }
  }

  public void tqli2() {
    int n = rows;

    int m;
    int l;
    int iter;
    int i;
    int k;
    double s;
    double r;
    double p;;
    double g;
    double f;
    double dd;
    double c;
    double b;
    
    for (i=2;i<=n;i++) {
      e[i-2] = e[i-1];
    }
    e[n-1] = 0.0;
    for (l=1;l<=n;l++) {
      iter=0;
      do {
	for (m=l;m<=(n-1);m++) {
	  dd=Math.abs(d[m-1]) + Math.abs(d[m]);
	  if (Math.abs(e[m-1]) + dd == dd) break;
	}
	if (m != l) {
	  iter++;
	  if (iter == 30) {
	    System.out.print("Too many iterations in tqli");
	    System.exit(0);
	  } else {
	    //	    System.out.println("Iteration " + iter);
	  }
	  g=(d[l]-d[l-1])/(2.0*e[l-1]);
	  r = Math.sqrt((g*g) + 1.0);
	  g=d[m-1]-d[l-1]+e[l-1]/(g + sign(r,g));
	  c = 1.0;
	  s = c;
	  p=0.0;
	  for (i=m-1;i>=l;i--) {
	    f = s*e[i-1];
	    b = c*e[i-1];
	    if (Math.abs(f) >= Math.abs(g)) {
	      c=g/f;
	      r = Math.sqrt((c*c)+1.0);
	      e[i] = f*r;
	      s = 1.0/r;
	      c *= s;
	    } else {
	      s=f/g;
	      r = Math.sqrt((s*s)+1.0);
	      e[i] = g*r;
	      c = 1.0/r;
	      s *= c;
	    }
	    g=d[i] -p;
	    r=(d[i-1]-g)*s + 2.0*c*b;
	    p=s*r;
	    d[i] = g + p;
	    g = c * r - b;
	    for (k=1; k <= n; k++) {
	      f=value[k-1][i];
	      value[k-1][i] = s*value[k-1][i-1] + c*f;
	      value[k-1][i-1] = c*value[k-1][i-1] - s*f;
	    }
	  }
	  d[l-1] = d[l-1] - p;
	  e[l-1] = g;
	  e[m-1] = 0.0;
	}
      } while ( m != l);
    }
  }
			       
  public double sign(double a, double b) {
    if (b < 0) {
      return -Math.abs(a);
    } else {
      return Math.abs(a);
    }
  }

  public double[] getColumn(int n) {
    double[] out  = new double[rows];
    for (int i=0;i<rows;i++) {
      out[i] = value[i][n];
    }
    return out;
  }


  public void printD(PrintStream ps) {
    
    for (int j = 0; j < rows;j++) {
      Format.print(ps,"%15.4e",d[j]);
    }
  }
  public void printE(PrintStream ps) {
    
    for (int j = 0; j < rows;j++) {
      Format.print(ps,"%15.4e",e[j]);
    }
  }

  public static void main(String[] args) {
    int n = Integer.parseInt(args[0]);
    double[][] in = new double[n][n];
 
    for (int i = 0;i < n;i++) {
      for (int j = 0; j < n; j++) {
	in[i][j] = (double)Math.random();
      }
    }

    Matrix origmat = new Matrix(in,n,n);
    //    System.out.println(" --- Original matrix ---- ");
    ///    origmat.print(System.out);
      //System.out.println();

      //System.out.println(" --- transpose matrix ---- ");
    Matrix trans = origmat.transpose();
    //trans.print(System.out);
    //System.out.println();
    
    //System.out.println(" --- OrigT * Orig ---- ");
    
    Matrix symm = trans.postMultiply(origmat);
    //symm.print(System.out);
    //System.out.println();   

    // Copy the symmetric matrix for later
    Matrix origsymm = symm.copy();
    

    // This produces the tridiagonal transformation matrix
    long tstart = System.currentTimeMillis();
    symm.tred();
    long tend = System.currentTimeMillis();
    //System.out.println("Time take for tred = " + (tend-tstart) + "ms");
    //System.out.println(" ---Tridiag transform matrix ---");
    //symm.print(System.out);
    //System.out.println();   
   
    //System.out.println(" --- D vector ---");
    //symm.printD(System.out);
    //System.out.println();   
    //System.out.println(" --- E vector ---");
    //symm.printE(System.out);
    //System.out.println();   


    // Now produce the diagonalization matrix
    tstart = System.currentTimeMillis();
    symm.tqli();
    tend = System.currentTimeMillis();
    //System.out.println("Time take for tqli = " + (tend-tstart) + " ms");

    //System.out.println(" --- New diagonalization matrix ---");
    //symm.print(System.out);
    //System.out.println();

    //System.out.println(" --- D vector ---");
    //symm.printD(System.out);
    //System.out.println();   
    //System.out.println(" --- E vector ---");
    //symm.printE(System.out);
    //System.out.println();   

    //System.out.println(" --- First eigenvector --- ");
    //double[] eigenv = symm.getColumn(0);
    //for (int i=0; i < eigenv.length;i++) {
    //  Format.print(System.out,"%15.4f",eigenv[i]);
    // }
    //System.out.println();      

    //double[] neigenv = origsymm.vectorPostMultiply(eigenv);
  
    //for (int i=0; i < neigenv.length;i++) {
    //  Format.print(System.out,"%15.4f",neigenv[i]/symm.d[0]);
    //}

    //System.out.println();      
  }
}


















