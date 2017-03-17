package org.hisp.dhis.rbf.api;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

public interface LookupStore extends GenericNameableObjectStore<Lookup>
{
    String ID = LookupStore.class.getName();
    
    // -------------------------------------------------------------------------
    // Lookup
    // -------------------------------------------------------------------------
    
    void addLookup( Lookup lookup );

    void updateLookup( Lookup lookup );

    void deleteLookup( Lookup lookup );

    Lookup getLookup( int id );

    Lookup getLookupByName( String name );
    
    Collection<Lookup> getAllLookupsByType( String type );
    
    Collection<Lookup> getAllLookups();
    
    Collection<Lookup> getAllLookupsSortByType();
}
