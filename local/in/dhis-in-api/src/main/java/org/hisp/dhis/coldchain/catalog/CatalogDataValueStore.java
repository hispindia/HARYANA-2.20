package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogDataValueStore
{
    String ID = CatalogDataValueStore.class.getName();
    
    void addCatalogDataValue( CatalogDataValue catalogDataValue );

    void updateCatalogDataValue( CatalogDataValue catalogDataValue );

    void deleteCatalogDataValue( CatalogDataValue catalogDataValue );

    Collection<CatalogDataValue> getAllCatalogDataValues();
    
    Collection<CatalogDataValue> getAllCatalogDataValuesByCatalog( Catalog catalog );
    
    CatalogDataValue catalogDataValue( Catalog catalog ,CatalogTypeAttribute catalogTypeAttribute );
    
    CatalogDataValue catalogDataValue( Catalog catalog ,CatalogTypeAttribute catalogTypeAttribute, CatalogTypeAttributeOption catalogTypeAttributeOption );

}
