package core.unit;

import core.btree.tasks.blueprint.template.composite.CompositeTask;
import core.btree.tasks.blueprint.template.leaf.LeafTask;

import java.util.ArrayList;
import java.util.List;

import static core.util.SystemUtil.random;


// Needed for searching available tasks and to register unit in UnitHandler
public class UnitTypeInfo {

    private String name;
    private String symbol;
    private Class<? extends Unit> unitClass;
    private List<Class<? extends LeafTask>> availableLeafTasks;
    private List<Class<? extends CompositeTask>> availableCompositeTasks;

    UnitTypeInfo(String name, String symbol, Class<? extends Unit> unitClass,
                 List<Class<? extends LeafTask>> availableLeafTasks,
                 List<Class<? extends CompositeTask>> availableCompositeTasks) {
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

    public List<Class<? extends LeafTask>> getAvailableLeafTaskClasses() {
        return new ArrayList<>(availableLeafTasks);
    }
    
    public LeafTask getRandomAvailableLeafTask() {
        try {
            return availableLeafTasks.get(random.nextInt(availableLeafTasks.size())).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Class<? extends CompositeTask>> getAvailableCompositeTaskClasses() {
        return new ArrayList<>(availableCompositeTasks);
    }

    public CompositeTask getRandomAvailableCompositeTask() {
        try {
            return availableCompositeTasks.get(random.nextInt(availableCompositeTasks.size())).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
