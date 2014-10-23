package com.andreldm.rcontrol.server;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
	public static void main(String[] args) {
		try {
			Util.loadLib();

			switch(Util.whichOS()) {
			case Constants.OS_LINUX:
			    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			    break;
			case Constants.OS_WINDOWS:
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			    break;
			default:
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    break;
			}

	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                Window w = new Window();
	                w.setVisible(true);
	            }
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
