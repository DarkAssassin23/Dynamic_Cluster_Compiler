import java.lang.Math;
import java.util.HashMap;
import java.util.UUID;
import java.math.RoundingMode;  
import java.text.DecimalFormat;

public class Main 
{
    public static void main(String[] args) 
    {
    	DecimalFormat df = new DecimalFormat("0.00");
    	df.setRoundingMode(RoundingMode.UP);
    	
    	final long startTime = System.currentTimeMillis();
  
    	//setLenRun(startTime);
    	setLenRunNoUnfinished(startTime);
        
        final long endTime = System.currentTimeMillis();
        double timeTaken = (endTime-startTime)/1000.0; // convert milliseconds to seconds
        System.out.println("Execution time: "+df.format(timeTaken)+"sec");
    }
    
    /**
     * Run for the length of time set by Globals.RUN_TIME.
     * Then wait until all VMs have been
     * completed before exiting
     * @param startTime the start time of the simulation run
     */
    public static void setLenRunNoUnfinished(long startTime)
    {
    	Controller c = new Controller();
    	for(int x = 1; x <= Globals.NUM_INIT_VMS; x++)
    	{
    		c.addVM(new VirtualMachine());
    	}
    	for(int x = 0; x < Globals.NUM_INIT_VEHICLES; x++)
    	{
    		Highway.addVehicle(new Vehicle());
    	}
    	System.out.println("Simulation "+Statistics.runID+" Started...");
    	while((System.currentTimeMillis()-startTime)<Globals.RUN_TIME)
    	{
	    	for(HashMap.Entry<UUID, Vehicle> v : Highway.getVehicles().entrySet())
			{
		    	if(c.availableToWork(v.getValue()))
		    		c.assignWork(v.getValue());
			}
	    	Highway.update(c);

	    	int rand = (int)(Math.random()*100)+1;
	    	if(rand%10==0)
	    	{
	    		Vehicle temp = new Vehicle();
	    		Highway.addVehicle(temp);
	    		c.updateVehiclesInAPCoverage();
	    		c.assignWork(temp);
	    	}
	    	else if(rand % 5==0)
	    	{
	    		c.addVM(new VirtualMachine());
	    	}
    	}
    	// The run is over, just wait until all jobs are done
    	while(!c.jobsCompleted())
    	{
	    	for(HashMap.Entry<UUID, Vehicle> v : Highway.getVehicles().entrySet())
			{
		    	if(c.availableToWork(v.getValue()))
		    		c.assignWork(v.getValue());
			}
	    	Highway.update(c);

	    	int rand = (int)(Math.random()*100)+1;
	    	if(rand%10==0)
	    	{
	    		Vehicle temp = new Vehicle();
	    		Highway.addVehicle(temp);
	    		c.updateVehiclesInAPCoverage();
	    		c.assignWork(temp);

	    	}
    	}
    	// print out a summary of the run
    	Statistics.printSimulationSummary(c);
    }
    
    /**
     * Run for the length of time set by Globals.RUN_TIME.
     * Then, once that time is up, wait till all pending transfers
     * have completed, and the manually transfer back all VMs currently
     * being worked on, regardless of completion
     * @param startTime the start time of the simulation run
     */
    public static void setLenRun(long startTime)
    {
    	Controller c = new Controller();
    	for(int x = 1; x <= Globals.NUM_INIT_VMS; x++)
    	{
    		c.addVM(new VirtualMachine());
    	}
    	for(int x = 0; x < Globals.NUM_INIT_VEHICLES; x++)
    	{
    		Highway.addVehicle(new Vehicle());
    	}
    	System.out.println("Simulation "+Statistics.runID+" Started...");
    	while((System.currentTimeMillis()-startTime)<Globals.RUN_TIME)
    	{
	    	for(HashMap.Entry<UUID, Vehicle> v : Highway.getVehicles().entrySet())
			{
		    	if(c.availableToWork(v.getValue()))
		    		c.assignWork(v.getValue());
			}
	    	Highway.update(c);

	    	int rand = (int)(Math.random()*100)+1;
	    	if(rand==10)
	    	{
	    		Vehicle temp = new Vehicle();
	    		Highway.addVehicle(temp);
	    		c.updateVehiclesInAPCoverage();
	    		c.assignWork(temp);
	    	}
	    	else if(rand==20)
	    	{
	    		c.addVM(new VirtualMachine());
	    		Vehicle temp = new Vehicle();
	    		Highway.addVehicle(temp);
	    		c.updateVehiclesInAPCoverage();
	    		c.assignWork(temp);
	    	}
    	}
    	// The run is over, any VM's still being worked on
    	// will be migrated back
    	
    	// First complete any pending transfers
    	while(Network.getTransferList().size()>0)
    	{
	    	Highway.update(c);
    	}
    	
    	// Now that all pending transfers are complete
    	// we can migrate all the vm's back
    	for(HashMap.Entry<UUID, Vehicle> v : Highway.getVehicles().entrySet())
		{
	    	if(v.getValue().getVM()!=null) 
	    		Network.addNewPendingTransfer(new Transfers(v.getValue().getVM(), v.getValue(), false));
		}
    	while(Network.getTransferList().size()>0)
    	{
	    	Highway.update(c);
    	}
    	
    	Statistics.printSimulationSummary(c);
    }
}