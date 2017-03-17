package org.hisp.dhis.spis;

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;

/**
 * @author Mithilesh Kumar Thakur
 */

public interface CriteriaStore
    extends GenericIdentifiableObjectStore<Criteria>
{
    String ID = CriteriaStore.class.getName();
    
    int addCriteria( Criteria criteria );
    
    void updateCriteria( Criteria criteria );
    
    void deleteCriteria( Criteria criteria );
    
    Criteria getCriteriaById( int Id );

    Collection<Criteria> getAllCriteria();
}