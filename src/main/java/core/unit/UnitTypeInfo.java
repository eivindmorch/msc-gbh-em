package core.unit;

import com.badlogic.gdx.ai.btree.Task;
import com.sun.istack.internal.NotNull;
import core.model.btree.Blackboard;

import java.util.HashMap;
import java.util.List;


// Needed for searching available tasks and to register unit in UnitHandler
public class UnitTypeInfo {

    private String name;
    private String symbol;
    private Class<? extends Unit> unitClass;
    private List<? extends Class<? extends Task<? extends Blackboard>>> availableTasks;

    private static HashMap<String, UnitTypeInfo> symbolToUnitInfoMap = new HashMap<>();

    public static UnitTypeInfo getUnitInfoFromSymbol(String symbol) {
        return symbolToUnitInfoMap.get(symbol);
    }

    public static void add(String name, String symbol, Class<? extends Unit> unitClass,
                           @NotNull List<? extends Class<? extends Task<? extends Blackboard>>> availableTasks) {
        UnitTypeInfo unitTypeInfo = new UnitTypeInfo(name, symbol, unitClass, availableTasks);
        UnitTypeInfo.symbolToUnitInfoMap.put(symbol, unitTypeInfo);
    }

    private UnitTypeInfo(String name, String symbol, Class<? extends Unit> unitClass,
                         @NotNull List<? extends Class<? extends Task<? extends Blackboard>>> availableTasks) {
        this.name = name;
        this.symbol = symbol;
        this.unitClass = unitClass;
        this.availableTasks = availableTasks;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public Class<? extends Unit> getUnitClass() {
        return unitClass;
    }

    public List<? extends Class<? extends Task<? extends Blackboard>>> getAvailableTasks() {
        return availableTasks;
    }
}
