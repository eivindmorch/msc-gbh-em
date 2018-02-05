package core.unit;

import core.data.rows.DataRow;
import core.util.ToStringBuilder;
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

    public PhysicalEntityObject getPhysicalEntityObject() {
        return PhysicalEntityObject.getAllPhysicalEntitys().get(this.getHandle());
    }

    @Override
    public String toString() {
        return ToStringBuilder.toStringBuilder(this)
                .add("marking", marking)
                .add("identifier", identifier)
                .add("handle", handle)
                .toString();
    }
}
