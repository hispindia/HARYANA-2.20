package org.hisp.dhis.coldchain.catalog.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroup;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version CatalogTypeAttributeGroupOrderComparator.javaOct 11, 2012 4:30:38 PM	
 */

public class CatalogTypeAttributeGroupOrderComparator implements Comparator<CatalogTypeAttributeGroup>
{
    public int compare( CatalogTypeAttributeGroup group1, CatalogTypeAttributeGroup group2 )
    {
        if ( group1.getCatalogType() != null && group2.getCatalogType() != null )
        {
            int catalogType = group1.getCatalogType().getName().compareTo( group2.getCatalogType().getName() );
            
            if ( catalogType != 0 )
            {
                return catalogType;
            }
        }
        
        return group1.getSortOrder() - group2.getSortOrder();
    }
}
