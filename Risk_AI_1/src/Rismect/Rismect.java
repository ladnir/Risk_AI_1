package Rismect;
import com.sillysoft.lux.*;
import com.sillysoft.lux.agent.LuxAgent;
import com.sillysoft.lux.util.*;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Rismect implements LuxAgent{
	// This agent's ownerCode:
	protected int ID;
	int test;
	// We store some refs the board and to the country array
	protected Board board;
	protected com.sillysoft.lux.Country[] countries;
	JFrame jf ;
	JTextArea text;
	// It is useful to have a random number generator for a couple of things
	protected Random rand;

	public Rismect()
		{
		test =1;
		jf = new JFrame("Console");
		jf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		text = new JTextArea();
		jf.getContentPane().add(text,BorderLayout.CENTER);
		
		text.setText("text test!\n");
		text.setEditable(false);
		
		jf.pack();
		jf.setVisible(true);
		
		
		
		rand = new Random();
		}
	public void print(String message){
		
		text.append(message+"\n");
		test++;		
	}
	// Save references to 
	public void setPrefs( int newID, Board theboard )
		{
		ID = newID;		// this is how we distinguish what countries we own

		board = theboard;
		countries = board.getCountries();
		}

	public String name()
		{
		return "Rismect";
		}

	public float version()
		{
		return 0.1f;
		}

	public String description()
		{
		return "Rismect is the firsst iteration of my risk agent.";
		}

	
	// we will try to get some continents at the beginning,
	// by choosing countries in the smallest continents.
	public int pickCountry()
		{
		int goalCont = BoardHelper.getSmallestPositiveEmptyCont(countries, board);

		if (goalCont == -1) // oops, there are no unowned conts
			goalCont = BoardHelper.getSmallestPositiveOpenCont(countries, board);

		// So now pick a country in the desired continent
		return pickCountryInContinent(goalCont);
		}


	// Picks a country in <cont>, starting with	countries that have neighbors that we own.
	// If there are none of those then pick the country with the fewest neighbors total.
	protected int pickCountryInContinent(int cont)
		{
		// Cycle through the continent looking for unowned countries that have neighbors we own
		CountryIterator continent = new ContinentIterator(cont, countries);
		while (continent.hasNext())
			{
			com.sillysoft.lux.Country c = continent.next();
			if (c.getOwner() == -1 && c.getNumberPlayerNeighbors(ID) > 0)
				{
				// We found one, so pick it
				return c.getCode();
				}
			}

		// we neighbor none of them, so pick the open country with the fewest neighbors
		continent = new ContinentIterator(cont, countries);
		int bestCode = -1;
		int fewestNeib = 1000000;
		while (continent.hasNext())
			{
			com.sillysoft.lux.Country c = continent.next();
			if (c.getOwner() == -1 && c.getNumberNeighbors() < fewestNeib)
				{
				bestCode = c.getCode();
				fewestNeib = c.getNumberNeighbors();
				}
			}

		if (bestCode == -1)
			{
			// We should never get here, so print an alert if we do
			System.out.println("ERROR in Angry.pickCountryInContinent() -> there are no open countries");
			}
		
		return bestCode;
		}


	// Treat initial armies the same as normal armies
	public void placeInitialArmies( int numberOfArmies )
		{
		placeArmies( numberOfArmies );
		}

	// The game will automatically cash a random set of ours if we
	// return from this method and still have 5 or more cards.
	// For now just let that always happen
	public void cardsPhase( Card[] cards )
		{
		// xxagentxx Angry cards phase
		}

	// Angry's thought process when placing his armies is simple. 
	// He puts all of his armies where they can attack the most countries.
	// Thus we will cycle through all the countries that we own remembering 
	// the one with the most enemy countries beside it.

	// Cycle through all our countries placing <num> armies on each country that
	// has at least <numberOfArmies> armies
	public void placeArmies( int numberOfArmies )
		{
		int mostEnemies = -1;
		com.sillysoft.lux.Country placeOn = null;
		int subTotalEnemies = 0;
		CountryIterator neighbors = null;

		// Use a PlayerIterator to cycle through all the countries that we own.
		CountryIterator own = new PlayerIterator( ID, countries );
		while (own.hasNext()) 
			{
			com.sillysoft.lux.Country us = own.next();
			subTotalEnemies = us.getNumberEnemyNeighbors();

			// If it's the best so far store it
			if ( subTotalEnemies > mostEnemies )
				{
				mostEnemies = subTotalEnemies;
				placeOn = us;
				}
			}

		// So now placeOn is the country that we own with the most enemies.
		// Tell the board to place all of our armies there
		board.placeArmies( numberOfArmies, placeOn);
		}

	// During the attack phase, Angry has a clear goal:
    // Take over as much land as possible. 
	// Therefore he performs every available attack that he thinks he can win,
	// starting with the best matchups
	public void attackPhase(){
		
		// ----- STATIC Board Information ------- \\
		BoardInfo.cardProgression = board.getCardProgression(); 
		BoardInfo.continentIncrease =  board.getContinentIncrease();
		BoardInfo.continentsBonus = Factory.getContinentBonuses(board);
		BoardInfo.map = Factory.getBoardMap(board.getCountries());
		
		
		// ----- dynamic state information ------ \\
		Players players = new Players(board.getNumberOfPlayers(),ID, board.getCountries());
		HashMap<Integer, Army> armies = Factory.getArmies(players,board.getCountries());
		
		State state = Factory.newState(rand,armies, players,State.ATTACK_STAGE);
		
		Node root = new Node(state);
		
//------------------------------------------------------------------------------------------------//
		long start = System.currentTimeMillis();
		
		while( start > System.currentTimeMillis() - 5000 ){
			
			Node selectedNode = treePolicy(root);
			
			int winningPlayer = defaultPolicy(selectedNode.getClonedGameState());
			
			selectedNode.updateNodeRankings(winningPlayer);
			
		}
		
		ListIterator<Move> moveSet = root.getFinalMoves().listIterator();
		Move cur;
		
		while(moveSet.hasNext()){
			cur = moveSet.next();
			if(cur.us.owner == this.ID)
				switch(cur.getMoveType()){
				case Move.ATTACK : {
						assert(cur.allout);
						board.attack(cur.us.country.code, cur.attacking.country.code, cur.allout);
						
					break;
				}
				case Move.END_TURN :{
					
				
					return;
					
				}
				default: 
					assert(false);
				}
			else{
				assert(false);
			}
			
		}
	}
	
	private int defaultPolicy(State currentState) {
		
		while( ! currentState.isTerminal()){
		
			Move move = null;
			try {
				move = currentState.getRandomMove();
			} catch (Exception e) {

				text.append(e.getMessage());
				e.printStackTrace();
			}
			currentState.performMove(move);
			
		}
		
		return currentState.players.currentPlayer.id;
	}
	
	private Node treePolicy(Node current) {
		
		while(! current.isTerminal()){
			
			if(current.isFullyExpanded())
				current = current.getBestChild(.7);
			else 
				return current.expandNewChild();
		}
		
		return current;
	}
	
	// When deciding how many armies to move in after a successful attack, 
	// Angry will just put them all into the country with more enemies
	public int moveArmiesIn( int cca, int ccd)
		{
		int Aenemies = countries[cca].getNumberEnemyNeighbors();
		int Denemies = countries[ccd].getNumberEnemyNeighbors();

		// If the attacking country had more enemies, then we leave all possible 
		// armies in the country they attacked from (thus we move in 0):
		if ( Aenemies > Denemies )
			return 0;

		// Otherwise the defending country has more neighboring enemies, move in everyone:
		return countries[cca].getArmies()-1;
		}

	public void fortifyPhase()
		{	
		// Cycle through all the countries and find countries that we could move from:
		for (int i = 0; i < board.getNumberOfCountries(); i++)
			{
			if (countries[i].getOwner() == ID && countries[i].getMoveableArmies() > 0)
				{
				// This means we've found a country of ours that we can move from if we want to.

				// We determine the best country by counting the enemy neighbors it has.
				// The most enemy neighbors is where we move. Also, if there are 0 enemy 
				// neighbors where the armies are on now, we move to a random neighbor (in 
				// the hopes we'll find an enemy eventually).

				// To cycle through the neighbors we could use a NeighborIterator, 
				// but we can also directly use the country's AdjoingingList array.
				// Let's use the array...
				com.sillysoft.lux.Country[] neighbors = countries[i].getAdjoiningList();
				int countryCodeBestProspect = -1;
				int bestEnemyNeighbors = 0;
				int enemyNeighbors = 0;

				for (int j = 0; j < neighbors.length; j++)
					{
					if (neighbors[j].getOwner() == ID)
						{
						enemyNeighbors = neighbors[j].getNumberEnemyNeighbors();

						if ( enemyNeighbors > bestEnemyNeighbors )
							{
							// Then so far this is the best country to move to:
							countryCodeBestProspect = neighbors[j].getCode();
							bestEnemyNeighbors = enemyNeighbors;
							}
						}
					}
				// Now let's calculate the number of enemies of the country where the armies 
				// already are, to see if they should stay here:
				enemyNeighbors = countries[i].getNumberEnemyNeighbors();

				// If there's a better country to move to, move:
				if ( bestEnemyNeighbors > enemyNeighbors )
					{
					// Then the armies should move:
					// So now the country that had the best ratio should be moved to:
					board.fortifyArmies( countries[i].getMoveableArmies(), i, countryCodeBestProspect );
					}
				// If there are no good places to move to, move to a random place:
				else if ( enemyNeighbors == 0 )
					{
					// We choose an int from [0, neighbors.length]:
					int randCC = rand.nextInt(neighbors.length);
					board.fortifyArmies( countries[i].getMoveableArmies(), i, neighbors[randCC].getCode() );
					}
				}
			}
		}	// End of fortifyPhase() method


	// Oh boy. If this method ever gets called it is because we have won the game.
	// Send back something witty to tell the user.
	public String youWon()
		{ 
		// For variety we store a bunch of answers and pick one at random to return.
		String[] answers = new String[] {"Muh-Ha-Ha-Ha\n I win!" };

		return answers[ rand.nextInt(answers.length) ];
		}

	/** We get notified through this methos when certain things happen. Angry parses out all the different messages and does nothing with them. */
	public String message( String message, Object data )
		{
		if ("youLose".equals(message))
			{
			
			}
		else if ("attackNotice".equals(message))
			{
			
			}
		else if ("chat".equals(message))
			{
			
			}
		else if ("emote".equals(message))
			{
			
			}
		return null;
		}
	      
}	
