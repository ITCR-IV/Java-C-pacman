#include "threads.h"

#include <string.h>
#include <unistd.h>

#include "socket_handler.h"
#include "vec.h"

// mutex vars 
static pthread_mutex_t stopMutex, clientsMutex;

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

int addPlayer(int socket){
	int players = 0;
	pthread_mutex_lock(&clientsMutex);
	for(int i = 0; i < clients_vec.length; i++){
		enum ClientTypes type = ((struct client*) vec_get(&clients_vec, i))->type;
		if(type == PLAYER){
			players++;
		}
	}
	int ret;
	if(players < 2){
		struct client* newClient = (struct client*) vec_emplace(&clients_vec);
		newClient->type = PLAYER;
		newClient->socket = socket;
		newClient->playerNum = players;
		ret = 0;
	}
	else{
		ret = 1;
	}
	pthread_mutex_unlock(&clientsMutex);
	return ret;
}

int addObserver(int socket, int observedPlayer){
	pthread_mutex_lock(&clientsMutex);
	pthread_mutex_unlock(&clientsMutex);
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
			pthread_mutex_unlock(&clientsMutex);
			writeToClient(socket, client_message);
			return;
		}
	}
	pthread_mutex_unlock(&clientsMutex);
	return;
}

void writeToClient(int socket, char* client_message){
	pthread_mutex_lock(&clientsMutex);
	write(socket , client_message , strlen(client_message));
	pthread_mutex_unlock(&clientsMutex);
}

void removeClient(int socket){
	pthread_mutex_lock(&clientsMutex);
	for(int i = 0; i < clients_vec.length; i++){
		int sock_test = ((struct client*) vec_get(&clients_vec, i))->socket;
		if(socket == sock_test){
			vec_delete(&clients_vec, i);
			break;
		}
	}
	pthread_mutex_unlock(&clientsMutex);
}
