import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

public class Statistics 
{
	private static int unfinishedVMs = 0;
	private static double averageCompletionTime = 0;
	private static double averageTaskLength = 0;
	private static int totalSuccessfulMigrations = 0;
	private static int totalFailedMigrations = 0;
	private static int totalVehiclesInvolved = 0;
	private static double averageSuccessfulMigrations = 0;
	private static double averageFailedMigrations = 0;
	private static String maxSuccessfulMigrationsVM = "";
	private static String maxFailedMigrationsVM = "";
	private static int totalCoresUsed = 0;
	private static double averageCoresUsed = 0;
	private static int totalMemoryUsed = 0;
	private static double averageMemoryUsed = 0;
	private static String mostVehicleContributions = "x 0";
	final public static String runID = UUID.randomUUID().toString().substring(0, 8);
	

	/**
	 * Prints a high level over view of the run
	 * and generates a log file
	 * @param c the controller to pull data from
	 */
	public static void printSimulationSummary(Controller c)
	{
		setOverallStats(c);
		System.out.println(summaryOverview(c));
    	String logFile = "Simulation_Run_"+runID+"_Results.log";
    	logResults(logFile, c);
    	System.out.println("More information about the run can be viewed in the generated log file \'log/"+logFile+"\'");
	}
	
	/**
	 * Gets an high level summary of the simulation
	 * @param c the Controller to pull data from
	 * @return string which contains a high level summary of the sim
	 */
	private static String summaryOverview(Controller c)
	{
		String data = "Simulation Summary:\n";
		data += "- Total number of VM's: "+(c.getVmList().size()+c.getFinishedJobs().size())+"\n";
		data += "- Total number of completed jobs: "+c.getFinishedJobs().size()+"\n";
    	data += "- Total number of VM's started, but not yet finished: "+getUnfinishedVMs()+"\n";
    	data += "- Total number of VM's not yet started or assigned: "+(c.getVmList().size()-getUnfinishedVMs())+"\n";
    	data += "- Total number of Vehicles involved in working on jobs: "+totalVehiclesInvolved+"\n";
    	data += "- Total number of Successful VM Migrations: "+getTotalSuccessfulMigrations()+"\n";
    	data += "- Total number of Failed VM Migrations: "+getTotalFailedMigrations()+"\n";
    	data += "- Total number of CPU cores used: "+getTotalCoresUsed()+"\n";
    	data += "- Total amount of Memory (RAM) used: "+((getTotalMemoryUsed()/1024.0)/1024.0)+" TB\n";
    	
    	data += "\n";
    	
    	data += "- Average VM task length: "+getAverageTaskLength()+" sec\n";
    	data += "- Average time to complete a job: "+getAverageCompletionTime()+" sec\n";
    	data += "- Average number of Successful VM Migrations per VM: "+getaverageSuccessfulMigrations()+"\n";
    	data += "- Average number of Failed VM Migrations per VM: "+getAverageFailedMigrations()+"\n";
    	data += "- Average number of CPU cores used per VM: "+getAverageCoresUsed()+"\n";
    	data += "- Average amount of Memory (RAM) used per VM: "+(getAverageMemoryUsed()/1024)+" GB\n";
    	
    	data += "\n";
    	
    	String[] maxMigrations = getMaxSuccessfulMigrationsVM().split(" ");
    	String[] maxFailedMigrations = getMaxFailedMigrationsVM().split(" ");
    	String[] vehicleAndVMs = getMostVehicleContributions().split(" ");
    	if(!maxMigrations[0].equals(null))
    		data += "- VM "+maxMigrations[0]+" had the most successful VM migrations with "+maxMigrations[1]+"\n";
    	if(!maxFailedMigrations[1].equals("0"))
    	{
    			data += "- VM "+maxFailedMigrations[0]+" had the most failed VM migrations, "+
    	"due to vehicles leaving the highway before migrating their VM, with "+maxFailedMigrations[1]+"\n";
    	}
    	data += "- Vehicle "+vehicleAndVMs[0]+" worked on the most VM's working on "+vehicleAndVMs[1]+" different VM's\n";
    	
    	return data;
	}
	
	
	/**
	 * Generate a log file of the data from the run
	 * @param filename the name of the log file
	 * @param c the controller to pull data from
	 */
	private static void logResults(String filename, Controller c)
	{	
		System.out.println("Generating log file...");
		String data = summaryOverview(c);
		data += "Summary of Completed Jobs:\n";
		for(HashMap.Entry<UUID, VirtualMachine> vm : c.getFinishedJobs().entrySet())
		{
			data += "\t VM ID: "+vm.getValue().getId()+"\n";
			data += "\t\t- Job completed in: "+vm.getValue().getTimeTaken()+"\n";
			data += "\t\t- Estimated task length (excluding transfer overhead) was "+
					vm.getValue().getTaskLength()+" sec\n";
			data += "\t\t- Job was migrated successfully "+vm.getValue().getTimesMigrated()+" time(s)\n";
			data += "\t\t- Job was lost "+vm.getValue().getTimesProgressWasLost()+
					" time(s) due to the vehicle leaving the highway before migrating the VM\n";
		}
		
		data += "Summary of Unfinished Jobs:\n";
		for(HashMap.Entry<UUID, VirtualMachine> vm : c.getVmList().entrySet())
		{
			data += "\t VM ID: "+vm.getValue().getId()+"\n";
			data += "\t\t- Job was "+vm.getValue().getPercentComplete()+"% complete\n";
			data += "\t\t- Time spent working on job: "+vm.getValue().getTimeTaken()+" sec\n";
			data += "\t\t- Estimated task length (excluding transfer overhead) was "+
					vm.getValue().getTaskLength()+" sec\n";
			if(vm.getValue().getTimeTaken()==0)
				data += "\t\t- Job was never started or assigned\n";
			else 
			{
				data += "\t\t- Job was migrated successfully "+vm.getValue().getTimesMigrated()+" time(s)\n";
				data += "\t\t- Job was lost "+vm.getValue().getTimesProgressWasLost()+
						" time(s) due to the vehicle leaving the highway before migrating the VM\n";
			}
		}
		
		try 
		{
			// Check if the log directory exists, if not, make it
			if (!Files.isDirectory(Paths.get("log/"))) 
			{
			    new File("log/").mkdir();
			}
			Files.write(Paths.get("log/"+filename), data.getBytes());
			System.out.println("Log file generation successful, and can be found at \'log/"+filename+"\'");
		} catch (IOException e) 
		{
			System.out.println("An error occured writting the log file: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void setOverallStats(Controller c)
	{
		for(HashMap.Entry<UUID, VirtualMachine> vm : c.getVmList().entrySet())
		{
			if(vm.getValue().getTimeTaken()>0)
				unfinishedVMs++;
		}
		
		double numFinishedJobs = c.getFinishedJobs().size();
		int maxMigrations = 0;
		UUID maxMigrationsVMID = null;
		int maxFailedMigrations = 0;
		UUID maxFailedMigrationsVMID = null;
		for(HashMap.Entry<UUID, VirtualMachine> vm : c.getFinishedJobs().entrySet())
		{
			averageTaskLength += vm.getValue().getTaskLength();
			averageCompletionTime += vm.getValue().getTimeTaken();
			totalFailedMigrations += vm.getValue().getTimesProgressWasLost();
			totalSuccessfulMigrations += vm.getValue().getTimesMigrated();
			if(maxMigrations<vm.getValue().getTimesMigrated())
			{
				maxMigrations = vm.getValue().getTimesMigrated();
				maxMigrationsVMID = vm.getValue().getId();
			}
			if(maxFailedMigrations<vm.getValue().getTimesProgressWasLost())
			{
				maxFailedMigrations = vm.getValue().getTimesProgressWasLost();
				maxFailedMigrationsVMID = vm.getValue().getId();
			}
		}
		setAverageCompletionTime(averageCompletionTime/numFinishedJobs);
		setAverageTaskLength(averageTaskLength/numFinishedJobs);
		setAverageFailedMigrations(totalFailedMigrations/numFinishedJobs);
		setAverageSuccessfulMigrations(totalSuccessfulMigrations/numFinishedJobs);
		setAverageCoresUsed(totalCoresUsed/numFinishedJobs);
		setAverageMemoryUsed(totalMemoryUsed/numFinishedJobs);
		setMaxSuccessfulMigrationsVM(maxMigrationsVMID+" "+maxMigrations);
		setMaxFailedMigrationsVM(maxFailedMigrationsVMID+" "+maxFailedMigrations);
	}

	/**
	 * @return the number of unfinished VMs
	 */
	public static int getUnfinishedVMs() 
	{
		return unfinishedVMs;
	}

	/**
	 * Set the number of unfinished VMs
	 * @param unfinishedVMs the number of unfinished VMs
	 */
	public static void setUnfinishedVMs(int unfinishedVMs) 
	{
		Statistics.unfinishedVMs = unfinishedVMs;
	}
	
	/**
	 * @return the averageCompletionTime
	 */
	public static double getAverageCompletionTime() 
	{
		return averageCompletionTime;
	}

	/**
	 * @param averageCompletionTime the averageCompletionTime to set
	 */
	public static void setAverageCompletionTime(double averageCompletionTime) 
	{
		Statistics.averageCompletionTime = averageCompletionTime;
	}

	/**
	 * @return the averageTaskLength
	 */
	public static double getAverageTaskLength() 
	{
		return averageTaskLength;
	}

	/**
	 * @param averageTaskLength the average Task Length to set
	 */
	public static void setAverageTaskLength(double averageTaskLength) 
	{
		Statistics.averageTaskLength = averageTaskLength;
	}

	/**
	 * @return the totalSuccessfulMigrations
	 */
	public static int getTotalSuccessfulMigrations() 
	{
		return totalSuccessfulMigrations;
	}

	/**
	 * @param totalSuccessfulMigrations the totalSuccessfulMigrations to set
	 */
	public static void setTotalSuccessfulMigrations(int totalSuccessfulMigrations) 
	{
		Statistics.totalSuccessfulMigrations = totalSuccessfulMigrations;
	}

	/**
	 * @return the totalFailedMigrations
	 */
	public static int getTotalFailedMigrations() 
	{
		return totalFailedMigrations;
	}

	/**
	 * @param totalFailedMigrations the totalFailedMigrations to set
	 */
	public static void setTotalFailedMigrations(int totalFailedMigrations) 
	{
		Statistics.totalFailedMigrations = totalFailedMigrations;
	}

	/**
	 * @return the totalVehiclesInvolved
	 */
	public static int getTotalVehiclesInvolved() 
	{
		return totalVehiclesInvolved;
	}

	/**
	 * Increment the total number of vehicles contributing by one
	 */
	public static void incrementTotalVehiclesInvolved() 
	{
		totalVehiclesInvolved++;
	}

	/**
	 * @return the averageSuccessfulMigrations
	 */
	public static double getaverageSuccessfulMigrations() 
	{
		return averageSuccessfulMigrations;
	}

	/**
	 * @param averageSuccessfulMigrations the averageSuccessfulMigrations to set
	 */
	public static void setAverageSuccessfulMigrations(double averageSuccessfulMigrations) 
	{
		Statistics.averageSuccessfulMigrations = averageSuccessfulMigrations;
	}

	/**
	 * @return the averageFailedMigrations
	 */
	public static double getAverageFailedMigrations() 
	{
		return averageFailedMigrations;
	}

	/**
	 * @param averageFailedMigrations the averageFailedMigrations to set
	 */
	public static void setAverageFailedMigrations(double averageFailedMigrations) 
	{
		Statistics.averageFailedMigrations = averageFailedMigrations;
	}

	/**
	 * @return the maxSuccessfulMigrationsVM
	 */
	public static String getMaxSuccessfulMigrationsVM() 
	{
		return maxSuccessfulMigrationsVM;
	}

	/**
	 * @param maxSuccessfulMigrationsVM the maxSuccessfulMigrationsVM to set
	 */
	public static void setMaxSuccessfulMigrationsVM(String maxSuccessfulMigrationsVM) 
	{
		Statistics.maxSuccessfulMigrationsVM = maxSuccessfulMigrationsVM;
	}

	/**
	 * @return the maxFailedMigrationsVM
	 */
	public static String getMaxFailedMigrationsVM() 
	{
		return maxFailedMigrationsVM;
	}

	/**
	 * @param maxFailedMigrationsVM the maxFailedMigrationsVM to set
	 */
	public static void setMaxFailedMigrationsVM(String maxFailedMigrationsVM) 
	{
		Statistics.maxFailedMigrationsVM = maxFailedMigrationsVM;
	}

	/**
	 * @return the mostVehicleContributions
	 */
	public static String getMostVehicleContributions() 
	{
		return mostVehicleContributions;
	}

	/**
	 * Updates the vehicle with the most amount of VM's its worked on
	 * if it is greater than the current vehicle
	 * @param vehicleID the ID the vehicle
	 * @param vmsWorkedOn the number of VM's that vehicle has worked on
	 */
	public static void setMostVehicleContributions(UUID vehicleID, int vmsWorkedOn) 
	{
		String[] vehicleAndVMs =  mostVehicleContributions.split(" ");
		if(vmsWorkedOn>Integer.parseInt(vehicleAndVMs[1]))
		{
			mostVehicleContributions = vehicleID+" "+vmsWorkedOn;
		}
	}

	/**
	 * @return the totalCoresUsed
	 */
	public static int getTotalCoresUsed() 
	{
		return totalCoresUsed;
	}

	/**
	 * @param totalCoresUsed the totalCoresUsed to set
	 */
	public static void setTotalCoresUsed(int totalCoresUsed) 
	{
		Statistics.totalCoresUsed = totalCoresUsed;
	}

	/**
	 * @return the totalMemoryUsed
	 */
	public static int getTotalMemoryUsed() 
	{
		return totalMemoryUsed;
	}

	/**
	 * @param totalMemoryUsed the totalMemoryUsed to set
	 */
	public static void setTotalMemoryUsed(int totalMemoryUsed) 
	{
		Statistics.totalMemoryUsed = totalMemoryUsed;
	}

	/**
	 * @return the averageCoresUsed
	 */
	public static double getAverageCoresUsed() 
	{
		return averageCoresUsed;
	}

	/**
	 * @param averageCoresUsed the average number of cores used per VM
	 */
	public static void setAverageCoresUsed(double averageCoresUsed) 
	{
		Statistics.averageCoresUsed = averageCoresUsed;
	}

	/**
	 * @return the averageMemoryUsed
	 */
	public static double getAverageMemoryUsed() 
	{
		return averageMemoryUsed;
	}

	/**
	 * @param averageMemoryUsed the average amount of memory used per VM
	 */
	public static void setAverageMemoryUsed(double averageMemoryUsed) 
	{
		Statistics.averageMemoryUsed = averageMemoryUsed;
	}

}
