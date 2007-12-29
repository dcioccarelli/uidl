package net.uidl.carsales;

import javax.persistence.*;
import java.util.List;
import java.io.Serializable;

/**
 * Option entity object implementation for carsales example application
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
@Entity
@Table(name="Options")
public class Option implements Serializable
{
    private int         id;
    private String      name;
    private String      description;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

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

    public String toString()
    {
        return id + ": " + name;
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (id != option.id) return false;

        return true;
    }

    public int hashCode()
    {
        return id;
    }
}
