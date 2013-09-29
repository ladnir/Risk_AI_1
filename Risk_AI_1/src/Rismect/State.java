package Rismect;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;


public class State {

	static final int SUPPLY_STAGE = 0;
	static final int ATTACK_STAGE = 1;
	static final int FORTIFY_STAGE = 2;
	static final int FORCE_CARD_CASH_STAGE =3;
	
	
	int round;//TODO make rounds increase...
	int stage;
	Random rand;
	Players players;
	HashMap<Integer, Army> armies;

	
	public State(Random rand,HashMap<Integer, Army> armies, Players players,int stage) {
		this.stage = stage;
		this.armies = armies;
		this.rand = rand;		
		this.players = players;  
	}

	public State(State oldGameState, Move move) {

		this.copy(oldGameState);		
		move.PerformMove(this);
	}

	public State(State oldGameState) {
		this.copy(oldGameState);
	}

	private void copy(State oldGameState) {
		stage = oldGameState.stage;
		rand = oldGameState.rand;
		players = new Players(oldGameState.players);
		
		for(Entry<Integer,Army> entry : oldGameState.armies.entrySet() ){
			Army newArmy =  new Army(entry.getValue().owner,entry.getValue().armyCount,entry.getValue().country) ;
			armies.put(entry.getKey(),newArmy);
		}
		
	}

	public Move getRandomMove(){
		if(this.stage == State.FORCE_CARD_CASH_STAGE){
			int income = players.currentPlayer.cashCards(round);
			
			return Factory.getSupplyMove(income);
			
		}else if(this.stage == State.SUPPLY_STAGE){
			int income = getIncome();
			
			if(players.currentPlayer.canCashCards())				
				income += players.currentPlayer.cashCards(round);
			
			return Factory.getSupplyMove(income);
			
		}else if(this.stage == State.ATTACK_STAGE){
			int totalMoveCount = 0;
			
			// gets the total number a available attacks
			for(Entry<Integer,Army> entry : this.armies.entrySet()){
				Army army = entry.getValue();
				
				if(army.armyCount > 1 && army.owner == this.players.currentPlayer )
					for(CountryInfo neighbors : army.country.neighbors)					
						if(armies.get(neighbors.code).owner != this.players.currentPlayer)
							totalMoveCount++;
			}
			
			totalMoveCount++;//fortify move
			
			// selects a random attack among the available
			int moveIndex = rand.nextInt()% totalMoveCount;
			
			if(moveIndex == totalMoveCount-1)// check for index == fortify move
				return Factory.getFortifyMove();
			
			int curIndex=0;
			
			// finds and returns that attack
			for(Entry<Integer,Army> entry : this.armies.entrySet()){
				Army army = entry.getValue();
			
				if(army.armyCount > 1 && army.owner == this.players.currentPlayer )
					for(CountryInfo neighbor : army.country.neighbors)					
						if(armies.get(neighbor.code).owner != this.players.currentPlayer)
							if(curIndex==moveIndex)
								return Factory.getAttackMove(army.country,neighbor);
							else curIndex++;
			}
		}else if(this.stage == State.FORTIFY_STAGE){
			
			return Factory.getFortifyMove();
		}
		
		assert false;
		
		return null;
	}

	public LinkedList<Move> getAllMoves() {
		LinkedList<Move> moves = new LinkedList<Move>();
		
		if(this.stage == State.SUPPLY_STAGE){
			int income = getIncome();
			
			if(players.currentPlayer.canCashCards())				
				income += players.currentPlayer.cashCards(round);
			
			moves.add(Factory.getSupplyMove(income));
			
		}else if(this.stage == State.ATTACK_STAGE){
			
			
			// finds and returns that attack
			for(Entry<Integer,Army> entry : this.armies.entrySet()){
				Army army = entry.getValue();
			
				if(army.armyCount > 1 && army.owner == this.players.currentPlayer )
					for(CountryInfo neighbor : army.country.neighbors)					
						if(armies.get(neighbor.code).owner != this.players.currentPlayer)
							moves.add( Factory.getAttackMove(army.country,neighbor));
							
			}
			
			moves.add( Factory.getFortifyMove());
		}
		
		assert(moves.size()>0);
		
		
		return moves;
	}

	private int getIncome() {
		
		if(this.players.currentPlayer.countryCount < 1 )
			return 0;
		
		int income = Math.max(this.players.currentPlayer.countryCount, 3);
		
		for(Entry<Integer,Integer> ContEntry : BoardInfo.continentsBonus.entrySet()){
			boolean ownsCont =true;
			
			for(Entry<Integer,Army> countryEntry : this.armies.entrySet()){
				if(countryEntry.getValue().country.continent == ContEntry.getValue().intValue()){
					if(countryEntry.getValue().owner != this.players.currentPlayer)
						ownsCont = false;
				}
			}
			if(ownsCont)
				income+= ContEntry.getValue().intValue() * (1 +((round * BoardInfo.continentIncrease)/100));
		}
	
		return income;
	}

	// checks to see if at least two different players still have one or more countries 
	public boolean isTerminal() {
		players.checkPlayersStatus();
		
		if(players.playerCount ==1)
			return true;
		
		return true;
	}

	public void progressToNextPlayer() {
		players.checkPlayersStatus();
		
		Boolean newRound = players.progressToNextPlayer();
		if(newRound){
			round++;
		}
		
	}


}
