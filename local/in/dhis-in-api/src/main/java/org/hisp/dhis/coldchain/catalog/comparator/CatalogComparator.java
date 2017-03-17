package org.hisp.dhis.coldchain.catalog.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.catalog.Catalog;

public class CatalogComparator implements Comparator<Catalog>
{
    public int compare( Catalog catalog0, Catalog catalog1 )
    {
        return catalog0.getName().compareToIgnoreCase( catalog1.getName() );
    }
}
