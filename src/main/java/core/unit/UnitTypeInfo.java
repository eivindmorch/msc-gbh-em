package core.unit;

import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.LeafTasks.TempLeafTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static core.util.SystemUtil.random;


// Needed for searching available tasks and to register unit in UnitHandler
public class UnitTypeInfo {

    private String name;
    private String symbol;
    private Class<? extends Unit> unitClass;
    private List<Class<? extends TempLeafTask>> availableLeafTasks;
    private List<Class<? extends TempCompositeTask>> availableCompositeTasks;

    public static void add(String name, String symbol, Class<? extends Unit> unitClass,
                           List<Class<? extends TempLeafTask>> availableLeafTasks,
                           List<Class<? extends TempCompositeTask>> availableCompositeTasks) {

        UnitTypeInfo unitTypeInfo = new UnitTypeInfo(name, symbol, unitClass, availableLeafTasks, availableCompositeTasks);
        UnitTypeInfo.symbolToUnitInfoMap.put(symbol, unitTypeInfo);
        UnitTypeInfo.unitClassToUnitInfoMap.put(unitClass, unitTypeInfo);
    }

    private UnitTypeInfo(String name, String symbol, Class<? extends Unit> unitClass,
                         List<Class<? extends TempLeafTask>> availableLeafTasks,
                         List<Class<? extends TempCompositeTask>> availableCompositeTasks) {
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

    public List<Class<? extends TempLeafTask>> getAvailableLeafTaskClasses() {
        return new ArrayList<>(availableLeafTasks);
    }
    
    public TempLeafTask getRandomAvailableLeafTask() {
        try {
            return availableLeafTasks.get(random.nextInt(availableLeafTasks.size())).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Class<? extends TempCompositeTask>> getAvailableCompositeTaskClasses() {
        return new ArrayList<>(availableCompositeTasks);
    }

    public TempCompositeTask getRandomAvailableCompositeTask() {
        try {
            return availableCompositeTasks.get(random.nextInt(availableCompositeTasks.size())).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
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
