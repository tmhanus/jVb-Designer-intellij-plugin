package specification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.util.List;

/**
 *  Created by Tomas Hanus on 4/12/2015.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ExceptionClasses {
    @XmlElements({
            @XmlElement(name = "include", type = IncludeClass.class)
    })
    private List<IncludeClass> includeClasses;

    @XmlElements({
            @XmlElement(name = "exclude", type = ExcludeClass.class)
    })
    private List<ExcludeClass> excludeClasses;

    public ExceptionClasses() {
    }

    public List<IncludeClass> getIncludeClasses() {
        return includeClasses;
    }

    public void setIncludeClasses(List<IncludeClass> includeClasses) {
        this.includeClasses = includeClasses;
    }

    public List<ExcludeClass> getExcludeClasses() {
        return excludeClasses;
    }

    public void setExcludeClasses(List<ExcludeClass> excludeClasses) {
        this.excludeClasses = excludeClasses;
    }

}
