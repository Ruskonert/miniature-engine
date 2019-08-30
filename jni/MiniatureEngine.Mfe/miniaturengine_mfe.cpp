#include <iostream>
#include <istream>
#include <ostream>
#include <vector>
#include <bitset>
#include <array>
#include <string>

#include "miniaturengine_mfe.hpp"
#include "miniaturengine_anti.hpp"

static NtQueryInformationProcess ntQueryInformationProcess;
static LPFN_ISWOW64PROCESS fnIsWow64Process;

void ZHvri7IhENSG();
void ExitError();

BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
	{
		std::cout << "MiniatureEngine.Mfe.dll is loaded successfully by Process." << std::endl;
		NtQueryInformationProcess ntQueryInformationProcess = (NtQueryInformationProcess)GetProcAddress(GetModuleHandle(TEXT("ntdll")), "NtQueryInformationProcess");
		if (ntQueryInformationProcess == NULL) {
			std::cout << "[External] [Miniature-Native thread/WARNING]: MiniatureEngine.Mfe.dll is load failed::NtQueryInformationProcess" << std::endl;
		}
		else {
			std::cout << "[External] [Miniature-Native thread/INFO]: MiniatureEngine.Mfe.dll is load successfully::NtQueryInformationProcess" << std::endl;
			NTSTATUS status;
			HANDLE handle = GetCurrentProcess();
			BOOL debugPort;
			ULONG returnLength = 0UL;
			status = ntQueryInformationProcess(handle, ProcessDebugPort, &debugPort, sizeof(debugPort), &returnLength);
			std::cout << "[External] [Miniature-Native thread/INFO]: NtQueryInformationProcess (0x" << std::hex << ntQueryInformationProcess << ") Returned -> 0x" << std::hex << status << std::endl;
			if (debugPort != 0) ZHvri7IhENSG();
		}

		//IsWow64Process is not available on all supported versions of Windows.
		//Use GetModuleHandle to get a handle to the DLL that contains the function
		//and GetProcAddress to get a pointer to the function if available.
		LPFN_ISWOW64PROCESS fnIsWow64Process = (LPFN_ISWOW64PROCESS)GetProcAddress(GetModuleHandle(TEXT("kernel32")), "IsWow64Process");
		if (fnIsWow64Process == NULL) {
			std::cout << "[External] [Miniature-Native thread/WARNING] MiniatureEngine.Mfe.dll is load failed::LPFN_ISWOW64PROCESS" << std::endl;
		}
		break;
	}
	case DLL_THREAD_ATTACH:
		std::cout << "MiniatureEngine.Mfe.dll is loaded successfully by Thread Worker." << std::endl;
		break;
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		break;
	}
	return TRUE;
}

void ZHvri7IhENSG() {
	std::cout << "[External] [Miniature-Native thread/INFO] Maybe you're computer is running with debugger :(" << std::endl;
	std::cout << "[External] [Miniature-Native thread/INFO] Need more information? Contract me ruskoner@gmail.com" << std::endl;
	throw 0x00000005L;
}

void ExitError() {

}


namespace mfe
{
	bool getWindowsBit(bool& isWindows64bit)
	{
		#if _WIN64
			isWindows64bit = true;
			return true;
		#elif _WIN32
			BOOL isWow64 = FALSE;
			if (fnIsWow64Process)
			{
				if (!fnIsWow64Process(GetCurrentProcess(), &isWow64))return false;
				if (isWow64) isWindows64bit = true;
				else isWindows64bit = false;
				return true;
			}
			else return false;
		#else
			assert(0);
			return false;
		#endif
	}
}

extern "C" {
	JNIEXPORT jstring JNICALL Java_io_github_emputi_mc_miniaturengine_communication_MfeDataDeliver_getPublicKey
	(JNIEnv* env, jobject javaObject) {
		std::string publicKey;
		int regs[4] = { 0 };
		char vendor[13];
		__cpuid(regs, 0);
		memcpy(vendor, &regs[1], 4);
		memcpy(vendor + 4, &regs[3], 4);
		memcpy(vendor + 8, &regs[2], 4);
		vendor[12] = '\0';
		
		std::string vendor_string = std::string(vendor);
		for (int i = 0; i < vendor_string.size(); i++) {
			switch (i % 6) {

			}
		}
	}

	JNIEXPORT jint JNICALL Java_io_github_emputi_mc_miniaturengine_communication_MfeDataDeliver_obfuscate0
	(JNIEnv* env, jobject javaObject, jint jpatternRepeat, jstring jseed, jstring jreadFilePath, jstring joutputFilePath) {
		return 0;
	}

	JNIEXPORT jboolean JNICALL Java_io_github_emputi_mc_miniaturengine_communication_MfeDataDeliver_isValidMfeFile
	(JNIEnv* env, jobject javaObject, jstring jtarget) {
		return true;
	}

	JNIEXPORT jboolean JNICALL Java_io_github_emputi_mc_miniaturengine_communication_MfeDataDeliver_isValidMfeChecksum
	(JNIEnv* env, jobject javaObject, jstring jtarget) {
		return true;
	}

	JNIEXPORT void JNICALL Java_io_github_emputi_mc_miniaturengine_communication_MfeDataDeliver_initializeHeader
	(JNIEnv*, jobject, jstring) {
		return;
	}
}

