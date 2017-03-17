package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.hisp.dhis.common.GenericStore;

public interface EquipmentStatusStore extends GenericStore<EquipmentStatus>
{
    String ID = EquipmentStatusStore.class.getName();
    
    //int addEquipmentStatus( EquipmentStatus equipmentStatus );

    //void updateEquipmentStatus( EquipmentStatus equipmentStatus );

    //void deleteEquipmentStatus( EquipmentStatus equipmentStatus );

    //Collection<EquipmentStatus> getAllEquipmentStatus();
    
    Collection<EquipmentStatus> getEquipmentStatusHistory( EquipmentInstance equipmentInstance );
    
    Collection<EquipmentStatus> getEquipmentStatusHistoryDescOrder( EquipmentInstance equipmentInstance );
}
