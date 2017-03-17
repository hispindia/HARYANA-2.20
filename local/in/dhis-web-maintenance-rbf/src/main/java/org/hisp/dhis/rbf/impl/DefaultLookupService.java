package org.hisp.dhis.rbf.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.hisp.dhis.rbf.api.LookupStore;

public class DefaultLookupService
implements LookupService
{
// -------------------------------------------------------------------------
// Dependencies
// -------------------------------------------------------------------------

private LookupStore lookupStore;

public void setLookupStore( LookupStore lookupStore )
{
    this.lookupStore = lookupStore;
}

// -------------------------------------------------------------------------
// Lookup
// -------------------------------------------------------------------------

@Override
public void addLookup( Lookup lookup )
{
    lookupStore.addLookup( lookup );
}

@Override
public void updateLookup( Lookup lookup )
{
    lookupStore.updateLookup( lookup );
}

@Override
public void deleteLookup( Lookup lookup )
{
    lookupStore.deleteLookup( lookup );
}

@Override
public Lookup getLookup( int id )
{
    return lookupStore.getLookup( id );
}

@Override
public Lookup getLookupByName( String name )
{
    return lookupStore.getLookupByName( name );
}

@Override
public Collection<Lookup> getAllLookupsByType( String type )
{
    return lookupStore.getAllLookupsByType( type );
}

@Override
public Collection<Lookup> getAllLookups()
{
    return lookupStore.getAllLookups();
}


@Override
public Collection<Lookup> getAllLookupsSortByType()
{
    return lookupStore.getAllLookupsSortByType();
}

// Search lookup by name
public void searchLookupByName( List<Lookup> lookups, String key )
{
    Iterator<Lookup> iterator = lookups.iterator();

    while ( iterator.hasNext() )
    {
        if ( !iterator.next().getName().toLowerCase().contains( key.toLowerCase() ) )
        {
            iterator.remove();
        }
    }
}

}
