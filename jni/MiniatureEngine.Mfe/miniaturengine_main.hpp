#pragma once

#include <iostream>
#include <chrono>
#include <ctime>

namespace mfe
{
	class Server
	{
	public:
		static inline const std::string current_server_time()
		{
			time_t     now = time(0);
			struct tm  tstruct;
			char       buf[80];
			localtime_s(&tstruct, &now);
			strftime(buf, sizeof(buf), "%X", &tstruct);
			return buf;
		}
		Server() {
			
		}
	};
}