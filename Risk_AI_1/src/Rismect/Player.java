package Rismect;

public class Player {
	int cardCount;
	int id;
	int countryCount;
	Player nextPlayer,previousPlayer;
	
	public Player( int id,int cardCount) {
		super();
		this.cardCount = cardCount;
		this.id = id;
		this.countryCount = 1;
	}
	
	public Player(Player value) {
		this.cardCount = value.cardCount;
		this.id= value.id;
		this.countryCount = value.countryCount;
		
	}

	public boolean canCashCards(){
		
		if(cardCount>3) return true;
		
		return false;
	}
	
}
