package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;
import java.util.regex.Pattern;

import org.hisp.dhis.i18n.I18n;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version CatalogDataEntryService.java Jun 7, 2012 5:12:27 PM	
 */
public interface CatalogDataEntryService
{
    final Pattern INPUT_PATTERN = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
    
    //final Pattern IDENTIFIER_PATTERN_FIELD = Pattern.compile( "id=\"(\\d+)-(\\d+)-val\"" );
    
    final Pattern IDENTIFIER_PATTERN_FIELD = Pattern.compile( "id=\"attr(\\d+)\"" );
    
    //--------------------------------------------------------------------------
    // ProgramDataEntryService
    //--------------------------------------------------------------------------
    
    String prepareDataEntryFormForCatalog( String htmlCode, Collection<CatalogDataValue> dataValues, String disabled,
        I18n i18n, CatalogType catalogType );
    
    String prepareDataEntryFormForEdit( String htmlCode );
}
