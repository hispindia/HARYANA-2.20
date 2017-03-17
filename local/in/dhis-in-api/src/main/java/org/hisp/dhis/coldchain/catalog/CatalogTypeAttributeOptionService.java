package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogTypeAttributeOptionService
{
    String ID = CatalogTypeAttributeOptionService.class.getName();
    
    int addCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption );

    void updateCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption );

    void deleteCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption );
    
    CatalogTypeAttributeOption getCatalogTypeAttributeOption( int id );
    
    int countByCatalogTypeAttributeoption( CatalogTypeAttributeOption catalogTypeAttributeOption );

    Collection<CatalogTypeAttributeOption> getCatalogTypeAttributeOptions( CatalogTypeAttribute catalogTypeAttribute );
    
    CatalogTypeAttributeOption getCatalogTypeAttributeOptionName( CatalogTypeAttribute catalogTypeAttribute, String name );
    
    Collection<CatalogTypeAttributeOption> getAllCatalogTypeAttributeOptions();

}
