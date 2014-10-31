package com.andreldm.rcontrol.server;

import java.nio.file.Paths;

import javax.swing.JOptionPane;

public class Util {
	public static int whichOS() {
		String os = System.getProperty("os.name");
		if (os == null || os.isEmpty()) {
			return Constants.OS_UNKNOWN;
		}

		if (os.toLowerCase().contains("linux")) {
			return Constants.OS_LINUX;
		}

		if (os.toLowerCase().contains("windows")) {
			return Constants.OS_WINDOWS;
		}

		return Constants.OS_UNKNOWN;
	}

	public static int whichArch() {
		String arch = System.getProperty("sun.arch.data.model");
		if (arch == null || arch.isEmpty()) {
			return Constants.ARCH_UNKNOWN;
		}

		if (arch.toLowerCase().contains("32")) {
			return Constants.ARCH_32;
		}

		if (arch.toLowerCase().contains("64")) {
			return Constants.ARCH_64;
		}

		return Constants.ARCH_UNKNOWN;
	}

	public static boolean loadLib() {
		String errorMsg = "Error while loading native library ([lib]rcontrol).";

	    try {
	    	String path = Paths.get(".").toAbsolutePath().normalize().toString();
	    	path += "/lib/";
	    	
	    	switch (Util.whichArch()) {
			case Constants.ARCH_32:
				System.load(path + System.mapLibraryName("rcontrol32"));
				System.out.println("Library: 32-bits");
				return true;

			case Constants.ARCH_64:
				System.load(path + System.mapLibraryName("rcontrol64"));
				System.out.println("Library: 64-bits");
				return true;

			default:
				throw new Exception(errorMsg);
			}
	    } catch (Throwable e) {
			JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	        System.exit(1);
	    }

	    return false;
	}
}
