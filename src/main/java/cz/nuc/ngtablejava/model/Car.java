package cz.nuc.ngtablejava.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * Created by mist on 17/11/14.
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Car {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @ManyToOne
    @XmlTransient
    private Member owner;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Member getOwner() {
        return owner;
    }
}
