package Rismect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;


public class SupplyMove implements Move{
	
	int reinforcements;
	
	public SupplyMove(int income) {
		reinforcements= income;
	}

	@Override
	public void PerformMove(State s) {
		assert(s.stage == State.SUPPLY_STAGE);
		
		HashMap<Integer,Double> BSR = new HashMap<Integer,Double>();
		int BSR_sum =0;
		
		double enemyCount;
		Set<Entry<Integer, Army>> armies = s.armies.entrySet();
		for(Entry<Integer,Army> entry : armies ){
			Army army = entry.getValue();
			
			if(army.owner == s.players.currentPlayer){
				
				enemyCount=0;
				Iterator<CountryInfo> neighbors = BoardInfo.map.get(army.country.code).neighbors.iterator();
				
				while(neighbors.hasNext()){
					CountryInfo neighbor = neighbors.next();
					Army neighborArmy 	= 	s.armies.get(neighbor.code);
					
					if(neighborArmy.owner != s.players.currentPlayer){
						enemyCount += neighborArmy.armyCount;
						
					}
				}
				
				if(enemyCount!=0){
					enemyCount = enemyCount/army.armyCount;
					BSR_sum += enemyCount;
					BSR.put(army.country.code, enemyCount);
				}
			}
		}
		
		for(Entry<Integer,Army> entry : armies ){
			Army army = entry.getValue();
			
			if(army.owner == s.players.currentPlayer){
				Double countrysSupply = reinforcements * BSR.get(army.country.code) /BSR_sum;
				army.armyCount += Math.round(countrysSupply);
			}
			
		}
		
		s.stage= State.ATTACK_STAGE;
	}

	@Override
	public void PerformMove(com.sillysoft.lux.Board board, int playerID) {
		HashMap<Integer,Double> BSR = new HashMap<Integer,Double>();
		int BSR_sum =0;
		
		double enemyCount;
		com.sillysoft.lux.Country[] armies = board.getCountries();
		for(com.sillysoft.lux.Country army : armies ){
			
			if(army.getOwner() == playerID){
				
				enemyCount=0;
				com.sillysoft.lux.util.NeighborIterator neighbors = 
						new com.sillysoft.lux.util.NeighborIterator(army, armies);
				 
				while(neighbors.hasNext()){
					com.sillysoft.lux.Country neighbor = neighbors.next();
					
					if(neighbor.getOwner() != playerID){
						enemyCount += neighbor.getArmies();
						
					}
				}
				
				if(enemyCount!=0){
					enemyCount = enemyCount/army.getArmies();
					BSR_sum += enemyCount;
					BSR.put(army.getCode(), enemyCount);
				}
			}
		}
		
		for(com.sillysoft.lux.Country army : armies){
			
			if(army.getOwner() == playerID){
				Double countrysSupply = reinforcements * BSR.get(army.getCode()) /BSR_sum;
				board.placeArmies((int)Math.round(countrysSupply), army.getCode());
			}
			
		}
		
	}

	@Override
	public String getID() {
		
		return formatID(this.reinforcements);
	}
	
	public static String formatID(int income){
		
		return "R"+ income;
	}
	

}
