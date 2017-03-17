package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;
import java.util.List;

public interface EquipmentService
{
    String ID = EquipmentService.class.getName();
    
    void addEquipment( Equipment equipment );

    void updateEquipment( Equipment equipment );

    void deleteEquipment( Equipment equipment );

    Collection<Equipment> getAllEquipments();

    Collection<Equipment> getEquipments( EquipmentInstance equipmentInstance );
    
    Equipment getEquipment( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute );
    
    //Map<String, String> inventryTypeAttributeAndValue( EquipmentInstance equipmentInstance, List<InventoryTypeAttribute> inventoryTypeAttributeList );
    
    String inventryTypeAttributeAndValue( EquipmentInstance equipmentInstance, List<InventoryTypeAttribute> inventoryTypeAttributeList );
}
