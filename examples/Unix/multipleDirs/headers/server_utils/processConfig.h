#pragma once
#include <time.h>

struct tm* processServerConfig();
void processClientConfig(char **ip, int *port, char **occasion);