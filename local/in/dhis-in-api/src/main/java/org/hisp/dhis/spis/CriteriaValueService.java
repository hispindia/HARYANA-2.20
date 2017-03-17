package org.hisp.dhis.spis;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.program.Program;

/**
 * @author Mithilesh Kumar Thakur
 */

public interface CriteriaValueService
{
    String ID = CriteriaValueService.class.getName();
    
    int addCriteriaValue( CriteriaValue criteriaValue );
    
    void updateCriteriaValue( CriteriaValue criteriaValue );
    
    void deleteCriteriaValue( CriteriaValue criteriaValue );
    
    CriteriaValue getCriteriaValueById( int Id );
        
    Collection<CriteriaValue> getAllCriteriaValue();
    
    CriteriaValue getCriteriaValue(TrackedEntityAttribute tea, String operator, String validationValue );
    
    Collection<CriteriaValue> getCriteriaValues( org.hisp.dhis.spis.Criteria criteria );
    
    Set<Program> getProgramByCriteriaValue( TrackedEntityAttribute tea, String operator, String validationValue );
    
    Collection<CriteriaValue> getCriteriaValues( Collection<TrackedEntityAttribute> teas );
    
    Set<Program>  getCriteriaValues( Collection<TrackedEntityAttribute> teas, Collection<String> operators, Collection<String> validationValues );
    
    List<Integer> getProgramIds();
}