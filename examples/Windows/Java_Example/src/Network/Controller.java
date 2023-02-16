import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Controller
{
    private HashMap<UUID, VirtualMachine> vmList = new HashMap<UUID, VirtualMachine>();
    private HashMap<UUID, VirtualMachine> finishedJobs = new HashMap<UUID, VirtualMachine>();
    private ArrayList<AccessPoint> APs = new ArrayList<AccessPoint>();
    
    /**
     * Constructor for the controller
     * @param vmList the vmList to set
     */
    public Controller(HashMap<UUID, VirtualMachine> vmList)
    {
    	this.setVmList(vmList);
    	setAPs();
    }
    
    /**
     * Default constructor
     */
    public Controller() 
    {
    	setAPs();
    }

    /**
     * @return the list of vm's
     */
	public HashMap<UUID, VirtualMachine> getVmList() 
	{
		return vmList;
	}

	/**
	 * Sets the vmList with the passed in list
	 * @param vmList the list of vm's to be set
	 */
	public void setVmList(HashMap<UUID, VirtualMachine> vmList) 
	{
		this.vmList = vmList;
	}
	
	/**
	 * @return the APs
	 */
	public ArrayList<AccessPoint> getAPs() 
	{
		return APs;
	}

	/**
	 * Creates as many access points as mile markers
	 * i.e. as long as the highway is
	 */
	public void setAPs() 
	{
    	for(int x=0;x<Globals.HIGHWAY_LEN+1;x++)
    	{
    		APs.add(new AccessPoint(x));
    	}
	}

	/**
	 * @return the finishedJobs
	 */
	public HashMap<UUID, VirtualMachine> getFinishedJobs() 
	{
		return finishedJobs;
	}

	/**
	 * @param finishedJobs the finishedJobs to set
	 */
	private void addFinishedJob(VirtualMachine vm) 
	{
		finishedJobs.put(vm.getId(), vm);
	}

	/**
	 * Adds a VM to the list
	 * @param vm the vm to add to the list
	 */
	public void addVM(VirtualMachine vm)
	{
		vmList.put(vm.getId(), vm);
	}
	
	public void archiveCompletedJobs()
	{
		ArrayList<UUID> finishedVMIDs = new ArrayList<UUID>();
		for(HashMap.Entry<UUID, VirtualMachine> vm : vmList.entrySet())
		{
			if(vm.getValue().getPercentComplete()>=100)
			{
				finishedVMIDs.add(vm.getValue().getId());
				addFinishedJob(vm.getValue());
			}
		}
		for(UUID id : finishedVMIDs)
		{
			vmList.remove(id);
		}
	}
	

	/**
	 * @return if all the jobs are completed
	 */
	public boolean jobsCompleted() 
	{
		for(HashMap.Entry<UUID, VirtualMachine> vm : vmList.entrySet())
		{
			if(vm.getValue().getPercentComplete()<100)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Updates the vehicles in each AP's range
	 */
	public void updateVehiclesInAPCoverage()
	{
		// Add new vehicles just coming in range
		for(HashMap.Entry<UUID, Vehicle> v : Highway.getVehicles().entrySet())
		{
			int mileMarker = (int) Math.round(v.getValue().getMileMarker());
			//System.out.println("Num APs: "+APs.size()+", car mile marker: "+v.getValue().getMileMarker()+", rounded mile marker: "+mileMarker+"");
			if(v.getValue().inRangeOfAP())
				APs.get(mileMarker).newVehicleInRange(v.getValue());
		}
		
		// Remove vehicles that are now out of range and update the vehicles with comms
		for(AccessPoint ap : APs)
		{
			ap.removeOutOfRangeVehicles();
			ap.hasValidComms();
		}
		
	}
	
	/**
	 * Goes though each vm, and if work has been started,
	 * but not yet completed, increments the time taken counter 
	 */
	public void incrementVMsTimeTaken()
	{
		for(HashMap.Entry<UUID, VirtualMachine> vm : vmList.entrySet())
		{
			if(vm.getValue().getPercentComplete()<100 && vm.getValue().getTimesMigrated()>=-1)
				vm.getValue().incrementTimeTaken();
		}
	}
	
	/**
	 * Estimates the time a vehicle will be on the highway for
	 * using how much distance it still has to travel, divided by the
	 * average speed on the highway 
	 * @param t The unit of time to be returned
	 * @param v The vehicle to estimate its time left on the highway
	 * @return the time the vehicle has left on the highway
	 */
	public double estimateTimeLeftOnHighway(Globals.TimeUnit t, Vehicle v)
	{
		//double avgSpeed = ((Globals.MAX_SPEED+Globals.MIN_SPEED)/2);
		//double avgSpeed = v.getSpeed();
		
		double avgSpeed = 0;
		for(HashMap.Entry<UUID, Vehicle> car : Highway.getVehicles().entrySet())
		{
			avgSpeed += car.getValue().getSpeed();
		}
		avgSpeed /= Highway.getVehicles().size();
		
		return v.getDistanceToTravel()/(avgSpeed/Globals.getTimeMultiple(t));
	}
	
	/**
	 * If there are any virtual machines the vehicle
	 * meets the requirements to work on, that vm will be
	 * assigned to that vehicle
	 * @param v the vehicle to assign work to
	 */
	public void assignWork(Vehicle v)
	{
		for(HashMap.Entry<UUID, VirtualMachine> vm : vmList.entrySet())
		{
			
//			if(!Highway.getVehicles().containsKey(vm.getValue().getVehicleAssignedTo()))
//			{
//				vm.getValue().setVehicleAssignedTo(null);
//				vm.getValue().incrementTimesProgressWasLost();
//			}
			
			// Check to see if the has not already been completed or already assigned, 
			// and if the vehicle has the required specs to take on the job
			if(vm.getValue().getPercentComplete()<100 && vm.getValue().getVehicleAssignedTo()==null &&
					(v.getCores()>=vm.getValue().getRequiredNumCores()) && 
					(v.getMemory()>=vm.getValue().getRequiredMemory()))
			{
				double vmTransferTime = Globals.calculateTransferRateTime(vm.getValue().getSize(), Globals.ASSUMED_BANDWITH)*2;
				//System.out.println("Assumed Time for VM Transfer: "+vmTransferTime);
				//System.out.println(v.getUuid()+" time left on the highway: "+v.timeLeftOnHighway(Globals.TimeUnit.SECONDS));
				
				// Make sure the vehicle is on highway long enough
				// to do work on the given vm and it is in range of an AP
				//if(vmTransferTime+Globals.BUFFER_WORK_TIME<estimateTimeLeftOnHighway(Globals.TimeUnit.SECONDS,v) && v.inRangeOfAP())
				if(vmTransferTime+(vm.getValue().getTaskLength()*0.25)<estimateTimeLeftOnHighway(Globals.TimeUnit.SECONDS,v) && v.inRangeOfAP())
				{
					Network.addNewPendingTransfer(new Transfers(vm.getValue(), v, true));
					vm.getValue().setVehicleAssignedTo(v.getUuid());
					
					// Only want to keep track of successful migrations
					// so aside from the first time it was assigned (which 
					// starts the clock for how long it took to complete)
					// we only want to increment this if the migration to and
					// from the vehicle was a success
					if(vm.getValue().getTimesMigrated()==-2)
						vm.getValue().incrementTimesMigrated();
					
//					System.out.println("Transferring VM: "+vm.getValue().getId()+", to Vehicle: "+ 
//							v.getUuid()+", has started");
					break;
				}
			}
		}
	}
	
	/**
	 * Checks if the given vehicle is able to work.
	 * Checks to see if the vehicle currently has a VM
	 * or is in the process of transferring one. If 
	 * neither are true, it is able to work
	 * @param v the vehicle to check work eligibility 
	 * @return if the vehicle can take on work
	 */
	public boolean availableToWork(Vehicle v)
	{
		if(v.getVM()!=null)
			return false; 
		
//		for(Transfers t : Network.getTransferList())
//		{
//			if(t.getVehicle()==v)
//				return false;
//		}
		
		return (!v.isTransferring() && v.isSuccessfulCommsWithAP());
	}
}