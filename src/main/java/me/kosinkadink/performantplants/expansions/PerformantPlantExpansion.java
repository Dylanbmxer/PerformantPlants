package me.kosinkadink.performantplants.expansions;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.kosinkadink.performantplants.Main;
import me.kosinkadink.performantplants.plants.Plant;
import me.kosinkadink.performantplants.plants.PlantItem;
import me.kosinkadink.performantplants.statistics.StatisticsAmount;
import me.kosinkadink.performantplants.storage.StatisticsTagStorage;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PerformantPlantExpansion extends PlaceholderExpansion {

    private Main main;

    private final Pattern BUYPRICE_PATTERN = Pattern.compile("^buyprice_(?<plantItemId>[a-zA-Z0-9_.\\-]+)$");
    private final Pattern SELLPRICE_PATTERN = Pattern.compile("^sellprice_(?<plantItemId>[a-zA-Z0-9_.\\-]+)$");
    private final Pattern HASSEED_PATTERN = Pattern.compile("^hasseed_(?<plantId>[a-zA-Z0-9_.\\-]+)$");
    private final Pattern SOLD_PATTERN = Pattern.compile("^sold_(?<playerUUID>[a-zA-Z0-9\\-]{36})_(?<plantItemId>[a-zA-Z0-9_.\\-]+)$");
    private final Pattern SOLDTAG_PATTERN = Pattern.compile("^soldtag_(?<playerUUID>[a-zA-Z0-9\\-]{36})_(?<plantTag>[a-zA-Z0-9_.\\-]+)$");

    public PerformantPlantExpansion(Main main) {
        this.main = main;
    }

    /**
     * Expansion class is internal, so have to override persist() to not unregister expansion on PlaceholderAPI reload
     * @return true to persist thorugh reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Expansion class is internal, so this check is not needed and always returns true
     * @return true since it's an internal class
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "performantplants";
    }

    @Override
    public String getAuthor() {
        return main.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return main.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null) {
            return "";
        }

        // replace any nested placeholders surrounded by brackets -> { }
        identifier = PlaceholderAPI.setBracketPlaceholders(player, identifier);

        // %performantplants_buyprice_<plantItemId>% -> double
        // Example:
        //   %performantplants_buyprice_strawberry%
        Matcher matcher = BUYPRICE_PATTERN.matcher(identifier);
        if (matcher.find()) {
            String plantId = matcher.group("plantItemId");
            PlantItem plantItem = main.getPlantTypeManager().getPlantItemById(plantId);
            if (plantItem != null) {
                return String.format("%.2f", plantItem.getBuyPrice());
            }
            return null;
        }

        // %performantplants_sellprice_<plantItemId>% -> double
        // Example:
        //   %performantplants_sellprice_strawberry%
        matcher = SELLPRICE_PATTERN.matcher(identifier);
        if (matcher.find()) {
            String plantId = matcher.group("plantItemId");
            PlantItem plantItem = main.getPlantTypeManager().getPlantItemById(plantId);
            if (plantItem != null) {
                return String.format("%.2f", plantItem.getSellPrice());
            }
            return null;
        }

        // %performantplants_hasseed_<plantId>% -> boolean
        // Example:
        //   %performantplants_hasseed_strawberry%
        matcher = HASSEED_PATTERN.matcher(identifier);
        if (matcher.find()) {
            String plantId = matcher.group("plantId");
            Plant plant = main.getPlantTypeManager().getPlantById(plantId);
            if (plant != null) {
                return String.format("%b", plant.hasSeed());
            }
            return null;
        }

        // %performantplants_sold_<playerUUID>_<plantItemId>% -> int
        // Examples:
        //   %performantplants_sold_{player_uuid}_strawberry%
        //   %performantplants_sold_00000000-0000-0000-0000-000000000000_strawberry%
        matcher = SOLD_PATTERN.matcher(identifier);
        if (matcher.find()) {
            String playerUUID = matcher.group("playerUUID");
            String plantId = matcher.group("plantItemId");
            StatisticsAmount plantItemsSold = main.getStatisticsManager()
                    .getPlantItemsSold(UUID.fromString(playerUUID), plantId);
            int amount = 0;
            if (plantItemsSold != null) {
                amount = plantItemsSold.getAmount();
            }
            return String.format("%d", amount);
        }

        // %performantplants_soldtag_<playerUUID>_<plantTag>% -> int
        // Examples:
        //   %performantplants_soldtag_{playerUUID}_vegetables%
        //   %performantplants_soldtag_00000000-0000-0000-0000-000000000000_vegetables%
        matcher = SOLDTAG_PATTERN.matcher(identifier);
        if (matcher.find()) {
            String playerUUID = matcher.group("playerUUID");
            String plantTag = matcher.group("plantTag");
            // get tag, if exists
            StatisticsTagStorage storage = main.getStatisticsManager().getPlantTag(plantTag);
            int amount = 0;
            if (storage != null) {
                ArrayList<String> plantIds = storage.getAllPlantIds();
                for (String plantId : plantIds) {
                    StatisticsAmount plantItemsSold = main.getStatisticsManager()
                            .getPlantItemsSold(UUID.fromString(playerUUID), plantId);
                    if (plantItemsSold != null) {
                        amount += plantItemsSold.getAmount();
                    }
                }
            }
            // add up sold statistic for each plant in tag
            return String.format("%d", amount);
        }

        // return null if invalid placeholder provided
        return null;
    }

}
