/* Jalview - a java multiple alignment editor
 * Copyright (C) 1998  Michele Clamp
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jalview;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class PostscriptPopup extends Popup {
  TextField tf;
  Label tfLabel;
  Mail mail;
  Label fontLabel;
  Choice font;
  Label fontsizeLabel;
  Choice fontSize;
  Label orientLabel;
  Choice orient;
  Label sizeLabel;
  Choice size;
  OutputGenerator og;
  public PostscriptPopup(Frame parent,String title, OutputGenerator og) {
    super(parent,title);
    this.og = og;

    createInterface();
  }

  public void createInterface() {
    tf = new TextField(40);
    tfLabel = new Label("Mail address : ");
    tf.setText(og.getMailProperties().address);
    
    fontLabel = new Label("Font");
    font = new Choice();
    font.addItem("Times-Roman");
    font.addItem("Courier");
    font.addItem("Helvetica");
    
    fontsizeLabel = new Label("Font size");
    fontSize = new Choice();
    fontSize.addItem("1");
    fontSize.addItem("2");
    fontSize.addItem("4");
    fontSize.addItem("6");
    fontSize.addItem("8");
    fontSize.addItem("10");
    fontSize.addItem("12");
    fontSize.addItem("14");
    fontSize.addItem("16");
    fontSize.addItem("20");
    fontSize.addItem("24");

    fontSize.select(new Integer(8).toString());

    orientLabel = new Label("Orientation");
    orient = new Choice();
    orient.addItem("Portrait");
    orient.addItem("Landscape");

    sizeLabel = new Label("Paper size");
    size = new Choice();
    size.addItem("A4");
    size.addItem("US letter");
    size.addItem("US letter small");

    gbc.fill = GridBagConstraints.NONE; 

    gbc.insets = new Insets(10,10,10,10);

    add(tfLabel,gb,gbc,0,0,1,1);
    add(tf,gb,gbc,1,0,4,1);

    add(fontLabel,gb,gbc,0,1,1,1);
    add(font,gb,gbc,1,1,1,1);
    
    add(fontsizeLabel,gb,gbc,0,2,1,1);
    add(fontSize,gb,gbc,1,2,1,1);
    add(orientLabel,gb,gbc,0,3,1,1);
    add(orient,gb,gbc,1,3,1,1);

    add(sizeLabel,gb,gbc,0,4,1,1);
    add(size,gb,gbc,1,4,1,1);

    add(status,gb,gbc,0,5,1,1);
    add(apply,gb,gbc,0,6,1,1);
    add(close,gb,gbc,1,6,1,1);
    
    this.pack();
    this.show();
    
  }

  public boolean handleEvent(Event evt) {
    if (evt.target == apply && evt.id == 1001) {
      String address = tf.getText();
      status.setText("Checking email address");
      status.validate();
      // Do some checks on the mail address
      if (address.indexOf('@') == -1) {
	status.setText("Invalid mail address (enter name@my.email.server )");
	status.validate();
	this.hide();
	this.dispose();
      } else {
	status.setText("done");
	status.validate();
	System.out.println("Mail address " + address) ;

	status.setText("Connecting to mail server");
	status.validate();
	mail = new Mail();
	
	// Send to the recipient running the applet
	String recipient = address;
	String author = "<michele@ebi.ac.uk>";
	String subject = "Jalview alignment";
	String text = "";
	
	status.setText("Mail Server = " + og.getMailProperties().server);
	status.validate();
	mail.send(og.getMailProperties().server,recipient,author,subject,text);
	
	sendText();
	mail.finish();
	
	this.hide();
	this.dispose();
      }
      return true;
    } else {
      return super.handleEvent(evt);
    }
  }

  public void sendText() {
    // Set the options
    og.getPostscriptProperties().font = font.getSelectedItem();
    og.getPostscriptProperties().fsize = (Integer.valueOf(fontSize.getSelectedItem()).intValue());
    
    if (orient.getSelectedItem().equals("Landscape")) {
      og.getPostscriptProperties().orientation = PostscriptProperties.LANDSCAPE;
    } else {
      og.getPostscriptProperties().orientation = PostscriptProperties.PORTRAIT;
    }

    if (size.getSelectedItem().equals("US letter")) {
      og.getPostscriptProperties().width = 576;
      og.getPostscriptProperties().height = 776;
    } else if (size.getSelectedItem().equals("US letter small")) {
      og.getPostscriptProperties().width = 552;
      og.getPostscriptProperties().height = 730;
    }
    // Now generate postscript
    status.setText("Generating postscript...(this takes a while)");
    og.getPostscript(mail.out);
    //    StringBuffer outbuf = og.getPostscript();
    //System.out.println(outbuf.toString());
    status.setText("done");
  }

}


