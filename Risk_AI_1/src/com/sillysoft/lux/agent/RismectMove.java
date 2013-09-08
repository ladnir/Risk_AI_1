package com.sillysoft.lux.agent;

public class RismectMove {
	
	public static final int ATTACK   = 0 ,
							END_TURN = 1;
	
	RismectCountry us;
	RismectCountry attacking;
	boolean allout;
	int player;
	private int moveType;
	public RismectMove(RismectCountry us,RismectCountry attacking,boolean allout){
		
		this.player=us.player;
		this.us=us;
		this.attacking=attacking;
		this.allout=allout;	
		this.moveType = ATTACK;
		
	}
	public RismectMove(int currentPlayer, int moveType) {
		this.moveType = moveType;
	}
	public static RismectMove END_TURN(int currentPlayer) {
		
		return null;
	}
	public int getMoveType() {
		
		return moveType;
	}
	
	
}
