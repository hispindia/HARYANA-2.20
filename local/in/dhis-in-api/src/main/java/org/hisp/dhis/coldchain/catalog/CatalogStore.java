package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

public interface CatalogStore extends GenericNameableObjectStore<Catalog>
{

    String ID = CatalogStore.class.getName();
    /*
    int addCatalog( Catalog catalog );

    void updateCatalog( Catalog catalog );

    void deleteCatalog( Catalog catalog );
    */
    Catalog getCatalog( int id );
    
    Catalog getCatalogByName( String name );

    Collection<Catalog> getAllCatalogs();
    
    Collection<Catalog> getCatalogs( CatalogType catalogType );
    
    
    
    int getCountCatalog( CatalogType catalogType );
    
    Collection<Catalog> getCatalogs( CatalogType catalogType, int min, int max );
    
    /*
    int getCountCatalog( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute, String searchText );
    
    Collection<Catalog> getCatalogs( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute, String searchText, int min, int max );
    */
    int getCountCatalog( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute, String searchText, String searchBy  );
    
    Collection<Catalog> getCatalogs( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute, String searchText, String searchBy, int min, int max );
}
