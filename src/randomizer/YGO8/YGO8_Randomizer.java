package randomizer.YGO8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import randomizer.Word;

public class YGO8_Randomizer {
	public static final int CARD_AMOUNT = 800;
	public static final int DATA_START = 0x91D4A;
	public static final int DATA_END = 0x94916;
	public YGO8_Randomizer(File gameFile){
		
		byte[] game_data = new byte[DATA_END - DATA_START];
		FileInputStream game_rom;
		try {
			game_rom = new FileInputStream(gameFile);
			game_rom.skip(DATA_START);
			game_rom.read(game_data);
			game_rom.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadAllCards(game_data);
		
	}
	
	public void loadAllCards(byte[] cardRom){
		// starts taken from froggy25's Reshef of Destruction page on datacrystal
		
		int def_start = 0x91D4A - DATA_START, def_size = 2;
		int atk_start = 0x9238C - DATA_START, atk_size = 2;
		int cost_start = 0x929D0 - DATA_START, cost_size = 4;
		int attribute_start = 0x93651 - DATA_START, attribute_size = 1;
		int level_start = 0x93972 - DATA_START, level_size = 1;
		int type_start = 0x93C93 - DATA_START, type_size = 1;
		int category_start = 0x93FB4 - DATA_START, category_size = 1;
		int monster_effect_start = 0x942D5 - DATA_START, monster_effect_size = 1;
		int other_effect_start = 0x945F6 - DATA_START, other_effect_size = 1;
		
		Card_Structure[] cards = new Card_Structure[CARD_AMOUNT];
		
		for(int i = 0; i < CARD_AMOUNT; i++){
			cards[i] = new Card_Structure();
			cards[i].DEF = new Word(cardRom, def_start + i*def_size, true);
			cards[i].ATK = new Word(cardRom, atk_start + i*atk_size, true);
			cards[i].cost = new Word(cardRom, cost_start + i*cost_size, true);
			cards[i].attribute = cardRom[attribute_start + i*attribute_size];
			cards[i].level = cardRom[level_start + i*level_size];
			cards[i].type = cardRom[type_start + i*type_size];
			cards[i].category = cardRom[category_start + i*category_size];
			cards[i].monster_effect = cardRom[monster_effect_start + i*monster_effect_size];
			cards[i].other_effect = cardRom[other_effect_start + i*other_effect_size];
		}

		for(int i = 0; i < CARD_AMOUNT; i++){
			System.out.println(cards[i].ATK + "\t" + cards[i].DEF + "\t" + cards[i].cost
					 + "\t" + (int)(0xFF & cards[i].attribute)
					 + "\t" + (int)(0xFF & cards[i].level)
					 + "\t" + (int)(0xFF & cards[i].type)
					 + "\t" + (int)(0xFF & cards[i].category)
					 + "\t" + (int)(0xFF & cards[i].monster_effect)
					 + "\t" + (int)(0xFF & cards[i].other_effect)
					 );
			
			
		}
		
	}
	
}
