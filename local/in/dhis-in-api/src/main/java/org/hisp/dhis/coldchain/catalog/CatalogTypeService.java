package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogTypeService
{
    String ID = CatalogTypeService.class.getName();
    
    int addCatalogType( CatalogType catalogType );

    void updateCatalogType( CatalogType catalogType );

    void deleteCatalogType( CatalogType catalogType );
    
    CatalogType getCatalogType( int id );
    
    CatalogType getCatalogTypeByName( String name );

    Collection<CatalogType> getAllCatalogTypes();
    
    //  methods
    
    int getCatalogTypeCount();
    
    int getCatalogTypeCountByName( String name );
    
    Collection<CatalogType> getCatalogTypesBetween( int first, int max );
    
    Collection<CatalogType> getCatalogTypesBetweenByName( String name, int first, int max );
    
    /*
    //Methods For Display
    CatalogTypeAttribute getCatalogTypeAttributeForDisplay( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute, boolean display );
    
    Collection<CatalogTypeAttribute> getAllCatalogTypeAttributeForDisplay( CatalogType catalogType, boolean display );
    */
    Collection<CatalogTypeAttribute> getAllCatalogTypeAttributeForDisplay( CatalogType catalogType );
    
}
