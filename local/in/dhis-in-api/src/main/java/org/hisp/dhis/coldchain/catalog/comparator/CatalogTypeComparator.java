package org.hisp.dhis.coldchain.catalog.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.catalog.CatalogType;

public class CatalogTypeComparator
implements Comparator<CatalogType>
{
    public int compare( CatalogType catalogType0, CatalogType catalogType1 )
    {
        return catalogType0.getName().compareToIgnoreCase( catalogType1.getName() );
    }
}
