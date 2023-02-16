# Standard makefile

JAR = bin/Vehicle_Cloud_Sim.jar
JC = javac
MAIN = Main

# Source Dirs
SRC = src
GLOBAL = $(SRC)/Globals
HIGHWAY = $(SRC)/Highway
NETWORK = $(SRC)/Network

JCFLAGS = -cp $(SRC):$(GLOBAL):$(HIGHWAY):$(NETWORK) -d $(CLSDIR)

#Class Files Directory
CLSDIR = obj

#Log Files Directory
LOGDIR = log

#Source Files
SRCS = $(wildcard $(SRC)/*.java $(GLOBAL)/*.java $(HIGHWAY)/*.java \
		$(NETWORK)/*.java)

#Class Files
CLS = $(patsubst %.java, $(CLSDIR)/%.class, $(SRCS))

default: jar
all: jar

$(CLSDIR)/%.class: %.java
	$(JC) $(JCFLAGS) $<
	
jar: $(CLS)
	jar cfe $(JAR) $(MAIN) -C $(CLSDIR) .
	
clean:
	-rm -rf $(CLSDIR) $(LOGDIR) bin
