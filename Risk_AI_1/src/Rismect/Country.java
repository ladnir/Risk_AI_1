package Rismect;

import java.util.ListIterator;

public class Country {

	int player;
	int armyCount;
	CountryInfo info;
	

	public void copy(lux.Country country) {
		player = country.getOwner();
		armyCount = country.getArmies();
		info.continent = country.getContinent();
		info.code = country.getCode();
	}

	public void copy(Country country) {
		player = country.player;
		armyCount = country.armyCount;
		this.info = country.info;
	}
	

	public void connect(Country neighbor) {
		
			neighbors.add(neighbor);
			neighbor.neighbors.add(this);
		
	}

	public boolean isNextTo(Country country) {
		ListIterator<Country> iterator = enimies.listIterator();
		while(iterator.hasNext())
			if(iterator.next().code == country.code) return true;
		
		iterator = neighbors.listIterator();
		while(iterator.hasNext())
			if(iterator.next().code == country.code) return true;
		
		return false;
	}



	
	
}
