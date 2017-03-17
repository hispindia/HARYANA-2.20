package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface EquipmentStore
{
    String ID = EquipmentStore.class.getName();
    
    void addEquipment( Equipment equipment );

    void updateEquipment( Equipment equipment );

    void deleteEquipment( Equipment equipment );
    
    Collection<Equipment> getAllEquipments();

    Collection<Equipment> getEquipments( EquipmentInstance equipmentInstance );
    
    Equipment getEquipment( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute );
    
}
