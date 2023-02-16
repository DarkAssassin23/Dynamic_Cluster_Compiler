import java.util.UUID;

public class VirtualMachine
{
    private UUID id;
    private int requiredNumCores;
    private int requiredMemory;
    private double percentComplete;
    private double taskLength;
    
    private double timeTaken; // in seconds
    private int size; // size of the VM in MB
    private int timesMigrated;
    private int timesProgressWasLost;
    private UUID vehicleAssignedTo;

    /**
     * Constructor for virtual machine class
     * @param requiredNumCores the number of cores required for the vm
     * @param requiredMemory the amount of memory, in KB, for the vm
     * @param taskLength the length of time the task should take to complete
     * @param size the size of the vm, in MB
     */
    public VirtualMachine(int requiredNumCores, int requiredMemory, double taskLength, int size)
    {
    	id = UUID.randomUUID();
        this.requiredNumCores = requiredNumCores;
        this.requiredMemory = requiredMemory;
        percentComplete = 0;
        this.taskLength = taskLength;
        this.size = size;
        timeTaken = 0;
        timesMigrated = -2;
        timesProgressWasLost = 0;
        vehicleAssignedTo = null;
    }
    
    /**
     * Constructor for the virtual machine class
     * this constructor randomly generates requirements for the VM
     * rather than manually setting them 
     */
    public VirtualMachine()
    {
    	id = UUID.randomUUID();
    	// add 1 to max cores to make the range inclusive
    	requiredNumCores = (int)(Math.random()*((Globals.MAX_VM_CORES+1)-Globals.MIN_VM_CORES))+Globals.MIN_VM_CORES;
    	setRequiredMemory();
    	setSize();
    	taskLength = Math.random()*(Globals.MAX_TASK_LEN-Globals.MIN_TASK_LEN)+Globals.MIN_TASK_LEN;
    	percentComplete = 0;
        timeTaken = 0;
        timesMigrated = -2;
        timesProgressWasLost = 0;
        vehicleAssignedTo = null;
    }

	/**
	 * @return the id
	 */
	public UUID getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) 
	{
		this.id = id;
	}

	/**
	 * @return the requiredNumCores
	 */
	public int getRequiredNumCores() 
	{
		return requiredNumCores;
	}

	/**
	 * @param requiredNumCores the requiredNumCores to set
	 */
	public void setRequiredNumCores(int requiredNumCores) 
	{
		this.requiredNumCores = requiredNumCores;
	}

	/**
	 * @return the requiredMemory
	 */
	public int getRequiredMemory() 
	{
		return requiredMemory;
	}

	/**
	 * @param requiredMemory the requiredMemory to set
	 */
	public void setRequiredMemory(int requiredMemory) 
	{
		this.requiredMemory = requiredMemory;
	}
	
	/**
	 * Dynamically sets the required memory based on 
	 * required cores, and rounds to the nearest GB
	 */
	public void setRequiredMemory() 
	{
		// Adjust the memory capacity based on the number of cores
		// the less cores the VM needs, the less memory it will need,
		// and the max memory it can have will be capped
//		int modifier = (Globals.MAX_VM_MEM/1024) - Globals.MAX_VM_CORES;
//		int range = Globals.MAX_VM_CORES-(Globals.MAX_VM_CORES - requiredNumCores)+modifier;
//		range *= Globals.MIN_VM_MEM;
		int modifier = 1;
		if(requiredNumCores>2)
			modifier = (int)(Math.round(Math.log(requiredNumCores)))*2;
		int minMem = Globals.MIN_VM_MEM * modifier;
		int range = minMem*2;

		requiredMemory = (int)(Math.random()*(range))+minMem;//*((int)(Math.random()*requiredNumCores)+1));
		// Rounds up to the nearest GB
		requiredMemory = (int)(Math.ceil(requiredMemory/1024.0))*1024;
		// Make sure required memory does not exceed the max
		requiredMemory = Math.min(requiredMemory, Globals.MAX_VM_MEM);
	}

	/**
	 * @return the percentComplete
	 */
	public double getPercentComplete() 
	{
		return percentComplete;
	}

	/**
	 * @param percentComplete the percentComplete to set
	 */
	public void setPercentComplete(double percentComplete) 
	{
		this.percentComplete = percentComplete;
	}

	/**
	 * @return the taskLength
	 */
	public double getTaskLength() 
	{
		return taskLength;
	}

	/**
	 * @param taskLength the taskLength to set
	 */
	public void setTaskLength(double taskLength) 
	{
		this.taskLength = taskLength;
	}

	/**
	 * @return the timeTaken in seconds
	 */
	public double getTimeTaken() 
	{
		return timeTaken;
	}

	/**
	 * Increments the time taken for the VM to complete by
	 * one second
	 */
	public void incrementTimeTaken() 
	{
		timeTaken++;
	}

	/**
	 * @return the size in MB
	 */
	public int getSize() 
	{
		return size;
	}

	/**
	 * @param size the size to set in MB
	 */
	public void setSize(int size) 
	{
		this.size = size;
	}
	
	/**
	 * Dynamically set size of VM based on 
	 * how many cores are required. Typically,
	 * smaller applications which require less cores are 
	 * often smaller in size, so their max size is adjusted
	 * accordingly
	 */
	public void setSize()
	{
		// Adjust the VM size based on the number of cores
//		int minSize = (int)(Globals.MIN_VM_SIZE*Math.pow(requiredNumCores, 2));
//		double multiple = Math.log(Math.pow(requiredNumCores, 2));
//		
//		if(multiple == 0)
//			multiple = .5;
//		
//		int range = (int)((Globals.MAX_VM_SIZE/Globals.MAX_VM_CORES)*multiple);
//
//		if((range+minSize)>Globals.MAX_VM_SIZE)
//			range = Globals.MAX_VM_SIZE - minSize;
		int range;
		int minSize;
		if(requiredNumCores<3)
		{
			range = Globals.MIN_VM_SIZE*4;
			minSize = Globals.MIN_VM_SIZE;
		}
		else if(requiredNumCores==3)
		{
			minSize = Globals.MIN_VM_SIZE*requiredNumCores;
			range = minSize*4;
		}
		else
		{
			minSize = Globals.MAX_VM_SIZE/2;
			range = Globals.MAX_VM_SIZE - minSize;
		}
		
//		System.out.println(range);
//		System.out.println(minSize);
		
		size = (int)(Math.random()*range)+minSize;

	}

	/**
	 * @return the timesMigrated
	 */
	public int getTimesMigrated() 
	{
		return timesMigrated;
	}

	/**
	 * Adds one to the times the VM has been migrated
	 */
	public void incrementTimesMigrated() 
	{
		timesMigrated++;
	}

	/**
	 * Returns the number of times progress on 
	 * the vm was lost due to the vehicle leaving the 
	 * highway before the vm could be migrated
	 * @return the number of times progress was lost
	 */
	public int getTimesProgressWasLost() 
	{
		return timesProgressWasLost;
	}

	/**
	 * Increments the number of times progress was lost
	 * due to a vehicle leaving the highway before the vm 
	 * could be migrated by one
	 */
	public void incrementTimesProgressWasLost()
	{
		timesProgressWasLost++;
	}

	/**
	 * Returns the UUID of the Vehicle it is assigned to
	 * @return the vehicleAssignedTo
	 */
	public UUID getVehicleAssignedTo() 
	{
		return vehicleAssignedTo;
	}

	/**
	 * @param vehicleID the vehicleID to set
	 */
	public void setVehicleAssignedTo(UUID vehicleID) 
	{
		vehicleAssignedTo = vehicleID;
	}
	
	/**
	 * Prints out info about the VM
	 */
	public void printVMInfo()
	{
		System.out.println("VM ID: "+id);
		System.out.println("Required Cores: "+requiredNumCores);
		System.out.println("Required Memory: "+requiredMemory+" MB");
		System.out.println("Size: "+size+" MB");
		System.out.println("Projected time to complete: "+taskLength+" sec");
		
		System.out.println();
	}

}