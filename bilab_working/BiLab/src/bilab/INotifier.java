package bilab;


/// interface for notifications
public interface INotifier
{
  void StartProgress(Object from, String task);
  void Progress(Object from, double percent);
  void EndProgress(Object from);

  void PushLevel(Object from);
  void PopLevel(Object from);

  void UserStatus(Object from, String message);
  void UserInfo(Object from, String message);
  void UserWarning(Object from, String message);
  void UserError(Object from, String message);

  void DevStatus(Object from, String message);
  void DevInfo(Object from, String message);
  void DevWarning(Object from, String message);
  void DevError(Object from, String message);

  void LogInfo(Object from, String message);
  void LogError(Object from, String message);
}


