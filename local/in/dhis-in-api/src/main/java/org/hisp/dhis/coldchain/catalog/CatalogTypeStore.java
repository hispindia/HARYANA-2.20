package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

//public interface CatalogTypeStore
public interface CatalogTypeStore extends GenericNameableObjectStore<CatalogType>
{
    String ID = CatalogTypeStore.class.getName();
    /*
    int addCatalogType( CatalogType catalogType );

    void updateCatalogType( CatalogType catalogType );

    void deleteCatalogType( CatalogType catalogType );
    */
    
    CatalogType getCatalogType( int id );
    
    CatalogType getCatalogTypeByName( String name );
    
    //CatalogType getCatalogTypeByAttribute( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute);

    Collection<CatalogType> getAllCatalogTypes();
    
    /*
    //Methods For Display
    CatalogTypeAttribute getCatalogTypeAttributeForDisplay( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute, boolean display );
    
    Collection<CatalogTypeAttribute> getAllCatalogTypeAttributeForDisplay( CatalogType catalogType, boolean display );
    */

}
