package core.unit;

import com.badlogic.gdx.ai.btree.BranchTask;
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
    private List<Class<? extends Task<? extends Blackboard>>> availableLeafTasks;
    private List<Class<? extends BranchTask>> availableCompositeTasks;

    public static void add(String name, String symbol, Class<? extends Unit> unitClass,
                           List<Class<? extends Task<? extends Blackboard>>> availableLeafTasks,
                           List<Class<? extends BranchTask>> availableCompositeTasks) {

        UnitTypeInfo unitTypeInfo = new UnitTypeInfo(name, symbol, unitClass, availableLeafTasks, availableCompositeTasks);
        UnitTypeInfo.symbolToUnitInfoMap.put(symbol, unitTypeInfo);
        UnitTypeInfo.unitClassToUnitInfoMap.put(unitClass, unitTypeInfo);
    }

    private UnitTypeInfo(String name, String symbol, Class<? extends Unit> unitClass,
                         List<Class<? extends Task<? extends Blackboard>>> availableLeafTasks,
                         List<Class<? extends BranchTask>> availableCompositeTasks) {
        this.name = name;
        this.symbol = symbol;
        this.unitClass = unitClass;
        this.availableLeafTasks = availableLeafTasks;
        this.availableCompositeTasks = availableCompositeTasks;
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

    public List<Class<? extends Task<? extends Blackboard>>> getAvailableLeafTasks() {
        return availableLeafTasks;
    }

    public List<Class<? extends BranchTask>> getAvailableCompositeTasks() {
        return availableCompositeTasks;
    }


    // STATIC
    private static HashMap<String, UnitTypeInfo> symbolToUnitInfoMap = new HashMap<>();
    private static HashMap<Class<? extends Unit>, UnitTypeInfo> unitClassToUnitInfoMap = new HashMap<>();

    public static UnitTypeInfo getUnitInfoFromSymbol(String symbol) {
        return symbolToUnitInfoMap.get(symbol);
    }

    public static UnitTypeInfo getUnitInfoFromUnitClass(Class<? extends Unit> unitClass) {
        return unitClassToUnitInfoMap.get(unitClass);
    }
}
