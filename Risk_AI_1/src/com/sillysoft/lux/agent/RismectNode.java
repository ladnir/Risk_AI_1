package com.sillysoft.lux.agent;

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

public class RismectNode {
	
	private RismectNode parent;
	private RismectMove move;
	private RismectState gameState;
	
	private LinkedList<RismectMove> moveStack = null;
	private ListIterator<RismectMove> iterator= null;
	
	private List<RismectNode> children;
	
	private int visitCount;
	private double totalValue;
	
	// used to add a new node to the existing search tree
	public RismectNode(RismectMove move, RismectState oldGameState,RismectNode parent) {
		this.parent = parent;
		this.move = move;
		gameState = new RismectState(oldGameState,move);
		moveStack = gameState.getAllMoves();
		iterator = moveStack.listIterator();
		
	}

	public RismectNode(int id, Board board) {
		
		
		
		gameState= new RismectState(id, board);
	}

	public void updateNodeRankings(int winningPlayer) {
		
		RismectNode current = this;
		
		while(current!=null){
			current.visitCount++;
			if(current.gameState.players.currentPlayer.id == winningPlayer)	
				current.totalValue++;
		}
		
	}

	public List<RismectMove> getFinalMoves() {
		List<RismectMove> finalMoveStack = new LinkedList<RismectMove>();
		
		RismectNode current = this;
		while(current.gameState.players.currentPlayer.id == this.gameState.players.currentPlayer.id ){
			finalMoveStack.add(current.move);
		}
		
		return finalMoveStack;
	}

	
	public boolean isFullyExpanded() {
		
		return ! this.iterator.hasNext();
	}

	public RismectNode getBestChild(double d) {
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

	
	public RismectNode expandNewChild() {
		
		if(this.iterator.hasNext()){
			return new RismectNode(iterator.next(),gameState,this);
		}
		return null;
	}

	public boolean isTerminal() {
		return this.gameState.isTerminal();
	}

	public RismectState getClonedGameState() {
		return new RismectState(this.gameState);
		
	}
	
	
	
}
