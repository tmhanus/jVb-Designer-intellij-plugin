package specification;

import designer.ui.editor.element.Element;
import designer.ui.editor.element.JobElement;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by Tomas Hanus on 3/22/2015.
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "job")
//@XmlRootElement(name = "job", namespace = "http://xmlns.jcp.org/xml/ns/javaee")


public class Job extends JobElement {
    private static final Boolean RESTARTABLE_DEFAULT = true;

    @XmlAttribute
    private boolean restartable;
    @XmlAttribute
    private final String version = "1.0";

    @XmlElement
    private Properties properties;

    @XmlElement
    private Listeners listeners;

    @XmlElements({
            @XmlElement(name = "step", type = Step.class),
            @XmlElement(name = "split", type = Split.class),
            @XmlElement(name = "flow", type = Flow.class),
            @XmlElement(name = "decision", type = Decision.class)
    })
    private List<Element> elements = null;

    public Job() {
        super();
    }

    public Job(String id) {
        this.setId(id);
        this.restartable = RESTARTABLE_DEFAULT;
    }

    public Element getElement(String id) {
        if (this.elements != null)
            for (Element element : this.elements) {
                if (element.getId().equals(id)) return element;
            }
        return null;
    }

    public List<Element> getElements() {
        return this.elements;
    }

    public void addElement(Element element) {
        if (this.elements == null) this.elements = new ArrayList<Element>();
        this.elements.add(element);
    }

    public void addElement(int i, Element element) {
        if (this.elements == null) this.elements = new ArrayList<Element>();
        this.elements.add(i, element);
    }

    public void removeElement(Element element) {
        this.elements.remove(element);
    }

    public boolean isRestartable() {
        return restartable;
    }

    public void setRestartable(boolean restartable) {
        this.restartable = restartable;
    }

    public Listeners getListeners() {
        return listeners;
    }

    public void setListeners(Listeners listeners) {
        this.listeners = listeners;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
