package me.kosinkadink.performantplants.scripting.operations.compare;

import me.kosinkadink.performantplants.blocks.PlantBlock;
import me.kosinkadink.performantplants.scripting.ScriptBlock;
import me.kosinkadink.performantplants.scripting.ScriptResult;
import me.kosinkadink.performantplants.scripting.ScriptType;
import org.bukkit.entity.Player;

public class ScriptOperationGreaterThanOrEqualTo extends ScriptOperationCompare {

    public ScriptOperationGreaterThanOrEqualTo(ScriptBlock left, ScriptBlock right) {
        super(left, right);
    }

    @Override
    protected void validateInputs() {
        super.validateInputs();
        if (getLeft().getType() == ScriptType.BOOLEAN || getRight().getType() == ScriptType.BOOLEAN) {
            throw new IllegalArgumentException("GreaterThanOrEqualTo operation does not support ScriptType BOOLEAN");
        }
    }

    @Override
    public ScriptResult perform(PlantBlock plantBlock, Player player) {
        // if left or right is variable, use proper temp value;
        ScriptResult leftInstance = getLeft().loadValue(plantBlock, player);
        ScriptResult rightInstance = getRight().loadValue(plantBlock, player);
        // check if types compatible
        if (leftInstance.getType() != leftInstance.getType()) {
            if (leftInstance.getType() != ScriptType.LONG || leftInstance.getType() != ScriptType.DOUBLE ||
                    rightInstance.getType() != ScriptType.LONG || rightInstance.getType() != ScriptType.DOUBLE) {
                return new ScriptResult(Boolean.FALSE);
            }
        }
        switch (leftInstance.getType()) {
            case NULL:
                return new ScriptResult(Boolean.TRUE); // always equal, since had to be same type to get here
            case LONG:
            case DOUBLE:
                return new ScriptResult(leftInstance.getDoubleValue() >= rightInstance.getDoubleValue());
            case STRING:
                return new ScriptResult(leftInstance.getStringValue().compareTo(rightInstance.getStringValue()) >= 0);
            default:
                return new ScriptResult(Boolean.FALSE);
        }
    }
}
