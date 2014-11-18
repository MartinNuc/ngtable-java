package cz.nuc.ngtablejava.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class FilterParam {
    public Integer itemsPerPage;
    public Integer firstResult;
    @XmlElementWrapper(name = "filterByFields")
    @XmlElement(name = "filterByFields")
    public Map<String, String> filterByFields;
    @XmlElementWrapper(name = "orderBy")
    @XmlElement(name = "orderBy")
    public Map<String, String> orderBy;
}
