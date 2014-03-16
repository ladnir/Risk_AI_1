package Rismect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;


public class FortifyMove implements Move{

	@Override
	public void PerformMove(State s) {
		assert(s.stage !=State.SUPPLY_STAGE);
		
		s.stage = State.FORTIFY_STAGE;
		
		for(Entry<Integer,Army> entry : s.armies.entrySet()){
			Army army = entry.getValue();
			if(army.owner == s.players.currentPlayer ){
				
				if(army.mobeableArmyCount > 1 && noEnemyNeighbors(army,s)){
					
					Army neighbor = NearestBorderSearch(army, s);
					
					int moveableArmys = Math.min(army.armyCount - 1, army.mobeableArmyCount);
					
					neighbor.armyCount += moveableArmys;
					army.mobeableArmyCount -= moveableArmys;
					army.armyCount -=moveableArmys;
				}
				
				
			}
		}
		
		s.progressToNextPlayer();
		s.stage=State.SUPPLY_STAGE;
	}

	@Override
	public void PerformMove(com.sillysoft.lux.Board board, int playerID) {
		com.sillysoft.lux.Country[] countries =board.getCountries();
		for(com.sillysoft.lux.Country country : countries){
			if(country.getOwner() == playerID){
				if(country.getArmies() > 1 && noEnemyNeighbors(country,board)){
					
					com.sillysoft.lux.Country neighbor = nearestBorderSearch(country,board);
					
					int moveableArmy = country.getMoveableArmies();
					
					board.fortifyArmies(moveableArmy, country, neighbor);
				}
			}
		}
	}


	@Override
	public String getID() {
		
		return formatID();
	}
	
	public static String formatID() {
		
		return "F";
	}
	
	private boolean noEnemyNeighbors(Army army,State s) {
		
		Iterator<CountryInfo> neighbors = army.country.neighbors.iterator();
		
		if(!neighbors.hasNext())
			return false;
		
		while(neighbors.hasNext()){
			CountryInfo neighbor = neighbors.next();
			Army neighborArmy = s.armies.get(neighbor.code);	
			if(neighborArmy.owner != army.owner)
				return false;
			
		}
		
		return true;
	}
	
	private boolean noEnemyNeighbors(com.sillysoft.lux.Country country, com.sillysoft.lux.Board board) {
		com.sillysoft.lux.util.NeighborIterator neighbors = 
				new com.sillysoft.lux.util.NeighborIterator(country,board.getCountries());
		
		if(!neighbors.hasNext())
			return false;
		
		while(neighbors.hasNext()){
			com.sillysoft.lux.Country neighbor = neighbors.next();
			
			if(neighbor.getOwner() != country.getOwner())
				return false;
			
		}
		
		return true;
	}
	
	private Army NearestBorderSearch(Army army, State s) {
		
		LinkedList<Army> notVisted = new LinkedList<Army>();
		LinkedList<Army> visited = new LinkedList<Army>();
		HashMap<Army,Army> parent = new HashMap<Army,Army>();
		
		Army curArmy = null;
		notVisted.add(army);
		
		while(!notVisted.isEmpty()){
			curArmy = notVisted.removeFirst();
			visited.add(curArmy);
			
			for(CountryInfo country : curArmy.country.neighbors){
				Army neighborArmy = s.armies.get(country.code);
				
				if(neighborArmy.owner != s.players.currentPlayer)
					break;
				
				if(! visited.contains(neighborArmy)){
					notVisted.addLast(neighborArmy);
					
					//          child        parent
					parent.put(neighborArmy, curArmy);
				}
			}
		}
		
		while(parent.get(curArmy)!= army)
			curArmy = parent.get(curArmy);
			
		assert(army.country.neighbors.contains(curArmy.country));
		
		return curArmy;
	}
	
	private Country nearestBorderSearch(Country country, Board board) {
		LinkedList<Country> notVisted = new LinkedList<Country>();
		LinkedList<Country> visited = new LinkedList<Country>();
		HashMap<Country,Country> parent = new HashMap<Country,Country>();
		
		Country curArmy = null;
		notVisted.add(country);
		
		while(!notVisted.isEmpty()){
			curArmy = notVisted.removeFirst();
			visited.add(curArmy);
			
			for(Country neighborArmy : board.getCountries()){
				
				if(neighborArmy.getOwner() != country.getOwner())
					break;
				
				if(! visited.contains(neighborArmy)){
					notVisted.addLast(neighborArmy);
					
					//          child        parent
					parent.put(neighborArmy, curArmy);
				}
			}
		}
		
		while(parent.get(curArmy)!= country)
			curArmy = parent.get(curArmy);
		
		LinkedList cNeighbors = new LinkedList<Country>();
		cNeighbors.addAll(cNeighbors);
		
		assert(cNeighbors.contains(curArmy));
		
		return curArmy;
	}




}




















