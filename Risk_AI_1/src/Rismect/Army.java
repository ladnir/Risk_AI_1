package Rismect;


public class Army {

	Player owner;
	int armyCount;
	int mobeableArmyCount;
	CountryInfo country;
	
	public Army(Player owner, int armyCount, CountryInfo country) {
		this.owner = owner;
		this.mobeableArmyCount = armyCount;
		this.armyCount = armyCount;
		this.country = country;
	}
}
