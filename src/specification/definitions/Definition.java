package specification.definitions;

import designer.ui.editor.element.Element;
import designer.ui.editor.element.SplitElementEnd;
import specification.Start;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 4/8/2015.
 */
@XmlRootElement
public class Definition {
    @XmlElementWrapper(name = "elements-spec")
    @XmlElements({
            @XmlElement(name = "element-spec", type = ElementSpec.class)
    })
    private List<ElementSpec> elementsSpec;

    @XmlElementWrapper(name = "aditional-elements")
    @XmlElements({
            @XmlElement(name = "additional-element", type = AdditionalElement.class)
    })
    private List<AdditionalElement> additionalElements;

    @XmlElementWrapper(name = "edges-spec")
    @XmlElements({
            @XmlElement(name = "edge-spec", type = EdgeSpec.class)
    })
    private List<EdgeSpec> edgesSpec;

    // *****************************************************************
    // *                      CONSTRUCTORS                             *
    // *****************************************************************

    public Definition() {
    }

    // *****************************************************************
    // *                         METHODS                               *
    // *****************************************************************

    public void createElementsSpecification(List<Element> elements) {
        if (this.elementsSpec == null) this.elementsSpec = new ArrayList<ElementSpec>();
        for (Element element : elements) {
            if ((element instanceof Start) || (element instanceof SplitElementEnd)) {
                if (this.additionalElements == null) this.additionalElements = new ArrayList<AdditionalElement>();
                this.additionalElements.add(new AdditionalElement(element));
            } else {
                ElementSpec elementSpecification = new ElementSpec(element);
                this.elementsSpec.add(elementSpecification);
            }
        }
    }

    public void createElementSpecification(Element element) {
        if (element != null) {
            this.elementsSpec.add(new ElementSpec(element));
        }
    }

    public ElementSpec getElementSpec(String id) {
        if (this.elementsSpec == null) return null;
        for (ElementSpec elementSpec : this.elementsSpec) {
            if (elementSpec.getElementId() != null && elementSpec.getElementId().equals(id)) {
                return elementSpec;
            }
        }
        return null;
    }

    public List<ElementSpec> getAllElementSpec() {
        return this.elementsSpec;
    }

    public List<AdditionalElement> getAdditionalElements() {
        return this.additionalElements;
    }

}
