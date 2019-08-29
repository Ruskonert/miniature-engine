#include "FrameApplicationManager.h"

#include <iostream>
#include <cstdlib>
#include <Windows.h>
#include <process.h>

#ifdef _WIN32
#define IS_WINDOWS 1
#define IS_LINUX 0
#define IS_UNIX 0
#elif _MAC
#define IS_WINDOWS 0
#define IS_LINUX 0
#define IS_UNIX 1
#endif

#define MINIATURE_EXCEPTION 1LL

UINT WINAPI ThreadFunc(void*)
{
	Sleep(1000);
	std::cout << "[external] [Miniature thread/INFO] After 3 seconds, The screen is reset for executed command." << std::endl;
	std::cout << "[external] [Miniature thread/INFO] After 3 seconds, The screen is reset for executed command." << std::endl;
	Sleep(3000);
	try {
		if (IS_WINDOWS)
		{
			system("cls");
		}
		else if (IS_LINUX)
		{
			system("clear");
		}
		std::cout << "[external] [Miniature thread/INFO] Succressfully cleared." << std::endl;
		std::cout << ">";
		return 0;
	}
	catch (...) {
		return 255;
	}
}

extern "C" {
	JNIEXPORT jboolean JNICALL Java_io_github_emputi_mc_miniaturengine_application_FrameApplicationManager_clearConsoleWindow
	(JNIEnv* env, jobject javaObject)
	{
		PSECURITY_ATTRIBUTES psa;
		UINT dwThreadID;
		HANDLE hHandle = (HANDLE)_beginthreadex(NULL, 0, ThreadFunc, (void*)NULL, 0, &dwThreadID);
		DWORD returnCode = 0;
		GetExitCodeThread(hHandle, &returnCode);
		if (returnCode == 0) {
			return true;
		}
		else return false;
	}

	JNIEXPORT jboolean JNICALL Java_io_github_emputi_mc_miniaturengine_application_FrameApplicationManager_setConsoleColors
	(JNIEnv* env, jobject javaObject, jint attribs)
	{
		try {
			HANDLE hOutput = GetStdHandle(STD_OUTPUT_HANDLE);
			CONSOLE_SCREEN_BUFFER_INFOEX cbi;
			cbi.cbSize = sizeof(CONSOLE_SCREEN_BUFFER_INFOEX);
			GetConsoleScreenBufferInfoEx(hOutput, &cbi);
			cbi.wAttributes = attribs;
			SetConsoleScreenBufferInfoEx(hOutput, &cbi);
		}
		catch (...) {
			return false;
		}
		return true;
	}
}