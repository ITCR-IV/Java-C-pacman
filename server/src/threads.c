#include "threads.h"

#include <string.h>
#include <unistd.h>
#include <stdio.h>

#include "socket_handler.h"
#include "vec.h"

// mutex vars 
static pthread_mutex_t stopMutex, clientsMutex, socket_writeMutex;

// quit var
static int quit = 0;

int getQuit() {
	int ret = 0;
	pthread_mutex_lock(&stopMutex);
	ret = quit;
	pthread_mutex_unlock(&stopMutex);
	return ret;
}

void setQuit(int val) {
	pthread_mutex_lock(&stopMutex);
	quit = val;
	pthread_mutex_unlock(&stopMutex);
}

// clients vector
static struct vec clients_vec;

void init_clients(){
	pthread_mutex_lock(&clientsMutex);
	clients_vec = vec_new(sizeof(struct client));
	pthread_mutex_unlock(&clientsMutex);
}

void clearClients(){
	pthread_mutex_lock(&clientsMutex);
	vec_clear(&clients_vec);
	pthread_mutex_unlock(&clientsMutex);
}

int getPlayerId(int socket){
	pthread_mutex_lock(&clientsMutex);
	struct client* curr_client;
	for(int i = 0; i < clients_vec.length; i++){
		curr_client = (struct client*) vec_get(&clients_vec, i);
		enum ClientTypes type = curr_client->type;
		int client_socket = curr_client->socket;
		if(type == PLAYER && socket == client_socket)
			return curr_client->playerNum;

	}
	pthread_mutex_unlock(&clientsMutex);
	return -1;
}

int addPlayer(int socket){
	int players = 0;
	int last_id_seen = 1;
	pthread_mutex_lock(&clientsMutex);
	for(int i = 0; i < clients_vec.length; i++){
		struct client* curr_client = (struct client*) vec_get(&clients_vec, i);
		enum ClientTypes type = curr_client->type;
		if(type == PLAYER){
			players++;
			last_id_seen = curr_client->playerNum;
		}
	}
	int ret;
	if(players < 2){
		struct client* newClient = (struct client*) vec_emplace(&clients_vec);
		newClient->type = PLAYER;
		newClient->socket = socket;
		// 0 if none were seen, 0 if 1 was seen, 1 if 0 was seen
		newClient->playerNum = 1-last_id_seen;
		ret = 0;
		printf("Connected player #%d\n", players);
	}
	else{
		ret = 1;
	}
	pthread_mutex_unlock(&clientsMutex);
	return ret;
}

int addObserver(int socket, int observedPlayer){
	int ret = 1;
	pthread_mutex_lock(&clientsMutex);
	struct client* curr_client;
	for(int i = 0; i < clients_vec.length; i++){
		curr_client = (struct client*) vec_get(&clients_vec, i);
		enum ClientTypes type = curr_client->type;
		int id = curr_client->playerNum;
		if(type == PLAYER && id == observedPlayer){
			struct client* newClient = (struct client*) vec_emplace(&clients_vec);
			newClient->type = OBSERVER;
			newClient->socket = socket;
			newClient->playerNum = observedPlayer;
			printf("Connected an observer to player %d\n", observedPlayer);
			ret = 0;
		}
	}
	pthread_mutex_unlock(&clientsMutex);
	return ret;
	return 0;
}

void writeToPlayer(int playerId, char* client_message){
	pthread_mutex_lock(&clientsMutex);
	struct client* curr_client;
	for(int i = 0; i < clients_vec.length; i++){
		curr_client = (struct client*) vec_get(&clients_vec, i);
		enum ClientTypes type = curr_client->type;
		int id = curr_client->playerNum;
		if(type == PLAYER && id == playerId){
			int socket = curr_client->socket;
			writeToClient(socket, client_message);
			return;
		}
	}
	pthread_mutex_unlock(&clientsMutex);
	return;
}

void writeToObservers(int playerId, char* client_message){
	pthread_mutex_lock(&clientsMutex);
	struct client* curr_client;
	for(int i = 0; i < clients_vec.length; i++){
		curr_client = (struct client*) vec_get(&clients_vec, i);
		enum ClientTypes type = curr_client->type;
		int id = curr_client->playerNum;
		if(type == OBSERVER && id == playerId){
			int socket = curr_client->socket;
			writeToClient(socket, client_message);
		}
	}
	pthread_mutex_unlock(&clientsMutex);
	return;
}

void writeToClient(int socket, char* client_message){
	pthread_mutex_lock(&socket_writeMutex);
	write(socket , client_message , strlen(client_message));
	pthread_mutex_unlock(&socket_writeMutex);
}

void removeObservers(int id){
	for(int i = 0; i < clients_vec.length; i++){
		struct client* curr_client = (struct client*) vec_get(&clients_vec, i);
		int observerId = curr_client->playerNum;
		enum ClientTypes type = curr_client->type;
		if(type == OBSERVER && id == observerId){
			// Closing the socket will cause its thread to fail which will then remove it from the vector
			close(curr_client->socket);
			break;
		}
	}
}

void removeClient(int socket){
	pthread_mutex_lock(&clientsMutex);
	for(int i = 0; i < clients_vec.length; i++){
		struct client* curr_client = (struct client*) vec_get(&clients_vec, i);
		int sock_test = curr_client->socket;
		enum ClientTypes type = curr_client->type;
		if(socket == sock_test){
			vec_delete(&clients_vec, i);
			if(type == PLAYER)
				removeObservers(curr_client->playerNum);
			break;
		}
	}
	pthread_mutex_unlock(&clientsMutex);
}
