package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

//public interface InventoryTypeStore

public interface InventoryTypeStore extends GenericNameableObjectStore<InventoryType>
{
    String ID = InventoryTypeStore.class.getName();
    /*
    int addInventoryType( InventoryType inventoryType );

    void updateInventoryType( InventoryType inventoryType );

    void deleteInventoryType( InventoryType inventoryType );
    */
    Collection<InventoryType> getAllInventoryTypes();
    
    InventoryType getInventoryTypeByName( String name );
    
    InventoryType getInventoryType( int id );
    
    //Collection<InventoryTypeAttribute> getAllInventoryTypeAttributesForDisplay( InventoryType inventoryType );
}
