package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface InventoryTypeAttributeOptionStore
{
    String ID = InventoryTypeAttributeOptionStore.class.getName();
    
    int addInventoryTypeAttributeOption( InventoryTypeAttributeOption inventoryTypeAttributeOption );

    void updateInventoryTypeAttributeOption( InventoryTypeAttributeOption inventoryTypeAttributeOption );

    void deleteInventoryTypeAttributeOption( InventoryTypeAttributeOption inventoryTypeAttributeOption );

    InventoryTypeAttributeOption getInventoryTypeAttributeOption( int id );
    
    Collection<InventoryTypeAttributeOption> getAllInventoryTypeAttributeOptions();

    Collection<InventoryTypeAttributeOption> get( InventoryTypeAttribute inventoryTypeAttribute);
    
    InventoryTypeAttributeOption get( InventoryTypeAttribute inventoryTypeAttribute, String name );
}
