# Standard makefile

TARGET = hello
CC = gcc
CFLAGS = -g -Wall

ROOT = .
BINDIR =  bin
SRCDIR =  src
OBJDIR = obj

.PHONY: default all clean

default: $(BINDIR)/$(TARGET)
all: default

INCLUDES = -I $(SRCDIR)

SRCS = $(wildcard $(SRCDIR)/*.c)
OBJECTS = $(patsubst $(SRCDIR)/%.c, $(OBJDIR)/%.o, $(SRCS))
HEADERS = $(wildcard $(SRCDIR)/*.h)

$(OBJDIR)/%.o: $(SRCDIR)/%.c $(HEADERS)
	@mkdir -p $(@D)
	$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@

.PRECIOUS: $(TARGET) $(OBJECTS)

$(BINDIR)/$(TARGET): $(OBJECTS)
	@mkdir -p $(@D)
	$(CC) $(OBJECTS) -g -Wall $(LIBS) -o $@

clean:
	-rm -rf $(OBJDIR) $(BINDIR)