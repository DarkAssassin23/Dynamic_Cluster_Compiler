#!/usr/bin/env python3
import sys, os, shutil, time, platform
from subprocess import Popen, PIPE

dirList = ""
path = os.path.abspath(os.path.dirname(__file__))+"/"
baseDir = os.path.expanduser("./")
onlineHosts = []
typesOfFiles = []
mountConfig = {}
validMountKeys = [
    "remoteHost",
    "remoteUser",
    "remoteDir",
    "mountPoint"
]

requiredCfgKeys = [
    "fileTypes",
    "makeBuild",
    "makeExecutable"
]

mainConfig = {}

if(len(sys.argv) == 2):
    baseDir = sys.argv[1]
    if(not "/" in baseDir):
        baseDir += "/"
    dirList = [baseDir]
else:
    dirList = [baseDir]


def pingHost(ip):
    cmd = ['ping', '-c', '1', '-t', '1', ip]
    process = Popen(cmd, stdout=PIPE, stderr=PIPE)
    stdout, stderr = process.communicate()
    return process.returncode == 0 # code=0 means available

def masterNodeCheck(master):
    if(master=="" or master.startswith("#")):
        print("ERROR: No master node found. Check your \'inventory.ini\' file")
        print("Also check to make sure your master node is not commented out")
        return False
    
    if(not pingHost(master)):
        print("ERROR: Unable to ping the master node, and thus it is assumed down")
        print("Exiting...")
        return False

    return True

def getHostList():
    invalidMaster = False
    contents = ""
    filename = ""
    try:
        if("inventoryFile" not in mainConfig):
            filename = baseDir+"inventory.ini"
            mainConfig["inventoryFile"] = filename
        else:
            filename = mainConfig["inventoryFile"]

        with open(filename) as f:
            contents = f.read()
    except:
        print("ERROR: Unable to read your \'inventory.ini\' file")
        print("If you moved it from its default location, the root of the given directory,\nor called it something "+
            "other than \'inventory.ini\', make sure the \'inventoryFile\'\nvariable in your \'distributed.cfg\' file is "+
             "properly configured")
        exit()
    try:
        # Strip out the workers and master from the inventory.ini file
        workers = contents.split("[workers]")[1].strip().split("\n[master]")[0].strip().split("\n")
        master = contents.split("[master]")[1].strip().split("\n")[0].strip()
        if(masterNodeCheck(master)):
            hosts = [master]
            for w in workers:
                # Don't include commented out hosts
                # or reinclude the master node
                if(not (w.startswith("#")) and not (w==master)):
                    # Check if host is online
                    if(pingHost(w)):
                        hosts.append(w)
            return hosts
        else:
            invalidMaster = True
    except:
        print("ERROR: Parsing your \'inventory.ini\' file failed")
        print("Make sure it is properly formated")


    if(invalidMaster):
        exit()

# Makes sure the distributed makefile is present
def distributedMakefilePresent():
    filename = ""
    if("distributedMakefile" not in mainConfig):
        filename = baseDir+"distributed_makefile"
        mainConfig["distributedMakefile"] = filename    
    else:
        filename = mainConfig["distributedMakefile"]

    if(not os.path.isfile(filename)):
        print("ERROR: Unable to find your distributed makefile")
        print("If you moved it from its default location, the root of the given directory,\nor called it something "+
            "other than \'distributed_makefile\', make sure the \n\'distributedMakefile\' variable in your \'distributed.cfg\' file is "+
             "properly configured")        
        exit()

# Takes in the lines of a config file and parses it
def parseConfig(lines, type):
    configDictionary = {}
    for line in lines:
        # Make sure a key value pair is present and not
        # commented out
        if(":" in line and (not line.startswith("#"))):
            validLine = True
            # A comment exists on the line, so remove everything
            # after it
            if("#" in line):
                commentIndex = line.index("#")
                colonIndex = line.index(":")
                # Check if the commented section begins before
                # the colon, if so this is not a valid line
                # and should be ignored
                if(commentIndex<colonIndex):
                    validLine = False
                # Remove everything after the commented section
                else:
                    line = line[:commentIndex]
            if(validLine):
                key, val = line.split(":")
                if(type=="mnt" and not key in validMountKeys):
                    pass
                else:
                    key = key.strip()
                    val = val.strip()
                    if(type=="main"):
                        val = val.lower()
                    configDictionary[key] = val
    
    return configDictionary

# Loads the main 'distributed.cfg' file
def loadDistributedCfg():
    try:
        with open(baseDir+"distributed.cfg") as f:
            lines = f.readlines()
        cfg = parseConfig(lines, "main")
        validConfig = True
        for x in requiredCfgKeys:
            if(not x in cfg):
                validConfig = False
                break
    except:
        print("ERROR: Unable to read your distributed.cfg file, make sure it exists in the root\nof the given directory,"+
                " and is named \'distributed.cfg\'")
        exit()

    if(not validConfig):
        print("ERROR: One, or more, required key value pairs are missing from your distributed.cfg file")
        print("The distributed.cfg file must contain, at least, the following keys: ", end="")
        count = 0
        for k in requiredCfgKeys:
            ending = ", "
            if(count == len(requiredCfgKeys)-1):
                ending = "\n"
            print(f'{k}', end=ending)
            count += 1
        exit()

    # Since ./ is the same as the base directory
    # update the path accordingly
    for key,val in cfg.items():
        if(val.startswith("./")):
            cfg[key] = baseDir+val[2:]

    return cfg        

# Loads in the 'mnt.cfg' file and ensures
# all the necessary key value pairs are present
def loadMountPointCfg():
    validConfig = True
    cfg = {}
    try:
        lines = []
        filename = ""
        if("mntCfgFile" not in mainConfig):
            filename = baseDir+"mnt.cfg"
            mainConfig["mntCfgFile"] = filename
        else:
            filename = mainConfig["mntCfgFile"]

        with open(filename) as f:
            lines = f.readlines()

        cfg = parseConfig(lines, "mnt")

        # Makes sure the config file has all the
        # required keys. Since in the parseConfig() function,
        # only valid mount keys are added so the only way the 
        # lengths will be the same is if all the valid mount keys
        # are present
        validConfig = len(cfg)==len(validMountKeys)

    except:
        print("ERROR: Unable to read your mount point config file")
        print("If you moved it from its default location, the root of the given directory,\nor called it something "+
            "other than \'mnt.cfg\', make sure the \'mntCfgFile\'\nvariable in your \'distributed.cfg\' file is "+
             "properly configured")
        exit()

    if(not validConfig):
        print("ERROR: One, or more, required key value pairs are missing from your mnt.cfg file")
        print("The mnt.cfg file, should contain the following keys: ", end="")
        count = 0
        for k in validMountKeys:
            ending = ", "
            if(count == len(validMountKeys)-1):
                ending = "\n"
            print(f'{k}', end=ending)
            count += 1
        exit()
    return cfg

# Grabs the types of source files from the main config.
# It strips out the curly braces and splits them based on commas
def loadFilesTypes():
    typesOfFiles = mainConfig["fileTypes"].replace("{","").replace("}","").split(",")

    if(len(typesOfFiles)==1 and typesOfFiles[0]==""):
        print("ERROR: No file types were provided")
        exit()

    for x in range(len(typesOfFiles)):
        typesOfFiles[x] = typesOfFiles[x].strip()

    return typesOfFiles

# Based on the OS sets the appropriate command that 
# 'make -j' would use all available cores
def getJFlag():
    if ("clusterOS" not in mainConfig):
        return "nproc"
    if(mainConfig["clusterOS"]=="macos"):
        return "sysctl -n hw.ncpu"
    elif(mainConfig["clusterOS"]=="windows"):
        return "(Get-CimInstance -ClassName Win32_Processor).NumberOfLogicalProcessors"

    return "nproc"

def makeBatchfile():
    mountCMD = "@echo off\nif exist "+mountConfig["mountPoint"]+":\\ (\n"
    mountCMD += "\tnet use "+mountConfig["mountPoint"]+": /delete\n)\n"
    mountCMD += "net use "+mountConfig["mountPoint"]+": "
    mountCMD += "\\\\sshfs.kr\\"+mountConfig["remoteUser"]+"@"+mountConfig["remoteHost"]
    mountCMD += mountConfig["remoteDir"]+"\n"

    return mountCMD

files = []
try:
    mainConfig = loadDistributedCfg()
    typesOfFiles = loadFilesTypes()
    onlineHosts = getHostList()
    distributedMakefilePresent()
    mountConfig = loadMountPointCfg()
    jFlag = getJFlag()

    # Make a directory for each host that is online
    # and copy the distributed makefile into it
    # if it already exists, remove it to not cause conflicts
    # later when distributing source code
    for host in onlineHosts:
        dir = baseDir+host
        if(os.path.isdir(dir)):
            shutil.rmtree(dir)
        os.mkdir(dir)    
        shutil.copy(mainConfig["distributedMakefile"], dir+"/makefile")

    isFirstLoop = True
    basePath = ""
    # Get all of the files in the directory
  
    while len(dirList) > 0:
        for (dirpath, dirnames, filenames) in os.walk(dirList.pop()):
            if(isFirstLoop):
                basePath = dirpath
                isFirstLoop = False
            dirList.extend(dirnames)
            files.extend(map(lambda n: os.path.join(*n), zip([dirpath] * len(filenames), filenames)))

    totalFiles = 0
    for x in files:
        for i in typesOfFiles:
            if(x.endswith(i)):
                srcPath = ""
                if(platform.system == "Windows"):
                    srcPath = x[:x.rfind("\\")].replace(basePath, "")
                else:
                    srcPath = x[:x.rfind("/")].replace(basePath, "")
                fileDestPath = basePath+onlineHosts[(totalFiles%len(onlineHosts))]+"/"+srcPath+"/"
                if(not os.path.isdir(fileDestPath)):
                    os.makedirs(fileDestPath)
                shutil.copy(x, fileDestPath)
                totalFiles += 1
                break

    deploy = "deploy.yml"
    if("clusterOS" in mainConfig and mainConfig["clusterOS"]=="windows"):
        deploy = "deployWin.yml"
        mountConfig["remoteDir"] = mountConfig["remoteDir"].replace("/","\\")
        with open(path+"dcc-mount.bat","w") as f:
            f.write(makeBatchfile())

    extraVars = ""
    for key, val in mountConfig.items():
        extraVars += key+"="+val+" "
    extraVars += "makeBuild="+mainConfig["makeBuild"]+" "
    extraVars += "makeExecutable="+mainConfig["makeExecutable"]+" "

    extraVars += "jFlag="+jFlag
    
    cmd = "ansible-playbook -i "+mainConfig["inventoryFile"]+" "+path+""+deploy+" --extra-vars \""+extraVars+"\""
    #print(cmd)
    os.system(cmd)
    if(os.path.exists(path+"dcc-mount.bat")):
        os.remove(path+"dcc-mount.bat")

except Exception as e:
    print(e)
    print("If you manually entered a path name, make sure it is correct")
