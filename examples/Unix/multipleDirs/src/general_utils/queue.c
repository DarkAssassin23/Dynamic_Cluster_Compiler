#include "queue.h"
#include <stdlib.h>

node_t* head = NULL;
node_t* tail = NULL;

// Adds new connection to the queue
void enqueue(int *clientSocket)
{
    node_t *newNode = malloc(sizeof(node_t));
    newNode->clientSocket = clientSocket;
    newNode->next = NULL;
    if(tail == NULL)
        head = newNode;
    else
        tail->next = newNode;
    tail = newNode;
}

// Returns null if the queue is empty
// returns the pointer to a connection if there is one
int* dequeue()
{
    if(head==NULL)
        return NULL;
    else
    {
        int *result = head->clientSocket;
        node_t *temp = head;
        head = head->next;
        if(head == NULL)
            tail = NULL;
        free(temp);
        return result;
    }
}