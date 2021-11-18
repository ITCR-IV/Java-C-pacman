IDIR =include
CC=gcc
CFLAGS=-g -I$(IDIR) 

ODIR=obj
LDIR =lib
SRCDIR=src

LIBS=-lm -lpthread

_DEPS = constants.h prompt.h threads.h json.h vec.h socket_handler.h
DEPS = $(patsubst %,$(IDIR)/%,$(_DEPS))

_OBJ = main.o prompt.o threads.o json.o vec.o socket_handler.o
OBJ = $(patsubst %,$(ODIR)/%,$(_OBJ))

#$(info $$OBJ/%.o is [${OBJ}])

$(ODIR)/%.o: $(SRCDIR)/%.c $(DEPS)
	@mkdir -p $(@D)
	$(CC) -c -o $@ $< $(CFLAGS)

server: $(OBJ)
	$(CC) -o $@ $^ $(CFLAGS) $(LIBS)

.PHONY: clean

clean:
	rm -f $(ODIR)/*.o *~ core $(INCDIR)/*~ server
	rmdir $(ODIR)
