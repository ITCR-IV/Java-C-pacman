#ifndef SOCKETHANDLER_H
#define SOCKETHANDLER_H

enum ClientTypes{OBSERVER, PLAYER};

// Struct con información necesaria para manejar clientes
struct client {
	 enum ClientTypes type;
	 int socket;
	 int playerNum; // for players this is their id and for observers this is who they observe
};

void *connection_handler(void *socket_desc);

#endif
