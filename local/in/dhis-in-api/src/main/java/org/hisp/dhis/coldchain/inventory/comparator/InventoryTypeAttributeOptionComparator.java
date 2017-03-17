package org.hisp.dhis.coldchain.inventory.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeOption;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version InventoryTypeAttributeOptionComparator.java Aug 1, 2012 3:50:35 PM	
 */
public class InventoryTypeAttributeOptionComparator implements Comparator<InventoryTypeAttributeOption>
{
    public int compare( InventoryTypeAttributeOption inventoryTypeAttributeOption0, InventoryTypeAttributeOption inventoryTypeAttributeOption1 )
    {
        return inventoryTypeAttributeOption0.getName().compareToIgnoreCase( inventoryTypeAttributeOption1.getName() );
    }
}

