package Rismect;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;

import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;

public class State {
	
	Random rand;
	BoardInfo staticInfo;
	//RismectCountry[] allCountries;
	Map map;
	Players players;

	public State(BoardInfo info,Random rand,Map map, Players players) {
		
		
		this.staticInfo =  info;
		this.rand = rand;		
		this.players = players;  // new RismectPlayers(board.getNumberOfPlayers(),currentPlayerID,countries);
		this.map = map;
		
//		this.allCountries = new RismectCountry[countries.length];
//		
//		for(int i = 0 ; i<countries.length ; i++){
//			allCountries[i].copy(countries[i]);
//			for(int j = i-1 ; j>=0;j--){
//				if(countries[i].isNextTo(countries[j])){
//					allCountries[i].connect(allCountries[j]);
//					
//				}
//			}
//		}
	}

	public State(State oldGameState, Move move) {
		this.copy(oldGameState);
		this.performMove(move);
	}

	public State(State oldGameState) {
		this.copy(oldGameState);
	}

	private void copy(State oldGameState) {
		
		this.staticInfo = oldGameState.staticInfo;
		this.allCountries = new Country[oldGameState.allCountries.length];
		this.players = new Players(oldGameState.players);
		
		this.rand = new Random(System.currentTimeMillis());
		
		for(int i = 0;i<allCountries.length;i++){
			allCountries[i].copy( oldGameState.allCountries[i]);
			for(int j = i-1 ; j>=0;j--){
				if(oldGameState.allCountries[i].isNextTo(oldGameState.allCountries[j])){
					allCountries[i].connect(allCountries[j]);
					
				}
			}
		}
		
	}

	public Move getRandomMove() throws Exception {
		int totalMoveCount = 0;
		
		for(int i = 0;i<allCountries.length;i++){
			if( allCountries[i].player == this.players.currentPlayer.id &&
				allCountries[i].armyCount > 1){
				totalMoveCount += allCountries[i].enimies.size();
			}
		}
		
		totalMoveCount++;
		
		
		int index = rand.nextInt() % totalMoveCount;
		
		// end turn move
		if(index == totalMoveCount -1)
			return new Move(this.players.currentPlayer.id,Move.END_TURN);
			
		// attacking moves
		int cur = 0;
		for( int i = 0; i < allCountries.length ; i++){
			cur += allCountries[i].enimies.size();
			if(cur  > index ){
				
				int localIndex = index-cur+ allCountries[i].enimies.size();
				
				return new Move( this.allCountries[i], 
										this.allCountries[i].enimies.get(localIndex),
										true);
			}
		}
		throw new Exception("getRandomMove in RismectState failed");
	}

	// checks to see if at least two different players still have one or more countries 
	public boolean isTerminal() {
		int player = this.allCountries[0].player;
		
		for(int i =1; i<this.allCountries.length; i++)
		{
			if(this.allCountries[i].player != player)
				return false;
		}
		
		return true;
	}

	//for defaultPolicy where the changes are applied to this instance
	public void performMove(Move move) {
		
		Country us = null,them = null;
		
		for(int i =0; us==null || them ==null ; i++){
			
			if(this.allCountries[i].code == move.attacking.code){
				them = this.allCountries[i];
			}else if(this.allCountries[i].code == move.us.code){
				us = this.allCountries[i];
			}
		}
		
		switch(move.getMoveType()){
			case Move.ATTACK :{
				while(us.armyCount>1 && them.armyCount != 0 ){
					
					//diceAttack( us, them);
					staticAttack(us,them);
				}
				
				if(them.armyCount == 0){
					players.playerList.get(them.player).countryCount--;
					players.checkPlayerStatus(them.player);
					
					players.playerList.get(us.player).countryCount++;
					them.armyCount = us.armyCount -1;
					us.armyCount = 1;
				}
			}
			case Move.END_TURN :{
				fortify();
				
				players.progressToNextPlayer();
				
			}
		}
		
	}


	private void fortify() {
		//TODO
		TreeSet<Country> set = new TreeSet<Country>();
		
		for(int i = 0; i< this.allCountries.length; i++){
			
			
		}
	}

	private void staticAttack(Country attacker, Country defender) {
		double  attackerArmy=attacker.armyCount,
				defenderArmy=defender.armyCount;
		
		double[][] attackerValues = new double[][]
				{// 	1d				2d
					{ 0.5483922,  0.7134150}, //2a
					{ 0.3838224,  1.1244847}, //3a
					{ 0.2994903,  0.8244588}  //4a
				
				},
			defenderValues = new double[][]
				{// 	1d				2d
					{ 0.45160780,  0.2865850}, //2a
					{ 0.61617764,  0.8755153}, //3a
					{ 0.70050970,  1.1755412}  //4a
			
				};
		
		int attackerEffectiveArmy,defenderEffectiveArmy;
		
		while(attackerArmy >=2 && defenderArmy >=1 ){
			
			attackerEffectiveArmy = (int) Math.min(4, attackerArmy)-1;
			defenderEffectiveArmy = (int) Math.min(2, defenderArmy);
			
			attackerArmy-= attackerValues[attackerEffectiveArmy - 1][defenderEffectiveArmy-1];
			defenderArmy-= defenderValues[attackerEffectiveArmy - 1][defenderEffectiveArmy-1];
			
		
		}
		
		if(attackerArmy < 1 && defenderArmy < 1){
			if(attackerArmy > defenderArmy)
				attackerArmy ++;
			else 
				defenderArmy ++;
		}
		
		attacker.armyCount = (int) attackerArmy;
		defender.armyCount = (int) defenderArmy;
		
		
	}

	private void diceAttack( Country attacker, Country defender) {
		
		
		
		PriorityQueue<Integer> attackerDice = new PriorityQueue<Integer>(),
							   defenderDice = new PriorityQueue<Integer>();

		// get attackers dice (1 to 3 dice) 
		for(int i = Math.min(3, attacker.armyCount-1) ; i>0 ; i--){
			attackerDice.add(rand.nextInt() % 6 +1);
		}
		
		// get defenders dice ( 0 to 2 dice)
		for(int i = Math.min(2, defender.armyCount) ; i>0 ; i--){
			defenderDice.add(rand.nextInt() % 6 +1);
		}
		
		//compare top 1 or 2 dice
		for(int i = Math.min(attackerDice.size(), defenderDice.size()); i >0; i-- ){
			if(attackerDice.remove() > defenderDice.remove())
				defender.armyCount--;
			else
				attacker.armyCount--;
		}
		
		
		
	}

	public LinkedList<Move> getAllMoves() {
		LinkedList<Move> moveStack = new LinkedList<Move>();
		
		for( int i = 0; i < allCountries.length ; i++){
			if(allCountries[i].player == this.players.currentPlayer.id){
				for( int j =0 ; j<allCountries[i].enimies.size(); j++){
					
					moveStack.add(new Move(allCountries[i],allCountries[i].enimies.get(j),true));
					
				}
			}
		}
		
		
		//add end turn move
		moveStack.add(new Move(this.players.currentPlayer.id, Move.END_TURN) );

		return moveStack;
	}


}
