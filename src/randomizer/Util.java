package randomizer;
//For contstants/magic numbers/global vars/global functions
public class Util {

	
	
	
	
	
	
	
	

	public static int[] getEnergyFromFlag(byte fg,byte lw, byte fp, byte c_){
		int[] retVals = new int[7];
		retVals[0] = getEnergyFromUpperFlag(fg);
		retVals[1] = getEnergyFromLowerFlag(fg);
		retVals[2] = getEnergyFromUpperFlag(lw);
		retVals[3] = getEnergyFromLowerFlag(lw);
		retVals[4] = getEnergyFromUpperFlag(fp);
		retVals[5] = getEnergyFromLowerFlag(fp);
		retVals[6] = getEnergyFromUpperFlag(c_);
		
		return retVals;
	}
	public static int getEnergyFromUpperFlag(byte in){
		return (in & 0xF0)/16;//this converts in to an integer, then only takes the second nibble
		//divides by 16 because that's how they were stored
		
		
	}
	public static int getEnergyFromLowerFlag(byte in){
		return (in & 0x0F);
		
		
	}
	
	
	
	
	
	
}

