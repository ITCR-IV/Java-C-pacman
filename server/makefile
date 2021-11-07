IDIR =include
CC=gcc
CFLAGS=-g -I$(IDIR) 

ODIR=obj
LDIR =lib
SRCDIR=src

LIBS=-lm -lpthread

_DEPS = prompt.h threads.h
DEPS = $(patsubst %,$(IDIR)/%,$(_DEPS))

_OBJ = main.o prompt.o threads.o
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
