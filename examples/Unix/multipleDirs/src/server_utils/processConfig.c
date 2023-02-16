#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <ctype.h>
#include <time.h>

#include "processConfig.h"
#include "stringUtils.h"

void printTime(struct tm* tm)
{
    printf("%04d-%02d-%02d %02d:%02d:%02d\n",
    tm->tm_year, tm->tm_mon+1, tm->tm_mday, tm->tm_hour, tm->tm_min, tm->tm_sec);
}

bool readConfig(bool server, char **fileContents)
{
    char *filename;
    if(server)
        filename = "server.cfg";
    else
        filename = "client.cfg";

    FILE *fin = fopen(filename, "r");

    if(fin != NULL)
    {
        // Get size of the file
        fseek(fin, 0, SEEK_END);
        long size = ftell(fin);
        fseek(fin, 0, SEEK_SET);

        // Malloc the amount of memory to 
        // read in the file, and read in the
        // entire file into the buffer
        char *buffer = malloc(sizeof(char)*(size+1));
        size_t readBytes = fread(buffer, 1, size, fin);
        buffer[readBytes] = '\0';
        *fileContents = buffer;
        fclose(fin);
    }
    else
    {
        perror(filename);
        return false;
    }
    return true;
}

void saveTimeComponent(struct tm *tm, char *line)
{
    char* lineElements = strtok(removeWhiteSpace(line), "=");
    lineElements = strlwr(lineElements);
    int timeUnit = atoi(strtok(NULL, "="));

    if(strcmp(lineElements, "year") == 0)
        tm->tm_year = timeUnit;
    else if(strcmp(lineElements, "month") == 0)
        tm->tm_mon = --timeUnit;
    else if(strcmp(lineElements, "day") == 0)
        tm->tm_mday = timeUnit;
    else if(strcmp(lineElements, "hour") == 0)
        tm->tm_hour = timeUnit;
    else if(strcmp(lineElements, "minute") == 0)
        tm->tm_min = timeUnit;
    else if(strcmp(lineElements, "second") == 0)
        tm->tm_sec = timeUnit;
}

void parseServerConfigLines(struct tm *tm, const char* fileContents)
{
    int lineStart = 0;
    int pos = 0;
    while(fileContents[pos] != '\0')
    {
        char *line;
        while (fileContents[pos] != '\n' && fileContents[pos] != '\0')
            pos++;

        size_t lineLen = (pos - lineStart) + 1;
        line = malloc(sizeof(char)*lineLen);
        line = getSubstr(fileContents, lineStart, pos++);
        saveTimeComponent(tm, line);
        lineStart = pos++;
        free(line);
    }
    //printTime(tm);
}

struct tm* processServerConfig()
{
    struct tm* tm;
    time_t t = time(NULL);
    tm = localtime(&t);

    char *cfgFileContent = NULL;
    if(readConfig(true, &cfgFileContent))
    {
        //printf("cfg File:\n%s",cfgFileContent);
        parseServerConfigLines(tm, cfgFileContent);
    }
    else
    {
        printf("Error: Unable to read config file\n");
        exit(EXIT_FAILURE);
    }
    free(cfgFileContent);
    return tm;
}

int getValueIndex(const char* line)
{
    char* equalLoc = strstr(line, "=");
    int equalIndex = 0;
    if(equalLoc != NULL)
        equalIndex = (equalLoc - line) + 1;
    
    while(isspace((unsigned char)line[equalIndex]) || line[equalIndex] == '\0')
        equalIndex++;
    
    return equalIndex;
}

void parseClientConfigLines(const char *fileContents, char **ip, int *port, char **occasion)
{
    int lineStart = 0;
    int pos = 0;
    while(fileContents[pos] != '\0')
    {
        char *line;
        while (fileContents[pos] != '\n' && fileContents[pos] != '\0')
            pos++;

        size_t lineLen = (pos - lineStart) + 1;
        line = malloc(sizeof(char)*lineLen);
        line = getSubstr(fileContents, lineStart, pos++);

        char* value = getSubstr(line, getValueIndex(line), strlen(line));
        char* lineElements = strtok(line, "=");
        lineElements = strlwr(removeWhiteSpace(lineElements));

        if(strcmp(lineElements, "serverip") == 0)
            *ip = value;      
        else if(strcmp(lineElements, "port") == 0)
            *port = atoi(value);
        else if(strcmp(lineElements, "occasion") == 0)
            *occasion = value;
            
        lineStart = pos++;
        free(line);
    }
}

void processClientConfig(char **ip, int *port, char **occasion)
{
    char *cfgFileContent = NULL;
    if(readConfig(false, &cfgFileContent))
    {
        //printf("cfg File:\n%s",cfgFileContent);
        parseClientConfigLines(cfgFileContent, ip, port, occasion);
    }
    else
    {
        printf("Error: Unable to read config file\n");
        exit(EXIT_FAILURE);
    }
    free(cfgFileContent);
}
