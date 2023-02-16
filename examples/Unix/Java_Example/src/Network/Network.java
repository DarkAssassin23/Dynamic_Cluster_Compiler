import java.util.ArrayList;

public class Network 
{
	private static ArrayList<Transfers> transfersList = new ArrayList<Transfers>();

	/**
	 * Default network constructor
	 */
	public Network() {}

	/**
	 * @return the transfers list
	 */
	public static ArrayList<Transfers> getTransferList()
	{
		return transfersList;
	}
	
	/**
	 * Adds a new transfer to the list
	 * @param t the Transfer to add
	 */
	public static void addNewPendingTransfer(Transfers t)
	{
		transfersList.add(t);
	}
	
	/**
	 * Transfers VMs to and from Vehicles, only updating
	 * VM completion percentage on a successful migration. 
	 */
	public static void performTransfers()
	{
		// Any transfer that was either completed or
		// had the vehicle leave the highway before completing the transfer,
		// will be removed
		ArrayList<Transfers> transfersToRemove = new ArrayList<Transfers>();
		for(Transfers t : transfersList)
		{
			if(t.getTimeLeftOnTransfer()<=0)
			{
				
				if(t.isToVehicle())
				{
					t.getVehicle().setVM(t.getVM());
					transfersToRemove.add(t);
//					System.out.println("Transferring VM: "+t.getVM().getId()+", to Vehicle: "+ 
//							t.getVechile().getUuid()+", has completed");
				}
				else
				{
					t.getVM().setPercentComplete(t.getVehicle().getVmCompletionPercentage());
					t.getVM().incrementTimesMigrated();
					t.getVehicle().setVM(null);
					t.getVM().setVehicleAssignedTo(null);
					transfersToRemove.add(t);
//					System.out.println("Transferring VM: "+t.getVM().getId()+", from Vehicle: "+ 
//							t.getVechile().getUuid()+", has completed");
//					if(t.getVM().getPercentComplete()>=100)
//						System.out.println("VM "+t.getVM().getId()+" finished");
				}
			}
			// if the vehicle is no longer on the highway, remove it
			else if(!Highway.getVehicles().containsKey(t.getVehicle().getUuid()))
			{
				t.getVM().setVehicleAssignedTo(null);
				t.getVM().incrementTimesProgressWasLost();
				transfersToRemove.add(t);
//				System.out.println("Transfer of VM "+t.getVM().getId()+" failed due to the Vehicle leaving the highway");
//				System.out.println("Vehicle "+t.getVehicle().getUuid()+" had "+
//						(t.getVehicle().getVmCompletionPercentage()-t.getVM().getPercentComplete())+
//						"% completed");
//				System.out.println("Vehicle completion %: "+t.getVehicle().getVmCompletionPercentage());
			}
			
//			System.out.println("VM "+t.getVM().getId()+" amount transfered: "+t.getAmountTransferred()+ 
//					" transfering at: "+t.getVehicle().getDownloadSpeed()+"Mbps, with "+t.getTimeLeftOnTransfer()+
//					" sec left on the transfer, vehicle on the highway for "+t.getVehicle().timeLeftOnHighway(Globals.TimeUnit.SECONDS)+
//					" more seconds");
			t.decrementTimeLeftOnTransfer();
			
		}
		// Remove the transfers that need to be removed
		for(Transfers t : transfersToRemove)
			transfersList.remove(t);
	}
	
}
