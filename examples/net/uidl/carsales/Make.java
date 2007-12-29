package net.uidl.carsales;

import javax.persistence.*;
import java.util.List;
import java.io.Serializable;

/**
 * Make entity object for carsales example application
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
@Entity
public class Make implements Serializable
{
    private String          name;
    private String          description;
    private List<Model>     models;
    private List<Option>    options;
    // private List<Customer>  customers;

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

    // @OneToMany(mappedBy="make")
    // @OrderBy("name")
    @OneToMany(cascade=CascadeType.ALL)
    // @JoinColumn(name="CUST_ID")
    public List<Model> getModels()
    {
        return models;
    }

    public void setModels(List<Model> models)
    {
        this.models = models;
    }

    // @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.EAGER)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public List<Option> getOptions()
    {
        return options;
    }

    public void setOptions(List<Option> options)
    {
        this.options = options;
    }

    /*
    @ManyToMany(mappedBy="preferences", targetEntity=Customer.class)
    public List<Customer> getCustomers()
    {
        return customers;
    }

    public void setCustomers(List<Customer> customers)
    {
        this.customers = customers;
    }
    */

    public String toString()
    {
        return name;
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Make make = (Make) o;

        if (!name.equals(make.name)) return false;

        return true;
    }

    public int hashCode()
    {
        return name.hashCode();
    }
}
