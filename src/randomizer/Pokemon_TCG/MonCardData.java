package randomizer.Pokemon_TCG;

import randomizer.Word;

public class MonCardData {

	//besides the moves, this will actually literally just be laid out in memory like the cards.asm file
	byte type;
	Word gfx;
	Word name;
	byte rarity;
	byte set;
	byte cardConstant;
	byte hp;
	byte stage;
	Word preEvoName;
	
	Move move1;
	Move move2;
	
	byte retreatCost;
	byte weakness;
	byte resistance;
	Word kind;
	byte pokedexNumber;
	byte unknownByte1;
	byte level;
	Word length;
	Word weight;
	Word description;
	byte unknownByte2;
	


	public static int moveSize = 19;
	
	public MonCardData(byte[] rom, int startIndex){
		int c = startIndex;
				
		type = rom[c++];
		gfx = new Word(rom,c,true); c+=2;//I want every var to be one line, so words increment on the same line
		name = new Word(rom,c,true); c+=2;
		rarity = rom[c++];
		set = rom[c++];
		cardConstant = rom[c++];
		hp = rom[c++];
		stage = rom[c++];
		preEvoName = new Word(rom,c,true); c+=2;
		
		
		move1 = new Move(rom,c,0xFF & stage); c+= moveSize;
		move2 = new Move(rom,c,0xFF & stage); c+= moveSize;
		
		
		retreatCost = rom[c++];
		weakness = rom[c++];
		resistance = rom[c++];
		kind = new Word(rom,c,true); c+=2;
		pokedexNumber = rom[c++];
		unknownByte1 = rom[c++];
		level = rom[c++];
		length = new Word(rom,c,true); c+=2;
		weight = new Word(rom,c,true); c+=2;
		description = new Word(rom,c,true); c+=2;
		unknownByte2 = rom[c++];
			
	
		
	}
	
	public void writeToRom(byte[] rom, int startIndex){
		int c = startIndex;
		
		
		rom[c++] = type;
		gfx.writeToRom(rom,c);c+=2;
		name.writeToRom(rom,c);c+=2;
		rom[c++] = rarity;
		rom[c++] = set;
		rom[c++] = cardConstant;
		rom[c++] = hp;
		rom[c++] = stage;
		preEvoName.writeToRom(rom,c);c+=2;
		
		move1.writeToRom(rom,c);c+=moveSize;
		move2.writeToRom(rom,c);c+=moveSize;
		
		rom[c++] = retreatCost;
		rom[c++] = weakness;
		rom[c++] = resistance;
		kind.writeToRom(rom,c);c+=2;
		rom[c++] = pokedexNumber;
		rom[c++] = unknownByte1;
		rom[c++] = level;
		length.writeToRom(rom,c);c+=2;
		weight.writeToRom(rom,c);c+=2;
		description.writeToRom(rom,c);c+=2;
		rom[c++] = unknownByte2;
		
		
		
		
	}
	

	
}
