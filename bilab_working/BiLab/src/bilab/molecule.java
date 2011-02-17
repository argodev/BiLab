package bilab;


import scigol.accessor;



public interface molecule extends IUserText, IAnnotated
{
  
  @Summary("true if the structure of this molecule is represented")
  boolean get_StructureKnown();

  @accessor
  @Summary("A human-readable, name for this molecule")
  String get_name();

  @accessor
  @Summary("Resource associated with this molecule, or null if none exists")
  @Sophistication(Sophistication.Developer)
  String get_AssociatedResource();

  @Sophistication(Sophistication.Advanced)
  String ToMDL();
}

