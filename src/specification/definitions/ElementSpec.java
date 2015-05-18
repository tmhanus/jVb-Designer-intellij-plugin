package specification.definitions;

import designer.ui.editor.element.Element;
import specification.Partition;
import specification.Step;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *  Created by Tomas Hanus on 4/8/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ElementSpec {
    @XmlAttribute(required = true)
    private String elementId;
    @XmlElement
    private Label label;
    @XmlElement(required = true)
    private Position position;
    @XmlElement
    private boolean partitionEnabled;
    @XmlElement
    private boolean runtimePlanning;
    @XmlElement
    private Partition partition;

    // *****************************************************************
    // *                      CONSTRUCTORS                             *
    // *****************************************************************

    public ElementSpec() {
    }

    public ElementSpec(Element element) {
        this.elementId = element.getId();
        this.position = new Position(element.getPosition().x, element.getPosition().y);

        if (element instanceof Step) {
            this.partitionEnabled = ((Step) element).isPartitionEnabled();
            if (((Step) element).getPartition() == null) {
                this.runtimePlanning = false;
                this.partition = null;
            } else {
                this.runtimePlanning = ((Step) element).getPartition().isPartitionPlanDefinedAtRuntime();
                this.partition = ((Step) element).getPartition();
            }


        } else this.partitionEnabled = false;
    }

    // *****************************************************************
    // *                         METHODS                               *
    // *****************************************************************


    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }


    public Partition getPartition() {
        return partition;
    }

    public void setPartition(Partition partition) {
        this.partition = partition;
    }

    public Boolean isPartitionEnabled() {
        return partitionEnabled;
    }

    public void setPartitionEnabled(boolean partitionEnabled) {
        this.partitionEnabled = partitionEnabled;
    }

    public boolean isRuntimePlanning() {
        return runtimePlanning;
    }

    public void setRuntimePlanning(boolean runtimePlanning) {
        this.runtimePlanning = runtimePlanning;
    }
}
