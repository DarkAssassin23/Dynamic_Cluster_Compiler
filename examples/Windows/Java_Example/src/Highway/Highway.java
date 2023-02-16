import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Highway 
{

	private static HashMap<UUID, Vehicle> vehicles = new HashMap<UUID, Vehicle>();

	/**
	 * @param vehicles
	 */
	public Highway(HashMap<UUID, Vehicle> vehicles) 
	{
		Highway.vehicles = vehicles;
	}
	
	/**
	 * Default constructor
	 */
	public Highway() {}
	
	/**
	 * @return the vehicles
	 */
	public static HashMap<UUID, Vehicle> getVehicles() 
	{
		return vehicles;
	}

	/**
	 * @param vehicles the vehicles to set
	 */
	public void setVehicles(HashMap<UUID, Vehicle> vehicles) 
	{
		Highway.vehicles = vehicles;
	}
	
	/**
	 * Adds a new vehicle to the highway
	 * @param v the vehicle to add
	 */
	public static void addVehicle(Vehicle v) 
	{
		vehicles.put(v.getUuid(), v);
	}
	
	/**
	 * Removes a vehicle from the highway
	 * @param v the vehicle to remove
	 */
	public static void removeVehicle(Vehicle v) 
	{
		if(v.getVM()!=null)
			v.getVM().setVehicleAssignedTo(null);
		vehicles.remove(v.getUuid());
	}
	
    /**
     * Prints out all vehicle travel info for the highway
     */
    public static void printVehiclesInfo()
    {
		for(Map.Entry<UUID, Vehicle> v : vehicles.entrySet())
		{
			v.getValue().printInfo();
		}
    }

	/**
	 * Updates the highway.
	 * Moving, and removing vehicles as needed
	 */
	public static void update(Controller c)
	{
		ArrayList<Vehicle> toRemove = new ArrayList<Vehicle>();
		for(HashMap.Entry<UUID, Vehicle> v : vehicles.entrySet())
		{
			// If the moving vehicle is no longer on the highway
			// queue it up for removal
			v.getValue().move(Globals.TimeUnit.SECONDS);
			if(!v.getValue().isOnHighway())
				toRemove.add(v.getValue());
		}
		for(Vehicle v : toRemove)
		{
			removeVehicle(v);
			//System.out.println(v.getUuid().toString()+" got off the highway");
		}
		
    	c.incrementVMsTimeTaken();
    	c.updateVehiclesInAPCoverage();
    	Network.performTransfers();
    	c.archiveCompletedJobs();
	}
}
