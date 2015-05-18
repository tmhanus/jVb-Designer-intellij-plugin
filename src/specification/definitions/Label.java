package specification.definitions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *  Created by Tomas Hanus on 4/8/2015.
 */

@XmlAccessorType(XmlAccessType.NONE)
public class Label {
    private Position position;
    @XmlElement
    private String text;

    // *****************************************************************
    // *                      CONSTRUCTORS                             *
    // *****************************************************************

    public Label() {
    }

    public Label(Position position, String text) {
        this.position = position;
        this.text = text;
    }

    // *****************************************************************
    // *                         METHODS                               *
    // *****************************************************************

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
