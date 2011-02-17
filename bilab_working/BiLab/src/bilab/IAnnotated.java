package bilab;

public interface IAnnotated
{
  @Summary("get a map of annotation (key -> value)")
  scigol.Map get_annotations();
}