#include "prompt.h"
#include "threads.h"

// main
int main( int argc, char *args[] ) {

	// Start command prompt
	pthread_t prompt_t;
	pthread_create( &prompt_t, NULL, promptLoop, NULL);

	// Main loop that takes stuff from the server
	while(getQuit() == 0){
		// JOSE AQUÍ IRÍA LO DEL SOCKET EN CASO DE QUE SE VENGA A IMPLEMENTARLO
		;
	}

	pthread_join( prompt_t, NULL);

}
