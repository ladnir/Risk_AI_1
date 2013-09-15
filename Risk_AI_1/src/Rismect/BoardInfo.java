package Rismect;

import java.util.HashMap;
import java.util.Map;

import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;

public class BoardInfo {
	
	Map<Integer, Integer> continentsBonus;
	int continentIncrease;
	String cardProgression;
	
	public BoardInfo(Map<Integer, Integer> continentsBounus,int continentIncrease, String cardProgression) {
	
		this.cardProgression = cardProgression;
		this.continentIncrease = continentIncrease;
		this.continentsBonus = continentsBounus;
	}

	public BoardInfo(Board board) {
		this.cardProgression = board.getCardProgression();
		this.continentIncrease = board.getContinentIncrease();
		
		this.continentsBonus = getContinentBonuses(board);
		

	}

	private Map<Integer, Integer> getContinentBonuses(Board board) {
		
		HashMap<Integer, Integer> bonuses = new HashMap<Integer, Integer>(board.getNumberOfContinents());		
			
		Country[] countries = board.getCountries();
		int continentID;
		
		for(int i = 0; i <countries.length; i++){
			continentID = countries[i].getContinent();
			
			if( ! continentsBonus.containsKey(continentID))
				continentsBonus.put(continentID, board.getContinentBonus(continentID));
		}
		
		return bonuses;
	}

	public int getCardValue(int round){
		switch(cardProgression){
			case "5, 5, 5..." :{
				return 5;
			}
			case "4, 4, 6, 6, 6, 8, 8, 8, 8, 10...":{
				int value = 2;
				for(int i = 2 ; round >=0 ; i++){
					value += 2;
					round -= i;
					
				}
				return value;
			}
			case "4, 5, 6...":{
				return 4 + round;
			}
			case "4, 6, 8...":{
				return 4 + 2*round;
			}
			case "3, 6, 9...":{
				return 3 + 3*round;
			}
			case "4, 6, 8, 10, 15, 20...":{
				if(round > 3)
					return 4 + 2*round;
				else
					return -5 + 5*(round );
			}
			case "5, 10, 15...":{
				return 5 + 5*round;
			}
			case "4, 6, 8, 10, 15, 20, 25, 10, 10, 10...":{
				if(round < 3)
					return 4 + 2*round;
				else if(round < 7)
					return -5 + 5*(round );
				else 
					return 10;
			}default:{
				
				assert(false);
				return -1;
			}
		}
		
	}
}
