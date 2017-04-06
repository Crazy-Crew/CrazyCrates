package me.badbones69.crazycrates.multisupport;

import org.bukkit.Bukkit;

public enum Version {
	
	TOO_OLD(-1),
	v1_8_R1(181), v1_8_R2(182), v1_8_R3(183),
	v1_9_R1(191), v1_9_R2(192),
	v1_10_R1(1101),
	v1_11_R1(1111),
	TOO_NEW(-2);
	
	Integer versionInteger;
	Version version;
	Version newest;
	
	private Version(Integer versionInteger){
		this.versionInteger = versionInteger;
	}
	
	/**
	 * 
	 * @return Get the server's minecraft version.
	 */
	public static Version getVersion(){
		String ver = Bukkit.getServer().getClass().getPackage().getName();
		ver = ver.substring(ver.lastIndexOf('.')+1);
		ver=ver.replaceAll("_", "").replaceAll("R", "").replaceAll("v", "");
		int version = Integer.parseInt(ver);
		if(version == Version.v1_11_R1.getVersionInteger())return Version.v1_11_R1;
		if(version == Version.v1_10_R1.getVersionInteger())return Version.v1_10_R1;
		if(version == Version.v1_9_R2.getVersionInteger())return Version.v1_9_R2;
		if(version == Version.v1_9_R1.getVersionInteger())return Version.v1_9_R1;
		if(version == Version.v1_8_R3.getVersionInteger())return Version.v1_8_R3;
		if(version == Version.v1_8_R2.getVersionInteger())return Version.v1_8_R2;
		if(version == Version.v1_8_R1.getVersionInteger())return Version.v1_8_R1;
		if(version > Version.v1_11_R1.getVersionInteger()) return Version.TOO_NEW;
		return Version.TOO_OLD;
	}
	
	/**
	 * 
	 * @return The server's minecraft version as an integer.
	 */
	public Integer getVersionInteger(){
		return this.versionInteger;
	}
	
}