package specification;

import designer.ui.editor.element.Element;
import designer.ui.editor.element.SplitElement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 4/3/2015.
 */
public class Split extends SplitElement {
    @XmlAttribute(name = "next")
    private String nextElementId;

    @XmlElements({
            @XmlElement(name = "flow", type = Flow.class)
    })
    private List<Element> elements = null;

    // *****************************************************************
    // *                      CONSTRUCTORS                             *
    // *****************************************************************

    public Split() {
    }

    ;

    public Split(String id) {
        this.setId(id);
    }

    // *****************************************************************
    // *                         METHODS                               *
    // *****************************************************************

    public List<Element> getElements() {
        return this.elements;
    }

    public void addElement(Element element) {
        if (this.elements == null) this.elements = new ArrayList<Element>();
        this.elements.add(element);
    }

    public List<Element> getAllElements() {
        return this.elements;
    }

    public void removeElement(Element element) {
        if (this.elements != null)
            this.elements.remove(element);
    }

    public void removeAllElements() {
        this.elements = null;
    }


    public String getNextElementId() {
        return this.nextElementId;
    }

    ;

    public void setNextElementId(String next) {
        this.nextElementId = next;
    }

}