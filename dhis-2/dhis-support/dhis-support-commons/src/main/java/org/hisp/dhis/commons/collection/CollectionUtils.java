package org.hisp.dhis.commons.collection;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.commons.functional.Function1;
import org.hisp.dhis.commons.functional.Predicate;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility methods for operations on various collections.
 * 
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class CollectionUtils
{
    public static String[] STRING_ARR = new String[0];
    public static String[][] STRING_2D_ARR = new String[0][];

    /**
     * Applies the given Function1 on each member of the Collection.
     *
     * @param collection the Collection.
     * @param function the Function1.
     * @param <T> the type.
     */
    public static <T> void forEach( Collection<T> collection, Function1<T> function )
    {
        for ( T object : collection )
        {
            if ( object == null )
            {
                continue;
            }

            function.apply( object );
        }
    }

    /**
     * Filters the given Collection on the given Predicate.
     *
     * @param collection the Collection.
     * @param predicate the Predicate.
     * @param <T> the type.
     */
    public static <T> void filter( Collection<T> collection, Predicate<T> predicate )
    {
        Iterator<T> iterator = collection.iterator();

        while ( iterator.hasNext() )
        {
            T object = iterator.next();

            if ( !predicate.evaluate( object ) )
            {
                iterator.remove();
            }
        }
    }

    /**
     * Returns the intersection of the given Collections.
     *
     * @param c1 the first Collection.
     * @param c2 the second Collection.
     * @param <T> the type.
     * @return the intersection of the Collections.
     */
    public static <T> Collection<T> intersection( Collection<T> c1, Collection<T> c2 )
    {
        Set<T> set1 = new HashSet<>( c1 );
        set1.retainAll( new HashSet<>( c2 ) );
        return set1;
    }

    /**
     * Constructs a Map Entry (key, value). Used to construct a Map with asMap.
     *
     * @param key map entry key
     * @param value map entry value
     * @return entry with the key and value
     */
    public static <K, V> AbstractMap.SimpleEntry<K, V> asEntry( K key, V value )
    {
        return new AbstractMap.SimpleEntry<>( key, value );
    }

    /**
     * Constructs a Map from Entries, each containing a (key, value) pair.
     *
     * @param entries any number of (key, value) pairs
     * @return Map of the entries
     */
    @SafeVarargs
    public static final <K, V> Map<K, V> asMap( final AbstractMap.SimpleEntry<K, V>... entries )
    {
        Map<K, V> map = new HashMap<>();

        for ( AbstractMap.SimpleEntry<K, V> entry : entries )
        {
            map.put( entry.getKey(), entry.getValue() );
        }

        return map;
    }

    /**
     * Creates a map with the elements of the collection as values using the
     * specified keyMethod to obtain the key from the elements.
     *
     * @param collection the Collection.
     * @param keyMethod the name of the method to obtain the key.
     * @return Map of the elements.
     */
    @SuppressWarnings( "unchecked" )
    public static <K, T> Map<K, T> createMap( Collection<T> collection, String keyMethod )
    {
        Map<K, T> map = new HashMap<>( collection.size() );

        if ( collection.isEmpty() )
        {
            return map;
        }

        Class<?> elementClass = collection.iterator().next().getClass();

        Method getKeyMethod;

        try
        {
            getKeyMethod = elementClass.getMethod( keyMethod, new Class[0] );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to get key method", e );
        }

        for ( T element : collection )
        {
            K key;
            try
            {
                key = (K) getKeyMethod.invoke( element, (Object[]) null );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( "Failed to get key", e );
            }

            map.put( key, element );
        }

        return map;
    }

    /**
     * Creates a list of values extracted from the provided list using the
     * specified value method, keeping the order of the provided list.
     *
     * @param list the List.
     * @param valueMethod the name of the method to obtain the value.
     * @return an ordered List of the obtained values.
     */
    @SuppressWarnings( "unchecked" )
    public static <K, T> List<K> createList( List<T> list, String valueMethod )
    {
        List<K> valueList = new ArrayList<>( list.size() );

        if ( list.isEmpty() )
        {
            return valueList;
        }

        Class<?> elementClass = list.iterator().next().getClass();

        Method getValueMethod;

        try
        {
            getValueMethod = elementClass.getMethod( valueMethod, new Class[0] );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to get key method", e );
        }

        for ( T element : list )
        {
            K value;

            try
            {
                value = (K) getValueMethod.invoke( element, (Object[]) null );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( "Failed to get key", e );
            }

            valueList.add( value );
        }

        return valueList;
    }
}
