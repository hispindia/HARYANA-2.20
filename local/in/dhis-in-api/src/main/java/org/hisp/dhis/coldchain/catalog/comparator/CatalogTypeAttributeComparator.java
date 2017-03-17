package org.hisp.dhis.coldchain.catalog.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;

public class CatalogTypeAttributeComparator
    implements Comparator<CatalogTypeAttribute>
{
    public int compare( CatalogTypeAttribute catalogTypeAttribute0, CatalogTypeAttribute catalogTypeAttribute1 )
    {
        return catalogTypeAttribute0.getName().compareToIgnoreCase( catalogTypeAttribute1.getName() );
    }
}
