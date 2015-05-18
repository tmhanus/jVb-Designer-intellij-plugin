package specification;

import designer.ui.editor.element.Element;
import designer.ui.editor.element.StepElement;
import specification.core.DefaultValuesBundle;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 2/21/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Step extends StepElement {
    private static final String CHUNK_ELEMENT = "Chunk";
    private static final String BATCHLET_ELEMENT = "Batchlet";

    @XmlElement
    private Properties properties;

    @XmlElement
    private Listeners listeners;

    // **** STEP TYPE ***
    @XmlElement
    private Chunk chunk;
    @XmlElement
    private Batchlet batchlet;

    // **** ATTRIBUTES ***

    @XmlAttribute(name = "start-limit")
    private Integer startLimit;

    @XmlAttribute(name = "allow-start-if-complete")
    private Boolean allowStartIfComplete;

    @XmlAttribute(name = "next")
    private String nextElementId;

    @XmlAttribute(name = "abstract")
    private Boolean isAbstract;

    @XmlAttribute(name = "parent")
    private String parentElement;

    // **** ELEMENTS ***


    @XmlElement
    private Partition partition;

    @XmlElements({@XmlElement(name = "stop", type = Stop.class)})
    private List<Stop> stops;
    @XmlElements({@XmlElement(name = "end", type = End.class)})
    private List<End> ends;
    @XmlElements({@XmlElement(name = "fail", type = Fail.class)})
    private List<Fail> fails;
    @XmlElements({@XmlElement(name = "next", type = Next.class)})
    private List<Next> nexts;

    private boolean partitionEnabled = false;

    private PartitionPlan partitionPlanBackup;

    private PartitionMapper partitionMapperBackup;

    public Step() {
        super();
    }

    public Step(String id, String type) {
        super(type);
        this.setId(id);

        if (type.equals(this.CHUNK_ELEMENT)) {
            this.chunk = new Chunk(id);
            this.batchlet = null;
        } else {
            this.batchlet = new Batchlet(id);
            this.chunk = null;
        }
    }

    /**
     * Returns true if step is chunkOriented (contains chunk) or false if it is taskOriented (conains batchlet)
     *
     * @return
     */
    public Boolean isChunkOriented() {
        if (this.chunk != null) {
            return true;
        } else {
            return false;
        }
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public Batchlet getBatchlet() {
        return batchlet;
    }

    public void setBatchlet(Batchlet batchlet) {
        this.batchlet = batchlet;
    }

    /**
     * Returns start limit. If there is no start limit defined (null) returns it's default value.
     */
    public Integer getStartLimit() {
        if (this.startLimit == null) return Integer.valueOf(DefaultValuesBundle.value("step.startLimit"));
        return startLimit;
    }

    public void setStartLimit(Integer startLimit) {
        if (startLimit == Integer.valueOf(DefaultValuesBundle.value("step.startLimit")))
            this.startLimit = null;
        else
            this.startLimit = startLimit;
    }

    public Boolean isAllowStartIfComplete() {
        if (this.allowStartIfComplete == null)
            return Boolean.valueOf(DefaultValuesBundle.value("step.allowStartIfComplete"));
        return allowStartIfComplete;
    }

    public void setAllowStartIfComplete(Boolean allowStartIfComplete) {
        if (allowStartIfComplete == Boolean.valueOf(DefaultValuesBundle.value("step.allowStartIfComplete")))
            this.allowStartIfComplete = null;
        else
            this.allowStartIfComplete = allowStartIfComplete;
    }

    public Boolean isAbstract() {
        if (this.isAbstract == null) return Boolean.valueOf(DefaultValuesBundle.value("step.abstract"));
        return isAbstract;
    }

    public void setIsAbstract(Boolean isAbstract) {
        if (isAbstract == Boolean.valueOf(DefaultValuesBundle.value("step.abstract")))
            this.isAbstract = null;
        else
            this.isAbstract = isAbstract;
    }

    public String getParentElement() {
        return parentElement;
    }

    public void setParentElement(String parentElement) {
        if (parentElement.equals("")) this.parentElement = null;
        else this.parentElement = parentElement;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Listeners getListeners() {
        return this.listeners;
    }

    public void setListeners(Listeners listeners) {
        this.listeners = listeners;
    }

    public Partition getPartition() {
        //if (this.partition == null) this.partition = new Partition();
        return partition;
    }

    public void setPartition(Partition partition) {
        this.partition = partition;
    }

    public boolean isPartitionEnabled() {
        return partitionEnabled;
    }

    public void setPartitionEnabled(boolean partitionEnabled) {
        this.partitionEnabled = partitionEnabled;
    }

    public void removePartitionPlan() {
        if (this.partition.getPartitionPlan() == null) {
            this.partitionPlanBackup = null;
        } else {
            this.partitionPlanBackup = new PartitionPlan(this.partition.getPartitionPlan().getPartitionsNumber(),
                    this.partition.getPartitionPlan().getThreadsNumber(), this.partition.getPartitionPlan().getProperties());
        }
        this.partition.setPartitionPlan(null);
    }

    public void removePartitionMapper() {
        if (this.partition.getPartitionMapper() == null) {
            this.partitionMapperBackup = null;
        } else {

//            this.partitionMapperBackup = this.partition.getPartitionMapper().deepClone();
            this.partitionMapperBackup = new PartitionMapper(this.partition.getPartitionMapper());
        }
        this.partition.setPartitionMapper(null);
    }

    public void getPartitionPlanFromBackup() {
        this.partition.setPartitionPlan(partitionPlanBackup);
    }

    public void getPartitionMapperFromBackup() {
        this.partition.setPartitionMapper(partitionMapperBackup);
    }

    public void setPartitionPlanBackup(PartitionPlan partitionPlanBackup) {
        this.partitionPlanBackup = partitionPlanBackup;
    }

    public void setPartitionMapperBackup(PartitionMapper partitionMapperBackup) {
        this.partitionMapperBackup = partitionMapperBackup;
    }

    public void addStop(Stop stop) {
        if (this.stops == null) this.stops = new ArrayList<Stop>();
        if (!this.stops.contains(stop))
            this.stops.add(stop);
    }

    // ---------------- STOP ------------------
    public Stop getStop(String stopId) {
        if (this.stops == null) return null;
        Stop stopToReturn = null;
        for (Stop stop : this.stops) {
            if (stop.getId().equals(stopId))
                stopToReturn = stop;
        }
        return stopToReturn;
    }

    public void removeStop(Stop stop) {
        if (this.stops == null) return;
        this.stops.remove(stop);
        if (this.stops.size() == 0) this.stops = null;
    }

    public void removeStop(String stopId) {
        if (this.stops == null) return;
        Stop stopToRemove = null;
        for (Stop stop : this.stops) {
            if (stop.getId().equals(stopId))
                stopToRemove = stop;
        }
        if (stopToRemove == null) return;
        this.stops.remove(stopToRemove);
        if (this.stops.size() == 0) this.stops = null;
    }

    @Override
    public List<Stop> getStops() {
        return stops;
    }
    // ---------------- END ------------------

    public void addEnd(End end) {
        if (this.ends == null) this.ends = new ArrayList<End>();
        if (!this.ends.contains(end))
            this.ends.add(end);
    }

    public End getEnd(String endId) {
        if (this.ends == null) return null;
        End endToReturn = null;
        for (End end : this.ends) {
            if (end.getId().equals(endId))
                endToReturn = end;
        }
        return endToReturn;
    }

    public void removeEnd(End end) {
        if (this.ends == null) return;
        this.ends.remove(end);
        if (this.ends.size() == 0) this.ends = null;
    }

    public void removeEnd(String endId) {
        if (this.ends == null) return;
        End endToRemove = null;
        for (End end : this.ends) {
            if (end.getId().equals(endId))
                endToRemove = end;
        }
        if (endToRemove == null) return;
        this.ends.remove(endToRemove);
        if (this.ends.size() == 0) this.ends = null;
    }

    @Override
    public List<End> getEnds() {
        return ends;
    }

    // ---------------- FAIL ------------------

    public void addFail(Fail fail) {
        if (this.fails == null) this.fails = new ArrayList<Fail>();
        if (!this.fails.contains(fail))
            this.fails.add(fail);
    }

    public void removeFail(Fail fail) {
        if (this.fails == null) return;
        this.fails.remove(fail);
        if (this.fails.size() == 0) this.fails = null;
    }

    public void removeFail(String failId) {
        if (this.fails == null) return;
        Fail failToRemove = null;
        for (Fail fail : this.fails) {
            if (fail.getId().equals(failId))
                failToRemove = fail;
        }
        if (failToRemove == null) return;
        this.fails.remove(failToRemove);
        if (this.fails.size() == 0) this.fails = null;
    }

    @Override
    public List<Fail> getFails() {
        return fails;
    }

    public Fail getFail(String failId) {
        if (this.fails == null) return null;
        Fail failToReturn = null;
        for (Fail fail : this.fails) {
            if (fail.getId().equals(failId))
                failToReturn = fail;
        }
        return failToReturn;
    }

    public boolean canAddStop(String id) {
        if (this.stops == null) return true;
//        if (this.stops.size() >= 1) return false;
        for (Stop stop : this.stops) {
            if (stop.getId().equals(id)) return false;
        }
        return true;
    }

    public boolean canAddFail(String id) {
        if (this.fails == null) return true;
//        if (this.fails.size() >= 1) return false;
        for (Fail fail : this.fails) {
            if (fail.getId().equals(id)) return false;
        }
        return true;
    }

    public boolean canAddEnd(String id) {
        if (this.ends == null) return true;
        //if (this.ends.size() >= 1) return false;
        for (End end : this.ends) {
            if (end.getId().equals(id)) return false;
        }
        return true;
    }

    @Override
    public boolean canAddTransitionElement(Element transitionElement) {
//        if (transitionElement instanceof EndTransition){
//            if (this.ends == null) return true;
//            if (this.ends.size() >= 1 ) return false;
//            for (End end : this.ends){
//                if (end.getId().equals(transitionElement.getId())) return false;
//            }
//            return true;
//        }else if (transitionElement instanceof StopTransition){
//            if (this.stops == null) return true;
//            if (this.stops.size() >= 1 ) return false;
//            for (Stop stop : this.stops){
//                if (stop.getId().equals(transitionElement.getId())) return false;
//            }
//            return true;
//        }
//        else if (transitionElement instanceof FailTransition){
//            if (this.fails == null) return true;
//            if (this.fails.size() >= 1 ) return false;
//            for (Fail fail : this.fails){
//                if (fail.getId().equals(transitionElement.getId())) return false;
//            }
//            return true;
//        }
        return true;
    }

    public void addNextElementId(String newNextElementId) {
        if (getNextTransition(newNextElementId) != null) return;
        if (this.nextElementId == null && this.nexts == null) {
            this.nextElementId = newNextElementId;
            return;
        }
        if (this.nextElementId != null && !this.nextElementId.equals("")) {
            this.nexts = new ArrayList<Next>();
            this.nexts.add(new Next("", nextElementId)); // makes next element from next attribute
            this.nextElementId = null;
        }
        this.nexts.add(new Next("", newNextElementId));
    }

    public Next getNextTransition(String id) {
        if (nextElementId != null && nextElementId.equals(id)) return new Next(null, nextElementId);
        if (nexts != null) {
            for (Next next : this.nexts) {
                if (next.getTo().equals(id)) return next;
            }
        }
        return null;
    }

    public void removeNextElementId(String nextElementIdToRemove) {
        if (this.nextElementId != null && this.nexts == null) {
            if (this.nextElementId.equals(nextElementIdToRemove)) {
                this.nextElementId = null;
                return;
            }
        }
        if (this.nextElementId == null && this.nexts != null) {
            for (Next next : this.nexts) {
                if (next.getTo().equals(nextElementIdToRemove)) {
                    this.nexts.remove(next);
                    break;
                }
            }
            if (this.nexts.size() == 0) {
                this.nexts = null;
                return;
            }
            if (this.nexts.size() == 1 && (this.nexts.get(0).getOn() == null || this.nexts.get(0).getOn().equals(""))) {
                this.nextElementId = this.nexts.get(0).getTo();
                this.nexts = null;
            }
        }
    }

    public List<Next> getAllNextTransitions() {
        List<Next> nextsToReturn = new ArrayList<Next>();
        if (this.nextElementId != null && this.nexts == null) {
            nextsToReturn.add(new Next(null, nextElementId));
        } else if (this.nextElementId == null && this.nexts != null) {
            for (Next next : this.nexts) {
                nextsToReturn.add(next);
            }
        }
        if (nextsToReturn.size() == 0) return null;
        return nextsToReturn;
    }

    public void editNextTransitionTo(String nextTransitionTo, String newOnValue) {
        if (this.nextElementId != null && this.nexts == null) {
            if (newOnValue == null || newOnValue.equals("")) return;
            else {
                this.nexts = new ArrayList<Next>();
                this.nexts.add(new Next(newOnValue, nextTransitionTo));
                this.nextElementId = null;
            }
        } else if (this.nextElementId == null && this.nexts != null) {
            if (this.nexts.size() == 1) {
                if (newOnValue == null || newOnValue.equals("")) {
                    this.nextElementId = nextTransitionTo;
                    this.nexts = null;
                } else {
                    this.nexts.get(0).setOn(newOnValue);
                }
            } else getNextTransition(nextTransitionTo).setOn(newOnValue);
        }
    }

    public void renameDestinationOfNextTransition(String nextTransitionTo, String newNextTransitionDestination) {
        if (this.nextElementId != null && this.nexts == null) {
            this.nextElementId = newNextTransitionDestination;
        } else if (this.nextElementId == null && this.nexts != null) {
            getNextTransition(nextTransitionTo).setTo(newNextTransitionDestination);
        }
    }
}