#include <stdio.h>
#include <stdlib.h>

#include "test.h"
#include "hello.h"

int main(int argc, char *argv[])
{
    if (argc != 2)
    {
        printf("Usage %s [name]\n",argv[0]);
        exit(EXIT_FAILURE);
    }

    printTest();
    printHello(argv[1]);

    return 0;
}