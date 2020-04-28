package me.kosinkadink.performantplants.effects;

import me.kosinkadink.performantplants.util.BlockHelper;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlantSoundEffect extends PlantEffect {

    private Sound sound;
    private float volume = 1;
    private float pitch = 1;
    private double offsetX = 0;
    private double offsetY = 0;
    private double offsetZ = 0;
    private boolean eyeLocation = true;
    private boolean clientSide = true;

    public PlantSoundEffect() { }

    @Override
    void performEffectAction(Player player, Location location) {
        Location spawnLocation;
        if (eyeLocation) {
            spawnLocation = player.getEyeLocation();
        } else {
            spawnLocation = player.getLocation();
        }
        spawnLocation.add(offsetX, offsetY, offsetZ);
        if (clientSide) {
            player.playSound(spawnLocation, sound, volume, pitch);
        } else {
            player.getWorld().playSound(spawnLocation, sound, volume, pitch);
        }
    }

    @Override
    void performEffectAction(Block block) {
        Location spawnLocation = BlockHelper.getCenter(block);
        spawnLocation.add(offsetX, offsetY, offsetZ);
        block.getWorld().playSound(spawnLocation, sound, volume, pitch);
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0, volume);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = Math.max(0.5F,Math.min(2.0F, pitch));
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
    }

    public boolean isEyeLocation() {
        return eyeLocation;
    }

    public void setEyeLocation(boolean eyeLocation) {
        this.eyeLocation = eyeLocation;
    }

    public boolean isClientSide() {
        return clientSide;
    }

    public void setClientSide(boolean clientSide) {
        this.clientSide = clientSide;
    }

}
