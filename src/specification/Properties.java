package specification;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 2/21/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Properties {
    @XmlAttribute(name = "partition")
    private String partitionName;
    @XmlElements({@XmlElement(name = "property", type = Property.class)})
    private List<Property> properties;

    public Properties() {
    }

    public Properties(List<Property> properties) {
        this.properties = properties;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public void addProperty(Property property) {
        if (this.properties == null) this.properties = new ArrayList<Property>();
        this.properties.add(property);
    }

    public void removeProperty(Property property) {
        if (this.properties == null) return;
        this.properties.remove(property);
    }

    public Property getProperty(String name, String value) {
        if (this.properties == null) return null;

        for (Property property : this.properties) {
            if ((property.getName().equals(name)) && (property.getValue().equals(value)))
                return property;
        }
        return null;
    }

    public String getPartitionName() {
        return partitionName;
    }

    public void setPartitionName(String partitionName) {
        this.partitionName = partitionName;
    }
}