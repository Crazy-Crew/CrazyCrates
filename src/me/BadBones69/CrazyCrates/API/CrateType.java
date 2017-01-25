package me.BadBones69.CrazyCrates.API;

public enum CrateType {
	
	MENU("Menu"),
	COSMIC("Cosmic"), 
	CRATE_ON_THE_GO("Crate_On_The_Go"), 
	CSGO("CSGO"), 
	FIRE_CRACKER("Fire_Cracker"),
	QUAD_CRATE("Quad_Crate"),
	QUICK_CRATE("Quick_Crate"), 
	ROULETTE("Roulette"), 
	WHEEL("Wheel"), 
	WONDER("Wonder");
	
	String Name;
	
	private CrateType(String name){
		Name=name;
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