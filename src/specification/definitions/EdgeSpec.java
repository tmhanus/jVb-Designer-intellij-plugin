package specification.definitions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *  Created by Tomas Hanus on 4/8/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class EdgeSpec {
    @XmlAttribute
    private String from;
    @XmlAttribute
    private String to;
    @XmlElement
    private Label label;

    // *****************************************************************
    // *                      CONSTRUCTORS                             *
    // *****************************************************************

    public EdgeSpec() {
    }

    public EdgeSpec(String from, String to, Label label) {
        this.from = from;
        this.to = to;
        this.label = label;
    }

    // *****************************************************************
    // *                         METHODS                               *
    // *****************************************************************

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
