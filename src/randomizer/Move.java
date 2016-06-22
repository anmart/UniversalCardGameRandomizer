package randomizer;

public class Move {

	byte energy_fg,energy_lw,energy_fp,energy_c_;
	Word name;
	Word description;
	Word description_extended;
	byte damage;
	byte category;
	Word effectCommands;
	byte flag1,flag2,flag3;
	byte unknownByte1,unknownByte2;
	
	//variable that aren't related to data on disk
	int ownerStage = -1;


	public Move(){
		energy_fg = 0x00;
		energy_lw = 0x00;
		energy_fp = 0x00;
		energy_c_ = 0x00;
		name = new Word();
		description = new Word();
		description_extended = new Word();
		damage = 0x00;
		category = 0x00;
		effectCommands = new Word();
		flag1 = 0x00;
		flag2 = 0x00;
		flag3 = 0x00;
		unknownByte1 = 0x00;
		unknownByte2 = 0x00;
	}
	
	public Move(byte[] rom, int startIndex,int stage){
		
		int c = startIndex;
		
		//System.out.print(c);
		energy_fg = rom[c++];
		energy_lw = rom[c++];
		energy_fp = rom[c++];
		energy_c_ = rom[c++];
		name = new Word(rom,c); c+=2;
		description = new Word(rom,c); c+=2;
		description_extended = new Word(rom,c); c+=2;
		damage = rom[c++];
		category = rom[c++];
		//jSystem.out.println(", "+ c +", " + (0xFF & category) + ", " + (0xFF & name.high) + ", " + (0xFF & name.low) );
		effectCommands = new Word(rom,c); c+=2;
		flag1 = rom[c++];
		flag2 = rom[c++];
		flag3 = rom[c++];
		unknownByte1 = rom[c++];
		unknownByte2 = rom[c++];
		

		ownerStage = stage;
		
		
	}
	
	
	public void writeToRom(byte[] rom, int startIndex){
		
		int c = startIndex;
		
		rom[c++] = energy_fg;
		rom[c++] = energy_lw;
		rom[c++] = energy_fp;
		rom[c++] = energy_c_;
		name.writeToRom(rom,c);c+=2;
		description.writeToRom(rom,c);c+=2;
		description_extended.writeToRom(rom,c);c+=2;
		rom[c++] = damage;
		rom[c++] = category;
		effectCommands.writeToRom(rom,c);c+=2;
		rom[c++] = flag1;
		rom[c++] = flag2;
		rom[c++] = flag3;
		rom[c++] = unknownByte1;
		rom[c++] = unknownByte2;
		
	}
	
	public int getStage(){
		return ownerStage;
	}
	public boolean isPokePower(){
		return (0xFF & category) == 4;
	}
	public boolean isExists(){
		//if(name.isNothing())
			//System.out.println("" + pokedexNumber);
		
		
		return !name.isNothing();
		
	}
	
	public int[] getEnergyAmounts(){
		
		return Util.getEnergyFromFlag(energy_fg, energy_lw, energy_fp, energy_c_);
		
	}
	
	
	
}
