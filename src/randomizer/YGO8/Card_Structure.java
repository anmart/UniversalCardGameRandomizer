package randomizer.YGO8;

import randomizer.Word;

public class Card_Structure {

	public Word DEF,ATK,cost;//cost is actually four bytes but is staying a word as the max is 999
	public byte attribute, level, type, category, monster_effect, other_effect;
	
	// skip to the pertinent sections of rom and load the necessary ranges into byte arrays
	// then sort and deliver into different cards
	
	// or just create a huge buffer from def all the way to other effect then sort it out
	
}
