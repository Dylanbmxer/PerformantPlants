package me.kosinkadink.performantplants.chunks;

import me.kosinkadink.performantplants.Main;
import me.kosinkadink.performantplants.blocks.PlantBlock;
import me.kosinkadink.performantplants.locations.BlockLocation;
import me.kosinkadink.performantplants.locations.ChunkLocation;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class PlantChunk {

    private final ChunkLocation location;
    private ConcurrentHashMap<BlockLocation, PlantBlock> plantBlocks = new ConcurrentHashMap<>();
    private boolean loaded = false;

    public PlantChunk(ChunkLocation chunkLocation) {
        location = chunkLocation;
    }

    public ChunkLocation getLocation() {
        return location;
    }

    public ConcurrentHashMap<BlockLocation, PlantBlock> getPlantBlocks() {
        return plantBlocks;
    }

    public void addPlantBlock(PlantBlock plantBlock) {
        // add plantBlock to hash map
        plantBlocks.put(plantBlock.getLocation(), plantBlock);
    }

    public boolean removePlantBlock(PlantBlock plantBlock) {
        // remove plantBlock from hash map
        PlantBlock removed = plantBlocks.remove(plantBlock.getLocation());
        return removed != null;
    }

    public PlantBlock getPlantBlock(BlockLocation blockLocation) {
        return plantBlocks.get(blockLocation);
    }

    public boolean isEmpty() {
        return plantBlocks.isEmpty();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isChunkLoaded() {
        return location.getChunk().isLoaded();
    }

    public void load(Main main) {
        // start up task for each plantBlock
        plantBlocks.forEach((blockLocation, plantBlock) -> main.getPlantManager().startGrowthTask(plantBlock));
        loaded = true;
    }

    public void unload(Main main) {
        // pause task for each plantBlock
        plantBlocks.forEach((blockLocation, plantBlock) -> main.getPlantManager().pauseGrowthTask(plantBlock));
        loaded = false;
    }

    @Override
    public String toString() {
        return "PlantChunk @ " + location.toString();
    }

}