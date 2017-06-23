package me.badbones69.crazycrates.multisupport;

import org.bukkit.Bukkit;

public enum Version {
	
	TOO_OLD(-1),
	v1_8_R1(181), v1_8_R2(182), v1_8_R3(183),
	v1_9_R1(191), v1_9_R2(192),
	v1_10_R1(1101),
	v1_11_R1(1111),
	v1_12_R1(1121),
	TOO_NEW(-2);
	
	public static Version currentVersion;
	
	private Integer versionInteger;
	
	private Version(int versionInteger){
		this.versionInteger = versionInteger;
	}
	
	/**
	 * 
	 * @return Get the server's Minecraft version.
	 */
	public static Version getVersion(){
		if(currentVersion == null){
			String ver = Bukkit.getServer().getClass().getPackage().getName();
			int v = Integer.parseInt(ver
					.substring(ver.lastIndexOf('.') + 1)
					.replaceAll("_", "")
					.replaceAll("R", "")
					.replaceAll("v", ""));
			for(Version version : values()){
				if(version.getVersionInteger() == v){
					currentVersion = version;
					break;
				}
			}
		}
		return currentVersion;
	}
	
	/**
	 * 
	 * @return The server's minecraft version as an integer.
	 */
	public Integer getVersionInteger(){
		return this.versionInteger;
	}
	
	/**
	 * This checks if the current version is older, newer, or is the checked version.
	 * @param version The version you are checking.
	 * @return -1 if older, 0 if the same, and 1 if newer.
	 */
	public Integer comparedTo(Version version){
		int resault = -1;
		int current = this.getVersionInteger();
		int check = version.getVersionInteger();
		if(current > check){
			resault = 1;
		}else if(current == check){
			resault = 0;
		}else if(current < check){
			resault = -1;
		}
		return resault;
	}
	
}