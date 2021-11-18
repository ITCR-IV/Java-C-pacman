#include <sys/socket.h>
#include <arpa/inet.h>	//inet_addr
#include <stdio.h>
#include <stdlib.h>

#include "prompt.h"
#include "threads.h"
#include "socket_handler.h"

int main( int argc, char *args[] ) {
	// Start command prompt
	pthread_t prompt_t;
	pthread_create( &prompt_t, NULL, promptLoop, NULL);

	// Socket stuff
	init_clients();

	int socket_desc , new_socket , c , *new_sock;
	struct sockaddr_in server , client;
	char *message;
	
	//Create socket
	socket_desc = socket(AF_INET , SOCK_STREAM , 0);
	if (socket_desc == -1)
	{
		perror("Could not create socket");
		return 1;
	}

	// Be able to reuse socket when program crashes
	if (setsockopt(socket_desc, SOL_SOCKET, SO_REUSEADDR, &(int){1}, sizeof(int)) < 0)
		    perror("setsockopt(SO_REUSEADDR) failed");
	
	//Prepare the sockaddr_in structure
	server.sin_family = AF_INET;
	server.sin_addr.s_addr = INADDR_ANY;
	server.sin_port = htons( 8888 );
	
	//Bind
	if( bind(socket_desc,(struct sockaddr *)&server , sizeof(server)) < 0)
	{
		perror("bind failed");
		return 1;
	}
	puts("bind done");
	
	//Listen
	listen(socket_desc , 3);
	
	//Accept and incoming connection
	puts("Waiting for incoming connections...");
	c = sizeof(struct sockaddr_in);

	// Main loop that receives clients
	while( (new_socket = accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&c))  && getQuit() == 0)
	{
		puts("Connection accepted");

		pthread_t sniffer_thread;
		new_sock = malloc(1);
		*new_sock = new_socket;

		if( pthread_create( &sniffer_thread , NULL ,  connection_handler , (void*) new_sock) < 0)
		{
			perror("could not create thread");
			return 1;
		}

		puts("Handler assigned");
	}

	if (new_socket<0)
	{
		perror("accept failed");
		return 1;
	}

	pthread_join( prompt_t, NULL);

	// Clear clients vector managed by threads.h
	clearClients();

	return 0;
}
