package randomizer.Pokemon_TCG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import randomizer.Word;


public class PTCG1_Randomizer {

	byte[] rom;
	MonCardData[] mons;

	int monAmount = 187;
	int monStartLocation = 200232;
	int monSize = 65;

	Random rand;
	String randSeed;

	Move blankMove = new Move();

	public PTCG1_Randomizer(File game, String seed){


		if(!seed.equals("")){
			randSeed = seed;
		}
		else{
			rand = new Random();
			randSeed = "" + rand.nextInt(); 
			// doing it fairly roundabout like this lets the user see what seed was used if none was set
		}
		rand = new Random(randSeed.hashCode());

		try {

			rom = Files.readAllBytes(game.toPath());
			parseMons();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public void saveRom(String fileLocation){

		writeMonsToRom();
		try {
			FileOutputStream fos = new FileOutputStream(fileLocation);
			fos.write(rom);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public void parseMons(){
		//first pokemon is at 16x30e28 10x200232
		//last pokemon is at 16x33d62 10x212322
		//at 16x41 bytes a pop, that puts us at 187 pokemon

		mons = new MonCardData[monAmount];

		for(int i = 0; i < monAmount; i++){
			mons[i] = new MonCardData(rom,monStartLocation + i*monSize);			
		}

		//right now we've parsed a mon

	}

	public void writeMonsToRom(){

		for(int i = 0; i < monAmount; i++){
			mons[i].writeToRom(rom, monStartLocation + i*monSize);
		}

	}

	// for both of these, consider making them work by swapping existing values, or otherwise controlling the values better
	public void randomizeRetreatCosts(){
		int maxRetreat = 3;
		for(int i = 0; i < mons.length; i++){
			mons[i].retreatCost = (byte) (0xFF & rand.nextInt(maxRetreat+1));
		}
	}
	public void randomizeTypes(){
		int typeAmount = 7;
		for(int i = 0; i < mons.length; i++){
			mons[i].type = (byte) (0xFF & rand.nextInt(typeAmount));
		}
	}

	public void deleteInvisibleWallMove(){
		//NOTE: if this option is checked on the rom it has to have top priority. it forcefully assumes it knows where mr mime and his moves are
		int mrMimeLoc = 147;
		mons[mrMimeLoc].move1 = mons[mrMimeLoc].move2;

		//bulbasaur doesn't have a second move, so we can overwrite mr.mime's second move with bulbasaurs blank second move
		//the values are all probably blank but this is safest
		mons[mrMimeLoc].move2 = blankMove;
	}

	public void randomizeWarps(){
		// consider adding Overworld map indices to this too
		int warpDataStartLoc = 0x1c0dd;
		int roomAmount = 33;

		// not sure if this is bad practice but I didn't want a class that was everywhere and unrelated to all but this method
		// if we ever come up with more warp shenanigans then move this to its own class file, but I can't envision it happening any time soon
		class WarpRoom{		
			class  WarpEntry{
				byte xCurrent, yCurrent, connection_id, xNew, yNew;
				public WarpEntry(byte[] rom, int startLoc){
					xCurrent = rom[startLoc++];
					yCurrent = rom[startLoc++];
					connection_id = rom[startLoc++];
					xNew = rom[startLoc++];
					yNew = rom[startLoc++];
				}
				public void writeWarpEntry(byte[] rom, int startLoc){
					rom[startLoc++] = xCurrent;
					rom[startLoc++] = yCurrent;
					rom[startLoc++] = connection_id;
					rom[startLoc++] = xNew;
					rom[startLoc++] = yNew;
				}
			}

			public ArrayList<WarpEntry> warps = new ArrayList<WarpEntry>();

			public int interpretWarps(byte[] rom, int startLoc){

				while(rom[startLoc] != 0 || rom[startLoc+1] != 0){
					warps.add(new WarpEntry(rom, startLoc));
					startLoc += 5;
				}
				startLoc += 2;
				return startLoc;
			}

			public int writeWarps(byte[] rom, int startLoc){
				for(int i = 0; i < warps.size(); i++){
					warps.get(i).writeWarpEntry(rom, startLoc);
					startLoc+=5;
				}

				startLoc += 2; //since the terminators are already in the code we don't need to rewrite them
				return startLoc;
			}

		}

		WarpRoom[] rooms = new WarpRoom[roomAmount];
		int startLoc = warpDataStartLoc;
		for(int i = 0; i < roomAmount; i++){
			rooms[i] = new WarpRoom();
			startLoc = rooms[i].interpretWarps(rom, startLoc);
		}

		//a little inefficient but w/e, this is why it's beta :^)
		ArrayList<WarpRoom.WarpEntry> setList = new ArrayList<WarpRoom.WarpEntry>();
		for(int i = 0; i < roomAmount; i++){
			for(int j = 0; j < rooms[i].warps.size(); j++){
				setList.add(rooms[i].warps.get(j));
			}
		}

		// setting the values back
		Collections.shuffle(setList, rand);
		for(int i = 0; i < roomAmount; i++){
			for(int j = 0; j < rooms[i].warps.size(); j++){
				rooms[i].warps.get(j).connection_id = setList.get(0).connection_id;
				rooms[i].warps.get(j).xNew = setList.get(0).xNew;
				rooms[i].warps.get(j).yNew = setList.get(0).yNew;
				setList.remove(0);
			}
		}

		// saving
		startLoc = warpDataStartLoc;
		for(int i = 0; i < roomAmount; i++){
			startLoc = rooms[i].writeWarps(rom, startLoc);
		}

	}

	public void randomizeAllSets(){
		//Note: randomizes sets in a way that there are the exact same amount of pokemon in each set.
		//just redistributes which pokemon has which set flags
		ArrayList<Byte> setList = new ArrayList<Byte>();

		//throw all set flags into an arraylist
		for(MonCardData mon : mons){
			setList.add(mon.set);

		}

		//randomize setList
		Collections.shuffle(setList, rand);
		for(MonCardData mon : mons){
			mon.set = setList.get(0);
			setList.remove(0);
		}

	}

	public void randomizeMoves(boolean keepStage, boolean keepMoveAmount){
		//keepStage: whether or not all basics get basic moves or other. keepMoveAmount: whether it should be 1:1 moves with no duplicates or losses

		//this is probably stupid but it seems to work best
		ArrayList<Move> moveList = new ArrayList<Move>();
		ArrayList<MonCardData> monList = new ArrayList<MonCardData>();

		for(int i = 0; i < monAmount; i++){
			moveList.add(mons[i].move1);
			if(mons[i].move2.isExists())
				moveList.add(mons[i].move2);
			monList.add(mons[i]);			
		}


		Collections.shuffle(moveList, rand);
		Collections.shuffle(monList, rand);
		for(int i = moveList.size()-1; i >= 0; i--){
			if(moveList.get(i).isPokePower()){
				moveList.add(moveList.remove(i++));//move it to the back of the list
			}
		}	

		for(int i = 0; i< monList.size(); i++){
			MonCardData curr = monList.get(i);
			int stage = 0xFF & curr.stage;
			//	System.out.println(stage);
			boolean foundPokePower = false;
			for(int j = moveList.size()-1; j >= 0; j--){
				Move currMove = moveList.get(j);

				if(!keepStage || currMove.ownerStage == stage){

					if(foundPokePower){ 
						if(currMove.isPokePower())
							continue;
						curr.move2 = currMove;
						moveList.remove(j);
						break;
					}



					//we found one! yay!
					curr.move1 = currMove;
					moveList.remove(j);
					if(currMove.isPokePower()){
						//the one we found is a pokepower, we need to find another non pokepower move
						foundPokePower=true;		
						continue;
					}else{
						break;

					}


				}


			}

			if(foundPokePower){
				//if after all that we got two moves (move + pokepower), remove self from list
				monList.remove(i--);

			}	
		}

		//Alright so where are we now?
		//we have a list of mons with only one move, and a list of moves that still don't have a home
		for(int i = 0; i< monList.size(); i++){
			MonCardData curr = monList.get(i);
			int stage = 0xFF & curr.stage;
			for(int j = moveList.size()-1; j >= 0; j--){
				Move currMove = moveList.get(j);
				if(!keepStage || currMove.ownerStage == stage){
					curr.move2 = currMove;
					moveList.remove(j);
					monList.remove(i--);
					break;					
				}
			}


		}

		for(int i = 0; i < monList.size(); i++){
			monList.get(i).move2 = blankMove;


		}


		/*
		 * alright new plan for moves
		 * store literally every move (including pokepowers) into movelist
		 * shuffle movelist
		 * sort movelist to have all pokepowers at the end NOTE loop through movelist backwards
		 * create a list of all pokemon indices
		 * shuffle this list
		 * 
		 *	loop through all pokemon indices
		 *	walk through MoveList until we find one that comes from the same stage as the current pokemon (if necessary)
		 *	add current move to pokemon, remove from movelist
		 * 	if the current move is a pokepower, walk through moveList until a new move that works on the current pokemon (and is not a pokepower) shows up 
		 * 	attach the new move to the current pokemon and remove it from the movelist
		 * 	if we had to deal with a pokepower, remove the current pokemon's index from the list as it now has 2 moves
		 * 	continue until we hit the end of the list of pokemon
		 * 
		 * 	loop through the remaining pokemon
		 * 	walk through MoveList until we find one that comes from the same stage as the current pokemon (if necessary) OR we reach the end of movelist
		 * 	if we found a move, add it to the current pokemon, remove both from their respective lists (technically the pokemon doesn't have to be removed)
		 * 	if we did not find a move, skip this pokemon and move on to the next pokemon
		 * 	continue until we run out of moves (which will occur before the end of the pokemon list every single time, i believe, as no stage has every pokemon with 2 moves)
		 * 
		 */



	}

	public void setAllEnergyToColorless(boolean costNothing){

		int mod = 16;
		if(costNothing)
			mod = 1;

		//This is another prepatch setting
		ArrayList<Move> moveList = new ArrayList<Move>();

		for(int i = 0; i < monAmount; i++){
			moveList.add(mons[i].move1);
			if(mons[i].move2.isExists())
				moveList.add(mons[i].move2);		
		}


		for(Move m : moveList){
			int energyAmounts=0;
			for(int i : m.getEnergyAmounts())
				energyAmounts+=i;

			m.energy_fg = 0x00;//set everything but c_ to 00
			m.energy_fp = 0x00;
			m.energy_lw = 0x00;
			m.energy_c_ = (byte) (mod*energyAmounts);//according to the internet it chops off the first 24 bits leaving the last byte. since energy amount is >=0 and <5 this will still be correct no matter what (



		}



	}

	public void randomizeDeckPointersInRom(boolean skipStarterDecks){
		//We're just screwing around with pointers right now, not the decks themselves
		//so we're not going to bother with a parser, we're just going to lost all pointers as words
		//then rewrite them in a different order
		//NOTE this modifies the rom data directly

		//Magic numbers
		int deckPointerAmount = 55;
		int deckPointerStartLocation = 0x30000;
		int[] skippedDeckIDs = new int[]{}; // for skipping starter decks. eventually might let users pick decks to skip

		if(skipStarterDecks){
			skippedDeckIDs = new int[]{ 5,7,9 }; // hardcoded values from the assembly. might need to include extra decks here as well
		}

		ArrayList<Word> deckPointers = new ArrayList<Word>();

		for(int i = 0; i < deckPointerAmount; i++){
			boolean skipDeck = false;
			for(int j : skippedDeckIDs){
				if(i == j){
					skipDeck = true;
					break;
				}
			}
			if(skipDeck)
				continue;

			deckPointers.add(new Word(rom,i*2 + deckPointerStartLocation, true));
		}

		Collections.shuffle(deckPointers, rand);

		//now save over the deck pointers on rom
		for(int i = 0; i < deckPointerAmount; i++){
			boolean skipDeck = false;
			for(int j : skippedDeckIDs){
				if(i == j){
					skipDeck = true;
					break;
				}
			}
			if(skipDeck)
				continue;


			deckPointers.get(0).writeToRom(rom, i*2 + deckPointerStartLocation);
			deckPointers.remove(0);
		}




	}


	public void randomizeHP(int low, int high, boolean allowGlitchHP){
		for(int i = 0; i < mons.length; i++){
			if(low == high)
				mons[i].hp =(byte) (low);
			else{
				if(allowGlitchHP)
					mons[i].hp =(byte) ((int)(rand.nextInt(high-low) + low));
				else
					mons[i].hp =(byte) ((int)(rand.nextInt((high-low)/10)*10 + low));
			}
		}
	}

	public void SanquiRemoveTutorialFromRom(){
		//NOTE: this writes directly to rom

		//magic numbers
		int sizeOfTutorial = 229;
		int tutorialStartLoc = 0xD76F;

		//writes 228 bytes of 0x43 to ram...not sure why or how this works
		for(int i = 0; i < sizeOfTutorial; i++){
			rom[i + tutorialStartLoc] = 0x43;
		}




	}



}
