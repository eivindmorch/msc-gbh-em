package core.util;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class ToStringBuilder {

    private String objectReference;
    private ArrayList<String> properties;
    private ArrayList<String> listedElements;

    private ToStringBuilder(Object object) {
        objectReference = object.getClass().getSimpleName() + '@' + String.format("%-8s", Integer.toHexString(object.hashCode()));
        properties = new ArrayList<>();
        listedElements = new ArrayList<>();
    }

    public static ToStringBuilder toStringBuilder(Object object) {
        return new ToStringBuilder(object);
    }

    public ToStringBuilder add(String label, Object value) {
        properties.add(label + "=" + ((value != null) ? value.toString() : "null"));
        return this;
    }

    public ToStringBuilder addListedElement(String element) {
        listedElements.add(element);
        return this;
    }

    public ToStringBuilder addListedElement(Object element) {
        return addListedElement(element.toString());
    }

    public <L extends List<?>> ToStringBuilder addListedElements(L elements) {
        for (Object element : elements) {
            addListedElement(element);
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(objectReference);

        if (properties.size() > 0) {
            stringBuilder.append('{');
            stringBuilder.append(properties.get(0));
            for (int i = 1; i < properties.size(); i++) {
                stringBuilder.append(", ").append(properties.get(i));
            }
            stringBuilder.append('}');
        }

        if (listedElements.size() > 0) {
            stringBuilder.append('[');
//            stringBuilder.append(String.format("\n%29s", ""));
            stringBuilder.append("\n");
            for (String element : listedElements) {
//                stringBuilder.append("\t\t").append(element).append(String.format("\n%29s", ""));
                stringBuilder.append("\t").append(element).append("\n");
            }
            stringBuilder.append(']');
        }

        return stringBuilder.toString();
    }
}