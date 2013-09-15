package Rismect;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import com.sillysoft.lux.Board;
import com.sillysoft.lux.Card;
import com.sillysoft.lux.Country;

public class Node {
	
	private Node parent;
	private Move move;
	private State gameState;
	
	private LinkedList<Move> moveStack = null;
	private ListIterator<Move> iterator= null;
	
	private List<Node> children;
	
	private int visitCount;
	private double totalValue;
	
	// used to add a new node to the existing search tree
	public Node(Move move, State oldGameState,Node parent) {
		this.parent = parent;
		this.move = move;
		gameState = new State(oldGameState,move);
		moveStack = gameState.getAllMoves();
		iterator = moveStack.listIterator();
		
	}

	public Node(State gameState) {
		
		this.gameState= gameState;
	}

	public void updateNodeRankings(int winningPlayer) {
		
		Node current = this;
		
		while(current!=null){
			current.visitCount++;
			if(current.gameState.players.currentPlayer.id == winningPlayer)	
				current.totalValue++;
		}
		
	}

	public List<Move> getFinalMoves() {
		List<Move> finalMoveStack = new LinkedList<Move>();
		
		Node current = this;
		while(current.gameState.players.currentPlayer.id == this.gameState.players.currentPlayer.id ){
			finalMoveStack.add(current.move);
		}
		
		return finalMoveStack;
	}

	
	public boolean isFullyExpanded() {
		
		return ! this.iterator.hasNext();
	}

	public Node getBestChild(double d) {
		int maxIndex=0;
		double temp=0,max = Double.MIN_VALUE;
		
		for(int i =1; i < children.size(); i++){
			temp = ( children.get(i).totalValue/children.get(i).visitCount ) + 
					d * Math.sqrt( 2* Math.log(this.visitCount) / children.get(i).visitCount );
			
			if(max < temp){
				maxIndex = i;
				max = temp;
			}
		}
		
		return children.get(maxIndex);
	}

	
	public Node expandNewChild() {
		
		if(this.iterator.hasNext()){
			return new Node(iterator.next(),gameState,this);
		}
		return null;
	}

	public boolean isTerminal() {
		return this.gameState.isTerminal();
	}

	public State getClonedGameState() {
		return new State(this.gameState);
		
	}
	
	
	
}
