import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AccessPoint 
{
	private HashMap<UUID, Vehicle> vehiclesInRange = new HashMap<UUID, Vehicle>();
	private int mileMarker;
	
	public AccessPoint(int mileMarker)
	{
		this.mileMarker = mileMarker;
	}

	/**
	 * @return the vehiclesInRange of the AP
	 */
	public HashMap<UUID, Vehicle> getVehiclesInRange() 
	{
		return vehiclesInRange;
	}

	/**
	 * @param vehiclesInRange the vehiclesInRange to set
	 */
	public void setVehiclesInRange(HashMap<UUID, Vehicle> vehiclesInRange) 
	{
		this.vehiclesInRange = vehiclesInRange;
	}
	
	/**
	 * @return the mileMarker the AP is set at
	 */
	public int getMileMarker() 
	{
		return mileMarker;
	}

	/**
	 * @param mileMarker the mileMarker to set the AP at
	 */
	public void setMileMarker(int mileMarker) 
	{
		this.mileMarker = mileMarker;
	}

	/**
	 * Adds a new vehicle into its range if it does not already exist
	 * @param v the new vehicle to add
	 */
	public void newVehicleInRange(Vehicle v)
	{
		if(!vehiclesInRange.containsKey(v.getUuid()))
			vehiclesInRange.put(v.getUuid(), v);
	}
	
	public void removeOutOfRangeVehicles()
	{
		ArrayList<Vehicle> toRemove = new ArrayList<Vehicle>();
		for(HashMap.Entry<UUID, Vehicle> v : vehiclesInRange.entrySet())
		{
			// The vehicle got off the highway
			if(!Highway.getVehicles().containsKey(v.getValue().getUuid()))
				toRemove.add(v.getValue());
			
			// The vehicle moved out of the range of the AP
			if(!v.getValue().inRangeOfAP())
				toRemove.add(v.getValue());
		}
		
		for(Vehicle v : toRemove)
		{
			v.setSuccessfulCommsWithAP(false);
			vehiclesInRange.remove(v.getUuid());
		}
	}
	
	/**
	 * Sets whether or not each vehicle in the coverage area of the AP
	 * has successfully established comms with it.
	 */
	public void hasValidComms()
	{

		ArrayList<Vehicle> vehiclesTryingToCommunicate = new ArrayList<Vehicle>();
		ArrayList<Vehicle> vehiclesWithComms = new ArrayList<Vehicle>();
		int numTransferring = 0;
		// Determine which vehicles need to try to establish comms
		for(HashMap.Entry<UUID, Vehicle> v : vehiclesInRange.entrySet())
		{
			// if the vehicle is trying to start a transfer or has a transfer in progress
			// and has not currently established comms with the AP, it get added
			// to the vehicles that will attempt to secure a communication slot 
			if((v.getValue().isTryingToTransfer() || v.getValue().isTransferring()) &&!v.getValue().isSuccessfulCommsWithAP())
				vehiclesTryingToCommunicate.add(v.getValue());
			if(v.getValue().isSuccessfulCommsWithAP())
			{
				vehiclesWithComms.add(v.getValue());
				if(v.getValue().isTransferring())
					numTransferring++;
			}
		}
		int periods = 2;
		UUID[][] contentionPeriods = new UUID[Globals.NUM_MINISLOTS][periods];
		UUID collision = UUID.fromString("00000000-0000-0000-0000-000000000000");
		for(Vehicle v : vehiclesTryingToCommunicate)
		{
			for(int period=0;period<periods;period++)
			{
				int rand = (int)(Math.random()*Globals.NUM_MINISLOTS);
				// if no vehicle has claimed the slot, the slot goes to the vehicle
				if(contentionPeriods[rand][period]==null)
					contentionPeriods[rand][period] = v.getUuid();
				// one or more vehicles have picked this slot so there is a collision
				else
					contentionPeriods[rand][period] = collision;
			}
		}
		// Add all vehicles who sent at least one ungarbled message
		for(int minislots=0;minislots<Globals.NUM_MINISLOTS;minislots++)
		{
			for(int period=0;period<periods;period++)
			{
				if(contentionPeriods[minislots][period]!=null && contentionPeriods[minislots][period]!=collision)
				{
					if(!vehiclesWithComms.contains(vehiclesInRange.get(contentionPeriods[minislots][period])))
					{
						vehiclesWithComms.add(vehiclesInRange.get(contentionPeriods[minislots][period]));
						if(vehiclesInRange.get(contentionPeriods[minislots][period]).isTransferring())
							numTransferring++;
					}
				}
			}
		}
		if(numTransferring==0)
			numTransferring = 1;
		// Set upload and download speed of vehicles and set their
		// status of successfulCommsWithAP to true
		for(Vehicle v : vehiclesWithComms)
		{
			v.setUploadSpeed(Globals.MAX_BANDWIDTH_SPEED/numTransferring);
			v.setDownloadSpeed(Globals.MAX_BANDWIDTH_SPEED/numTransferring);
			v.setSuccessfulCommsWithAP(true);
			
		}
	}
}
