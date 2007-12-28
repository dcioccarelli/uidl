package net.uidl.carsales;

import javax.persistence.*;
import java.util.List;
import java.io.Serializable;

@Entity
public class Model implements Serializable
{
    private String          name;
    private String          description;
    private List<Option>    options;

    @Id
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.EAGER)
    public List<Option> getOptions()
    {
        return options;
    }

    public void setOptions(List<Option> options)
    {
        this.options = options;
    }

    public String toString()
    {
        return this.getName();
    }
}
