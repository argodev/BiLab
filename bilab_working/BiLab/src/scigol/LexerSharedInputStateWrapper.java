
package scigol;

import java.io.*;

  
public class LexerSharedInputStateWrapper extends antlr.LexerSharedInputState
{
  public LexerSharedInputStateWrapper(CombinedSharedInputState istate, antlr.InputBuffer inbuf) 
  {
    super(inbuf);
    initialize(istate);
  }
  
  public LexerSharedInputStateWrapper(CombinedSharedInputState istate, InputStream inStream)
  {
    super(inStream);
    initialize(istate);
  }
  
  public LexerSharedInputStateWrapper(CombinedSharedInputState istate, Reader inReader)
  {
    super(inReader);
    initialize(istate);
  }
  

  protected void initialize(CombinedSharedInputState istate)
  {
    state = istate;
  }
    
  
  public CombinedSharedInputState state;
}

