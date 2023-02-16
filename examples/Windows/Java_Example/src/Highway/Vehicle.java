import java.util.UUID;

public class Vehicle
{	
	private UUID uuid;
    private double speed;
    private int distanceOnHighway;
    private double distanceToTravel;
    private double mileMarker;
    private boolean onHighway;
    
    private VirtualMachine vm;
    private double uploadSpeed; // in Mbps
    private double downloadSpeed; // in Mbps
    private short cores;
    private int memory; // in MB
    private double vmCompletionPercentage;
    private boolean transferring;
    private boolean counted;
    private boolean successfulCommsWithAP;
    private boolean tryingToTransfer;
    private int vmsWorkedOn;

	/**
	 * Constructor for Vehicle
	 * @param uuid
	 * @param speed
	 * @param distanceOnHighway
	 * @param mileMarker
	 */
	public Vehicle(double speed, int distanceOnHighway, double mileMarker) {
		uuid = UUID.randomUUID();
		this.speed = speed;
		this.distanceOnHighway = distanceOnHighway;
		this.mileMarker = mileMarker;
		distanceToTravel = distanceOnHighway;
		vmCompletionPercentage = 0;
		transferring = false;
		counted = false;
		successfulCommsWithAP = true;
		tryingToTransfer = true;
		setVmsWorkedOn(0);
		setUploadSpeed();
		setDownloadSpeed();
		setCores();
		setMemory();
	}
	
	/**
	 * Vehicle default constructor
	 */
	public Vehicle()
	{
		uuid = UUID.randomUUID();
		speed = (int)(Math.random()*(Globals.MAX_SPEED-Globals.MIN_SPEED))+Globals.MIN_SPEED;
		mileMarker = (int)(Math.random()*Globals.HIGHWAY_LEN);
		distanceOnHighway = (int)(Math.random()*(Globals.HIGHWAY_LEN-mileMarker));
		distanceToTravel = distanceOnHighway;
		vmCompletionPercentage = 0;
		transferring = false;
		counted = false;
		successfulCommsWithAP = true;
		tryingToTransfer = true;
		setVmsWorkedOn(0);
		setUploadSpeed();
		setDownloadSpeed();
		setCores();
		setMemory();
	}
    
	/**
	 * @return the speed
	 */
	public double getSpeed() 
	{
		return speed;
	}

	/**
	 * @param speed the speed to set in mph
	 */
	public void setSpeed(double speed) 
	{
		this.speed = speed;
	}

	/**
	 * @return the distanceOnHighway
	 */
	public double getDistanceOnHighway() 
	{
		return distanceOnHighway;
	}

	/**
	 * @param distanceOnHighway the distanceOnHighway to set
	 */
	public void setDistanceOnHighway(int distanceOnHighway) 
	{
		this.distanceOnHighway = distanceOnHighway;
	}

	/**
	 * @return the distanceToTravel
	 */
	public double getDistanceToTravel() 
	{
		return distanceToTravel;
	}

	/**
	 * @param distanceToTravel the distanceToTravel to set
	 */
	public void setDistanceToTravel(double distanceToTravel) 
	{
		this.distanceToTravel = distanceToTravel;
	}

	/**
	 * Gets the mile marker where the vehicle is at
	 * on the highway
	 * @return the mileMarker
	 */
	public double getMileMarker() 
	{
		return mileMarker;
	}

	/**
	 * Sets which mile marker the vehicle is at
	 * @param mileMarker the mileMarker to set
	 */
	public void setMileMarker(double mileMarker) 
	{
		this.mileMarker = mileMarker;
	}

	/**
	 * Returns if the vehicle is on the highway
	 * @return the onHighway
	 */
	public boolean isOnHighway() 
	{
		return onHighway;
	}

	/**
	 * @return the vm
	 */
	public VirtualMachine getVM() 
	{
		return vm;
	}

	/**
	 * Assigns the vehicle a VM to start work on
	 * it also updates statistical data
	 * @param vm the vm to set
	 */
	public void setVM(VirtualMachine vm) 
	{
		this.vm = vm;
		if(vm!=null)
		{
			vmCompletionPercentage = vm.getPercentComplete();
			vmsWorkedOn++;
			Statistics.setMostVehicleContributions(uuid, vmsWorkedOn);
			transferring = false;
			tryingToTransfer = false;
			if(!counted)
			{
				counted = true;
				Statistics.incrementTotalVehiclesInvolved();
				Statistics.setTotalCoresUsed(Statistics.getTotalCoresUsed()+cores);
				Statistics.setTotalMemoryUsed(Statistics.getTotalMemoryUsed()+memory);
			}

		}
		else
		{
			vmCompletionPercentage = 0;
			transferring = false;
			tryingToTransfer = false;
		}
	}

	/**
	 * @return the uploadSpeed
	 */
	public double getUploadSpeed() 
	{
		return uploadSpeed;
	}

	/**
	 * We assume the upload speed will vary
	 * so that is reflected here
	 */
	public void setUploadSpeed() 
	{
		uploadSpeed = (Math.random()*(Globals.MAX_BANDWIDTH_SPEED-Globals.MIN_BANDWIDTH_SPEED)+Globals.MIN_BANDWIDTH_SPEED);
	}

	/**
	 * @param uploadSpeed the upload speed to set
	 */
	public void setUploadSpeed(double uploadSpeed) 
	{
		this.uploadSpeed = uploadSpeed;
	}
	
	/**
	 * @return the downloadSpeed
	 */
	public double getDownloadSpeed() 
	{
		return downloadSpeed;
	}

	/**
	 * We assume the upload speed will vary
	 * so that is reflected here
	 */
	public void setDownloadSpeed() 
	{
		downloadSpeed = (Math.random()*(Globals.MAX_BANDWIDTH_SPEED-Globals.MIN_BANDWIDTH_SPEED)+Globals.MIN_BANDWIDTH_SPEED);
	}
	
	/**
	 * @param downloadSpeed the download speed to set
	 */
	public void setDownloadSpeed(double downloadSpeed) 
	{
		this.downloadSpeed = downloadSpeed;
	}

	/**
	 * @return the uuid
	 */
	public UUID getUuid() 
	{
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(UUID uuid) 
	{
		this.uuid = uuid;
	}

	/**
	 * @return the cores
	 */
	public short getCores() 
	{
		return cores;
	}

	/**
	 * Sets the number of cores the vehicle has
	 */
	public void setCores() 
	{
		int rand = (int)(Math.random()*4);
		switch (rand)
		{
		case 0:
			cores = 2;
			break;
		case 1:
			cores = 4;
			break;
		case 2: 
			cores = 6;
			break;
		default:
			cores = 8;
			break;
		}
	}

	/**
	 * @return the memory
	 */
	public int getMemory() 
	{
		return memory;
	}

	/**
	 * Sets the amount of memory the vehicle has
	 */
	public void setMemory() 
	{
		switch(cores)
		{
		case 2:
			memory = 4096;
			break;
		default:
			memory = 8192;
			break;
		}
	}

	/**
	 * @return the vmCompletionPercentage
	 */
	public double getVmCompletionPercentage() 
	{
		return vmCompletionPercentage;
	}

	/**
	 * @param vmCompletionPercentage the vmCompletionPercentage to set
	 */
	public void setVmCompletionPercentage(double vmCompletionPercentage) 
	{
		this.vmCompletionPercentage = vmCompletionPercentage;
	}
	
	/**
	 * @return the transferring
	 */
	public boolean isTransferring() 
	{
		return transferring;
	}

	/**
	 * @param transferring the transferring to set
	 */
	public void setTransferring(boolean transferring) 
	{
		this.transferring = transferring;
	}

	/**
	 * @return the if the vehicle has been counted as contributing or not
	 */
	public boolean isCounted() 
	{
		return counted;
	}

	/**
	 * @param counted set if the vehicle has been counted as contributing
	 */
	public void setCounted(boolean counted) 
	{
		this.counted = counted;
	}

	/**
	 * @return if the vehicle has secured a communication slot with an AP
	 */
	public boolean isSuccessfulCommsWithAP() 
	{
		return successfulCommsWithAP;
	}

	/**
	 * @param successfulCommsWithAP if the vehicle has secured a communication slot
	 */
	public void setSuccessfulCommsWithAP(boolean successfulCommsWithAP) 
	{
		this.successfulCommsWithAP = successfulCommsWithAP;
	}

	/**
	 * @return the vmsWorkedOn
	 */
	public int getVmsWorkedOn() 
	{
		return vmsWorkedOn;
	}

	/**
	 * @param vmsWorkedOn the number of VMs Worked on
	 */
	public void setVmsWorkedOn(int vmsWorkedOn) 
	{
		this.vmsWorkedOn = vmsWorkedOn;
	}

	/**
	 * @return if the vehicle trying to communicate
	 */
	public boolean isTryingToTransfer() 
	{
		return tryingToTransfer;
	}

	/**
	 * @param tryingToTransfer set if the vehicle is trying to communicate or not
	 */
	public void setTryingToTransfer(boolean tryingToTransfer) 
	{
		this.tryingToTransfer = tryingToTransfer;
	}

	/**
	 * Checks if the given Vehicle is the same
	 * by comparing UUID's
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vehicle other = (Vehicle) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	/**
	 * 
	 * @param t The unit of time to be returned
	 * @return the time the vehicle has left on the highway
	 */
	public double timeLeftOnHighway(Globals.TimeUnit t)
	{

		return distanceToTravel/(speed/Globals.getTimeMultiple(t));
	}
	
	/**
	 * Prints out information about the Vehicle's
	 * speed, distance to travel on the highway etc.
	 */
	public void printInfo()
	{
		System.out.println(uuid+":");
		System.out.println("\tSpeed: "+(int)getSpeed()+"mph");
		System.out.println("\tCurrent Mile Marker: "+getMileMarker());
		System.out.println("\tDistance to travel on Highway: "+getDistanceOnHighway()+"miles");
		System.out.println("\tDistance left to travel: "+getDistanceToTravel()+"miles");
		System.out.println("\tTime left on Highway: "+timeLeftOnHighway(Globals.TimeUnit.MINUTES)+"min");
		
		System.out.println("\tCores: "+cores);
		System.out.println("\tMemory: "+memory+" MB");
		System.out.println("\tUpload speed: "+uploadSpeed+"Mbps");
		System.out.println("\tDownload speed: "+downloadSpeed+"Mbps");
		
		System.out.println();
	}
	
	/**
	 * Moves the vehicle along the highway.
	 * Moving is updated based on the time unit given.
	 */
	public void move(Globals.TimeUnit t)
	{
		// Calculates how many miles were moved in the
		// given time interval
		double distanceMoved = (double)(speed/Globals.getTimeMultiple(t));
		distanceToTravel -= distanceMoved;
		mileMarker += distanceMoved;
		doWork();
		// If the distance to travel is greater than 0
		// then the vehicle is still on the highway
		onHighway = distanceToTravel>0;
	}
	
	/**
	 * Checks to see if the Vehicle is in range of an AP
	 * We assume there is an AP at every mile marker with
	 * a range of .45 miles in either direction of it.
	 * @return if the Vehicle is in range of the AP
	 */
	public boolean inRangeOfAP()
	{
		double dec = getMileMarker() - Math.floor(getMileMarker());
		return (dec<=(0+Globals.AP_MAX_RANGE)||dec>=(1-Globals.AP_MAX_RANGE));
	}
	
	/**
	 * If the Vehicle has a VM assigned to it
	 * do work.
	 */
	private void doWork()
	{
		if(vm!=null && vmCompletionPercentage<100 && !transferring)
		{
			double currentPercent = vmCompletionPercentage;
			double taskLenRemaining = vm.getTaskLength()*(currentPercent/100);
			
			// If the vehicle has more cores then the vm requires,
			// it can use the additional cores to speed up the work.
			// Similarly, if the vehicle has more RAM than is required,
			// it can also use that to speed up the work even further.
			double workDone = 1;
			workDone += Globals.ADDITIONAL_CORES_SPEED_UP*(cores-vm.getRequiredNumCores());
			workDone += Globals.ADDITIONAL_MEM_SPEED_UP*((memory-vm.getRequiredMemory())/1024);
			
			taskLenRemaining += workDone;
			currentPercent = (taskLenRemaining/vm.getTaskLength())*100;
			vmCompletionPercentage = currentPercent;
			
			//System.out.println("Percent Complete: "+vmCompletionPercentage);
			
//			double addedWork = vmCompletionPercentage - vm.getPercentComplete();
//			double bandwidth = Globals.ASSUMED_BANDWITH;
//			if(addedWork<25)
//				bandwidth -= 0;
//			else if(addedWork<40)
//				bandwidth -= .5;
//			else if(addedWork < 60)
//				bandwidth -= 1;
//			else if(addedWork < 80)
//				bandwidth -= 1.5;
//			else if(addedWork < 95)
//				bandwidth -= 2;
//			else
//				bandwidth -= 3;
			double buffer = vm.getTaskLength()*0.05;
			//double buffer = Globals.BUFFER_WORK_TIME;
			double vmTransferTime = Globals.calculateTransferRateTime(vm.getSize(), Globals.ASSUMED_BANDWITH);
			
			// There isn't enough time to complete the job, so the job
			// will be migrated off the vehicle, to attempt to save
			// the progress that has been made. Depending on how much
			// progress has been made, more buffer time will be added to try and
			// ensure that large amount of progress was not lost
			if(timeLeftOnHighway(Globals.TimeUnit.SECONDS) <= vmTransferTime+buffer && inRangeOfAP())
			{
				transferVM();
			}
			
			
		}
		else if(vm!=null && vmCompletionPercentage>=100 && !transferring)
			transferVM();
	}
	
	/**
	 * Begins the process of transferring the VM back to
	 * the controller via an AP.
	 * NOTE: If the vehicle has not secured a communication slot
	 * with the AP it will not be able to transmit, and must wait
	 * until it has successfully secured a communication 
	 */
	private void transferVM()
	{
		tryingToTransfer = true;
		if(successfulCommsWithAP)
		{
			Network.addNewPendingTransfer(new Transfers(vm, this, false));
			transferring = true;
			tryingToTransfer = false;
//			System.out.println("Transferring VM: "+vm.getId()+", from Vehicle: "+ 
//			uuid +", has started");
		}
	}
	
}