// Multi-threaded TCP server

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <stdbool.h>
#include <limits.h>
#include <pthread.h>
#include <time.h>
#include <sys/time.h>
#include <string.h>

#include "queue.h"
#include "remainingTime.h"
#include "processConfig.h"

#define SERVERPORT 8989
#define BUFSIZE 4096
#define SOCKETERROR (-1)
#define SERVER_BACKLOG 100
#define THREAD_POOL_SIZE 20 // play around with this number for best performance
#define CONNECTION_TIMEOUT_LENGTH 3 // time out length in seconds

typedef struct sockaddr_in SA_IN;
typedef struct sockaddr SA;

pthread_t threadPool[THREAD_POOL_SIZE];
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t conditionVar = PTHREAD_COND_INITIALIZER;

void * handleConnection(void* pclientSocket);
int check(int exp, const char *msg);
void * threadFunction(void *arg);

int main(int argc, char **argv)
{
    int serverSocket, clientSocket, addrSize;
    SA_IN serverAddr, clientAddr;

    // Create thread pool
    for(int x=0;x<THREAD_POOL_SIZE;x++)
        pthread_create(&threadPool[x], NULL, threadFunction, NULL);

    // Create a TCP socket and check if it failed or not
    check((serverSocket = socket(AF_INET, SOCK_STREAM, 0)), "Failed to create socket");

    // initialize address struct
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_port = htons(SERVERPORT);

    // Binds the socket to the port
    check(bind(serverSocket, (SA*)&serverAddr, sizeof(serverAddr)), "Bind Failed");

    // Listens on that port
    check(listen(serverSocket, SERVER_BACKLOG), "Listen Failed");

    while(true)
    {
        printf("Waiting for connections...\n");
        // Wait for and accept incoming connections
        addrSize = sizeof(SA_IN);
        check((clientSocket = accept(serverSocket, (SA*)&clientAddr, (socklen_t*)&addrSize)), "Accept Failed");

        // Sets a timeout for the socket
        struct timeval tv; 
		tv.tv_sec = CONNECTION_TIMEOUT_LENGTH;
        tv.tv_usec = 0;
        setsockopt(clientSocket, SOL_SOCKET, SO_RCVTIMEO, (const char*)&tv, sizeof tv);

        // Prints out IP Address of the connected client
        printf("Connected to %s\n", inet_ntoa(clientAddr.sin_addr));

        // Puts the connection in queue for thread to pull from
        int *pclient = malloc(sizeof(int));
        *pclient = clientSocket;
        pthread_mutex_lock(&mutex);
        enqueue(pclient);
        pthread_cond_signal(&conditionVar);
        pthread_mutex_unlock(&mutex);
    }

    return 0;
}

// Basic error checker
int check(int exp, const char* msg)
{
    if(exp == SOCKETERROR)
    {
        perror(msg);
        exit(-1);
    }
    return exp;
}

// Manage thread pool and check if there is work to do
void * threadFunction(void *arg)
{
    while(true)
    {
        int *pclient;
        pthread_mutex_lock(&mutex);
        if((pclient = dequeue())==NULL)
        {
            pthread_cond_wait(&conditionVar, &mutex);
            pclient = dequeue();
        }
        pthread_mutex_unlock(&mutex);
        if(pclient != NULL)
        {
            // We have a connection
            handleConnection(pclient);
        }
    }
}

// Function to handle what to do with an 
// incoming connection
void * handleConnection(void* pclientSocket)
{
    int clientSocket = *((int*)pclientSocket);
    free(pclientSocket); // free pointer since we don't need it
    
    char *buffer = getTimeStr();
    write(clientSocket, buffer, strlen(buffer));

    close(clientSocket);
    printf("closing connection...\n");
    return NULL;
}
