package com.sillysoft.lux.agent;

import java.util.HashMap;
import java.util.Map;

public class RismectPlayers {

	Map<Integer, RismectPlayer> playerList;
	int playerCount;
	RismectPlayer currentPlayer;
	
	public RismectPlayers(){
		
		this.playerList = new HashMap<Integer, RismectPlayer>();
	}
	
	
	public void progressToNextPlayer(){
		
		this.currentPlayer = this.currentPlayer.nextPlayer;
	}

	public void checkPlayerStatus(int playerID) {
		
		RismectPlayer player = this.playerList.get(playerID);
		
		if(player.countryCount<1){
			player.previousPlayer.nextPlayer = player.nextPlayer;
			player.nextPlayer.previousPlayer = player.previousPlayer;
			
			player.nextPlayer= null;
			player.previousPlayer = null;
			
			playerList.remove(playerID);
			
			playerCount --;
		}
		
		
	}
	
	public void generatePlayerOrder(int currentPlayerID){
		
		currentPlayer = playerList.get(currentPlayerID);
		
		int links =0, i=0;
		RismectPlayer first,cur,next;
		while((first = playerList.get(i))== null)i++;
		
		cur = first;
		
		for(; links != playerCount ; i++ ){
			while((next = playerList.get(i))== null)i++;
			
			cur.nextPlayer = next;
			next.previousPlayer = cur;
			
			cur= next;
			
			links++;
		}
		
		cur.nextPlayer = first;
		first.previousPlayer = cur;
		
	}
	
	
}
