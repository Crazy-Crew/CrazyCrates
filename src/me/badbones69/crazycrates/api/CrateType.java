package me.badbones69.crazycrates.api;

public enum CrateType {
	
	MENU("Menu"),
	COSMIC("Cosmic"),
	CRATE_ON_THE_GO("CrateOnTheGo"),
	CSGO("CSGO"),
	FIRE_CRACKER("FireCracker"),
	QUAD_CRATE("QuadCrate"),
	QUICK_CRATE("QuickCrate"),
	ROULETTE("Roulette"),
	WHEEL("Wheel"),
	WONDER("Wonder"),
	WAR("War");
	
	private String Name;
	
	private CrateType(String name){
		Name = name;
	}
	
	public String getName(){
		return Name;
	}
	
	public static CrateType getFromName(String name){
		for(CrateType crate : CrateType.values()){
			if(crate.getName().equalsIgnoreCase(name)){
				return crate;
			}
		}
		return null;
	}
	
}