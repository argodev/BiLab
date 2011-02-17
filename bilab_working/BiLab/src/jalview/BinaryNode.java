package jalview;





public class BinaryNode {

  Object element;

  BinaryNode left;

  BinaryNode right;

  BinaryNode parent;



  public BinaryNode() {

    left = right = parent = null;

  }

  public BinaryNode(Object element, BinaryNode parent) {

    this.element = element;

    this.parent = parent;

    left=right=null;

  }



  public Object element() {return element;}

  public Object setElement(Object v) { return element=v;}



  public BinaryNode left() { return left;}

  public BinaryNode setLeft(BinaryNode n) {return left=n;}

  public BinaryNode right() { return right;}

  public BinaryNode setRight(BinaryNode n) {return right=n;}



  public boolean isLeaf() {

    return (left == null) && (right == null);

  }



}

