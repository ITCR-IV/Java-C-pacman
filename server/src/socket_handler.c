#include "socket_handler.h"

#include <string.h>
#include <stdio.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>

#include "threads.h"
#include "json.h"
#include "constants.h"


static void process_value(json_value* value)
{
	//pasar a observadores
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
				fprintf(stderr, "Unable to parse data\n");
				removeClient(sock);
				close(sock);
				free(socket_desc);
				return (void *) 1;
			}
			if(value->type == json_object && strcmp(value->u.object.values[0].name, "init")==0){
				// Add new client to client vector
				if(strcmp(value->u.object.values[0].value->u.string.ptr, "player") == 0){
					// Add new player
					int ret = addPlayer(sock);
					if(ret){
						perror("Maximum amount of players already reached, connection refused");
						close(sock);
						free(socket_desc);
						return (void *) 1;
					}
				}else if(strcmp(value->u.object.values[0].value->u.string.ptr, "observer") == 0){
					// Add new observer
					addObserver(sock, value->u.object.values[1].value->u.integer);
				}
			}
			else{
				writeToClient(sock, client_message);
				//process_value(value);
			}

			json_value_free(value);
			//Send the message back to client
		} while( (read_size = recv(sock , client_message , BUFFER_SIZE , 0)) > 0 && getQuit() == 0);
	}

	if(read_size == 0)
	{
		puts("Client disconnected");
		fflush(stdout);
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
