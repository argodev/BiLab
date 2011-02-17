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

 

 public class ThresholdPanel extends Panel {

    int low;

    int high;

    Scrollbar sb;

    Label label;

    TextField tf;

    GridBagLayout gb;

    GridBagConstraints gbc;

    

    public ThresholdPanel(Frame parent,String label,int low, int high, int value) {

	this.low = low;

	this.high = high;

	

	gb = new GridBagLayout();

        gbc = new GridBagConstraints();



	gbc.fill = GridBagConstraints.NONE;

	gbc.weightx = 100;

	gbc.weighty = 100;



	setLayout(gb);



	

	this.sb = new Scrollbar(Scrollbar.HORIZONTAL,value,(low-high)/100,low,high+1);

	this.label = new Label(label);

	this.tf = new TextField( new Integer(value).toString(),3);

	add(this.label,gb,gbc,0,0,1,1);

	add(tf,gb,gbc,1,0,1,1);

	gbc.insets = new Insets(10,10,10,10);

	gbc.fill = GridBagConstraints.HORIZONTAL;

	add(sb,gb,gbc,0,1,2,1);

    }

   public boolean action(Event e, Object arg) {

     if (e.target == tf) {

       sb.setValue(Integer.valueOf(tf.getText()).intValue());

       return false;

     } else {

       return super.action(e,arg);

     }

   }

   public boolean handleEvent(Event evt) {

    if (evt.target == sb) {

      tf.setText(new Integer(sb.getValue()).toString());

      return super.handleEvent(evt);

    } else return super.handleEvent(evt);

   } 

  public Dimension minimumSize() {

    return new Dimension(250,150);

  }

  public Dimension preferredSize() {

    return minimumSize();

  }

 public void add(Component c,GridBagLayout gbl, GridBagConstraints gbc,

		  int x, int y, int w, int h) {

    gbc.gridx = x;

    gbc.gridy = y;

    gbc.gridwidth = w;

    gbc.gridheight = h;

    gbl.setConstraints(c,gbc);

    add(c);

  }

    public static void main(String[] args) {

	Frame f = new Frame("ThresholdPanel");

	f.setSize(250,150);

	ThresholdPanel tp = new ThresholdPanel(f,"Percent Identity",0,100,80);

	f.add(tp);

	f.show();

    }

   }    

