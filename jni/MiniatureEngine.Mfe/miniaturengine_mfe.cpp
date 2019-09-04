#include <iostream>
#include <istream>
#include <ostream>
#include <vector>
#include <bitset>
#include <cstdlib>
#include <array>
#include <string>

#include "miniaturengine_main.hpp"
#include "miniaturengine_mfe.hpp"
#include "miniaturengine_anti.hpp"


static NtQueryInformationProcess ntQueryInformationProcess;
static LPFN_ISWOW64PROCESS fnIsWow64Process;

void ZHvri7IhENSG();


BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
	{
		std::cout << "MiniatureEngine.Mfe.dll is loaded successfully by Process." << std::endl;
		NtQueryInformationProcess ntQueryInformationProcess = (NtQueryInformationProcess)GetProcAddress(GetModuleHandle(TEXT("ntdll")), "NtQueryInformationProcess");
		if (ntQueryInformationProcess == NULL)
		{
			OutputDebugString(_T("MiniatureEngine.Mfe.dll is load failed::NtQueryInformationProcess"));
		}
		else
		{
			NTSTATUS status;
			HANDLE handle = GetCurrentProcess();
			BOOL debugPort;
			ULONG returnLength = 0UL;
			status = ntQueryInformationProcess(handle, ProcessDebugPort, &debugPort, sizeof(debugPort), &returnLength);
			if (debugPort != 0) ZHvri7IhENSG();
		}

		//IsWow64Process is not available on all supported versions of Windows.
		//Use GetModuleHandle to get a handle to the DLL that contains the function
		//and GetProcAddress to get a pointer to the function if available.
		LPFN_ISWOW64PROCESS fnIsWow64Process = (LPFN_ISWOW64PROCESS)GetProcAddress(GetModuleHandle(TEXT("kernel32")), "IsWow64Process");
		if (fnIsWow64Process == NULL)
		{
			std::cout << "[" << mfe::Server::current_server_time() << "] [Miniature-Native thread/WARNING] MiniatureEngine.Mfe.dll is load failed::LPFN_ISWOW64PROCESS" << std::endl;
		}
		break;
	}
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		break;
	}
	return TRUE;
}

void ZHvri7IhENSG() {
	std::cout << "[" << mfe::Server::current_server_time() << "] [Miniature-Native thread/INFO] Maybe you're computer is running with debugger :(" << std::endl;
	std::cout << "[" << mfe::Server::current_server_time() << "] [Miniature-Native thread/INFO] Need more information? Contract me ruskoner@gmail.com" << std::endl;
	throw 0x00000005L;
}

namespace mfe
{
	bool get_windows_bit(bool& isWindows64bit)
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

static jstring get_string(JNIEnv* env, const char* message)
{
	return env->NewStringUTF(message);
}

const std::string CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

inline std::string generateUUID() {
	char strUuid[36] = { 0, };
	srand(time(NULL));
	sprintf_s(strUuid, "%x%x-%x-%x-%x-%x%x%x",
		rand(), rand(),                 // Generates a 64-bit Hex number
		rand(),                         // Generates a 32-bit Hex number
		((rand() & 0x0fff) | 0x4000),   // Generates a 32-bit Hex number of the form 4xxx (4 indicates the UUID version)
		rand() % 0x3fff + 0x8000,       // Generates a 32-bit Hex number in the range [0x8000, 0xbfff]
		rand(), rand(), rand());        // Generates a 96-bit Hex number
	return std::string(strUuid);
}

TCHAR *convertToTCHAR(const char* text) {
	TCHAR szUnicode[256] = { 0, };
	MultiByteToWideChar(CP_ACP, MB_PRECOMPOSED, text, strlen(text), szUnicode, 256);
	return szUnicode;
}

char *convertToCHAR(const TCHAR* text) {
	char cTemp[256] = { 0, };
	WideCharToMultiByte(CP_ACP, 0, text, 256, cTemp, 256, NULL, NULL);
	return cTemp;
}

extern "C" {
	JNIEXPORT jstring JNICALL Java_io_github_emputi_mc_miniaturengine_communication_MfeDataDeliver_getPublicKey(JNIEnv* env, jobject javaObject)
	{
		char publicKey[36] = { 0, };
		int regs[4] = { 0 };
		char vendor[13];
		__cpuid(regs, 0);
		memcpy(vendor, &regs[1], 4);
		memcpy(vendor + 4, &regs[3], 4);
		memcpy(vendor + 8, &regs[2], 4);
		vendor[12] = '\0';

		const TCHAR* env_name_chr = _T("Miniature_MFE_Data");
		size_t value_length = 36;

		TCHAR* env_value = NULL;

		std::string vendor_string = std::string(vendor);

		try
		{
			_tdupenv_s(&env_value, &value_length, env_name_chr);
		}
		catch (LONG &dwExceptionCode)
		{

		}

		std::wstring mfe_data;
		if (env_value != NULL)
			mfe_data = std::wstring(env_value);
		else
			mfe_data = _T("");

		unsigned char* key2 = new unsigned char[vendor_string.size()];
		
		if (mfe_data.size() == 0) {
			const char* uid = generateUUID().c_str();
			BOOL successful = SetEnvironmentVariable(env_name_chr, convertToTCHAR(uid));
			if (!successful) return get_string(env, "");
			env_value = convertToTCHAR(std::string(uid).c_str());
		}

		size_t key2_size = 0;
		for (int i = 0; i < vendor_string.size(); i++) {
			char a = vendor_string.at(i);
			switch (vendor_string.at(i) % 6) {
			case 0: key2[i] = (a + 2) << 1; break;
			case 1: key2[i] = (a + 2) << 2; break;
			case 2: key2[i] = (a + 3) + 3; break;
			case 3: key2[i] = (a + 4) << 4; break;
			case 4: key2[i] = (a + 5) - 5 + ((key2_size * i) % 3); break;
			case 5: key2[i] = (a + 6) >> 6; break;
			default: break;
			}
			key2_size++;
		}
		char* envi = convertToCHAR(env_value);
		size_t envi_length = _tcslen(env_value);

		for (int i = 0; i < key2_size; i++) {
			key2[i] = key2[i] << (unsigned)envi[i % envi_length];
		} 

		std::string sPublicKey = std::string();
		for (int i = 0; i < key2_size; i++) {
			sPublicKey = sPublicKey.append(std::to_string(key2[i])).append("-");
		}

		delete[] key2;
		return get_string(env, sPublicKey.c_str());
	}
	JNIEXPORT jboolean JNICALL Java_io_github_emputi_mc_miniaturengine_communication_MfeDataDeliver_delegateToThread(JNIEnv* env, jlong threadId) {
		return true;
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

