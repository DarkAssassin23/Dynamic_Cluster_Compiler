#include <stdio.h>
#include <stdlib.h>

#include "remainingTime.h"
#include "processConfig.h"

#define SEC_TO_DAY 86400
#define SEC_TO_HR 3600
#define SEC_TO_MIN 60

typedef struct timeUntil {
    int days;
    int hours;
    int minutes;
    int seconds;
} timeUntil;

struct tm* getCurrTime()
{
    time_t rawtime;
    struct tm *tm;
    time(&rawtime);
    tm = localtime(&rawtime);
    tm->tm_year += 1900;
    mktime(tm);
    return tm;
}

struct tm* getEndTime()
{
    struct tm* tm = processServerConfig();
    if(tm == NULL)
        tm = getCurrTime();
    return tm;
}

char *getTimeStr()
{
    double s = difftime(mktime(getEndTime()), mktime(getCurrTime()));
    if(s<=0)
        return "Time is up!!!\n";
    
    time_t seconds = (time_t)s;
    timeUntil tu;
    tu.days = seconds / SEC_TO_DAY;
    seconds %= SEC_TO_DAY;
    tu.hours = seconds / SEC_TO_HR;
    seconds %= SEC_TO_HR;
    tu.minutes = seconds / SEC_TO_MIN;
    tu.seconds = seconds % SEC_TO_MIN;

    char *result = malloc(sizeof(char)*MAX_MSG_SIZE);
    sprintf(result, "Time remaining: %d Days, %d Hours, %d Minutes, %d Seconds\n", 
        tu.days, tu.hours, tu.minutes, tu.seconds);

    return result;
}