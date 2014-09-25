#include <X11/extensions/XTest.h>
#include <X11/XF86keysym.h>
#include "sendkey.h"
#include <stdio.h>

// XF86XK_AudioLowerVolume   0x1008FF11
// XF86XK_AudioMute          0x1008FF12
// XF86XK_AudioRaiseVolume   0x1008FF13
// XF86XK_AudioPlay          0x1008FF14
// XF86XK_AudioStop          0x1008FF15
// XF86XK_AudioPrev          0x1008FF16
// XF86XK_AudioNext          0x1008FF17

JNIEXPORT void JNICALL
Java_com_andreldm_rcontrol_server_SendKey_sendkey(JNIEnv * a, jobject b, jint c)
{
    Display* display = XOpenDisplay( NULL );
    KeySym keysym = c;
    KeyCode keycode = XKeysymToKeycode( display , keysym );

    // key press
    XTestFakeKeyEvent( display , keycode , True  , CurrentTime );

    // key release
    XTestFakeKeyEvent( display , keycode , False , CurrentTime );

    XFlush( display );

    XCloseDisplay( display );
}