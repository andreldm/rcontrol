#define WINVER 0x0500
#include <windows.h>
#include "Main.h"

JNIEXPORT void JNICALL
Java_com_andreldm_rcontrol_server_SendKey_sendkey(JNIEnv * a, jobject b, jint c)
{
    INPUT ip;
    ip.type = INPUT_KEYBOARD;
    ip.ki.wScan = 0;
    ip.ki.time = 0;
    ip.ki.dwExtraInfo = 0;

    // key press
    ip.ki.wVk = c;
    ip.ki.dwFlags = KEYEVENTF_EXTENDEDKEY | 0;
    SendInput(1, &ip, sizeof(INPUT));

    // key release
    ip.ki.dwFlags = KEYEVENTF_EXTENDEDKEY | KEYEVENTF_KEYUP;
    SendInput(1, &ip, sizeof(INPUT));
}

