package Rismect;

import java.util.HashMap;
import java.util.Map.Entry;

import com.sillysoft.lux.Country;

public class Players {

	
	HashMap<Integer, Player> playerList;
	int playerCount;
	Player currentPlayer;
	
	public Players(int playerCount,int currentPlayerID, com.sillysoft.lux.Board board){
		
		Country[] countries = board.getCountries();
		this.playerList = new HashMap<Integer, Player>(playerCount);
		
		int ownerID;
		
		for(int i = 0; i <countries.length; i++){
			
			ownerID = countries[i].getOwner();
			
			if( ! playerList.containsKey(ownerID) ){
				Player newPlayer = new Player(ownerID, board.getPlayerCards(ownerID) );
				playerList.put(ownerID, newPlayer);
				playerCount++;
			}else{
				playerList.get(ownerID).countryCount++;
				
			}
			
		}
		
		this.generatePlayerOrder(currentPlayerID);
	}
	
	
	public Players(Players players) {
		playerList = new HashMap<Integer,Player>(players.playerList.size());
		
		for(Entry<Integer,Player> player : players.playerList.entrySet())			
			playerList.put(player.getKey(), new Player(player.getValue()));
		
		this.generatePlayerOrder(players.currentPlayer.id);
	}


	public Boolean progressToNextPlayer(){
		
		this.currentPlayer = this.currentPlayer.nextPlayer;
		
		return this.currentPlayer.id < this.currentPlayer.previousPlayer.id;
	}
	
	public void generatePlayerOrder(int currentPlayerID){
		
		currentPlayer = playerList.get(currentPlayerID);
		
		int links =0, i=0;
		Player first,cur,next;
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


	public void checkPlayersStatus() {
		
		
	}
	
	
}
