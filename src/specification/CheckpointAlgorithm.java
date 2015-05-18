package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *  Created by Tomas Hanus on 4/12/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CheckpointAlgorithm {
    @XmlAttribute
    private String ref;

    @XmlElement
    private Properties properties;

    public CheckpointAlgorithm() {
    }

    public CheckpointAlgorithm(String ref) {
        this.ref = ref;
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
