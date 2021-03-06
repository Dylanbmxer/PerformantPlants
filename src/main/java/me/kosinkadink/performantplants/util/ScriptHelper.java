package me.kosinkadink.performantplants.util;

import me.kosinkadink.performantplants.Main;
import me.kosinkadink.performantplants.blocks.PlantBlock;
import me.kosinkadink.performantplants.scripting.PlantData;
import me.kosinkadink.performantplants.scripting.ScriptBlock;
import me.kosinkadink.performantplants.scripting.ScriptResult;
import me.kosinkadink.performantplants.scripting.ScriptType;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptHelper {

    private static final Pattern variablesPattern = Pattern.compile("\\$([-_a-zA-Z0-9%\\\\.{}]+?)\\$");

    public static ScriptType getType(Object o) {
        if (o == null) {
            return ScriptType.NULL;
        } else if (o instanceof Integer || o instanceof Long) {
            return ScriptType.LONG;
        } else if (o instanceof Double || o instanceof Float) {
            return ScriptType.DOUBLE;
        } else if (o instanceof Boolean) {
            return ScriptType.BOOLEAN;
        } else if (o instanceof String) {
            return ScriptType.STRING;
        }
        return null;
    }

    public static boolean isNumeric(ScriptBlock scriptBlock) {
        switch(scriptBlock.getType()) {
            case LONG:
            case DOUBLE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isLong(ScriptBlock scriptBlock) {
        return scriptBlock.getType() == ScriptType.LONG;
    }

    public static boolean isDouble(ScriptBlock scriptBlock) {
        return scriptBlock.getType() == ScriptType.DOUBLE;
    }

    public static boolean isString(ScriptBlock scriptBlock) {
        return scriptBlock.getType() == ScriptType.STRING;
    }

    public static boolean isBoolean(ScriptBlock scriptBlock) {
        return scriptBlock.getType() == ScriptType.BOOLEAN;
    }

    public static boolean isNull(ScriptBlock scriptBlock) {
        return scriptBlock == null || scriptBlock.getType() == ScriptType.NULL;
    }

    public static String setVariables(PlantBlock plantBlock, String text) {
        PlantData plantData = null;
        if (plantBlock != null) {
            plantData = plantBlock.getEffectivePlantData();
        }
        // figure out which variables are present in the string
        Matcher matcher = variablesPattern.matcher(text);
        StringBuffer stringBuffer = new StringBuffer(text.length());
        while (matcher.find()) {
            String variableName = matcher.group(1);
            // see if variable is recognized;
            String value = getVariableValue(plantBlock, plantData, variableName);
            if (value == null) {
                matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement("$"+variableName+"$"));
            } else {
                matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(value));
            }
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    public static boolean updateGlobalPlantDataVariableValue(PlantData plantData, String variableName, Object value) {
        // if variable name contains period, then it refers to a plant variable
        if (variableName.contains(".")) {
            String[] variableParts = getVariableNameParts(variableName);
            if (variableParts != null) {
                return Main.getInstance().getPlantTypeManager().updateVariable(
                        variableParts[0],
                        variableParts[1],
                        variableParts[2],
                        variableParts[3],
                        value);
            }
        }
        // otherwise it could be referring to a specific plant block's PlantData
        else {
            if (plantData != null) {
                return plantData.updateVariable(variableName, value);
            }
        }
        return false;
    }

    public static Object getGlobalPlantDataVariableValue(PlantData plantData, String variableName) {
        // if variable name contains period, then it refers to a plant variable
        if (variableName.contains(".")) {
            String[] variableParts = getVariableNameParts(variableName);
            if (variableParts != null) {
                return Main.getInstance().getPlantTypeManager().getVariable(
                        variableParts[0],
                        variableParts[1],
                        variableParts[2],
                        variableParts[3]);
            }
        }
        // otherwise it could be referring to a specific plant block's PlantData
        else {
            if (plantData != null) {
                return plantData.getVariable(variableName);
            }
        }
        return null;
    }

    private static String[] getVariableNameParts(String variableName) {
        String[] variableParts = variableName.split("\\.");
        if (variableParts.length == 4) {
            return variableParts;
        } else if (variableParts.length == 2) {
            return new String[] { variableParts[0], "", "", variableParts[1] };
        }
        return null;
    }

    private static String getVariableValue(PlantBlock plantBlock, PlantData plantData, String variableName) {
        // check if it is a property name
        if (variableName.startsWith("_")) {
            if ("_random_uuid".equals(variableName)) {
                return UUID.randomUUID().toString();
            }
            String relevantVariableName = variableName;
            PlantBlock relevantPlantBlock = plantBlock;
            if (variableName.startsWith("_parent")) {
                if (plantBlock.hasParent()) {
                    PlantBlock parentBlock = Main.getInstance().getPlantManager().getPlantBlock(plantBlock.getParentLocation());
                    if (parentBlock != null) {
                        relevantPlantBlock = parentBlock;
                    }
                }
                try {
                    relevantVariableName = variableName.substring("_parent".length());
                } catch (IndexOutOfBoundsException e) {
                    relevantVariableName = "";
                }
            }
            switch (relevantVariableName) {
                case "_x":
                    return Integer.toString(relevantPlantBlock.getLocation().getX());
                case "_y":
                    return Integer.toString(relevantPlantBlock.getLocation().getY());
                case "_z":
                    return Integer.toString(relevantPlantBlock.getLocation().getZ());
                case "_x_center":
                    return Double.toString(relevantPlantBlock.getLocation().getX() + 0.5);
                case "_y_center":
                    return Double.toString(relevantPlantBlock.getLocation().getY() + 0.5);
                case "_z_center":
                    return Double.toString(relevantPlantBlock.getLocation().getZ() + 0.5);
                case "_world":
                    return relevantPlantBlock.getLocation().getWorldName();
                case "_plantId":
                    return relevantPlantBlock.getPlant().getId();
                default:
                    return null;
            }
        }
        // check if variable exists in plant block data
        Object variableValue = getGlobalPlantDataVariableValue(plantData, variableName);
        if (variableValue != null) {
            // create a ScriptResult for easy conversion to string
            try {
                return new ScriptResult(variableValue).getStringValue();
            } catch (IllegalArgumentException e) {
                // something went wrong, return null
                return null;
            }
        }
        // variable name not recognized, return null
        return null;
    }

}
