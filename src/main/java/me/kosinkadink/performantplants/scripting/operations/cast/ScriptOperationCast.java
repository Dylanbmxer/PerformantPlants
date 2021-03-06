package me.kosinkadink.performantplants.scripting.operations.cast;

import me.kosinkadink.performantplants.blocks.PlantBlock;
import me.kosinkadink.performantplants.scripting.ScriptBlock;
import me.kosinkadink.performantplants.scripting.ScriptCategory;
import me.kosinkadink.performantplants.scripting.ScriptResult;
import me.kosinkadink.performantplants.scripting.operations.type.ScriptOperationUnary;
import org.bukkit.entity.Player;

public abstract class ScriptOperationCast extends ScriptOperationUnary {

    public ScriptOperationCast(ScriptBlock input) {
        super(input);
    }

    @Override
    protected void validateInputs() {
        switch (getInput().getType()) {
            case BOOLEAN:
            case LONG:
            case DOUBLE:
            case STRING:
                break;
            default:
                throw new IllegalArgumentException("Cast operation only supports BOOLEAN, LONG, DOUBLE, and STRING");
        }
    }

    @Override
    public ScriptResult perform(PlantBlock plantBlock, Player player) {
        return getInput().loadValue(plantBlock, player);
    }

    @Override
    public ScriptCategory getCategory() {
        return ScriptCategory.CAST;
    }

}
