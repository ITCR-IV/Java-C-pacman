#include "threads.h"

// mutex vars 
static pthread_mutex_t stopMutex;

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
