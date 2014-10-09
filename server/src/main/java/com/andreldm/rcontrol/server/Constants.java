package com.andreldm.rcontrol.server;

import java.util.HashMap;

public class Constants {
	public static final int CMD_PLAY = 0;
	public static final int CMD_STOP = 1;
	public static final int CMD_PREVIOUS = 2;
	public static final int CMD_NEXT = 3;
	public static final int CMD_VOLUP = 4;
	public static final int CMD_VOLDOWN = 5;
	public static final int CMD_MUTE = 6;

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
	    	commands.put(CMD_VOLUP, 0x1008FF13); 	// XF86XK_AudioRaiseVolume
	    	commands.put(CMD_VOLDOWN, 0x1008FF11); 	// XF86XK_AudioLowerVolume
	    	commands.put(CMD_MUTE, 0x1008FF12); 	// XF86XK_AudioMute
			break;
		case OS_WINDOWS:
	    	commands.put(CMD_PLAY, 0xB3); 			// VK_MEDIA_PLAY_PAUSE
	    	commands.put(CMD_STOP, 0xB2); 			// VK_MEDIA_STOP
	    	commands.put(CMD_PREVIOUS, 0xB1); 		// VK_MEDIA_PREV_TRACK
	    	commands.put(CMD_NEXT, 0xB0); 			// VK_MEDIA_NEXT_TRACK
	    	commands.put(CMD_VOLUP, 0xAF); 			// VK_VOLUME_UP
	    	commands.put(CMD_VOLDOWN, 0xAE); 			// VK_VOLUME_DOWN
	    	commands.put(CMD_MUTE, 0xAD); 			// VK_VOLUME_MUTE
			break;

		default:
			// TODO: Error!
			break;
		}

    }

}
