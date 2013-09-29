package Rismect;

import java.util.LinkedList;

public class CountryInfo {
	int continent;
	int code;
	LinkedList<CountryInfo> neighbors;

	public CountryInfo(int continent, int code) {
		this.continent = continent;
		this.code = code;
	}
}
