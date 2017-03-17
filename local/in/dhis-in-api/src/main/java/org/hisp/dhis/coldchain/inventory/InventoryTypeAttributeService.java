package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface InventoryTypeAttributeService
{
    String ID = InventoryTypeAttributeService.class.getName();
    
    int addInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    void updateInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    void deleteInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    Collection<InventoryTypeAttribute> getAllInventoryTypeAttributes();
    
    InventoryTypeAttribute getInventoryTypeAttribute( int id );
    
    InventoryTypeAttribute getInventoryTypeAttributeByName( String name );
    
    //  methods
    
    int getInventoryTypeAttributeCount();
    
    int getInventoryTypeAttributeCountByName( String name );
    
    Collection<InventoryTypeAttribute> getInventoryTypeAttributesBetween( int first, int max );
    
    Collection<InventoryTypeAttribute> getInventoryTypeAttributesBetweenByName( String name, int first, int max );
    
    
    //Collection<InventoryTypeAttribute> getAllInventoryTypeAttributesForDisplay( InventoryTypeAttribute inventoryTypeAttribute );
    

    
}
