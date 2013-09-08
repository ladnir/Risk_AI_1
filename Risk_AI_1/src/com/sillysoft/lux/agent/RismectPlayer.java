package com.sillysoft.lux.agent;

public class RismectPlayer {
	int cardCount;
	int id;
	int countryCount;
	RismectPlayer nextPlayer,previousPlayer;
	
	public RismectPlayer( int id,int cardCount) {
		super();
		this.cardCount = cardCount;
		this.id = id;
		this.countryCount = 1;
	}
	
	public boolean canCashCards(){
		
		if(cardCount>3) return true;
		
		return false;
	}
	
}
