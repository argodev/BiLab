package jalview;

import java.awt.Color;

public class SequenceNode extends BinaryNode {

  public float dist;
  public int count;
  public float height;
  public float ycount;
  public Color color = Color.black;

  public SequenceNode() {
    super();
  }

  public SequenceNode(Object val, SequenceNode parent, float dist) {
    super(val,parent);
    this.dist = dist;
  }
}
