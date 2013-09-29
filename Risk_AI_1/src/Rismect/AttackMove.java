package Rismect;

import com.sillysoft.lux.Board;

public class AttackMove implements Move {

	CountryInfo attacker,defender;
	
	public AttackMove(CountryInfo attacker,CountryInfo defender){
		this.attacker = attacker;
		this.defender = defender;
	}
	
	public void PerformMove(State s) {
		assert(s.stage !=State.FORTIFY_STAGE);
		if(s.stage== State.SUPPLY_STAGE)
			s.stage = State.ATTACK_STAGE;
		
		Army attArmy = s.armies.get(attacker.code);
		Army defArmy = s.armies.get(defender.code);
		
		assert(attArmy.owner != defArmy.owner);
		
		staticAttack(attArmy,defArmy);
		
		if(defArmy.armyCount ==0){
			
			if(defArmy.owner.countryCount == 1){
				attArmy.owner.cardCount += defArmy.owner.cardCount;
				if(attArmy.owner.cardCount>5)
					s.stage = State.FORCE_CARD_CASH_STAGE;
				
			}
			defArmy.owner = attArmy.owner;
			defArmy.armyCount = attArmy.armyCount -1;
			attArmy.armyCount =1;
			
		}
	}

	public void PerformMove(Board board, int playerID) {
		board.attack(attacker.code, defender.code, true);
	}

	public String getID() {
		return formatID(this.attacker,this.defender);
	}

	public static String formatID(CountryInfo attacker, CountryInfo defender) {
		
		return "A"+attacker.code+",d"+defender.code;
	}
	
	private void staticAttack(Army attArmy, Army defArmy) {
		double  attackerArmy=attArmy.armyCount,
				defenderArmy=defArmy.armyCount;
		
		double[][] attackerValues = new double[][]
				{// 	1d				2d
					{ 0.5483922,  0.7134150}, //2a
					{ 0.3838224,  1.1244847}, //3a
					{ 0.2994903,  0.8244588}  //4a
				
				},
			defenderValues = new double[][]
				{// 	1d				2d
					{ 0.45160780,  0.2865850}, //2a
					{ 0.61617764,  0.8755153}, //3a
					{ 0.70050970,  1.1755412}  //4a
			
				};
		
		int attackerEffectiveArmy,defenderEffectiveArmy;
		
		while(attackerArmy >=2 && defenderArmy >=1 ){
			
			attackerEffectiveArmy = (int) Math.min(4, attackerArmy)-1;
			defenderEffectiveArmy = (int) Math.min(2, defenderArmy);
			
			attackerArmy-= attackerValues[attackerEffectiveArmy - 1][defenderEffectiveArmy-1];
			defenderArmy-= defenderValues[attackerEffectiveArmy - 1][defenderEffectiveArmy-1];
			
		
		}
		
		if(attackerArmy < 1 && defenderArmy < 1){
			if(attackerArmy > defenderArmy)
				attackerArmy ++;
			else 
				defenderArmy ++;
		}
		
		attArmy.armyCount = (int) attackerArmy;
		defArmy.armyCount = (int) defenderArmy;
		
		
	}



//	private void diceAttack( Country attacker, Country defender) {
//		
//		
//		
//		PriorityQueue<Integer> attackerDice = new PriorityQueue<Integer>(),
//							   defenderDice = new PriorityQueue<Integer>();
//
//		// get attackers dice (1 to 3 dice) 
//		for(int i = Math.min(3, attacker.armyCount-1) ; i>0 ; i--){
//			attackerDice.add(rand.nextInt() % 6 +1);
//		}
//		
//		// get defenders dice ( 0 to 2 dice)
//		for(int i = Math.min(2, defender.armyCount) ; i>0 ; i--){
//			defenderDice.add(rand.nextInt() % 6 +1);
//		}
//		
//		//compare top 1 or 2 dice
//		for(int i = Math.min(attackerDice.size(), defenderDice.size()); i >0; i-- ){
//			if(attackerDice.remove() > defenderDice.remove())
//				defender.armyCount--;
//			else
//				attacker.armyCount--;
//		}
//		
//	}
}
