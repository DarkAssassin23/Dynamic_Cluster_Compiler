# Standard makefile

TARGET = timeRemainingServer
CC = gcc
CFLAGS = -Os
LIBS = -lpthread

GENERAL_UTILS_HEADERDIR = headers/general_utils
SERVER_UTILS_HEADERDIR = headers/server_utils

SRCDIR = src
GENERAL_UTILS_SRCDIR = $(SRCDIR)/general_utils
SERVER_UTILS_SRCDIR = $(SRCDIR)/server_utils

INCLUDES = -I $(GENERAL_UTILS_HEADERDIR) -I $(SERVER_UTILS_HEADERDIR)

OBJDIR = obj
BINDIR = bin

.PHONY: default all clean debug

default: $(BINDIR)/$(TARGET)
all: default

debug: CFLAGS = -g -Wall
debug: $(BINDIR)/$(TARGET)

SRCS = $(wildcard $(SRCDIR)/*.c $(GENERAL_UTILS_SRCDIR)/*.c $(SERVER_UTILS_SRCDIR)/*.c)
OBJECTS = $(patsubst %.c, $(OBJDIR)/%.o, $(SRCS))
HEADERS = $(wildcard $(GENERAL_UTILS_HEADERDIR)/*.h, $(SERVER_UTILS_HEADERDIR)/*.h)

$(OBJDIR)/%.o: %.c $(HEADERS)
	@mkdir -p $(@D)
	$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@

$(BINDIR)/$(TARGET): $(OBJECTS)
	@mkdir -p $(@D)
	$(CC) $(OBJECTS) $(CFLAGS) $(LIBS) -o $@
	@cp configs/server.cfg $(BINDIR)

clean:
	-rm -rf $(OBJDIR) $(BINDIR)
