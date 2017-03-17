package org.hisp.dhis.coldchain.inventory.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;

public class InventoryTypeAttributeComparator
    implements Comparator<InventoryTypeAttribute>
{
    public int compare( InventoryTypeAttribute inventoryTypeAttribute0, InventoryTypeAttribute inventoryTypeAttribute1 )
    {
        return inventoryTypeAttribute0.getName().compareToIgnoreCase( inventoryTypeAttribute1.getName() );
    }
}
