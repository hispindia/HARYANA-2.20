package org.hisp.dhis.schedule;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface SchedulingPolicyService
{
    String ID = SchedulingPolicyService.class.getName();

    // -------------------------------------------------------------------------
    // SchedulingPolicy
    // -------------------------------------------------------------------------
    
    int addSchedulingPolicy(SchedulingPolicy paramSchedulingPolicy);

    void updateSchedulingPolicy(SchedulingPolicy paramSchedulingPolicy);

    void deleteSchedulingPolicy(SchedulingPolicy paramSchedulingPolicy);

    SchedulingPolicy getSchedulingPolicy(int paramInt);

    SchedulingPolicy getSchedulingPolicyByName(String paramString);

    Collection<SchedulingPolicy> getAllSchedulingPolicies();
}
