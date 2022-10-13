package me.badbones69.crazycrates.multisupport.nms.v1_18_R2;

import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * All you need to handle the Mojang structure format
 * @author Michel_0
 * @version 2.4
 */
public class StructureService {

    public static Random random = new Random();

    /**
     * A comfort method for all lazy guys. Automatically switches to structure arrays, when using an area larger than 32x32x32
     * @param corners - 2 opposite edges (order doesn't matter)
     * @param destination - The destination file (for single structure) or the destination folder (for structure array)
     */
    public static void createAndSaveAny(Location[] corners, File destination) throws IOException {
        createAndSaveAny(corners, "?", destination);
    }

    /**
     * A comfort method for all lazy guys. Automatically switches to structure arrays, when using an area larger than 32x32x32
     * @param corners - 2 opposite edges (order doesn't matter)
     * @param author - The listed author
     * @param destination - The destination file (for single structure) or the destination folder (for structure array)
     */
    public static void createAndSaveAny(Location[] corners, String author, File destination) throws IOException {
        Location[] normalized = normalizeEdges(corners[0], corners[1]);
        int[] dimensions = getDimensions(normalized);
        if (dimensions[0] > 32 || dimensions[1] > 32 || dimensions[2] > 32) {
            DefinedStructure[] structures = createStructuresArray(normalized, author);
            saveStructuresArray(structures, destination);
            saveAreaDimFile(dimensions, destination);
        } else {
            DefinedStructure structure = createSingleStructure(normalized, author);
            saveSingleStructure(structure, destination);
        }
    }

    /**
     * Creates a single structure of maximum 32x32x32 blocks. If you need a larger area, use {@link #createStructuresArray(Location[], String)}
     * @param corners - The edges of the area (order doesn't matter)
     * @return DefinedStructure - The new structure instance
     */
    public static DefinedStructure createSingleStructure(Location[] corners) {
        return createSingleStructure(corners, "?");
    }

    /**
     * Creates a single structure of maximum 32x32x32 blocks. If you need a larger area, use {@link #createStructuresArray(Location[], String)}
     * @param corners - The edges of the area (order doesn't matter)
     * @param author - The listed author of the structure
     * @return DefinedStructure - The new structure instance
     */
    public static DefinedStructure createSingleStructure(Location[] corners, String author) {
        if (corners.length != 2) throw new IllegalArgumentException("An area needs to be set up by exactly 2 opposite edges!");
        Location[] normalized = normalizeEdges(corners[0], corners[1]);
        WorldServer world = ((CraftWorld) normalized[0].getWorld()).getHandle();
        int[] dimensions = getDimensions(normalized);
        if (dimensions[0] > 32 || dimensions[1] > 32 || dimensions[2] > 32) throw new IllegalArgumentException("A single structure can only be 32x32x32! If you need more, use #createStructuresArea.");
        DefinedStructure structure = new DefinedStructure();
        structure.a(world, new BlockPosition(normalized[0].getBlockX(), normalized[0].getBlockY(), normalized[0].getBlockZ()), new BlockPosition(dimensions[0], dimensions[1], dimensions[2]), true, Blocks.jb);
        structure.a(author);
        return structure;
    }

    /**
     * Saves a structure NBT file to a given destination file
     * @param structure - The structure to be saved
     * @param destination - The NBT file to be created
     */
    public static void saveSingleStructure(DefinedStructure structure, File destination) throws IOException {
        NBTTagCompound fileTag = new NBTTagCompound();
        fileTag = structure.a(fileTag);
        if (structure.b() != null && !structure.b().equals("?")) fileTag.a("author", structure.b());
        NBTCompressedStreamTools.a(fileTag, new FileOutputStream(destination + ".nbt"));
    }

    /**
     * Splits up an area in 32x32x32 structures, creates those and fills an array with them
     * @param corners - 2 Edges of the area (order doesn't matter)
     * @return DefinedStructure[] - The structures in a one dimensional array, sorted by y, z, x (iterates along x, then z, then y)
     */
    public static DefinedStructure[] createStructuresArray(Location[] corners) {
        return createStructuresArray(corners, "?");
    }

    /**
     * Splits up an area in 32x32x32 structures, creates those and fills an array with them
     * @param corners - 2 Edges of the area (order doesn't matter)
     * @param author - The listed author of the structure
     * @return DefinedStructure[] - The structures in a one dimensional array, sorted by y, z, x (iterates along x, then z, then y)
     */
    public static DefinedStructure[] createStructuresArray(Location[] corners, String author) {
        if (corners.length != 2) throw new IllegalArgumentException("An area needs to be set up by exactly 2 opposite edges!");
        Location[] normalized = normalizeEdges(corners[0], corners[1]);
        WorldServer world = ((CraftWorld) normalized[0].getWorld()).getHandle();
        int[] dimensions = getDimensions(normalized);
        int[] areas = getAreaSections(dimensions);
        DefinedStructure[] structures = new DefinedStructure[areas[0] * areas[1] * areas[2]];
        for (int x = 0; x < areas[0]; x++) {
            for (int y = 0; y < areas[1]; y++) {
                for (int z = 0; z < areas[2]; z++) {
                    DefinedStructure structure = new DefinedStructure();
                    int width, height, length;
                    if (x == areas[0] - 1 && dimensions[0] % 32 != 0) width = dimensions[0] % 32;
                    else width = 32;
                    if (y == areas[1] - 1 && dimensions[1] % 32 != 0) height = dimensions[1] % 32;
                    else height = 32;
                    if (z == areas[2] - 1 && dimensions[2] % 32 != 0) length = dimensions[2] % 32;
                    else length = 32;
                    structure.a(world, new BlockPosition((x * 32) + normalized[0].getBlockX(), (y * 32) + normalized[0].getBlockY(), (z * 32) + normalized[0].getBlockZ()), new BlockPosition(width, height, length), true, Blocks.jb);
                    structure.a(author);
                    structures[getYzxIndex(x, y, z, areas[0], areas[2])] = structure;
                }
            }
        }
        return structures;
    }

    /**
     * Saves a one dimensional array of structures to files within a given folder. For importing, the file structure is important, don't change it.
     * @param structures - The structures array
     * @param folder - The folder, which will directly be filled
     */
    public static void saveStructuresArray(DefinedStructure[] structures, File folder) throws IOException {
        if (!folder.exists()) folder.mkdirs();
        for (int i = 0; i < structures.length; i++) {
            NBTTagCompound fileTag = new NBTTagCompound();
            fileTag = structures[i].a(fileTag);
            if (structures[i].b() != null && !structures[i].b().equals("?")) fileTag.a("author", structures[i].b());
            NBTCompressedStreamTools.a(fileTag, new FileOutputStream(new File(folder, folder.getName() + "_" + i + ".nbt")));
        }
    }

    /**
     * A comfort method for all lazy guys. Automatically switches to structure arrays, when the source is a folder, no file
     * @param source - The structure array folder or the structure NBT file
     * @param startEdge - The starting corner for pasting (lowest x, y, z coordinates)
     * @param rotation - You may rotate the structure by 90 degrees steps
     */
    public static void loadAndInsertAny(File source, Location startEdge, Rotation rotation) throws IOException, SecurityException, IllegalArgumentException {
        if (source.isDirectory()) {
            DefinedStructure[] structures = loadStructuresArray(source);
            insertStructuresArray(structures, loadAreaDimFile(source), startEdge, rotation.getNMSRot());
        } else {
            DefinedStructure structure = loadSingleStructure(source);
            insertSingleStructure(structure, startEdge, rotation.getNMSRot());
        }
    }

    /**
     * A comfort method for all lazy guys. Automatically switches to structure arrays, when the source is a folder, no file
     * @param source - The structure array folder or the structure NBT file
     * @param startEdge - The starting corner for pasting (lowest x, y, z coordinates)
     */
    public static void loadAndInsertAny(File source, Location startEdge) throws IOException, SecurityException, IllegalArgumentException {
        loadAndInsertAny(source, startEdge, Rotation.DEG_0);
    }

    /*
     * Loads a single structure NBT file and creates a new structure object instance. Also converts pre1.13 versions.
     * param source - The structure file
     * param world - The world (actually ANY world) instance to receive a data fixer (legacy converter)
     * return DefinedStructure - The new instance
     * deprecated Only for pre1.13, uses the NMS 1.13 DataFixer to convert stuff
     *
     */
    public static DefinedStructure loadLegacySingleStructure(File source, World world) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method parseAndConvert = DefinedStructureManager.class.getDeclaredMethod("a", InputStream.class);
        parseAndConvert.setAccessible(true);
        return (DefinedStructure) parseAndConvert.invoke(((CraftWorld) world).getHandle().r(), new FileInputStream(source));
    }

    /**
     * Loads a single structure NBT file and creates a new structure object instance. Works only for structures created with 1.13 or later!
     * @param source - The structure file
     * @return DefinedStructure - The new instance
     */
    public static DefinedStructure loadSingleStructure(File source) throws IOException {
        DefinedStructure structure = new DefinedStructure();
        structure.b(NBTCompressedStreamTools.a(new FileInputStream(source)));
        return structure;
    }

    /**
     * Pastes a single structure into the world
     * @param structure - The structure to be pasted
     * @param startEdge - The starting corner with the lowest x, y, z coordinates
     * @param rotation - You may rotate the structure by 90 degrees steps
     */
    public static void insertSingleStructure(Object structure, Location startEdge, Rotation rotation) {
        insertSingleStructure((DefinedStructure) structure, startEdge, rotation.getNMSRot());
    }

    /**
     * Pastes a single structure into the world
     * @param structure - The structure to be pasted
     * @param startEdge - The starting corner with the lowest x, y, z coordinates
     * @param rotation - You may rotate the structure by 90 degrees steps
     */
    public static void insertSingleStructure(DefinedStructure structure, Location startEdge, EnumBlockRotation rotation) {
        WorldServer world = ((CraftWorld) startEdge.getWorld()).getHandle();
        DefinedStructureInfo structInfo = new DefinedStructureInfo().a(EnumBlockMirror.a).a(rotation).a(false).a().c(false).a(new Random());
        BlockPosition blockPosition = new BlockPosition(startEdge.getBlockX(), startEdge.getBlockY(), startEdge.getBlockZ());
        structure.a(world, blockPosition, blockPosition, structInfo, random, 0);
    }

    public static List<Location> getSingleStructureLocations(File source, Location startEdge) throws IOException {
        DefinedStructure structure = loadSingleStructure(source);
        List<Location> locations = new ArrayList<>();
        NBTTagCompound fileTag = new NBTTagCompound();
        fileTag = structure.a(fileTag);
        NBTTagList list = (NBTTagList) fileTag.c("size");
        Location endEdge = startEdge.clone().add(Integer.parseInt(list.get(0).toString()) - 1, Integer.parseInt(list.get(1).toString()) - 1, Integer.parseInt(list.get(2).toString()) - 1);
        int topBlockX = (Math.max(startEdge.getBlockX(), endEdge.getBlockX()));
        int bottomBlockX = (Math.min(startEdge.getBlockX(), endEdge.getBlockX()));
        int topBlockY = (Math.max(startEdge.getBlockY(), endEdge.getBlockY()));
        int bottomBlockY = (Math.min(startEdge.getBlockY(), endEdge.getBlockY()));
        int topBlockZ = (Math.max(startEdge.getBlockZ(), endEdge.getBlockZ()));
        int bottomBlockZ = (Math.min(startEdge.getBlockZ(), endEdge.getBlockZ()));
        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    locations.add(new Location(startEdge.getWorld(), x, y, z));
                }
            }
        }
        return locations;
    }

    public static Location getOtherEdge(File source, Location startEdge) throws IOException {
        DefinedStructure structure = loadSingleStructure(source);
        NBTTagCompound fileTag = new NBTTagCompound();
        fileTag = structure.a(fileTag);
        NBTTagList list = (NBTTagList) fileTag.c("size");
        return startEdge.clone().add(Integer.parseInt(list.get(0).toString()) - 1, Integer.parseInt(list.get(1).toString()) - 1, Integer.parseInt(list.get(2).toString()) - 1);
    }

    /**
     * Loads all structure segments back into one array. The folder file contents are important, don't change it after saving. Also converts pre1.13 versions.
     * @param folder - The folder containing an NBT file by the same name with dimensions and NBT files of structures with a counter added
     * @param world - The world (actually ANY world) instance to receive a data fixer (legacy converter)
     * @return DefinedStructure[] - A one dimensional array, just like the folder
     * deprecated Only for pre 1.13, uses the NMS 1.13 DataFixer to convert stuff
     **/

    @Deprecated
    public static DefinedStructure[] loadLegacyStructuresArray(File folder, World world) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (!new File(folder, folder.getName() + ".nbt").exists()) throw new IllegalArgumentException("This is not a valid structure area export folder!");
        DefinedStructure[] structures = new DefinedStructure[folder.listFiles().length - 1];
        for (File file : folder.listFiles()) {
            if (!file.getName().equals(folder.getName() + ".nbt")) {
                Method parseAndConvert = DefinedStructureManager.class.getDeclaredMethod("a", InputStream.class);
                parseAndConvert.setAccessible(true);
                // 1.13 WorldServer#C, 1.13.1 WorldServer#D
                DefinedStructure structure = (DefinedStructure) parseAndConvert.invoke(((CraftWorld) world).getHandle().r(), new FileInputStream(file));
                String suffix = file.getName().split("_")[file.getName().split("_").length - 1];
                suffix = suffix.substring(0, suffix.length() - 4);
                structures[Integer.parseInt(suffix)] = structure;
            }
        }
        return structures;
    }

    /**
     * Loads all structure segments back into one array. The folder file contents are important, don't change it after saving. Works only for structures created with 1.13 or later!
     * @param folder - The folder containing an NBT file by the same name with dimensions and NBT files of structures with an counter added
     * @return DefinedStructure[] - A one dimensional array, just like the folder
     */
    public static DefinedStructure[] loadStructuresArray(File folder) throws IOException {
        if (!new File(folder, folder.getName() + ".nbt").exists()) throw new IllegalArgumentException("This is not a valid structure area export folder!");
        DefinedStructure[] structures = new DefinedStructure[folder.listFiles().length - 1];
        for (File file : folder.listFiles()) {
            if (!file.getName().equals(folder.getName() + ".nbt")) {
                DefinedStructure structure = new DefinedStructure();
                structure.b(NBTCompressedStreamTools.a(new FileInputStream(file)));
                String suffix = file.getName().split("_")[file.getName().split("_").length - 1];
                suffix = suffix.substring(0, suffix.length() - 4);
                structures[Integer.parseInt(suffix)] = structure;
            }
        }
        return structures;
    }

    /**
     * Pastes an array of structures into the world
     * @param structures - A one dimensional array of structures, sorted by y, z, x (iterates along x, then z, then y)
     * @param dimensions - The width, height, length of the complete resulting area
     * @param startEdge - The starting edge with the lowest x, y, z coordinates
     * @param rotation - You may rotate the structure by 90 degrees steps
     */
    public static void insertStructuresArray(Object[] structures, int[] dimensions, Location startEdge, Rotation rotation) {
        insertStructuresArray((DefinedStructure[]) structures, dimensions, startEdge, rotation.getNMSRot());
    }

    /**
     * Pastes an array of structures into the world
     * @param structures - A one dimensional array of structures, sorted by y, z, x (iterates along x, then z, then y)
     * @param dimensions - The width, height, length of the complete resulting area
     * @param startEdge - The starting edge with the lowest x, y, z coordinates
     * @param rotation - You may rotate the structure by 90 degrees steps
     */
    public static void insertStructuresArray(DefinedStructure[] structures, int[] dimensions, Location startEdge, EnumBlockRotation rotation) {
        int[] areas = getAreaSections(dimensions);
        WorldServer world = ((CraftWorld) startEdge.getWorld()).getHandle();
        for (int x = 0; x < areas[0]; x++) {
            for (int y = 0; y < areas[1]; y++) {
                for (int z = 0; z < areas[2]; z++) {
                    DefinedStructureInfo structInfo = new DefinedStructureInfo().a(EnumBlockMirror.a).a(rotation).a(false).a().c(false).a(new Random());
                    BlockPosition pos = new BlockPosition((x * 32) + startEdge.getBlockX(), (y * 32) + startEdge.getBlockY(), (z * 32) + startEdge.getBlockZ());
                    structures[getYzxIndex(x, y, z, areas[0], areas[2])].a(world, pos, pos, structInfo, random, z);
                }
            }
        }
    }

    /**
     * Saves a simple NBT file (same name as folder name), containing an integer array with the given dimensions
     * @param dimension - The integer array
     * @param folder - The parent folder
     */
    public static void saveAreaDimFile(int[] dimension, File folder) throws IOException {
        NBTTagCompound fileTag = new NBTTagCompound();
        fileTag.a("dimensions", dimension);
        NBTCompressedStreamTools.a(fileTag, new FileOutputStream(new File(folder, folder.getName() + ".nbt")));
    }

    /**
     * Loads a simple NBT file (same name as folder name), containing an integer array with dimensions
     * @param folder - The parent folder
     * @return int[3] - width, height, length
     */
    public static int[] loadAreaDimFile(File folder) throws IOException {
        return NBTCompressedStreamTools.a(new FileInputStream(new File(folder, folder.getName() + ".nbt"))).n("dimensions");
    }

    /**
     * Get the amount of blocks along x axis (width), y axis (height), z axis (length)
     * @param corners - The 2 opposite edges, in best case the first has the lowest coordinates in x, y, z
     * @return int[3] - Width, height, length
     */
    public static int[] getDimensions(Location[] corners) {
        if (corners.length != 2) throw new IllegalArgumentException("An area needs to be set up by exactly 2 opposite edges!");
        return new int[] {corners[1].getBlockX() - corners[0].getBlockX() + 1, corners[1].getBlockY() - corners[0].getBlockY() + 1, corners[1].getBlockZ() - corners[0].getBlockZ() + 1};
    }

    /**
     * Calculates how many 32x32x32 sections are needed to fill an area
     * @param dimensions - The area size
     * @return int[3] - Amount of sections along x, y, z axis
     */
    public static int[] getAreaSections(int[] dimensions) {
        if (dimensions.length != 3) throw new IllegalArgumentException("An dimension needs to contain width, height & length!");
        int width, height, length;
        width = dimensions[0] / 32;
        height = dimensions[1] / 32;
        length = dimensions[2] / 32;
        if (dimensions[0] % 32 != 0) width = width + 1;
        if (dimensions[1] % 32 != 0) height = height + 1;
        if (dimensions[2] % 32 != 0) length = length + 1;
        return new int[] {width, height, length};
    }

    /**
     * Swaps the edge corners if necessary, so the first edge will be at the lowest coordinates and the highest will be at the edge with the highest coordinates
     * @param startBlock - Any corner
     * @param endBlock - The other corner
     * @return Location[2] array - [0] = the lowest edge, [1] = the highest edge
     */
    public static Location[] normalizeEdges(Location startBlock, Location endBlock) {
        int xMin, xMax, yMin, yMax, zMin, zMax;
        if (startBlock.getBlockX() <= endBlock.getBlockX()) {
            xMin = startBlock.getBlockX();
            xMax = endBlock.getBlockX();
        } else {
            xMin = endBlock.getBlockX();
            xMax = startBlock.getBlockX();
        }
        if (startBlock.getBlockY() <= endBlock.getBlockY()) {
            yMin = startBlock.getBlockY();
            yMax = endBlock.getBlockY();
        } else {
            yMin = endBlock.getBlockY();
            yMax = startBlock.getBlockY();
        }
        if (startBlock.getBlockZ() <= endBlock.getBlockZ()) {
            zMin = startBlock.getBlockZ();
            zMax = endBlock.getBlockZ();
        } else {
            zMin = endBlock.getBlockZ();
            zMax = startBlock.getBlockZ();
        }
        return new Location[] {new Location(startBlock.getWorld(), xMin, yMin, zMin), new Location(startBlock.getWorld(), xMax, yMax, zMax)};
    }

    /**
     * Calculates the index within a linear array, interpreting it as 3D area, Y Z X (Sorted by height, then length, then width )
     * @param x - The X position within the area
     * @param y - The Y position within the area
     * @param z - The Z position within the area
     * @param width - The width of the area
     * @param length - The length of the area
     * @return int - The array index
     */
    public static int getYzxIndex(int x, int y, int z, int width, int length) {
        return width * length * y + StructureService.getZxIndex(x, z, width);
    }

    /**
     * Calculates the index within a linear array, interpreting it as 2D area, Z X (Sorted by length, then width )
     * @param x - The X position within the area
     * @param z - The Z position within the area
     * @param width - The width of the area
     * @return int - The array index
     */
    public static int getZxIndex(int x, int z, int width) {
        return z * width + x;
    }

    /**
     * Find out, which structure in an array of structure is the one you need at a specific position
     * @param dimension - The width, height, length of the whole structure array
     * @param x - The x coordinate
     * @param y - The y coordinate
     * @param z - The z coordinate
     * @return int - The array index for your structure array
     */
    public static int getStructureArrayIndex(int[] dimension, int x, int y, int z) {
        int[] sections = getAreaSections(dimension);
        return getYzxIndex(x / 32, y / 32, z / 32, sections[0], sections[2]);
    }

    /**
     * Used as rotation interface to NMS
     * @author Michel_0
     */
    public enum Rotation {
        DEG_0(EnumBlockRotation.a), DEG_90(EnumBlockRotation.b), DEG_180(EnumBlockRotation.c), DEG_270(EnumBlockRotation.d);
        private final EnumBlockRotation rotNMS;

        Rotation(EnumBlockRotation rotNMS) {
            this.rotNMS = rotNMS;
        }

        public EnumBlockRotation getNMSRot() {
            return this.rotNMS;
        }
    }

}