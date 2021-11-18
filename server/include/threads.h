#ifndef THREADS_H
#define THREADS_H

#include <pthread.h>

// Revisar el flag de quit
int getQuit();

// Setear el flag de quit
void setQuit(int val);

// Inicializa el vector de clientes
// Llamar antes de cualquier otra operación relacionada
void init_clients();

// Limpia el vector de clientes
void clearClients();

// Agregar un nuevo jugador al vector de clientes, hay un máximo de 2 jugadores a la vez
int addPlayer(int socket);

// Agrega un observador que observe al jugador cuyo número sea igual a observedPlayer, si no existe retorna 1
int addObserver(int socket, int observedPlayer);

// Borra un cliente de la lista de vectores, si el cliente es un jugador entonces cierra todos los clientes observadores que lo estaban observando
void removeClient(int socket);

// Le escribe de manera segura a un socket
void writeToClient(int socket, char* client_message);

// Le escribe al socket del jugador dado por playerId (0-1)
void writeToPlayer(int playerId, char* client_message);

// Le escribe a todos los observadores de un jugador en específico
void writeToObservers(int playerId, char* client_message);

// Retorna el id de un jugador dado su socket
int getPlayerId(int socket);
#endif
