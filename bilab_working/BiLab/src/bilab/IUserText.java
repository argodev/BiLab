package bilab;

public interface IUserText
{
  @Sophistication(Sophistication.Developer)
  String get_ShortText();
  
  @Sophistication(Sophistication.Developer)
  String get_DetailText();
}