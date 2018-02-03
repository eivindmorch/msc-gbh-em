package core.unit;

import core.data.rows.DataRow;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;

import java.util.ArrayList;
import java.util.List;


public abstract class Unit {

    private String marking;
    private String identifier; //Internal
    private final ObjectInstanceHandle handle;

    public List<DataRow> dataRows; // Used for writing dataRows to file

    public Unit(String marking, String identifier, ObjectInstanceHandle handle) {
        this.marking = marking;
        this.identifier = identifier;
        this.handle = handle;
        this.dataRows = new ArrayList<>();
    }

    public abstract void updateData(double timestamp);

    public String getMarking() {
        return marking;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ObjectInstanceHandle getHandle() {
        return handle;
    }

    List<DataRow> getDataRows() {
        return dataRows;
    }

    @Override
    public String toString() {
        return "Unit@" + hashCode()
                + " {"
                + "Marking: " + marking + ", "
                + "Identifier: " + identifier + ", "
                + "Handle" + handle
                + "}";
    }

    public PhysicalEntityObject getPhysicalEntityObject() {
        PhysicalEntityObject physicalEntityObject = PhysicalEntityObject.getAllPhysicalEntitys().get(this.getHandle());
        return physicalEntityObject;
    }
}
