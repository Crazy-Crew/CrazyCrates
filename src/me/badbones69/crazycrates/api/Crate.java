package me.badbones69.crazycrates.api;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class Crate {
	
	private String name;
	private ItemStack key;
	private CrateType crateType;
	private FileConfiguration file;
	private ArrayList<Prize> prizes;
	
	/**
	 * 
	 * @param name The name of the crate.
	 * @param crateType The crate type of the crate.
	 * @param key The key as an item stack.
	 * @param prizes The prizes that can be won.
	 * @param file The crate file.
	 */
	public Crate(String name, CrateType crateType, ItemStack key, ArrayList<Prize> prizes, FileConfiguration file){
		this.key = key;
		this.file = file;
		this.name = name;
		this.prizes = prizes;
		this.crateType = crateType;
	}
	
	/**
	 * 
	 * @return name The name of the crate.
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * 
	 * @return The crate type of the crate.
	 */
	public CrateType getCrateType(){
		return this.crateType;
	}
	
	/**
	 * 
	 * @return The key as an item stack.
	 */
	public ItemStack getKey(){
		return this.key;
	}
	
	/**
	 * 
	 * @return The crates file.
	 */
	public FileConfiguration getFile(){
		return this.file;
	}
	
	/**
	 * 
	 * @return The prizes in the crate.
	 */
	public ArrayList<Prize> getPrizes(){
		return this.prizes;
	}
	
	/**
	 * 
	 * @param name Name of the prize you want.
	 * @return The prize you asked for.
	 */
	public Prize getPrize(String name){
		for(Prize prize : prizes){
			if(prize.getName().equalsIgnoreCase(name)){
				return prize;
			}
		}
		return null;
	}
	
}