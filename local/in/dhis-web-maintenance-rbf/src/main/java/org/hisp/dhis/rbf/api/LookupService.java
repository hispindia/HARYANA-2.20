package org.hisp.dhis.rbf.api;

import java.util.Collection;
import java.util.List;

public interface LookupService
{
    String ID = LookupService.class.getName();

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
    
    void searchLookupByName( List<Lookup> lookups, String key );
}
