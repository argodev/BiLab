package MCview;

public class Matrix {
  float matrix[][];
  float tmp[][];

  float mycos;
  float mysin;
  float myconst = (float)(Math.PI/180);

  public Matrix(int rows, int cols) {
    matrix = new float[rows][cols];
    tmp = new float[rows][cols];
  }

  public void addElement(int i, int j, float value) {
    matrix[i][j] = value;
  }

  public void print() {
    System.out.println(matrix[0][0] + " " + matrix[0][1] + " " + matrix[0][2]);
    System.out.println(matrix[1][0] + " " + matrix[1][1] + " " + matrix[1][2]);
    System.out.println(matrix[2][0] + " " + matrix[2][1] + " " + matrix[2][2]);
  } 
  public void rotatex(float degrees) {
    mycos = (float)(Math.cos(degrees*myconst));
    mysin = (float)(Math.sin(degrees*myconst));
    tmp[0][0] = 1;
    tmp[0][1] = 0;
    tmp[0][2] = 0;
    tmp[1][0] = 0;
    tmp[1][1] = mycos;
    tmp[1][2] = mysin;
    tmp[2][0] = 0;
    tmp[2][1] = -mysin;
    tmp[2][2] = mycos; 
    preMultiply(tmp);
  }

  public void rotatez(float degrees) {
    mycos = (float)(Math.cos(degrees*myconst));
    mysin = (float)(Math.sin(degrees*myconst));
    tmp[0][0] = mycos;
    tmp[0][1] = -mysin;
    tmp[0][2] = 0;
    tmp[1][0] = mysin;
    tmp[1][1] = mycos;
    tmp[1][2] = 0;
    tmp[2][0] = 0;
    tmp[2][1] = 0;
    tmp[2][2] = 1;
    preMultiply(tmp);
  }
  public void rotatey(float degrees) {
    mycos = (float)(Math.cos(degrees*myconst));
    mysin = (float)(Math.sin(degrees*myconst));
    tmp[0][0] = mycos;
    tmp[0][1] = 0;
    tmp[0][2] = -mysin;
    tmp[1][0] = 0;
    tmp[1][1] = 1;
    tmp[1][2] = 0;
    tmp[2][0] = mysin;
    tmp[2][1] = 0;
    tmp[2][2] = mycos;
    preMultiply(tmp);
  }

  public float[] vectorMultiply(float[] vect) {
     float temp[] = new float[3];

     temp[0] = vect[0];
     temp[1] = vect[1];
     temp[2] = vect[2];
    for (int i = 0; i < 3; i++) {
       temp[i] = (float)matrix[i][0]*vect[0] + (float)matrix[i][1]*vect[1] +(float)matrix[i][2]*vect[2];
    }
     vect[0] = temp[0];
     vect[1] = temp[1];
     vect[2] = temp[2];

     return vect;
  }

  public void preMultiply(float mat[][]) {
    float tmp[][]  = new float[3][3];

    for (int i = 0; i < 3 ; i++) {
      for (int j = 0; j < 3; j++ ){
         tmp[i][j] = mat[i][0]*matrix[0][j] +
                     mat[i][1]*matrix[1][j] +
                     mat[i][2]*matrix[2][j];
      }
    }

    for (int i = 0; i < 3 ; i++) {
      for (int j = 0; j < 3; j++ ){
          matrix[i][j] = tmp[i][j];
      }
    }
  }

  public void postMultiply(float mat[][]) {
    float tmp[][]  = new float[3][3];

    for (int i = 0; i < 3 ; i++) {
      for (int j = 0; j < 3; j++ ){
         tmp[i][j] = matrix[i][0]*mat[0][j] + 
                     matrix[i][1]*mat[1][j] +
                     matrix[i][2]*mat[2][j];
      }
    }

    for (int i = 0; i < 3 ; i++) {
      for (int j = 0; j < 3; j++ ){
          matrix[i][j] = tmp[i][j];
      }
    }
  }    

  public void setIdentity() {
     matrix[0][0] = 1;
     matrix[1][1] = 1;
     matrix[2][2] = 1;
     matrix[0][1] = 0;
     matrix[0][2] = 0;
     matrix[1][0] = 0;
     matrix[1][2] = 0;
     matrix[2][0] = 0;
     matrix[2][1] = 0;
  }
}


