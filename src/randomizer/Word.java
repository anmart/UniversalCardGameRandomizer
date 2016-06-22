package randomizer;

public class Word {

	byte low,high;//two bytes. in memory it's laid out as the low end byte first then the high end byte
	public Word(){
		low = 0x00;
		high = 0x00;
		
	}
	public Word(byte[] word){
		if(word.length != 2)
			System.out.println("Word size incorrect, please use the other constructor.");
		
		low = word[0];
		high = word[1];	
	}
	public Word(byte[] byteArray, int index){
		low = byteArray[index];
		high = byteArray[index+1];	
	}
	
	public void writeToRom(byte[] rom, int startIndex){
		rom[startIndex] = low;
		rom[startIndex+1] = high;
	}
	
	public boolean isNothing(){
		
		return (0xFF & low) == 0 && (0xFF & high) == 0;
		
		
	}
	
}
