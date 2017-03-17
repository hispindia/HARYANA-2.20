package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

//public interface InventoryTypeAttributeStore
public interface InventoryTypeAttributeStore extends GenericNameableObjectStore<InventoryTypeAttribute>
{
    String ID = InventoryTypeAttributeStore.class.getName();
    /*
    int addInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    void updateInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    void deleteInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );
    */
    
    Collection<InventoryTypeAttribute> getAllInventoryTypeAttributes();
    
    InventoryTypeAttribute getInventoryTypeAttribute( int id );
    
    InventoryTypeAttribute getInventoryTypeAttributeByName( String name );
    
    //Collection<InventoryTypeAttribute> getAllInventoryTypeAttributesForDisplay( InventoryTypeAttribute inventoryTypeAttribute );
    
    
}
