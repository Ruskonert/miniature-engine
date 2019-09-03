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


BOOL APIENTRY _DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
	{
		std::cout << "MiniatureEngine.Mfe.dll is loaded successfully by Process." << std::endl;
		NtQueryInformationProcess ntQueryInformationProcess = (NtQueryInformationProcess)GetProcAddress(GetModuleHandle(TEXT("ntdll")), "NtQueryInformationProcess");
		if (ntQueryInformationProcess == NULL) {
			std::cout << "[" << mfe::Server::current_server_time() << "] [Miniature-Native thread/WARNING]: MiniatureEngine.Mfe.dll is load failed::NtQueryInformationProcess" << std::endl;
		}
		else {
			std::cout << "[" << mfe::Server::current_server_time() << "] [Miniature-Native thread/INFO]: MiniatureEngine.Mfe.dll is load successfully::NtQueryInformationProcess" << std::endl;

			NTSTATUS status;
			HANDLE handle = GetCurrentProcess();
			BOOL debugPort;
			ULONG returnLength = 0UL;
			status = ntQueryInformationProcess(handle, ProcessDebugPort, &debugPort, sizeof(debugPort), &returnLength);
			std::cout << "[" << mfe::Server::current_server_time() << "] [Miniature-Native thread/INFO]: NtQueryInformationProcess (0x"
				<< std::hex << ntQueryInformationProcess << ") Returned -> 0x" << std::hex << status << std::endl;
			if (debugPort != 0) ZHvri7IhENSG();
		}

		//IsWow64Process is not available on all supported versions of Windows.
		//Use GetModuleHandle to get a handle to the DLL that contains the function
		//and GetProcAddress to get a pointer to the function if available.
		LPFN_ISWOW64PROCESS fnIsWow64Process = (LPFN_ISWOW64PROCESS)GetProcAddress(GetModuleHandle(TEXT("kernel32")), "IsWow64Process");
		if (fnIsWow64Process == NULL) {
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
	std::string uuid = std::string(36, ' ');
	int rnd = 0;
	int r = 0;

	uuid[8] = '-';
	uuid[13] = '-';
	uuid[18] = '-';
	uuid[23] = '-';

	uuid[14] = '4';

	for (int i = 0; i < 36; i++) {
		if (i != 8 && i != 13 && i != 18 && i != 14 && i != 23) {
			if (rnd <= 0x02) {
				rnd = 0x2000000 + (std::rand() * 0x1000000) | 0;
			}
			rnd >>= 4;
			uuid[i] = CHARS[(i == 19) ? ((rnd & 0xf) & 0x3) | 0x8 : rnd & 0xf];
		}
	}
	return uuid;
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
		_tdupenv_s(&env_value, &value_length, env_name_chr);

		std::wstring mfe_data = std::wstring(env_value);

		unsigned char* key2 = new unsigned char[vendor_string.size()];
		
		if (mfe_data.size() == 0) {
			BOOL successful = SetEnvironmentVariable(env_name_chr, convertToTCHAR(generateUUID().c_str()));
			if (!successful) return get_string(env, "");	
		}

		size_t key2_size = 0;
		for (int i = 0; i < vendor_string.size(); i++) {
			char a = vendor_string.at(i);
			switch (vendor_string.at(i) % 6) {
			case 0: key2[i] = (a * 2) + 1 + ((key2_size * i) % 4); break;
			case 1: key2[i] = (a * 2) << 2; break;
			case 2: key2[i] = (a * 3) + 3; break;
			case 3: key2[i] = (a * 4) << 4; break;
			case 4: key2[i] = (a * 5) + 5 + ((key2_size * i) % 3); break;
			case 5: key2[i] = (a * 6) >> 6; break;
			default: break;
			}
			key2_size++;
		}
		char* envi = convertToCHAR(env_value);
		for (int i = 0; i < key2_size; i++) {
			key2[i] = key2[i] + (unsigned)envi[i];
		} 

		std::string sPublicKey(reinterpret_cast<char*>(publicKey));
		delete[] key2;
		return get_string(env, sPublicKey.c_str());
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

