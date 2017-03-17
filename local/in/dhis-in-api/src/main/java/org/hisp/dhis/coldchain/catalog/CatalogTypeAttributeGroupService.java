package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version CatalogTypeAttributeGroupService.javaOct 9, 2012 3:25:45 PM	
 */

public interface CatalogTypeAttributeGroupService
{
    String ID = CatalogTypeAttributeGroupService.class.getName();
    
    // -------------------------------------------------------------------------
    // CatalogTypeAttributeGroup
    // -------------------------------------------------------------------------
    
    int addCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup );

    void deleteCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup );

    void updateCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup );
    
    
    CatalogTypeAttributeGroup getCatalogTypeAttributeGroupById( int id );

    CatalogTypeAttributeGroup getCatalogTypeAttributeGroupByName( String name );

    Collection<CatalogTypeAttributeGroup> getAllCatalogTypeAttributeGroups();
    
    Collection<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroupsByCatalogType( CatalogType catalogType );

    //  methods for paging 
    
    
    int getCatalogTypeAttributeGroupCount();
    
    int getCatalogTypeAttributeGroupCountByName( String name );
    
    Collection<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroupsBetween( int first, int max );
    
    Collection<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroupsBetweenByName( String name, int first, int max );
    

}
