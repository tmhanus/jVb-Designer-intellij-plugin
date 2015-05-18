package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *  Created by Tomas Hanus on 2/21/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Partition {

    @XmlElement(name = "mapper")
    private PartitionMapper partitionMapper;

    @XmlElement(name = "plan")
    private PartitionPlan partitionPlan;

    @XmlElement(name = "collector")
    private PartitionCollector partitionCollector;

    @XmlElement(name = "analyzer")
    private PartitionAnalyzer partitionAnalyzer;

    @XmlElement(name = "reducer")
    private PartitionReducer partitionReducer;

    private boolean isPartitionPlanDefinedAtRuntime;

    public Partition() {
        this.isPartitionPlanDefinedAtRuntime = false;
    }

    public PartitionPlan getPartitionPlan() {
        return partitionPlan;
    }

    public void setPartitionPlan(PartitionPlan partitionPlan) {
        this.partitionPlan = partitionPlan;
    }

    public PartitionMapper getPartitionMapper() {
        return partitionMapper;
    }

    public void setPartitionMapper(PartitionMapper partitionMapper) {
        this.partitionMapper = partitionMapper;
    }

    public PartitionReducer getPartitionReducer() {
        return partitionReducer;
    }

    public void setPartitionReducer(PartitionReducer partitionReducer) {
        this.partitionReducer = partitionReducer;
    }

    public PartitionCollector getPartitionCollector() {
        return partitionCollector;
    }

    public void setPartitionCollector(PartitionCollector partitionCollector) {
        this.partitionCollector = partitionCollector;
    }

    public PartitionAnalyzer getPartitionAnalyzer() {
        return partitionAnalyzer;
    }

    public void setPartitionAnalyzer(PartitionAnalyzer partitionAnalyzer) {
        this.partitionAnalyzer = partitionAnalyzer;
    }

    public boolean isPartitionPlanDefinedAtRuntime() {
        return isPartitionPlanDefinedAtRuntime;
    }

    public void setIsPartitionPlanDefinedAtRuntime(boolean isPartitionPlanDefinedAtRuntime) {
        this.isPartitionPlanDefinedAtRuntime = isPartitionPlanDefinedAtRuntime;
    }
}
