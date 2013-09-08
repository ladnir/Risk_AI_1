package com.sillysoft.lux.agent;

import java.util.LinkedList;
import java.util.ListIterator;

import com.sillysoft.lux.Country;

public class RismectCountry {

	int player;
	int armyCount;
	int continent;
	int code;
	LinkedList<RismectCountry> enimies,neighbors;
	

	public void copy(Country country) {
		player = country.getOwner();
		armyCount = country.getArmies();
		continent = country.getContinent();
		code = country.getCode();
	}

	public void copy(RismectCountry country) {
		player = country.player;
		armyCount = country.armyCount;
		continent = country.continent;
		code = country.code;
	}
	

	public void connect(RismectCountry neighbor) {
		
		if(player == neighbor.player){
			neighbors.add(neighbor);
			neighbor.neighbors.add(this);
		}
		else {
			enimies.add(neighbor);
			neighbor.enimies.add(this);
		}
	}

	public boolean isNextTo(RismectCountry country) {
		ListIterator<RismectCountry> iterator = enimies.listIterator();
		while(iterator.hasNext())
			if(iterator.next().code == country.code) return true;
		
		iterator = neighbors.listIterator();
		while(iterator.hasNext())
			if(iterator.next().code == country.code) return true;
		
		return false;
	}



	
	
}
