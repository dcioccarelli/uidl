package net.uidl.example;

import java.io.Serializable;

/**
 * User entity object for UserManager example
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public class User implements Serializable
{
    private String firstName;
    private String lastName;
    private String email;

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
