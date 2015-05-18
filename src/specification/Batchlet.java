package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Tomas Hanus on 2/21/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Batchlet {

    private String id;

    // Attributes
    @XmlAttribute
    private String ref;

    @XmlElement
    private Properties properties;

    public Batchlet(String id) {
        this.id = id;
        this.ref = this.id + "_ref";
    }

    public Batchlet() {
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}