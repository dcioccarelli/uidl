package net.uidl.example;

import java.util.List;
import java.util.LinkedList;

/**
 * Implementation for UserManager example
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public class UserManagerImpl implements UserManager
{
    public List getUsers()
    {
        List users = new LinkedList();
        User user;

        for(int i=0; i<100; i++)
        {
            user = new User();
            user.setFirstName("First " + i);
            user.setLastName("Last " + i);
            user.setEmail("e-mail " + i);
            users.add(user);
        }

        return users;
    }
}
