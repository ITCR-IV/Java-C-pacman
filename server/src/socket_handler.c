#include "socket_handler.h"

#include <string.h>
#include <stdio.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>

#include "threads.h"
#include "json.h"
#include "constants.h"


// Función para imprimir el error así como enviárselo al cliente y seguidamente cerrar y liberar el socket
void exit_socket_with_error(int* socket_desc, char* error_msg){
	int sock = *socket_desc;

	// Format json error
	char json_error[] = "{ \"Error\": \"%s\" }";
	char msg[strlen(json_error)+strlen(error_msg)+2]; // +2 for safety
	sprintf(msg, json_error, error_msg);

	// Send error to client
	writeToClient(sock, msg);

	// Print error
	fprintf(stderr, error_msg);
	fflush(stderr);
	removeClient(sock);
	close(sock);
	free(socket_desc);
}

void *connection_handler(void *socket_desc)
{
	//Get the socket descriptor
	int sock = *(int*)socket_desc;
	int read_size;
	char *message , client_message[BUFFER_SIZE];

	//Receive messages from client
	read_size = recv(sock , client_message , BUFFER_SIZE , 0);
	if(read_size > 0){
		json_char* json;
		json_value* value;
		do {
			json = (json_char*)client_message;
			value = json_parse(json,strlen(client_message));

			if (value == NULL) {
				char error_msg[] = "Unable to parse data";
				exit_socket_with_error(socket_desc, error_msg);
				return (void *) 1;
			}
			if(value->type == json_object && strcmp(value->u.object.values[0].name, "init")==0){
				// Add new client to client vector
				if(strcmp(value->u.object.values[0].value->u.string.ptr, "player") == 0){
					// Add new player
					int ret = addPlayer(sock);
					if(ret){
						char error_msg[] = "Maximum amount of players already reached, connection refused";
						exit_socket_with_error(socket_desc, error_msg);
						return (void *) 1;
					}
				}else if(strcmp(value->u.object.values[0].value->u.string.ptr, "observer") == 0){
					// Add new observer
					int ret = addObserver(sock, value->u.object.values[1].value->u.integer);
					if(ret){
						char error_msg[] = "No player with that index available at the moment";
						exit_socket_with_error(socket_desc, error_msg);
						return (void *) 1;
					}
				}
			}
			else{
				// reroute messages to the appropiate observers
				int id = getPlayerId(sock);
				if(id == -1){
					char error_msg[] =  "Trying to reroute messages from player socket that isn't in the clients vector (?!?!?! this really shouldn't happen";
					exit_socket_with_error(socket_desc, error_msg);
					return (void *) 1;
				}
				writeToObservers(id, client_message);
			}

			json_value_free(value);
			//Send the message back to client
		} while( (read_size = recv(sock , client_message , BUFFER_SIZE , 0)) > 0 && getQuit() == 0);
	}

	if(read_size == 0)
	{
		puts("Client disconnected");
		fflush(stdout);
		removeClient(sock);
		free(socket_desc);
		return 0;
	}
	else if(read_size == -1)
	{
		perror("recv failed");
	}

	//Close socket
	removeClient(sock);
	close(sock);
	//Free the socket pointer
	free(socket_desc);

	return 0;
}
