package org.hisp.dhis.spis;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.program.Program;

/**
 * @author Mithilesh Kumar Thakur
 */

public interface ProgramCriteriasService
{
    String ID = ProgramCriteriasStore.class.getName();
    
    void save( ProgramCriterias programCriterias );
    
    void deleteProgramCriterias( ProgramCriterias programCriterias );
    
    void updateProgramCriterias( org.hisp.dhis.spis.Criteria ctr, String[] programArray );
    
    Collection<ProgramCriterias> getProgramCriterias( Program program );
    
    Collection<ProgramCriterias> getProgramCriterias( org.hisp.dhis.spis.Criteria ctr );
    
    List<Program> getPrograms( org.hisp.dhis.spis.Criteria ctr );
    
    Collection<Criteria> getCriterias( Program program );
}