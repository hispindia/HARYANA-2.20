package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version InventoryTypeAttributeGroupService.javaMar 5, 2013 11:48:18 AM	
 */

public interface InventoryTypeAttributeGroupService
{
    String ID = InventoryTypeAttributeGroupService.class.getName();
    
    // -------------------------------------------------------------------------
    // CatalogTypeAttributeGroup
    // -------------------------------------------------------------------------
    
    int addInventoryTypeAttributeGroup( InventoryTypeAttributeGroup inventoryTypeAttributeGroup );

    void deleteInventoryTypeAttributeGroup( InventoryTypeAttributeGroup inventoryTypeAttributeGroup );

    void updateInventoryTypeAttributeGroup( InventoryTypeAttributeGroup inventoryTypeAttributeGroup );
    
    
    InventoryTypeAttributeGroup getInventoryTypeAttributeGroupById( int id );

    InventoryTypeAttributeGroup getInventoryTypeAttributeGroupByName( String name );

    Collection<InventoryTypeAttributeGroup> getAllInventoryTypeAttributeGroups();
    
    Collection<InventoryTypeAttributeGroup> getInventoryTypeAttributeGroupsByInventoryType( InventoryType inventoryType );

    
    //  methods for paging 
    
    
    int getInventoryTypeAttributeGroupCount();
    
    int getInventoryTypeAttributeGroupCountByName( String name );
    
    Collection<InventoryTypeAttributeGroup> getInventoryTypeAttributeGroupsBetween( int first, int max );
    
    Collection<InventoryTypeAttributeGroup> getInventoryTypeAttributeGroupsBetweenByName( String name, int first, int max );
    
    
    
}

