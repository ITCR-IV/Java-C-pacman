#ifndef THREADS_H
#define THREADS_H

#include <pthread.h>

int getQuit();
void setQuit(int val);

void init_clients();
void clearClients();
int addPlayer(int socket);
int addObserver(int socket, int observedPlayer);
void removeClient(int socket);
void writeToClient(int socket, char* client_message);
void writeToPlayer(int playerId, char* client_message);
void writeToObservers(int playerId, char* client_message);

int getPlayerId(int socket);
#endif
