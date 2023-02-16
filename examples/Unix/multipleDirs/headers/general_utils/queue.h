#ifndef QUEUE_H
#define QUEUE_H

struct node
{
    struct node* next;
    int *clientSocket;
};
typedef struct node node_t;

void enqueue(int *clientSocket);
int* dequeue();

#endif