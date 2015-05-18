package specification;

import designer.ui.editor.element.StartElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.awt.*;

/**
 *  Created by Tomas Hanus on 3/30/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Start extends StartElement {

    @XmlAttribute(name = "next")
    private String nextElementId;

    public Start() {
        super();
    }

    public Start(String id) {
        this.setId(id);
    }

    public Start(Point p) {
        super(p);
    }

    public String getNextElementId() {
        return this.nextElementId;
    }

    ;

    public void setNextElementId(String next) {
        this.nextElementId = next;
    }

}
