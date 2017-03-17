package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version InventoryTypeAttributeGroupStore.javaMar 5, 2013 11:49:28 AM	
 */

public interface InventoryTypeAttributeGroupStore extends GenericNameableObjectStore<InventoryTypeAttributeGroup>
{
    String ID = InventoryTypeAttributeGroupStore.class.getName();
    
    // -------------------------------------------------------------------------
    // InventoryTypeAttributeGroup
    // -------------------------------------------------------------------------
    
    /*
    int addInventoryTypeAttributeGroup( InventoryTypeAttributeGroup InventoryTypeAttributeGroup );

    void deleteInventoryTypeAttributeGroup( InventoryTypeAttributeGroup InventoryTypeAttributeGroup );

    void updateInventoryTypeAttributeGroup( InventoryTypeAttributeGroup InventoryTypeAttributeGroup );
    */
    
    
    
    InventoryTypeAttributeGroup getInventoryTypeAttributeGroupById( int id );

    InventoryTypeAttributeGroup getInventoryTypeAttributeGroupByName( String name );

    Collection<InventoryTypeAttributeGroup> getAllInventoryTypeAttributeGroups();
    
    Collection<InventoryTypeAttributeGroup> getInventoryTypeAttributeGroupsByInventoryType( InventoryType InventoryType );
    
    
}
