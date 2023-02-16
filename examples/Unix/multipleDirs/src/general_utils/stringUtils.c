#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#include "stringUtils.h"

char* strlwr(char *str)
{
    unsigned char *p = (unsigned char *)str;

    while (*p) {
        *p = tolower((unsigned char)*p);
        p++;
    }

    return str;
}

char* getSubstr(const char *line, int start, int end)
{
    if(end<start)
        return NULL;
    char* substr = malloc(sizeof(char)*((end-start) + 1));
    int i = 0;
    for (int x = start; x < end; x++)
    {
        substr[i] = line[x];
        i++;
    }
    substr[i] = '\0';
    return substr;
}

char* removeWhiteSpace(char *str)
{
    int pos = 0;
    int resultIndex = 0;
    char* result = malloc(sizeof(char)*strlen(str));
    while (str[pos] != '\0')
    {
        if (!isspace((unsigned char)str[pos]))
        {
            result[resultIndex] = str[pos];
            resultIndex++;
        }
        pos++;
    }
    result[resultIndex] = '\0';
    return result;
}