package me.kosinkadink.performantplants.scripting.operations.math;

import me.kosinkadink.performantplants.blocks.PlantBlock;
import me.kosinkadink.performantplants.scripting.ScriptBlock;
import me.kosinkadink.performantplants.scripting.ScriptResult;
import me.kosinkadink.performantplants.scripting.ScriptType;
import org.bukkit.entity.Player;

public class ScriptOperationDivide extends ScriptOperationBinaryMath {

    public ScriptOperationDivide(ScriptBlock left, ScriptBlock right) {
        super(left, right);
    }

    @Override
    public ScriptResult perform(PlantBlock plantBlock, Player player) {
        // if left or right is variable, use proper temp value;
        ScriptResult leftInstance = getLeft().loadValue(plantBlock, player);
        ScriptResult rightInstance = getRight().loadValue(plantBlock, player);
        ScriptResult result;
        if (leftInstance.getType() != ScriptType.DOUBLE && rightInstance.getType() != ScriptType.DOUBLE) {
            try {
                result = new ScriptResult(leftInstance.getLongValue() / rightInstance.getLongValue());
            } catch (ArithmeticException e) {
                result = new ScriptResult(0L);
            }
            return result;
        }
        if (rightInstance.getDoubleValue() == 0.0) {
            return new ScriptResult(0.0);
        }
        return new ScriptResult(leftInstance.getDoubleValue() / rightInstance.getDoubleValue());
    }

}
