package randomizer.Pokemon_TCG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

	boolean maxEvolutionsHaveBeenFound = false;
	
	
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

	public void randomizeWeakness(){
		for(int i = 0; i < mons.length; i++){
			byte weakness = 0x01;
			weakness <<= (rand.nextInt(6) + 2);
			mons[i].weakness = (byte) (0xff & weakness);
			weakness &= 0xff;
		}
	}

	public void randomizeResistance(){
		for(int i = 0; i < mons.length; i++){
			byte resistance = 0x01;
			resistance <<= (rand.nextInt(7) + 2);
			mons[i].resistance = (byte) (0xff & resistance);
			resistance &= 0xff;
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

		mons[mrMimeLoc].move2 = blankMove;
	}

	public void setMetronomeAmount(int m){
		int clefairyIndex = 0xab - 8;
		int clefableIndex = 0xac - 8;

		if(m==0){ // take out clefable's, clefairy's will be taken out right below
			mons[clefableIndex].move1 = mons[clefableIndex].move2;
			mons[clefableIndex].move2 = blankMove;
		}
		if(m < 2){ // we'll arbitrarily take out clefairy's
			mons[clefairyIndex].move2 = blankMove;
		}else{ // for any value >= 2
			Move metronome = mons[clefairyIndex].move2;			
			if (m>monAmount)
				m = monAmount;
			m-=2; // sets the maximum m to 2 minus mon amount to account for clef's

			ArrayList<MonCardData> tempMons = new ArrayList<MonCardData>(Arrays.asList(mons));
			//remove these so they don't accidentlaly get the move again
			tempMons.remove(clefairyIndex);
			tempMons.remove(clefableIndex);
			Collections.shuffle(tempMons, rand);
			while(m > 0){
				if(rand.nextBoolean()){
					tempMons.get(0).move1 = metronome;
				}else{
					tempMons.get(0).move2 = metronome;
				}
				//System.out.println(tempMons.get(0).pokedexNumber);
				m--;
				tempMons.remove(0);
			}





		}

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
				moveList.add(moveList.remove(i));//move it to the back of the list
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
			skippedDeckIDs = new int[]{ 5,6,7,8,9,10 }; // hardcoded values from the assembly. might need to include extra decks here as well
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
		
		//magic numbers
		int sizeOfTutorial = 229;
		int tutorialStartLoc = 0xD76F;

		//writes 228 bytes of 0x43 to ram...not sure why or how this works
		for(int i = 0; i < sizeOfTutorial; i++){
			rom[i + tutorialStartLoc] = 0x43;
		}
	}

	public void setInstantText(){
		int ldaLocation = 0x199c1;
		rom[ldaLocation] = 0x00;
	}
	public void setAnimationsOff(){
		char[] mod = { 0x3E, 0x01, 0xEA, 0x07, 0xA0, 0xEA, 0x09, 0xA0, 0xAF, 0xAF}; // compiled a mod and copied the relevant bytes
		int startLoc = 0x199c8;
		
		for(int i = 0; i < mod.length; i++){
			rom[startLoc + i] = (byte) (0xFF & mod[i]);
		}
		
	}

	public ArrayList<MonCardData> indicesOfPokemonFromNamePointer(Word namePointer, ArrayList<MonCardData> mons){
		ArrayList<MonCardData> ret = new ArrayList<MonCardData>();
		for(int i = 0; i < mons.size(); i++){
			
			if(mons.get(i).name.equals(namePointer))
				ret.add(mons.get(i));
		}
		return ret;
	}
	
	
	
	public void randomizeEvolutions(int maxSize, boolean monoType){
		
		if(maxSize < 1)
			maxSize = 999;
		
		ArrayList<MonCardData> randMons = new ArrayList<MonCardData>(Arrays.asList(mons));
		Collections.shuffle(randMons, rand);
		ArrayList<MonCardData> tempInd; // used a few times for a short amount of time
		ArrayList<MonCardData> usedMons = new ArrayList<MonCardData>();
		
		while(randMons.size() > 0){
			
			MonCardData currMon = randMons.get(0);

			tempInd = indicesOfPokemonFromNamePointer(currMon.name, randMons);
			for(MonCardData mc : tempInd){
				mc.stage = 0x0; // basic
				mc.preEvoName = new Word(); // no pre evo (default constructor returns 0000 word)
				usedMons.add(mc);
				randMons.remove(mc);				
			}
			
			
			
			int maxBound = maxSize;
			if(randMons.size() < maxBound)
				maxBound = randMons.size();
			
			
			//this lets us skip randomization and the for loop if we have no pokems left
			//we go straight to setting this mon to highest evo
			int rnum;
			if(maxBound < 1)
				rnum = 0;
			else
				rnum = rand.nextInt(maxBound);
			
			
			for(int i = rnum; i > 0; i--){
				
				boolean monFound = false;
				int newMonNum;
				
				for(newMonNum = 0; newMonNum <  randMons.size(); newMonNum++){
					if(!monoType || randMons.get(newMonNum).type == currMon.type){
						monFound = true;
						break;						
					}
				}
				
				if(!monFound)
					break;
				
				MonCardData newMon = randMons.get(newMonNum);
				
				if(currMon.stage > 0)
					newMon.stage = 0x02;
				else
					newMon.stage = 0x01;
				
				
				
				tempInd = indicesOfPokemonFromNamePointer(newMon.name, randMons);
				for(MonCardData mc : tempInd){
					mc.stage = newMon.stage;
					mc.preEvoName = currMon.name;
					usedMons.add(mc);
					randMons.remove(mc);
				}
				
				
				currMon = newMon;
			}
			
			tempInd = indicesOfPokemonFromNamePointer(currMon.name, usedMons);
			for(MonCardData mc : tempInd){
				mc.highestStage = true;
			}
			
			
		}
		
		maxEvolutionsHaveBeenFound = true;
	}

	public void setMoveTypeToMonType() {
		for( MonCardData mon : mons){

			ArrayList<Move> moveList = new ArrayList<Move>();
			moveList.add(mon.move1);
			if(mon.move2.isExists())
				moveList.add(mon.move2);		

			for(Move m : moveList){
				int eAmt = 0;
				
				int[] energies = m.getEnergyAmounts();
				for(int i = 0; i < energies.length-1; i++){//skip colorless at the end
					eAmt+=energies[i];
				}
				
				
				if((0xff & mon.type) == 0x6){//add more because colorless
					eAmt += energies[energies.length-1];
				}
				
				
				if(mon.type%2 == 0) // if it's even then cost needs to be shifted left
					eAmt = 0xFF & (eAmt << 4 );
				
				for(int i = 0; i < energies.length; i++){
					if(i == (0xFF & mon.type))
						energies[i] = eAmt;
					else
						energies[i] = 0x00;
				}
				
				
				m.energy_fg = (byte) (0xFF & (energies[0] + energies[1])); // cautionary 0xFF &'s because java is weird
				m.energy_lw = (byte) (0xFF & (energies[2] + energies[3]));
				m.energy_fp = (byte) (0xFF & (energies[4] + energies[5]));
				if((0xFF & mon.type) == 0x06) // only change colorless if mon type is colorless
					m.energy_c_ = (byte) (0xFF & energies[6]);
				// I almost feel like it'd be better if just hardcoded.
				
				
			}
			
			

			
			

		}

	}

	
	public void findMaxEvolutionsForMons(){
		for(MonCardData monCurrent : mons){
			boolean foundHigher = false;

			for(MonCardData monPossibleEvo : mons){
				if(monPossibleEvo.preEvoName.equals(monCurrent.name)){
					foundHigher = true;
					break;
				}
				
			}

			if(!foundHigher)
				monCurrent.highestStage = true;
			
		}
		
		
		maxEvolutionsHaveBeenFound = true;
	}
	

	public boolean fixHPForChains() {
		
		
		if(!maxEvolutionsHaveBeenFound)
			findMaxEvolutionsForMons();
		
		ArrayList<MonCardData> monsList = new ArrayList<MonCardData>(Arrays.asList(mons));
		
		while(monsList.size() > 0){
			
			ArrayList<MonCardData> evolutionChain = new ArrayList<MonCardData>();
			ArrayList<MonCardData> chainStageMons;
			ArrayList<Byte> HPs = new ArrayList<Byte>();
			
			MonCardData currMon = null;
			
			for(int i = 0; i < monsList.size(); i++){
				if(monsList.get(i).highestStage){
					currMon = monsList.get(i);
				}
			}
			
			if(currMon == null){
				return false;
			}
			
			
			chainStageMons = indicesOfPokemonFromNamePointer(currMon.name, monsList);
			while(chainStageMons.size() > 0){
				currMon = chainStageMons.get(0); //we know it's >0, we need another mon for the next stage
				for(MonCardData mc : chainStageMons){
					evolutionChain.add(mc);
					HPs.add(mc.hp);
					monsList.remove(mc);
					
				}

				chainStageMons = indicesOfPokemonFromNamePointer(currMon.preEvoName, monsList);
			}
			
			Collections.sort(HPs);
			for(int i = 0; i < evolutionChain.size(); i++){
				evolutionChain.get(i).hp = HPs.get(evolutionChain.size() - i - 1);
			}
			
		}
		
		
		return true;
	}



	public void removeRetreatCost() {
		for(MonCardData mc : mons)
			mc.retreatCost = 0x00;
		
	}



}
