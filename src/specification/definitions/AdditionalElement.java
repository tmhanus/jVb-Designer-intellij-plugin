package specification.definitions;

import designer.ui.editor.element.Element;
import designer.ui.editor.element.SplitElementEnd;
import specification.Start;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**
 *  Created by Tomas Hanus on 4/8/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class AdditionalElement {

    @XmlElements({
            @XmlElement(name = "start", type = Start.class),
            @XmlElement(name = "splitEnd", type = SplitElementEnd.class)
    })
    private Element element;

    @XmlElement(required = true)
    private Position position;

    @XmlElement(required = true)
    private String designerContextid;


    public AdditionalElement() {
    }

    public AdditionalElement(Element element) {
        this.element = element;
        this.position = new Position(element.getPosition().x, element.getPosition().y);
        this.designerContextid = element.getDesignerContext().getContextId();
    }

    public Element getElement() {
        return element;
    }

    public Position getPosition() {
        return position;
    }

    public String getDesignerContextid() {
        return designerContextid;
    }
}
