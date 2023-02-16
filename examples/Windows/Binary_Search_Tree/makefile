# Standard makefile

TARGET = bst
CC = g++
CFLAGS = -std=gnu++11 -g -Wall

ROOT = .
BINDIR =  bin
OBJDIR = obj

SRCDIR =  src
TREEDIR = $(SRCDIR)/tree

.PHONY: default all clean

default: $(BINDIR)/$(TARGET)
all: default

INCLUDES = -I $(TREEDIR)

SRCS = $(wildcard $(SRCDIR)/*.cpp $(TREEDIR)/*.cpp)
OBJECTS = $(patsubst %.cpp, $(OBJDIR)/%.o, $(SRCS))
HEADERS = $(wildcard $(TREEDIR)/*.h)

$(OBJDIR)/%.o: %.cpp $(HEADERS)
	@mkdir -p $(@D)
	$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@

.PRECIOUS: $(TARGET) $(OBJECTS)

$(BINDIR)/$(TARGET): $(OBJECTS)
	@mkdir -p $(@D)
	$(CC) $(OBJECTS) -g -Wall $(LIBS) -o $@

clean:
	-rm -rf $(OBJDIR) $(BINDIR)