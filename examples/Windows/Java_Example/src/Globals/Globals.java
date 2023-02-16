
public class Globals 
{
	// Assumptions:
	// Highway Assumptions
	final public static int MAX_SPEED = 70; // in MPH
	final public static int MIN_SPEED = 55; // in MPH
	final public static int NUM_INIT_VEHICLES = 500;
	// Length of I-95 in VA
	final public static int HIGHWAY_LEN = 179; // in Miles
	
	// Simulation Assumptions
	final public static int RUN_TIME = 5000; // in milliseconds
	final public static int NUM_INIT_VMS = 100;
	
	// Data Transfer Assumptions
	final public static double MAX_BANDWIDTH_SPEED = 27; // in Mbps
	final public static double MIN_BANDWIDTH_SPEED = 10; // in Mbps
	final public static double ASSUMED_BANDWITH = 13.5; // in Mbps
	final public static float AP_MAX_RANGE = 0.45f; // radius of the AP's range in miles
	// Used to ensure that the vehicle can actually have
	// time to do work on the vm rather than waste time 
	// transmitting it for no work to be done.
	// Also used as a buffer to attempt to mitigate failed transfers from
	// vehicles with slower upload/download speeds
	final public static int BUFFER_WORK_TIME = 360;//120; // time in seconds
	
	// Virtual Machine Assumptions
	final public static short MAX_VM_CORES = 6;
	final public static short MIN_VM_CORES = 1;
	final public static int MAX_VM_MEM = 8192; // in MB
	final public static int MIN_VM_MEM = 1024; // in MB
	
	// Since we are using MapReduce each individual VM
	// will be significantly smaller than a typical VM you 
	// run on a hypervisor
	final public static int MAX_VM_SIZE = 1024; // in MB
	final public static int MIN_VM_SIZE = 100; // in MB
	
	final public static double MAX_TASK_LEN = 3600; // time in seconds to complete
	final public static double MIN_TASK_LEN = 30; // time in seconds to complete	
	final public static float ADDITIONAL_CORES_SPEED_UP = 0.25f; // the percent performance boost additional cores will provide
	final public static float ADDITIONAL_MEM_SPEED_UP = 0.1f; // the percent performance boost additional memory will provide
	
	// AccessPoint Assumptions
	final public static int NUM_MINISLOTS = 5;

	/**
	 * Ways which time can be interpreted
	 */
	public enum TimeUnit
	{
		SECONDS,
		MINUTES,
		HOURS
	}
	
	/**
	 * Given the TimeUnit, t, it returns which multiple
	 * should be used to convert from hours
	 * @param t the TimeUnit
	 * @return the multiple for the given time unit
	 */
	public static int getTimeMultiple(TimeUnit t)
	{
		int multiple = 1;
		if(t==TimeUnit.SECONDS)
			multiple = 3600;
		else if(t==TimeUnit.MINUTES)
			multiple = 60;
		return multiple;
	}
	
	/**
	 * Takes in a size and speed, and computes 
	 * how long it will take to transfer in seconds
	 * @param size the size in MB
	 * @param transferSpeed the transfer speed in Mbps
	 * @return the time, in seconds, to complete the transfer
	 */
	public static double calculateTransferRateTime(double size, double transferSpeed)
	{
		double time = Math.max(size, 0)/(transferSpeed/8.0);
		return time;
	}
	
}
