package org.hisp.dhis.spis;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 */

public interface CriteriaService
{
    String ID = CriteriaService.class.getName();
    
    int addCriteria( Criteria criteria );
    
    void updateCriteria( Criteria criteria );
    
    void deleteCriteria( Criteria criteria );
    
    Criteria getCriteriaById( int Id );

    Collection<Criteria> getAllCriteria();

}