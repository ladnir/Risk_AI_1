package Rismect;

public interface Move {
	
	
	public void PerformMove(State s);

	public void PerformMove(com.sillysoft.lux.Board board, int playerID);
	
	public String getID();
	
}
