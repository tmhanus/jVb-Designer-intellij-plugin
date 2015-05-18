package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *  Created by Tomas Hanus on 4/12/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ExcludeClass {
    @XmlAttribute(name = "class")
    private String classToExclude;

    public ExcludeClass() {
    }

    public ExcludeClass(String classToExclude) {
        this.classToExclude = classToExclude;
    }

    public String getClassToExclude() {
        return classToExclude;
    }

    public void setClassToExclude(String classToExclude) {
        this.classToExclude = classToExclude;
    }
}