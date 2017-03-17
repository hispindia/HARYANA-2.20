package org.hisp.dhis.coldchain.inventory.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.inventory.InventoryType;

public class InventoryTypeComparator implements Comparator<InventoryType>
{
    public int compare( InventoryType inventoryType0, InventoryType inventoryType1 )
    {
        return inventoryType0.getName().compareToIgnoreCase( inventoryType1.getName() );
    }
}
