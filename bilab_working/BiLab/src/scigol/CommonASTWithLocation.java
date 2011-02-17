package scigol;

	
import antlr.collections.AST;  
import antlr.CommonAST;
import antlr.Token;



public class CommonASTWithLocation extends CommonAST 
{
  public CommonASTWithLocation()
  {
     loc = new Location(0,0,"?");
  }
  
//  protected CommonASTWithLocation(CommonASTWithLocation another) : base(another)
//  {
//    _location = another._location;
//  }
   
  public CommonASTWithLocation(Token tok)
  {
    if (!(tok instanceof CommonTokenWithLocation))
      initialize(new CommonTokenWithLocation(tok));
    else
      initialize(tok);
  }
                                                                                                                                                                                                                                             
  
  
  public void initialize(Token tok) {
    if (tok instanceof CommonTokenWithLocation)
      initialize((CommonTokenWithLocation)tok);
    else {
      super.initialize(tok);
      loc = new Location(tok.getLine(), tok.getColumn(), tok.getFilename());
    }
  }
  
  public void initialize(CommonTokenWithLocation tok) {
    super.initialize(tok);
    loc = new Location(tok.getLine(), tok.getColumn(), tok.getFilename());
  }
  
  
  public void  initialize(int t, String txt)
  {
    super.initialize(t,txt);
    loc = new Location(0,0,"?");
  }
  
  public void  initialize(int t, String txt, Location location)
  {
    initialize(t,txt);
    loc = location;
  }
  
  public void  initialize(AST t)
  {
    super.initialize(t);
    loc = new Location(0,0,"?");
  }
  
//  override public object Clone()
//  {
//    return new CommonASTWithLocation(this);
//  }   
  
  public Location loc;
}

