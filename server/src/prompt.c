#include "prompt.h"

#include <stdio.h>
#include <string.h>

#include "threads.h"

// Función para contar dígitos
int contar_digitos(int num){
	if (num==0)
		return 0;
	return 1 + contar_digitos(num/10);
}


// Función para parsear comandos relacionados a fantasmas
void ghost(int which, char* args){
	char* ghost_str;
	switch(which) {
		case 0:
			ghost_str = "blinky";
			break;
		case 1:
			ghost_str = "pinky";
			break;
		case 2:
			ghost_str = "inky";
			break;
		case 3:
			ghost_str = "clyde";
			break;
		default:
			printf("Dado un valor malo de 'which' a la función ghost en prompt.c");
			return;
	}

	int arg;
	char junk;
	if( 1 == sscanf(args, "%d", &arg)){ 
		char str[] ="Creando a %s para el jugador %d!\n";
		printf(str,ghost_str, arg);
		// crea buffer que debería tener hasta 4 espacios más de lo necesario
		char msg[strlen(str)+strlen(ghost_str)+contar_digitos(arg)]; 
		sprintf(msg, str, ghost_str, arg);
		writeToPlayer(arg, msg);
	}
	else if( 1 == sscanf(args, "%c", &junk)){ // Si detecta algún char que no sea dígito entonces todo mal
		printf("Uso incorrecto, el uso correcto de este comando es:\n%s [jugador]\n", ghost_str);
	}
	else{
		char str[] ="Creando a %s para el jugador 0\n";
		printf(str,ghost_str);
		char msg[strlen(str)+strlen(ghost_str)]; 
		sprintf(msg, str, ghost_str, arg);
		writeToPlayer(0, msg);
	}
}

// Función para parsear los comandos relacionados a las pastillas
// Entradas:
// 	which: 0 = pastillas grandes, 1 = pastillas pequeñas
// 	args: Array que contiene los argumentos de la función
void pastillas(int which, char* args){
	// which = 0: big ones
	// which = 1: smol ones

	char* pasti_str;
	switch(which) {
		case 0:
			pasti_str = "grandes";
			break;
		case 1:
			pasti_str = "pequeñas";
			break;
		default:
			printf("Dado un valor malo de 'which' a la función pastillas en prompt.c");
			return;
	}

	int arg;
	char junk;
	if( 1 == sscanf(args, "%d", &arg)){ 
		printf("Reseteando las pastillas %s para el jugador %d!\n",pasti_str, arg);
	}
	else if( 1 == sscanf(args, "%c", &junk)){ // Si detecta algún char que no sea dígito entonces todo mal
		printf("Uso incorrecto, el uso correcto de este comando es:\n%s [jugador]\n", which ? "pasti" : "pastillas" );
	}
	else{
		printf("Reseteando las pastillas %s para el jugador 0\n", pasti_str);
	}
}

// command prompt thread funct
void *promptLoop(){
	// posibles comandos:
	// help		= displays this message
	// quit		= exits the program closing the socket
	// Blinky/Pinky/Inky/Clyde [jugador]	= Envía al fantasma correspondiente
	// pastillas [jugador]			= Resetea las pastillas grandes del juego
	// pasti [jugador]			= Resetea las pastillas pequeñas del juego
	// fruta <valor> [jugador]		= Crea una fruta con un valor dado
	// velocidad +/- [n] [jugador]		= Cambia la velocidad de los fantasmas, aumentando con un + y disminuyendo con un -. Se puede especificar un 'n' de qué tanto variar la velocidad, si se omite se toma como 1.
	//
	// **Nota:** El campo opcional [jugador] es para escoger a cuál jugador enviarle el comando, si no se incluye por default es 0 (el primer jugador).
char* help_text = "posibles comandos:\n"
	"	help = Muestra este mensaje\n"
	"	quit = Se sale del programa y cierra la conexión\n"
	"	Blinky/Pinky/Inky/Clyde [jugador] = Envía al fantasma correspondiente\n"
	"	pastillas [jugador] = Resetea las pastillas grandes del juego\n"
	"	pasti [jugador] = Resetea las pastillas pequeñas del juego\n"
	"	fruta <valor> [jugador] = Crea una fruta con un valor dado\n"
	"	velocidad +/- [n] [jugador] = Cambia la velocidad de los fantasmas, aumentando con un + y disminuyendo con un -. Se puede especificar un 'n' de qué tanto variar la velocidad, si se omite se toma como 1.\n"
	"\nNota: El campo opcional [jugador] es para escoger a cuál jugador enviarle el comando, si no se incluye por default es 0 (el primer jugador).\n";

	char command[30];
	char args[100];

	while(getQuit() == 0){
		//printf("Enter a command:\n");
		fputs("> ", stdout);
		fflush(stdout);
		// command
		fscanf(stdin, "%29s", command);

		// args
		fgets (args, sizeof(args)-1, stdin);
		args[sizeof(args)-1] = 0;

		// remove newlines
		args[strcspn(args, "\r\n")] = 0;

		//printf("The following command was entered: '%s'\n", command);
		//printf("And the following args were used: '%s'\n", args);
		printf("\n");

		if(strcmp(command, "quit") == 0 || strcmp(command, "exit") == 0)
			setQuit(1);
		else if(strcmp(command, "help") == 0)
			printf(help_text);

		// fantasmas
		else if(strcmp(command, "Blinky") == 0 || strcmp(command, "blinky") == 0){
			ghost(0, args);
		}
		else if(strcmp(command, "Pinky") == 0 || strcmp(command, "pinky") == 0){
			ghost(1, args);
		}
		else if(strcmp(command, "Inky") == 0 || strcmp(command, "inky") == 0){
			ghost(2, args);
		}
		else if(strcmp(command, "Clyde") == 0 || strcmp(command, "clyde") == 0){
			ghost(3, args);
		}

		// Pastillas
		else if(strcmp(command, "pastillas") == 0){
			pastillas(0, args);
		}
		else if(strcmp(command, "pasti") == 0){
			pastillas(1, args);
		}

		// Fruta
		else if(strcmp(command, "fruta") == 0){
			int arg1, arg2 = 0;
			char junk;

			// Revisar si se metió la variación invalida donde dan un número pero seguido de basura
			if(2 == sscanf(args, "%d %c", &arg1, &junk) && 1 == sscanf(args, "%d %d", &arg1, &arg2)){
				printf("Uso incorrecto, el uso correcto de este comando es:\nfruta <n>\n");
			}
			// Caso válido
			else if(1 <= sscanf(args, "%d %d", &arg1, &arg2)){ // Si se tiene aunque sea el primer arg
				printf("Creando una fruta de %d puntos para el jugador %d!\n", arg1, arg2);
			}
			// Casos donde no se puso ni un dígito
			else{
				printf("Uso incorrecto, el uso correcto de este comando es:\nfruta <n>\n");
			}
		}

		// Velocidad
		else if(strcmp(command, "velocidad") == 0){
			int arg1 = 1, arg2 = 0;
			char PoM, junk;
			int r = sscanf(args, " %c %d %d", &PoM, &arg1, &arg2);

			// Evaluar casos donde se empieza bien el comando pero luego se pone alguna basura inválida
			if( (2 == sscanf(args, " %c %c", &PoM, &junk) && 1 == r) || ( 3 == sscanf(args, " %c %d %c", &PoM, &arg1, &junk) && 2 == r ) )
				printf("Uso incorrecto, el uso correcto de este comando es:\nvelocidad +/- [n] [jugador]\n");

			// Caso válido
			else if( 1 <= sscanf(args, " %c %d %d", &PoM, &arg1, &arg2)){
				if(PoM == '+'){ // Aumento
					printf("Aumentando la velocidad de los fantasmas del jugador %d, en %d incrementos!\n", arg2, arg1);
				}
				else if(PoM == '-'){ // Disminución
					printf("Disminuyendo la velocidad de los fantasmas del jugador %d, en %d incrementos\n", arg2, arg1);
				}
				else{ // Inválido si no se dio un + o un -
					printf("Uso incorrecto, debe especificar si + o -. El uso correcto de este comando es:\nvelocidad +/- [n] [jugador]\n");
				}
			}
			// Casos donde el comando empieza inválido
			else{
				printf("Uso incorrecto, el uso correcto de este comando es:\nvelocidad +/- [n] [jugador]\n");
			}
		}
		else
			printf("Comando no reconocido, utilice el comando 'help' para ver los posibles comandos.\n");
		printf("\n");
	}
}
