package me.badbones69.crazycrates.api;

public enum KeyType {
	
	PHYSICAL_KEY("Physical_Key"),
	VIRTUAL_KEY("Virtual_Key"),
	FREE("Free");
	
	private String Name;
	
	private KeyType(String name){
		Name = name;
	}
	
	public String getName(){
		return Name;
	}
	
}