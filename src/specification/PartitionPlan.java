package specification;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 *  Created by Tomas Hanus on 4/11/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PartitionPlan {
    @XmlAttribute(name = "partitions")
    private int partitionsNumber;

    @XmlAttribute(name = "threads")
    private int threadsNumber;

    @XmlElements({
            @XmlElement(name = "properties", type = Properties.class)
    })
    private List<Properties> properties;

    public PartitionPlan() {
    }

    public PartitionPlan(int partitionsNumber, int threadsNumber, List<Properties> properties) {
        this.partitionsNumber = partitionsNumber;
        this.threadsNumber = threadsNumber;
        this.properties = properties;
    }

    public int getPartitionsNumber() {
        return partitionsNumber;
    }

    public void setPartitionsNumber(int partitionsNumber) {
        this.partitionsNumber = partitionsNumber;
    }

    public int getThreadsNumber() {
        return threadsNumber;
    }

    public void setThreadsNumber(int threadsNumber) {
        this.threadsNumber = threadsNumber;
    }

    public List<Properties> getProperties() {
        //if (this.properties == null) this.properties = new ArrayList<Properties>();
        return properties;
    }

    public void setProperties(List<Properties> properties) {
        this.properties = properties;
    }
}
