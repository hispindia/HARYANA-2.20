package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version InventoryType_AttributeStore.java Jun 14, 2012 2:43:57 PM	
 */
//public interface InventoryType_AttributeStore extends GenericStore<InventoryType_Attribute>
public interface InventoryType_AttributeStore
{
    String ID = InventoryType_AttributeStore.class.getName();
    
    void addInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute );
    
    void updateInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute );

    void deleteInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute );
    
    Collection<InventoryType_Attribute> getAllInventoryTypeAttributes();
    
    InventoryType_Attribute getInventoryTypeAttribute( InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute );
    
    InventoryType_Attribute getInventoryTypeAttributeForDisplay( InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, boolean display);
    
    Collection<InventoryType_Attribute> getAllInventoryTypeAttributeForDisplay( InventoryType inventoryType, boolean display );

    Collection<InventoryType_Attribute> getAllInventoryTypeAttributesByInventoryType( InventoryType inventoryType );
    
    Collection<InventoryTypeAttribute> getListInventoryTypeAttribute( InventoryType inventoryType );    
    
}
