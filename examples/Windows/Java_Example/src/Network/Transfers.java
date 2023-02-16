

public class Transfers 
{
	private VirtualMachine vm;
	private Vehicle vehicle;
	private boolean toVehicle;
	private double timeLeftOnTransfer; // in seconds
	private double amountTransferred;
	
	/**
	 * Constructor to create vm's being transfered
	 * @param vmID id of the VM being transfered
	 * @param vehicleID id of the vehicle the vm is going to/coming from
	 * @param toVehicle whether or not the vm is going to or from the vehicle
	 */
	public Transfers(VirtualMachine vm, Vehicle vehicle, boolean toVehicle) 
	{
		this.vm = vm;
		this.vehicle = vehicle;
		this.toVehicle = toVehicle;
		amountTransferred = 0;
		if(toVehicle)
		{
			timeLeftOnTransfer = Globals.calculateTransferRateTime(vm.getSize(), vehicle.getDownloadSpeed());
			this.vehicle.setTransferring(true);
			this.vehicle.setTryingToTransfer(false);
		}
		else
		{
			timeLeftOnTransfer = Globals.calculateTransferRateTime(vm.getSize(), vehicle.getUploadSpeed());
			this.vehicle.setTransferring(true);
			this.vehicle.setTryingToTransfer(false);
		}
		//System.out.println("Time to transfer: "+timeLeftOnTransfer);
	}
	/**
	 * @return the vm
	 */
	public VirtualMachine getVM() 
	{
		return vm;
	}
	/**
	 * @param vm the vm to set
	 */
	public void setVM(VirtualMachine vm) 
	{
		this.vm = vm;
	}
	/**
	 * @return the vehicle
	 */
	public Vehicle getVehicle() 
	{
		return vehicle;
	}
	/**
	 * @param vehicle the vehicle to set
	 */
	public void setVehicleID(Vehicle vehicle) 
	{
		this.vehicle = vehicle;
	}
	/**
	 * @return if the vm is being sent to or from the vehicle
	 */
	public boolean isToVehicle() 
	{
		return toVehicle;
	}
	/**
	 * @param toVehicle if the vm is being sent to or from a vehicle
	 */
	public void setToVehicle(boolean toVehicle) 
	{
		this.toVehicle = toVehicle;
	}
	/**
	 * @return the amountTransferred
	 */
	public double getAmountTransferred() 
	{
		return amountTransferred;
	}
	/**
	 * @param amountTransferred the amountTransferred to set
	 */
	public void setAmountTransferred(double amountTransferred) 
	{
		this.amountTransferred = amountTransferred;
	}
	/**
	 * @return the timeLeftOnTransfer
	 */
	public double getTimeLeftOnTransfer() 
	{
		return timeLeftOnTransfer;
	}
	/**
	 * Reduce the time left on the transfer
	 * based on the current uplaod/download speed
	 * and how much data is left to transfer
	 */
	public void decrementTimeLeftOnTransfer() 
	{
		if(vehicle.isSuccessfulCommsWithAP())
		{
			if(toVehicle)
			{
				timeLeftOnTransfer = Globals.calculateTransferRateTime((vm.getSize()-amountTransferred), vehicle.getDownloadSpeed());
				setAmountTransferred((amountTransferred+(vehicle.getDownloadSpeed()/8)));
			}
			else
			{
				timeLeftOnTransfer = Globals.calculateTransferRateTime((vm.getSize()-amountTransferred), vehicle.getUploadSpeed());
				setAmountTransferred((amountTransferred+(vehicle.getUploadSpeed()/8)));
			}
		}
	}
}
