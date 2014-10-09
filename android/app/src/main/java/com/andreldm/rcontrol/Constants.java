package com.andreldm.rcontrol;

import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;

/**
 * Created by andre on 9/23/14.
 */
public class Constants {
    public static final String TAG = "RCONTROL";
    public static final ServiceId SERVICE_ID = new UDAServiceId("RControl");

    public static final int CMD_PLAY = 0;
    public static final int CMD_STOP = 1;
    public static final int CMD_PREVIOUS = 2;
    public static final int CMD_NEXT = 3;
    public static final int CMD_VOLUP = 4;
    public static final int CMD_VOLDOWN = 5;
    public static final int CMD_MUTE = 6;
}
