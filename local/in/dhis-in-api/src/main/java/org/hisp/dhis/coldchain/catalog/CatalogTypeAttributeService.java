package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogTypeAttributeService
{
    String ID = CatalogTypeAttributeService.class.getName();
    
    int addCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute );

    void updateCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute );

    void deleteCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute );
    
    CatalogTypeAttribute getCatalogTypeAttribute( int id );
    
    CatalogTypeAttribute getCatalogTypeAttributeByName( String name );
    
    //CatalogType getCatalogTypeByAttribute( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute);

    Collection<CatalogTypeAttribute> getAllCatalogTypeAttributes();
    
    //  methods
    
    int getCatalogTypeAttributeCount();
    
    int getCatalogTypeAttributeCountByName( String name );
    
    Collection<CatalogTypeAttribute> getCatalogTypeAttributesBetween( int first, int max );
    
    Collection<CatalogTypeAttribute> getCatalogTypeAttributesBetweenByName( String name, int first, int max );
    
}
