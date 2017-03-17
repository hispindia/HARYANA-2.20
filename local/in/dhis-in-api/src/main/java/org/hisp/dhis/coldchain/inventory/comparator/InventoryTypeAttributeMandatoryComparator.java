package org.hisp.dhis.coldchain.inventory.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;

public class InventoryTypeAttributeMandatoryComparator implements Comparator<InventoryTypeAttribute>
{
    public int compare( InventoryTypeAttribute inventoryTypeAttribute0, InventoryTypeAttribute inventoryTypeAttribute1 )
    {
        if( ( inventoryTypeAttribute0.isMandatory() ) && ( inventoryTypeAttribute1.isMandatory() ) )
        {
            return inventoryTypeAttribute0.getName().compareToIgnoreCase( inventoryTypeAttribute1.getName() );
           //Boolean boolean1 = inventoryTypeAttribute0.isMandatory();
           //return boolean1.compareTo( inventoryTypeAttribute1.isMandatory() );
        }
        else
        {
            return inventoryTypeAttribute0.getName().compareToIgnoreCase( inventoryTypeAttribute1.getName() );
        }
        /*
        if( inventoryTypeAttribute0.isMandatory() )
            return 1;
        else
            return 0;
        */
    }
}
