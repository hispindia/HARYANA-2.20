package org.hisp.dhis.coldchain.catalog.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeOption;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version CatalogTypeAttributeOptionComparator.java Aug 1, 2012 12:57:58 PM	
 */
public class CatalogTypeAttributeOptionComparator implements Comparator<CatalogTypeAttributeOption>
{
    public int compare( CatalogTypeAttributeOption catalogTypeAttributeOption0, CatalogTypeAttributeOption catalogTypeAttributeOption1 )
    {
        return catalogTypeAttributeOption0.getName().compareToIgnoreCase( catalogTypeAttributeOption1.getName() );
    }
}
