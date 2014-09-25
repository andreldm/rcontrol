package com.andreldm.rcontrol.server;

import java.util.HashMap;

public class Constants {
	public static final int CMD_PLAY = 0;
	public static final int CMD_STOP = 1;
	public static final int CMD_PREVIOUS = 2;
	public static final int CMD_NEXT = 3;

	public static final int OS_UNKNOWN = 0;
	public static final int OS_LINUX = 1;
	public static final int OS_WINDOWS = 2;

	public static final int ARCH_UNKNOWN = 0;
	public static final int ARCH_32 = 1;
	public static final int ARCH_64 = 2;

    public static final HashMap<Integer, Integer> commands = new HashMap<>();
	static {
		switch (Util.whichOS()) {
		case OS_LINUX:
	    	commands.put(CMD_PLAY, 0x1008FF14); 	// XF86XK_AudioPlay
	    	commands.put(CMD_STOP, 0x1008FF15); 	// XF86XK_AudioStop
	    	commands.put(CMD_PREVIOUS, 0x1008FF16); // XF86XK_AudioPrev
	    	commands.put(CMD_NEXT, 0x1008FF17); 	// XF86XK_AudioNext
			break;
		case OS_WINDOWS:
	    	commands.put(CMD_PLAY, 0xB3); 			// VK_PLAY_PAUSE
	    	commands.put(CMD_STOP, 0xB2); 			// VK_STOP
	    	commands.put(CMD_PREVIOUS, 0xB1); 		// VK_PREVIOUS
	    	commands.put(CMD_NEXT, 0xB0); 			// VK_NEXT
			break;

		default:
			// TODO: Error!
			break;
		}

    }

}
