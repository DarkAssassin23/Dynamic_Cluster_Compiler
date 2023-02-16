# Standard makefile

JAR = bin/Vehicle_Cloud_Sim.jar
JC = javac
MAIN = Main

# Source Dirs
SRC = src
GLOBAL = $(SRC)/Globals
HIGHWAY = $(SRC)/Highway
NETWORK = $(SRC)/Network

JCFLAGS = -cp $(CLSDIR) -d $(CLSDIR)

#Class Files Directory
CLSDIR = obj

#Log Files Directory
LOGDIR = log

#Source Files
SRCS = $(wildcard $(SRC)/*.java $(GLOBAL)/*.java $(HIGHWAY)/*.java \
		$(NETWORK)/*.java)

#Class Files
CLS = $(patsubst %.java, $(CLSDIR)/%.class, $(SRCS))

default: cls

cls: $(CLS)

$(CLSDIR)/%.class: %.java
	$(JC) $(JCFLAGS) $<
	
jar:
	jar cfe $(JAR) $(MAIN) -C $(CLSDIR) .
	
clean:
	-rm -rf $(CLSDIR) $(LOGDIR) bin
