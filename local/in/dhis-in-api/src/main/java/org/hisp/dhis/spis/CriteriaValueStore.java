package org.hisp.dhis.spis;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;

import java.util.Set;

import org.hisp.dhis.program.Program;


/**
 * @author Mithilesh Kumar Thakur
 */

public interface CriteriaValueStore
    extends GenericIdentifiableObjectStore<CriteriaValue>
{
    String ID = CriteriaValueStore.class.getName();
    
    
    int addCriteriaValue( CriteriaValue criteriaValue );
    
    void updateCriteriaValue( CriteriaValue criteriaValue );
    
    void deleteCriteriaValue( CriteriaValue criteriaValue );
    
    CriteriaValue getCriteriaValueById( int Id );
    
    Collection<CriteriaValue> getAllCriteriaValue();
    
    Collection<CriteriaValue> getCriteriaValues( org.hisp.dhis.spis.Criteria criteria );
    
    CriteriaValue getCriteriaValue(TrackedEntityAttribute tea, String operator, String validationValue );
    
    Set<Program> getProgramByCriteriaValue( TrackedEntityAttribute tea, String operator, String validationValue );
    
    Collection<CriteriaValue> getCriteriaValues( Collection<TrackedEntityAttribute> teas );
    
    Set<Program> getCriteriaValues( Collection<TrackedEntityAttribute> teas, Collection<String> operators, Collection<String> validationValues );
    
    List<Integer> getProgramIds();

}