package jalview;

import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;

 public class SimpleBrowser {
   String server;
   int port;
   String out;
   BrowserFrame bf;
   Vector pages;
   int position = 0;

   public static void main(String[] args) {
     if (args.length == 2) {
       SimpleBrowser sb = new SimpleBrowser("srs.ebi.ac.uk",80,
					    "srs5bin/cgi-bin/wgetz?-e+["+args[0] + "-id:" + args[1] + "]");
     } else if (args.length == 1) {
       SimpleBrowser sb = new SimpleBrowser(args[0]);
     } else {
       SimpleBrowser sb = new SimpleBrowser("");
     }
   }
   public SimpleBrowser(String url) {
     pages = new Vector();
     pages.addElement(url);
     frameInit(url);

   }
   public SimpleBrowser(String server, int port, String page) {
     this.server = server;
     this.port = port;

     if (page.indexOf("/") != 0) {
       page = "/" + page;
     }

     String url = "http://"+ server + ":" + port + page;
     pages = new Vector();
     pages.addElement(url);
     frameInit(url);

   }

   public void frameInit(String url) {
   
     bf = new BrowserFrame(this,"SimpleBrowser",25,72,"",url);

     bf.ta.setFont(new java.awt.Font("Courier",java.awt.Font.PLAIN,12));
     bf.resize(700,500);
     bf.show();
     bf.back.disable();
     bf.forward.disable();
     bf.status.setText("Connecting to server");

     connect(split(url));
   }

   public void connect(String page) {
     bf.status.setText("Connecting to server...");
     try{
       Socket socket = new Socket(server,port);
       //Set the server connect timeout to 5 seconds
       socket.setSoTimeout(5000);
       BufferedReader inputStream = 
                  new BufferedReader(new InputStreamReader(
                                  socket.getInputStream()));
       PrintWriter outputStream = 
                    new PrintWriter(new OutputStreamWriter(
                            socket.getOutputStream()),true);

       bf.status.setText("Reading URL...");
       outputStream.println("GET " + page + " HTTP/1.0");
       outputStream.println("Host: " + server);
       outputStream.println();
       String line = "";
       String text = "";
       int count = 0;
       boolean headers = true;
       while((line = inputStream.readLine()) != null) {
	 if (line.equals("")) {
	   headers = false;
	 }
	 if (!headers && !line.equals("")) {
	   text = text + parse(line) + "\n";
	 }
	 if (count != 1) {
	   bf.status.setText("Read " + count + " lines");
	 } else {
	   bf.status.setText("Read " + count + " line");
	 }
	 count++;
       }
       bf.setText(text);
       bf.status.setText("done");

       socket.close();
       
     }  catch(UnknownHostException e){
       System.out.println(e);
       bf.setText("Not online");
       bf.status.setText("");
     } catch (SocketException e) {
       System.out.println("Socket Exception " + e);
       bf.setText("Socket exception");      
       bf.status.setText("");
     } catch (InterruptedIOException e) {
       System.out.println("Read to server timed out " + e);
       bf.setText("Server connect timed out");
       bf.status.setText("");
     }  catch(IOException e){
       bf.setText("IO exception");
       bf.status.setText("");
       System.out.println("IOException " + e);
     }
   }

   public boolean action(Event e, Object arg) {
     System.out.println(e + " " + arg);
     if (e.target == bf.tf) {
       System.out.println("new page");
       String url = bf.tf.getText();
       pages.addElement(url);
       String page = split(url);
       System.out.println("Server = " + server);
       System.out.println("Port = " + port);
       System.out.println("Page = " + page);
       connect(page);
       position++;
       bf.back.enable();
       return true;
     } else if (e.target == bf.back) {
       System.out.println("pos " + position + " " + pages.size());
       if (position > 0)  {
	 position--;
	 String page = split((String)pages.elementAt(position));
	 bf.tf.setText((String)pages.elementAt(position));
	 connect(page);
	 if (position == 0) { bf.back.disable();}
	 bf.forward.enable();
       }
       return true;
     } else if (e.target == bf.forward) {
       if (position != pages.size()-1) {
	 position++;
	 bf.tf.setText((String)pages.elementAt(position));
	 String page = split((String)pages.elementAt(position));
	 if (position == pages.size()-1) {bf.forward.disable();}
	 bf.back.enable();
	 connect(page);
       }
       return true;
     } else if (e.target == bf.b) {
       System.out.println("Disposing of browser frame");
       bf.hide();
       bf.dispose();
       bf = null;
       return true;
     } else {
       return false;
     }
   }
   public boolean handleEvent(Event e) {
     if (e.id == Event.WINDOW_DESTROY) {
       System.out.println("Disposing of browser frame");
       bf.hide();
       bf.dispose();
       bf = null;
       return true;
     } else {
       return false;
     }
   }
   public static String removeString(String line, String off) {
     String tmp2 = "";
     
     while (line.indexOf(off) >= 0) {
       String tmp =  line.substring(0,line.indexOf(off)) + line.substring(line.indexOf(off) + off.length());
       line = tmp;
     }
     return line;
   }
   public static String parse(String line) {
       String out = ""; 
       line = SimpleBrowser.removeString(line,"&nbsp;");
       if (line.indexOf("<") >= 0) {
       StringTokenizer st = new StringTokenizer(line,"<");
       
       while ( st.hasMoreTokens()) {
	 String tmp = st.nextToken();
	
	 if (tmp.indexOf(">") >= 0) {
	   out  = out + tmp.substring(tmp.indexOf(">")+1);
	 } else {
	   out = out + tmp;
	 }
       }
       
     } else {
       out = line;
     }
     return out;
   }

   public String split(String url) {

     System.out.println(url);

     if (url.indexOf("http://") == 0) {
       url = url.substring(7);
     }

     System.out.println("URL = " + url);

     String page = "/";
     if (url.indexOf("/") != -1) {
       page = url.substring(url.indexOf("/"));
       url = url.substring(0,url.indexOf("/"));
     } 
     
     System.out.println("PAge = " + page);
     System.out.println("Url = " + url);

     if (url.indexOf(":") >= 0) {
       port = Integer.parseInt(url.substring(url.indexOf(":")+1));
       server = url.substring(0,url.indexOf(":"));
     } else {
       port = 80;
       if (url.indexOf("/") != -1) {
	 server = url.substring(0,url.indexOf("/"));
       } else {
	 server = url;
       }
     }
     System.out.println(server);
     System.out.println(port);
     return page;
   }
  
 

 }











