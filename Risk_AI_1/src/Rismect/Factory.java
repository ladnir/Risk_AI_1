package Rismect;

import java.util.HashMap;


public class Factory {
	
	private static HashMap<String,Move> moveCash = new HashMap<String,Move>();
	
	public static HashMap<Integer, Integer> getContinentBonuses(com.sillysoft.lux.Board board) {
		
		HashMap<Integer, Integer> bonuses = new HashMap<Integer, Integer>(50);		
			
		int continentID;
		
		for(int i = 0; i <board.getCountries().length; i++){
			continentID = board.getCountries()[i].getContinent();
			
			if( ! bonuses.containsKey(continentID))
				bonuses.put(continentID, board.getContinentBonus(continentID));
		}
		
		return bonuses;
	}

	public static HashMap<Integer, CountryInfo> getBoardMap( com.sillysoft.lux.Country[] countries) {
		
		HashMap<Integer, CountryInfo> map = new HashMap<Integer, CountryInfo>(countries.length);
		
		for(int i = 0 ; i<countries.length ; i++){
			
			map.put(countries[i].getCode(), Factory.getCountryInfo(countries[i]) );
			
			for(int j = i-1 ; j>=0;j--){
				if(countries[i].isNextTo(countries[j])){
					Connect(map.get(countries[i].getCode()),map.get(countries[j].getCode()) );
					
				}
			}
		}
		return map;
	}

	private static void Connect(CountryInfo ci1, CountryInfo ci2) {
		if(! ci1.neighbors.contains(ci2))
			ci1.neighbors.add(ci2);
			
		if(! ci2.neighbors.contains(ci1))
			ci2.neighbors.add(ci1);
		
	}

	private static CountryInfo getCountryInfo(com.sillysoft.lux.Country country) {
		
		return new CountryInfo(country.getContinent(), country.getCode());
	}

	public static HashMap<Integer, Army> getArmies(Players players, com.sillysoft.lux.Country[] countries) {
		
		HashMap<Integer, Army> armies = new HashMap<Integer, Army>(countries.length);
		
		for(int i = 0; i<countries.length;i++){
			
			int code = countries[i].getCode();
			
			Player      owner     = players.playerList.get(countries[i].getOwner());
			int         armyCount = countries[i].getArmies();
			CountryInfo country   = BoardInfo.map.get(code);
			
			armies.put(code, new Army(owner, armyCount, country));
			
		}
		
		
		return armies;
	}

	
	public static Move getSupplyMove(int income) {
		String key =SupplyMove.formatID(income);
		
		if(moveCash.containsKey(key))
			return moveCash.get(key);
		
		Move move = new SupplyMove(income);
		
		moveCash.put(move.getID(), move);
		
		return move;
	}

	public static Move getAttackMove(CountryInfo attacker, CountryInfo defender) {
		String key = AttackMove.formatID(attacker,defender);
		
		if(moveCash.containsKey(key))
			return moveCash.get(key);
		
		Move move = new AttackMove(attacker,defender);
		
		moveCash.put(move.getID(), move);
		
		return move;
	}

	public static Move getFortifyMove() {
		String key = FortifyMove.formatID();
		
		if(moveCash.containsKey(key))
			return moveCash.get(key);
		
		Move move = new FortifyMove();
		
		moveCash.put(move.getID(), move);
		
		return move;
	}


}
