package jalview;



import java.util.*;



public class ListProperties {



  public ListProperties() {

  }

  public static void print() {

    Properties p = System.getProperties();

    Enumeration e = p.propertyNames();



    while (e.hasMoreElements()) {

      String prop = (String)e.nextElement();

      System.out.println(prop + " - " + p.getProperty(prop));

    }

  }

  public static void main(String[] args) {

    ListProperties.print();

  }

}

