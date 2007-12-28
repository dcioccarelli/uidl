package net.uidl.util;

import org.hibernate.collection.AbstractPersistentCollection;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;

public class BeanUtils
{
    public static String getClassName(Class c)
    {
        String FQClassName = c.getName();
        int firstChar;
        firstChar = FQClassName.lastIndexOf('.') + 1;
        if (firstChar > 0)
        {
            FQClassName = FQClassName.substring(firstChar);
        }
        return FQClassName;
    }

    // ----------------------------------------------------- Manifest Constants


    /**
     * The delimiter that preceeds the zero-relative subscript for an
     * indexed reference.
     */
    public static final char INDEXED_DELIM = '[';


    /**
     * The delimiter that follows the zero-relative subscript for an
     * indexed reference.
     */
    public static final char INDEXED_DELIM2 = ']';


    /**
     * The delimiter that separates the components of a nested reference.
     */
    public static final char NESTED_DELIM = '.';


    // ------------------------------------------------------- Static Variables


    /**
     * The debugging detail level for this component.
     */
    private static int debug = 0;

    public static int getDebug()
    {
        return (debug);
    }

    public static void setDebug(int newDebug)
    {
        debug = newDebug;
    }


    /**
     * The cache of PropertyDescriptor arrays for beans we have already
     * introspected, keyed by the fully qualified class name of this object.
     */
    private static HashMap descriptorsCache = null;

    static
    {
        descriptorsCache = new HashMap();
    }

    // --------------------------------------------------------- Public Methods

    public static synchronized Collection hibernateCollectionFilter(Collection list)
        throws Exception
    {
        Collection newList = list.getClass().newInstance();

        for (Object o : list)
            newList.add(hibernateFilter(o));

        list = newList;

        return newList;
    }

    public static Object hibernateFilter(Object orig)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, InstantiationException
    {
        return hibernateFilter(orig, 0);
    }

    private static Object hibernateFilter(Object orig, int depth)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, InstantiationException
    {
        if (orig == null)
            throw new IllegalArgumentException("No origin bean specified");

        if (depth > 4)
            return orig;
        else
            depth ++;
        Object dest = orig.getClass().newInstance();

        PropertyDescriptor origDescriptors[] = getPropertyDescriptors(orig);
        for (int i = 0; i < origDescriptors.length; i++)
        {
            String name = origDescriptors[i].getName();
            if (getPropertyDescriptor(dest, name) != null)
            {
                Object value = getSimpleProperty(orig, name);
                try
                {
                    if (value instanceof AbstractPersistentCollection)
                    {
                        Collection tmp = copyCollection((Collection)value, LinkedList.class, depth);
                        setSimpleProperty(dest, name, tmp);
                    }
                    else
                        setSimpleProperty(dest, name, value);
                }
                catch (NoSuchMethodException e)
                {
                    ;   // Skip non-matching property
                }
            }
        }

        return dest;
    }

    private static Collection copyCollection(Collection in, Class collectionType, int depth)
    {
        Collection out = null;

        try
        {
            out = (Collection)collectionType.newInstance();
            assert (out instanceof Collection) : "Input type is not a Collection.";

            Object outObj;
            for (Object inObj : in)
            {
                outObj = inObj.getClass().newInstance();
                copyProperties(outObj, inObj);
                out.add(hibernateFilter(outObj, depth));
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception copying collection: " + e.getMessage());
        }

        return out;
    }

    /**
     * Copy property values from the "origin" bean to the "destination" bean
     * for all cases where the property names are the same (even though the
     * actual getter and setter methods might have been customized via
     * <code>BeanInfo</code> classes).  No conversions are performed on the
     * actual property values -- it is assumed that the values retrieved from
     * the origin bean are assignment-compatible with the types expected by
     * the destination bean.
     *
     * @param dest Destination bean whose properties are modified
     * @param orig Origin bean whose properties are retrieved
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if the <code>dest</code> or
     *                                  <code>orig</code> argument is null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static void copyProperties(Object dest, Object orig)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (dest == null)
            throw new IllegalArgumentException
                    ("No destination bean specified");
        if (orig == null)
            throw new IllegalArgumentException("No origin bean specified");

        PropertyDescriptor origDescriptors[] = getPropertyDescriptors(orig);
        for (int i = 0; i < origDescriptors.length; i++)
        {
            String name = origDescriptors[i].getName();
            if (getPropertyDescriptor(dest, name) != null)
            {
                Object value = getSimpleProperty(orig, name);
                try
                {
                    setSimpleProperty(dest, name, value);
                }
                catch (NoSuchMethodException e)
                {
                    ;   // Skip non-matching property
                }
            }
        }

    }

    public static void copyPropertiesNoNull(Object dest, Object orig)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (dest == null)
            throw new IllegalArgumentException
                    ("No destination bean specified");
        if (orig == null)
            throw new IllegalArgumentException("No origin bean specified");

        PropertyDescriptor origDescriptors[] = getPropertyDescriptors(orig);
        for (int i = 0; i < origDescriptors.length; i++)
        {
            String name = origDescriptors[i].getName();
            if (getPropertyDescriptor(dest, name) != null)
            {
                Object value = null;
                try
                {
                    value = getSimpleProperty(orig, name);
                }
                catch (Exception e)
                {
                    System.err.println("Error setting: " + name);
                }

                // skip if the input value is null or an empty string
                if ((value == null) || value.equals(""))
                    continue;

                try
                {
                    setSimpleProperty(dest, name, value);
                }
                catch (NoSuchMethodException e)
                {
                    ;   // Skip non-matching property
                }
            }
        }

    }

    /**
     * Return the entire set of properties for which the specified bean
     * provides a read method.  This map contains the unconverted property
     * values for all properties for which a read method is provided
     * (i.e. where the <code>getReadMethod()</code> returns non-null).
     *
     * @param bean Bean whose properties are to be extracted
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> is null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static Map describe(Object bean)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        PropertyDescriptor descriptors[] =
                BeanUtils.getPropertyDescriptors(bean);
        Map description = new HashMap(descriptors.length);
        for (int i = 0; i < descriptors.length; i++)
        {
            String name = descriptors[i].getName();
            if (descriptors[i].getReadMethod() != null)
                description.put(name, getProperty(bean, name));
        }
        return (description);

    }


    /**
     * Return the value of the specified indexed property of the specified
     * bean, with no type conversions.  The zero-relative index of the
     * required value must be included (in square brackets) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname[index]</code> of the property value
     *             to be extracted
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static Object getIndexedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        // Identify the index of the requested individual property
        int delim = name.indexOf(INDEXED_DELIM);
        int delim2 = name.indexOf(INDEXED_DELIM2);
        if ((delim < 0) || (delim2 <= delim))
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "'");
        int index = -1;
        try
        {
            String subscript = name.substring(delim + 1, delim2);
            index = Integer.parseInt(subscript);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "'");
        }
        name = name.substring(0, delim);

        // Request the specified indexed property value
        return (getIndexedProperty(bean, name, index));

    }


    /**
     * Return the value of the specified indexed property of the specified
     * bean, with no type conversions.
     *
     * @param bean  Bean whose property is to be extracted
     * @param name  Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static Object getIndexedProperty(Object bean,
                                            String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        // Retrieve the property descriptor for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");

        // Call the indexed getter method if there is one
        if (descriptor instanceof IndexedPropertyDescriptor)
        {
            Method readMethod = ((IndexedPropertyDescriptor) descriptor).
                    getIndexedReadMethod();
            if (readMethod != null)
            {
                Object subscript[] = new Object[1];
                subscript[0] = new Integer(index);
                return (readMethod.invoke(bean, subscript));
            }
        }

        // Otherwise, the underlying property must be an array
        Method readMethod = getReadMethod(descriptor);
        if (readMethod == null)
            throw new NoSuchMethodException("Property '" + name +
                    "' has no getter method");

        // Call the property getter and return the value
        Object value = readMethod.invoke(bean, new Object[0]);
        if (!value.getClass().isArray())
            throw new IllegalArgumentException("Property '" + name +
                    "' is not indexed");
        return (Array.get(value, index));

    }


    /**
     * Return the value of the (possibly nested) property of the specified
     * name, for the specified bean, with no type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws IllegalArgumentException if a nested reference to a
     *                                  property returns null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static Object getNestedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        while (true)
        {
            int delim = name.indexOf(NESTED_DELIM);
            if (delim < 0)
                break;
            String next = name.substring(0, delim);
            if (next.indexOf(INDEXED_DELIM) >= 0)
                bean = getIndexedProperty(bean, next);
            else
                bean = getSimpleProperty(bean, next);
            if (bean == null)
                throw new IllegalArgumentException
                        ("Null property value for '" +
                        name.substring(0, delim) + "'");
            name = name.substring(delim + 1);
        }

        if (name.indexOf(INDEXED_DELIM) >= 0)
            return (getIndexedProperty(bean, name));
        else
            return (getSimpleProperty(bean, name));

    }


    /**
     * Return the value of the specified property of the specified bean,
     * no matter which property reference format is used, with no
     * type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *             to be extracted
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static Object getProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        return (getNestedProperty(bean, name));

    }


    /**
     * Retrieve the property descriptor for the specified property of the
     * specified bean, or return <code>null</code> if there is no such
     * descriptor.  This method resolves indexed and nested property
     * references in the same manner as other methods in this class, except
     * that if the last (or only) name element is indexed, the descriptor
     * for the last resolved property itself is returned.
     *
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *             which a property descriptor is requested
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws IllegalArgumentException if a nested reference to a
     *                                  property returns null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static PropertyDescriptor getPropertyDescriptor(Object bean,
                                                           String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        // Resolve nested references
        while (true)
        {
            int period = name.indexOf(NESTED_DELIM);
            if (period < 0)
                break;
            String next = name.substring(0, period);
            if (next.indexOf(INDEXED_DELIM) >= 0)
                bean = getIndexedProperty(bean, next);
            else
                bean = getSimpleProperty(bean, next);
            if (bean == null)
                throw new IllegalArgumentException
                        ("Null property value for '" +
                        name.substring(0, period) + "'");
            name = name.substring(period + 1);
        }

        // Remove any subscript from the final name value
        int left = name.indexOf(INDEXED_DELIM);
        if (left >= 0)
            name = name.substring(0, left);

        // Look up and return this property from our cache
        if ((bean == null) || (name == null))
            return (null);
        PropertyDescriptor descriptors[] = getPropertyDescriptors(bean);
        if (descriptors == null)
            return (null);
        for (int i = 0; i < descriptors.length; i++)
        {
            if (name.equals(descriptors[i].getName()))
                return (descriptors[i]);
        }
        return (null);

    }


    /**
     * Retrieve the property descriptors for the specified bean, introspecting
     * and caching them the first time a particular bean class is encountered.
     *
     * @param bean Bean for which property descriptors are requested
     * @throws IllegalArgumentException if <code>bean</code> is null
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Object bean)
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");

        // Look up any cached descriptors for this bean class
        String beanClassName = bean.getClass().getName();
        PropertyDescriptor descriptors[] = null;
        descriptors =
                (PropertyDescriptor[]) descriptorsCache.get(beanClassName);
        if (descriptors != null)
            return (descriptors);

        // Introspect the bean and cache the generated descriptors
        BeanInfo beanInfo = null;
        try
        {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        }
        catch (IntrospectionException e)
        {
            return (new PropertyDescriptor[0]);
        }
        descriptors = beanInfo.getPropertyDescriptors();
        if (descriptors == null)
            descriptors = new PropertyDescriptor[0];
        descriptorsCache.put(beanClassName, descriptors);
        return (descriptors);

    }


    /**
     * Return the Java Class repesenting the property editor class that has
     * been registered for this property (if any).  This method follows the
     * same name resolution rules used by <code>getPropertyDescriptor()</code>,
     * so if the last element of a name reference is indexed, the property
     * editor for the underlying property's class is returned.
     * <p/>
     * Note that <code>null</code> will be returned if there is no property,
     * or if there is no registered property editor class.  Because this
     * return value is ambiguous, you should determine the existence of the
     * property itself by other means.
     *
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *             which a property descriptor is requested
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws IllegalArgumentException if a nested reference to a
     *                                  property returns null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static Class getPropertyEditorClass(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor != null)
            return (descriptor.getPropertyEditorClass());
        else
            return (null);

    }


    /**
     * Return the Java Class representing the property type of the specified
     * property, or <code>null</code> if there is no such property for the
     * specified bean.  This method follows the same name resolution rules
     * used by <code>getPropertyDescriptor()</code>, so if the last element
     * of a name reference is indexed, the type of the property itself will
     * be returned.  If the last (or only) element has no property with the
     * specified name, <code>null</code> is returned.
     *
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *             which a property descriptor is requested
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws IllegalArgumentException if a nested reference to a
     *                                  property returns null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static Class getPropertyType(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null)
            return (null);
        else if (descriptor instanceof IndexedPropertyDescriptor)
            return (((IndexedPropertyDescriptor) descriptor).
                    getIndexedPropertyType());
        else
            return (descriptor.getPropertyType());

    }


    /**
     * Return an accessible property getter method for this property,
     * if there is one; otherwise return <code>null</code>.
     *
     * @param descriptor Property descriptor to return a getter for
     */
    public static Method getReadMethod(PropertyDescriptor descriptor)
    {

        return (getAccessibleMethod(descriptor.getReadMethod()));

    }


    /**
     * Return the value of the specified simple property of the specified
     * bean, with no type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws IllegalArgumentException if the property name
     *                                  is nested or indexed
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static Object getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        // Validate the syntax of the property name
        if (name.indexOf(NESTED_DELIM) >= 0)
            throw new IllegalArgumentException
                    ("Nested property names are not allowed");
        else if (name.indexOf(INDEXED_DELIM) >= 0)
            throw new IllegalArgumentException
                    ("Indexed property names are not allowed");

        // Retrieve the property getter method for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");
        Method readMethod = getReadMethod(descriptor);
        if (readMethod == null)
            throw new NoSuchMethodException("Property '" + name +
                    "' has no getter method");

        // Call the property getter and return the value
        Object value = readMethod.invoke(bean, new Object[0]);
        return (value);

    }


    /**
     * Return an accessible property setter method for this property,
     * if there is one; otherwise return <code>null</code>.
     *
     * @param descriptor Property descriptor to return a setter for
     */
    public static Method getWriteMethod(PropertyDescriptor descriptor)
    {

        return (getAccessibleMethod(descriptor.getWriteMethod()));

    }


    /**
     * Set the value of the specified indexed property of the specified
     * bean, with no type conversions.  The zero-relative index of the
     * required value must be included (in square brackets) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean  Bean whose property is to be modified
     * @param name  <code>propertyname[index]</code> of the property value
     *              to be modified
     * @param value Value to which the specified property element
     *              should be set
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static void setIndexedProperty(Object bean, String name,
                                          Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        // Identify the index of the requested individual property
        int delim = name.indexOf(INDEXED_DELIM);
        int delim2 = name.indexOf(INDEXED_DELIM2);
        if ((delim < 0) || (delim2 <= delim))
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "'");
        int index = -1;
        try
        {
            String subscript = name.substring(delim + 1, delim2);
            index = Integer.parseInt(subscript);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "'");
        }
        name = name.substring(0, delim);

        // Set the specified indexed property value
        setIndexedProperty(bean, name, index, value);

    }


    /**
     * Set the value of the specified indexed property of the specified
     * bean, with no type conversions.
     *
     * @param bean  Bean whose property is to be set
     * @param name  Simple property name of the property value to be set
     * @param index Index of the property value to be set
     * @param value Value to which the indexed property element is to be set
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static void setIndexedProperty(Object bean, String name,
                                          int index, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        // Retrieve the property descriptor for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");

        // Call the indexed setter method if there is one
        if (descriptor instanceof IndexedPropertyDescriptor)
        {
            Method writeMethod = ((IndexedPropertyDescriptor) descriptor).
                    getIndexedWriteMethod();
            if (writeMethod != null)
            {
                Object subscript[] = new Object[2];
                subscript[0] = new Integer(index);
                subscript[1] = value;
                writeMethod.invoke(bean, subscript);
                return;
            }
        }

        // Otherwise, the underlying property must be an array
        Method readMethod = descriptor.getReadMethod();
        if (readMethod == null)
            throw new NoSuchMethodException("Property '" + name +
                    "' has no getter method");

        // Call the property getter to get the array
        Object array = readMethod.invoke(bean, new Object[0]);
        if (!array.getClass().isArray())
            throw new IllegalArgumentException("Property '" + name +
                    "' is not indexed");

        // Modify the specified value
        Array.set(array, index, value);

    }


    /**
     * Set the value of the (possibly nested) property of the specified
     * name, for the specified bean, with no type conversions.
     *
     * @param bean  Bean whose property is to be modified
     * @param name  Possibly nested name of the property to be modified
     * @param value Value to which the property is to be set
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws IllegalArgumentException if a nested reference to a
     *                                  property returns null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static void setNestedProperty(Object bean,
                                         String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        while (true)
        {
            int delim = name.indexOf(NESTED_DELIM);
            if (delim < 0)
                break;
            String next = name.substring(0, delim);
            if (next.indexOf(INDEXED_DELIM) >= 0)
                bean = getIndexedProperty(bean, next);
            else
                bean = getSimpleProperty(bean, next);
            if (bean == null)
                throw new IllegalArgumentException
                        ("Null property value for '" +
                        name.substring(0, delim) + "'");
            name = name.substring(delim + 1);
        }

        if (name.indexOf(INDEXED_DELIM) >= 0)
            setIndexedProperty(bean, name, value);
        else
            setSimpleProperty(bean, name, value);

    }


    /**
     * Set the value of the specified property of the specified bean,
     * no matter which property reference format is used, with no
     * type conversions.
     *
     * @param bean  Bean whose property is to be modified
     * @param name  Possibly indexed and/or nested name of the property
     *              to be modified
     * @param value Value to which this property is to be set
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static void setProperty(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        setNestedProperty(bean, name, value);

    }


    /**
     * Set the value of the specified simple property of the specified bean,
     * with no type conversions.
     *
     * @param bean  Bean whose property is to be modified
     * @param name  Name of the property to be modified
     * @param value Value to which the property should be set
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws IllegalArgumentException if the property name is
     *                                  nested or indexed
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the property accessor method
     *                                  throws an exception
     * @throws NoSuchMethodException    if an accessor method for this
     *                                  propety cannot be found
     */
    public static void setSimpleProperty(Object bean,
                                         String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        // Validate the syntax of the property name
        if (name.indexOf(NESTED_DELIM) >= 0)
            throw new IllegalArgumentException
                    ("Nested property names are not allowed");
        else if (name.indexOf(INDEXED_DELIM) >= 0)
            throw new IllegalArgumentException
                    ("Indexed property names are not allowed");

        // Retrieve the property setter method for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");
        Method writeMethod = getWriteMethod(descriptor);
        if (writeMethod == null)
            throw new NoSuchMethodException("Property '" + name +
                    "' has no setter method");

        // Call the property setter method
        Object values[] = new Object[1];
        values[0] = value;
        writeMethod.invoke(bean, values);

    }


    // -------------------------------------------------------- Private Methods


    /**
     * Return an accessible method (that is, one that can be invoked via
     * reflection) that implements the specified Method.  If no such method
     * can be found, return <code>null</code>.
     *
     * @param method The method that we wish to call
     */
    private static Method getAccessibleMethod(Method method)
    {

        // Make sure we have a method to check
        if (method == null)
        {
            return (null);
        }

        // If the requested method is not public we cannot call it
        if (!Modifier.isPublic(method.getModifiers()))
        {
            return (null);
        }

        // If the declaring class is public, we are done
        Class clazz = method.getDeclaringClass();
        if (Modifier.isPublic(clazz.getModifiers()))
        {
            return (method);
        }

        // Check the implemented interfaces and subinterfaces
        String methodName = method.getName();
        Class[] parameterTypes = method.getParameterTypes();
        method =
                getAccessibleMethodFromInterfaceNest(clazz,
                        method.getName(),
                        method.getParameterTypes());
        return (method);

        /*
        Class[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            // Is this interface public?
            if (!Modifier.isPublic(interfaces[i].getModifiers())) {
                continue;
            }
            // Does the method exist on this interface?
            try {
                method = interfaces[i].getDeclaredMethod(methodName,
                                                         parameterTypes);
            } catch (NoSuchMethodException e) {
                continue;
            }
            // We have found what we are looking for
            return (method);
        }

        // We are out of luck
        return (null);
        */

    }


    /**
     * Return an accessible method (that is, one that can be invoked via
     * reflection) that implements the specified method, by scanning through
     * all implemented interfaces and subinterfaces.  If no such Method
     * can be found, return <code>null</code>.
     *
     * @param clazz          Parent class for the interfaces to be checked
     * @param methodName     Method name of the method we wish to call
     * @param parameterTypes The parameter type signatures
     */
    private static Method getAccessibleMethodFromInterfaceNest
            (Class clazz, String methodName, Class parameterTypes[])
    {

        Method method = null;

        // Check the implemented interfaces of the parent class
        Class interfaces[] = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++)
        {

            // Is this interface public?
            if (!Modifier.isPublic(interfaces[i].getModifiers()))
                continue;

            // Does the method exist on this interface?
            try
            {
                method = interfaces[i].getDeclaredMethod(methodName,
                        parameterTypes);
            }
            catch (NoSuchMethodException e)
            {
                ;
            }
            if (method != null)
                break;

            // Recursively check our parent interfaces
            method =
                    getAccessibleMethodFromInterfaceNest(interfaces[i],
                            methodName,
                            parameterTypes);
            if (method != null)
                break;

        }

        // Return whatever we have found
        return (method);

    }


}
