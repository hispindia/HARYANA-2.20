package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

public interface CatalogTypeAttributeStore extends GenericNameableObjectStore<CatalogTypeAttribute>
{
    String ID = CatalogTypeAttributeStore.class.getName();
    
    /*
    int addCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute );
    
    void updateCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute );

    void deleteCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute );
    */
    CatalogTypeAttribute getCatalogTypeAttribute( int id );
    
    CatalogTypeAttribute getCatalogTypeAttributeByName( String name );
    
    Collection<CatalogTypeAttribute> getAllCatalogTypeAttributes();

}
