package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface InventoryTypeService
{
    String ID = InventoryTypeService.class.getName();
    
    int addInventoryType( InventoryType inventoryType );

    void updateInventoryType( InventoryType inventoryType );

    void deleteInventoryType( InventoryType inventoryType );

    Collection<InventoryType> getAllInventoryTypes();
    
    InventoryType getInventoryTypeByName( String name );
    
    InventoryType getInventoryType( int id );
    
    //  methods
    
    int getInventoryTypeCount();
    
    int getInventoryTypeCountByName( String name );
    
    Collection<InventoryType> getInventoryTypesBetween( int first, int max );
    
    Collection<InventoryType> getInventoryTypesBetweenByName( String name, int first, int max );
    
    Collection<InventoryTypeAttribute> getAllInventoryTypeAttributesForDisplay( InventoryType inventoryType );
    
    
}
