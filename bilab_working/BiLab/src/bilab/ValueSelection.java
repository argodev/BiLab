package bilab;

import org.eclipse.jface.viewers.ISelection;
import scigol.Value;


public class ValueSelection implements ISelection
{
  public ValueSelection(Value v) { value=v; }
  
  
  public boolean isEmpty() { return value==null; }
  
  public Value value;
}
