package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version CatalogTypeAttributeGroupStore.javaOct 9, 2012 3:26:28 PM	
 */

public interface CatalogTypeAttributeGroupStore extends GenericNameableObjectStore<CatalogTypeAttributeGroup>
{
    String ID = CatalogTypeAttributeGroupStore.class.getName();
    
    // -------------------------------------------------------------------------
    // CatalogTypeAttributeGroup
    // -------------------------------------------------------------------------
    
    /*
    int addCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup );

    void deleteCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup );

    void updateCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup );
    */
    
    
    
    CatalogTypeAttributeGroup getCatalogTypeAttributeGroupById( int id );

    CatalogTypeAttributeGroup getCatalogTypeAttributeGroupByName( String name );

    Collection<CatalogTypeAttributeGroup> getAllCatalogTypeAttributeGroups();
    
    Collection<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroupsByCatalogType( CatalogType catalogType );
    
    
}
