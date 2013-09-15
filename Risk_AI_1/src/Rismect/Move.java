package Rismect;

public class Move {
	
	public static final int ATTACK   = 0 ,
							END_TURN = 1;
	
	Country us;
	Country attacking;
	boolean allout;
	int player;
	private int moveType;
	public Move(Country us,Country attacking,boolean allout){
		
		this.player=us.player;
		this.us=us;
		this.attacking=attacking;
		this.allout=allout;	
		this.moveType = ATTACK;
		
	}
	public Move(int currentPlayer, int moveType) {
		this.moveType = moveType;
	}
	public static Move END_TURN(int currentPlayer) {
		
		return null;
	}
	public int getMoveType() {
		
		return moveType;
	}
	
	
}
