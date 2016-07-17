package randomizer;

public class Word {

	private byte low,high;//two bytes. in memory it's laid out as the low end byte first then the high end byte
	private boolean isLittleEndian;
	
	public Word(){
		low = 0x00;
		high = 0x00;
	}
	public Word(byte[] word, boolean littleEndian){
		if(word.length != 2)
			System.out.println("Word size incorrect, please use the other constructor.");
		
		if(littleEndian){
			low = word[0];
			high = word[1];	
		}else{
			high = word[0];
			low = word[1];
		}
	}
	public Word(byte[] byteArray, int index, boolean littleEndian){
		isLittleEndian = littleEndian;
		
		if(littleEndian){
			low = byteArray[index];
			high = byteArray[index+1];
		}else{
			high = byteArray[index];
			low = byteArray[index+1];
		}
	}
	public void writeToRom(byte[] rom, int startIndex){
		if(isLittleEndian){
			rom[startIndex] = low;
			rom[startIndex+1] = high;
		}else{
			rom[startIndex] = high;
			rom[startIndex+1] = low;
		}
	}
	public boolean isNothing(){
		return (0xFF & low) == 0 && (0xFF & high) == 0;	
	}
	public byte getLow(){
		return low;
	}
	public byte getHigh(){
		return high;
	}
	public String toString(){
		return "" + (0xFFFF & ((0xFF00 & high << 8) + (low & 0xFF)));
	}
}
