package org.hisp.dhis.schedule;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 */
public class DefaultSchedulingPolicy implements SchedulingPolicyService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SchedulingPolicyStore schedulingPolicyStore;

    public void setSchedulingPolicyStore( SchedulingPolicyStore schedulingPolicyStore )
    {
        this.schedulingPolicyStore = schedulingPolicyStore;
    }
    

    // -------------------------------------------------------------------------
    // SchedulingPolicy
    // -------------------------------------------------------------------------

    public int addSchedulingPolicy(SchedulingPolicy schedulingPolicy)
    {
        return schedulingPolicyStore.addSchedulingPolicy(schedulingPolicy);
    }

    public void updateSchedulingPolicy(SchedulingPolicy schedulingPolicy)
    {
        schedulingPolicyStore.updateSchedulingPolicy(schedulingPolicy);
    }

    public void deleteSchedulingPolicy(SchedulingPolicy schedulingPolicy)
    {
        schedulingPolicyStore.deleteSchedulingPolicy(schedulingPolicy);
    }

    public SchedulingPolicy getSchedulingPolicy(int id)
    {
        return schedulingPolicyStore.getSchedulingPolicy(id);
    }

    public SchedulingPolicy getSchedulingPolicyByName(String name)
    {
        return schedulingPolicyStore.getSchedulingPolicyByName(name);
    }

    public Collection<SchedulingPolicy> getAllSchedulingPolicies()
    {
        return schedulingPolicyStore.getAllSchedulingPolicies();
    }
}
