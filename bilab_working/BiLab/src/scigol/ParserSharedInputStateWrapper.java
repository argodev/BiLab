
package scigol;


  
public class ParserSharedInputStateWrapper extends antlr.ParserSharedInputState
{
  public ParserSharedInputStateWrapper(CombinedSharedInputState istate)
  {
    state = istate;
  }
  
  
  public CombinedSharedInputState state;
}


