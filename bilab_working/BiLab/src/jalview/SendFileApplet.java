package jalview;

import java.applet.Applet;
import java.net.*;

public class SendFileApplet extends Applet {

 public void init() {
    String out = "public class PostPutFile\n    public static int put(URL url, String filename, byte bytes[])\n        throws IOException, MalformedURLException";
    SendFileCGI sf= new SendFileCGI("circinus.ebi.ac.uk",6543,"cgi-bin/sendfile","/ebi/barton/delly/michele/htdocs/temp/out.ps",System.out,out);
    sf.run();
    try {
    URL fileski = new URL("http://circinus.ebi.ac.uk:6543/temp/out.ps");
    getAppletContext().showDocument(fileski);
    } catch (MalformedURLException e) {
      System.out.println(e);
    }    
  }
}
