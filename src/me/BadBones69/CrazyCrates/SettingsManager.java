package me.BadBones69.CrazyCrates;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {
	private SettingsManager(){ }
	static SettingsManager instance = new SettingsManager();
	public static SettingsManager getInstance() {
        return instance;
	}
	Plugin p;
	FileConfiguration config;
	File cfile;
	FileConfiguration data;
	File dfile;
	FileConfiguration locations;
	File locationsfile;
	File schem;
	File schemfolder;
	File cratefolder;
	boolean first = false;
	public void setup(Plugin p) {
		this.p=p;
        if (!p.getDataFolder().exists()){
        	p.getDataFolder().mkdir();
        }
        cratefolder = new File(p.getDataFolder()+"/Crates");
        if(!cratefolder.exists()){
        	cratefolder.mkdir();
        	try{
        		File basic = new File(p.getDataFolder()+"/Crates/", "Basic.yml");
         		File classic = new File(p.getDataFolder()+"/Crates/", "Classic.yml");
         		File crazy = new File(p.getDataFolder()+"/Crates/", "Crazy.yml");
         		InputStream B = getClass().getResourceAsStream("/Crates/Basic.yml");
         		InputStream Cl = getClass().getResourceAsStream("/Crates/Classic.yml");
         		InputStream Cr = getClass().getResourceAsStream("/Crates/Crazy.yml");
         		copyFile(B, basic);
         		copyFile(Cl, classic);
         		copyFile(Cr, crazy);
         	}catch (Exception e) {
         		e.printStackTrace();
         	}
        }
        cfile = new File(p.getDataFolder(), "config.yml");
        config = p.getConfig();
        dfile = new File(p.getDataFolder(), "data.yml");
        locationsfile = new File(p.getDataFolder(), "Locations.yml");
        locations = YamlConfiguration.loadConfiguration(locationsfile);
        if (!dfile.exists()) {
        	try {
        		dfile.createNewFile();
        	}
        	catch (IOException e) {
        		Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
        	}
        }
        data = YamlConfiguration.loadConfiguration(dfile);
        schemfolder = new File(p.getDataFolder()+"/Schematics/");
        if(!schemfolder.exists()){
        	schemfolder.mkdir();
        	ArrayList<String> schems = new ArrayList<String>();
        	schems.add("Classic");
        	schems.add("Nether");
        	schems.add("OutDoors");
        	schems.add("Sea");
        	schems.add("Soul");
        	schems.add("Wooden");
        	for(String sc : schems){
        		schem = new File(p.getDataFolder()+"/Schematics/", sc+".schematic");
        		InputStream f = getClass().getResourceAsStream("/Schematics/"+sc+".schematic");
            	try {
            		copyFile(f, schem);
            	}catch (Exception e) {
            		e.printStackTrace();
            	}
        	}
        }
    }
	public void saveAll(){
		try {config.save(cfile);}
        catch (IOException e){Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");}
		try {data.save(dfile);}
        catch (IOException e){Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");}
		for(File c : getAllCrates()){
			FileConfiguration crate = YamlConfiguration.loadConfiguration(c);
			try {crate.save(c);}
	        catch (IOException e){Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save "+c.getName()+"!");}
		}
	}
	public ArrayList<File> getAllCrates(){
		ArrayList<File> files = new ArrayList<File>();
		for(String name : cratefolder.list()){
			files.add(new File(cratefolder, name));
		}
		return files;
	}
	public ArrayList<String> getAllCratesNames(){
		ArrayList<String> files = new ArrayList<String>();
		for(String name : cratefolder.list()){
			File f = new File(cratefolder, name);
			files.add(f.getName().replaceAll(".yml", ""));
		}
		return files;
	}
	public static void copyFile(InputStream in, File out) throws Exception { // https://bukkit.org/threads/extracting-file-from-jar.16962/
        InputStream fis = in;
        FileOutputStream fos = new FileOutputStream(out);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }
	public FileConfiguration getData() {
        return data;
	}
	public FileConfiguration getLocations() {
		if(!locationsfile.exists()){
			try {
				locationsfile.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create the file Locations.yml!");
			}
		}
        return locations;
	}
	public void saveLocations() {
		try {
			locations.save(locationsfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Locations.yml!");
		}
	}
	public void reloadLocations() {
		locations = YamlConfiguration.loadConfiguration(locationsfile);
	}
	public FileConfiguration getFile(String crate){
		File file = new File(p.getDataFolder()+"/Crates/", crate+".yml");
		return YamlConfiguration.loadConfiguration(file);
	}
	public File getF(String file){
		File f = null;
		if(file.equalsIgnoreCase("Config"))f=new File(p.getDataFolder(), "config.yml");
		if(file.equalsIgnoreCase("Data"))f=new File(p.getDataFolder(), "data.yml");
		if(file.equalsIgnoreCase("Locations"))f=new File(p.getDataFolder(), "locations.yml");
		for(String crate : getAllCratesNames()){
			if(file.equalsIgnoreCase(crate))f=new File(p.getDataFolder()+"/Crates/", crate+".yml");
			break;
		}
		return f;
	}
	public void saveData() {
		try {
			data.save(dfile);
		}
		catch (IOException e) {
        	Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
		}
	}
	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dfile);
	}
	public FileConfiguration getConfig() {
		return config;
	}
	public void saveConfig() {
		try {
			config.save(cfile);
		}
        catch (IOException e) {
        	Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
        }
	}
	public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(cfile);
	}
	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}
	public void reloadAll(){
		config = YamlConfiguration.loadConfiguration(cfile);
		data = YamlConfiguration.loadConfiguration(dfile);
		for(File c : getAllCrates()){
			YamlConfiguration.loadConfiguration(c);
		}
	}
}
