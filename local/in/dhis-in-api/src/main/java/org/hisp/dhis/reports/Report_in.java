package org.hisp.dhis.reports;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in element and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of element code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.PeriodType;

@SuppressWarnings( "serial" )
public class Report_in
    implements Serializable
{

    /**
     * The unique identifier for this Report_in
     */
    private int id;

    /**
     * Name of Report_in. Required and unique.
     */
    private String name;

    /**
     * Model of the Report_in (like Static, dynamic etc.). Required.
     */
    private String model;

    /**
     * The PeriodType indicating the frequency that this Report_in should be
     * used
     */
    private PeriodType periodType;

    private String excelTemplateName;

    private String xmlTemplateName;

    private String reportType;

    private OrganisationUnitGroup orgunitGroup;

    private String dataSetIds;

    /**
     * All Sources that are generating this Report_in.
     */
    private Set<OrganisationUnit> sources = new HashSet<OrganisationUnit>();

    private boolean schedulable;

    private boolean scheduled;

    private boolean emailable;

    private String schedulingPolicyId;

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public Report_in()
    {
    }

    public Report_in( String name, String model, PeriodType periodType, String excelTemplateName, String xmlTemplateName, String reportType )
    {
        this.name = name;
        this.model = model;
        this.periodType = periodType;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
        this.reportType = reportType;
    }    
    
    
    
    public Report_in( String name, String model, PeriodType periodType, String excelTemplateName,
        String xmlTemplateName, String reportType, boolean schedulable, boolean emailable )
    {
        this.name = name;
        this.model = model;
        this.periodType = periodType;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
        this.reportType = reportType;
        this.schedulable = schedulable;
        this.emailable = emailable;
    }

    public Report_in( String name, String model, PeriodType periodType, String excelTemplateName,
        String xmlTemplateName, String reportType, OrganisationUnitGroup orgunitGroup, boolean schedulable,
        boolean emailable, String schedulingPolicyId )
    {
        this.name = name;
        this.model = model;
        this.periodType = periodType;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
        this.reportType = reportType;
        this.orgunitGroup = orgunitGroup;
        this.schedulable = schedulable;
        this.emailable = emailable;
        this.schedulingPolicyId = schedulingPolicyId;
    }

    public Report_in( String name, String model, PeriodType periodType, String excelTemplateName,
        String xmlTemplateName, String reportType, String dataSetIds, boolean schedulable )
    {
        this.name = name;
        this.model = model;
        this.periodType = periodType;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
        this.reportType = reportType;
        this.dataSetIds = dataSetIds;
        this.schedulable = schedulable;
    }

    public Report_in( String name, String model, PeriodType periodType, String excelTemplateName,
        String xmlTemplateName, String reportType, OrganisationUnitGroup orgunitGroup, String dataSetIds,
        boolean schedulable )
    {
        this.name = name;
        this.model = model;
        this.periodType = periodType;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
        this.reportType = reportType;
        this.orgunitGroup = orgunitGroup;
        this.dataSetIds = dataSetIds;
        this.schedulable = schedulable;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    public String toString()
    {
        return "Report_in [id=" + this.id + ", name=" + this.name + ", model=" + this.model + ", periodType="
            + this.periodType + ", excelTemplateName=" + this.excelTemplateName + ", xmlTemplateName="
            + this.xmlTemplateName + ", reportType=" + this.reportType + ", orgunitGroup=" + this.orgunitGroup
            + ", dataSetIds=" + this.dataSetIds + ", sources=" + this.sources + ", schedulable=" + this.schedulable
            + ", scheduled=" + this.scheduled + ", emailable=" + this.emailable + ", schedulingPolicyId="
            + this.schedulingPolicyId + "]";
    }

    @Override
    public int hashCode()
    {
        // int prime = 31;
        int result = 1;
        result = 31 * result + (this.dataSetIds == null ? 0 : this.dataSetIds.hashCode());

        result = 31 * result + (this.emailable ? 1231 : 1237);
        result = 31 * result + (this.excelTemplateName == null ? 0 : this.excelTemplateName.hashCode());

        result = 31 * result + this.id;
        result = 31 * result + (this.model == null ? 0 : this.model.hashCode());
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 31 * result + (this.orgunitGroup == null ? 0 : this.orgunitGroup.hashCode());

        result = 31 * result + (this.periodType == null ? 0 : this.periodType.hashCode());

        result = 31 * result + (this.reportType == null ? 0 : this.reportType.hashCode());

        result = 31 * result + (this.schedulable ? 1231 : 1237);
        result = 31 * result + (this.scheduled ? 1231 : 1237);
        result = 31 * result + (this.schedulingPolicyId == null ? 0 : this.schedulingPolicyId.hashCode());

        result = 31 * result + (this.sources == null ? 0 : this.sources.hashCode());
        result = 31 * result + (this.xmlTemplateName == null ? 0 : this.xmlTemplateName.hashCode());

        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        Report_in other = (Report_in) obj;
        if ( this.dataSetIds == null )
        {
            if ( other.dataSetIds != null )
                return false;
        }
        else if ( !this.dataSetIds.equals( other.dataSetIds ) )
            return false;
        if ( this.emailable != other.emailable )
            return false;
        if ( this.excelTemplateName == null )
        {
            if ( other.excelTemplateName != null )
                return false;
        }
        else if ( !this.excelTemplateName.equals( other.excelTemplateName ) )
            return false;
        if ( this.id != other.id )
            return false;
        if ( this.model == null )
        {
            if ( other.model != null )
                return false;
        }
        else if ( !this.model.equals( other.model ) )
            return false;
        if ( this.name == null )
        {
            if ( other.name != null )
                return false;
        }
        else if ( !this.name.equals( other.name ) )
            return false;
        if ( this.orgunitGroup == null )
        {
            if ( other.orgunitGroup != null )
                return false;
        }
        else if ( !this.orgunitGroup.equals( other.orgunitGroup ) )
            return false;
        if ( this.periodType == null )
        {
            if ( other.periodType != null )
                return false;
        }
        else if ( !this.periodType.equals( other.periodType ) )
            return false;
        if ( this.reportType == null )
        {
            if ( other.reportType != null )
                return false;
        }
        else if ( !this.reportType.equals( other.reportType ) )
            return false;
        if ( this.schedulable != other.schedulable )
            return false;
        if ( this.scheduled != other.scheduled )
            return false;
        if ( this.schedulingPolicyId == null )
        {
            if ( other.schedulingPolicyId != null )
                return false;
        }
        else if ( !this.schedulingPolicyId.equals( other.schedulingPolicyId ) )
            return false;
        if ( this.sources == null )
        {
            if ( other.sources != null )
                return false;
        }
        else if ( !this.sources.equals( other.sources ) )
            return false;
        if ( this.xmlTemplateName == null )
        {
            if ( other.xmlTemplateName != null )
                return false;
        }
        else if ( !this.xmlTemplateName.equals( other.xmlTemplateName ) )
            return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addOrganisationUnit( OrganisationUnit unit )
    {
        sources.add( unit );
    }

    public void removeOrganisationUnit( OrganisationUnit unit )
    {
        sources.remove( unit );
    }

    public void updateOrganisationUnits( Set<OrganisationUnit> updates )
    {
        for ( OrganisationUnit unit : new HashSet<OrganisationUnit>( sources ) )
        {
            if ( !updates.contains( unit ) )
            {
                removeOrganisationUnit( unit );
            }
        }

        for ( OrganisationUnit unit : updates )
        {
            addOrganisationUnit( unit );
        }
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
      return this.id;
    }

    public void setId(int id)
    {
      this.id = id;
    }

    public String getName()
    {
      return this.name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public String getModel()
    {
      return this.model;
    }

    public void setModel(String model)
    {
      this.model = model;
    }

    public PeriodType getPeriodType()
    {
      return this.periodType;
    }

    public void setPeriodType(PeriodType periodType)
    {
      this.periodType = periodType;
    }

    public String getExcelTemplateName()
    {
      return this.excelTemplateName;
    }

    public void setExcelTemplateName(String excelTemplateName)
    {
      this.excelTemplateName = excelTemplateName;
    }

    public String getXmlTemplateName()
    {
      return this.xmlTemplateName;
    }

    public void setXmlTemplateName(String xmlTemplateName)
    {
      this.xmlTemplateName = xmlTemplateName;
    }

    public String getReportType()
    {
      return this.reportType;
    }

    public void setReportType(String reportType)
    {
      this.reportType = reportType;
    }

    public Set<OrganisationUnit> getSources()
    {
      return this.sources;
    }

    public void setSources(Set<OrganisationUnit> sources)
    {
      this.sources = sources;
    }

    public OrganisationUnitGroup getOrgunitGroup()
    {
      return this.orgunitGroup;
    }

    public void setOrgunitGroup(OrganisationUnitGroup orgunitGroup)
    {
      this.orgunitGroup = orgunitGroup;
    }

    public String getDataSetIds()
    {
      return this.dataSetIds;
    }

    public void setDataSetIds(String dataSetIds)
    {
      this.dataSetIds = dataSetIds;
    }

    public boolean isSchedulable()
    {
      return this.schedulable;
    }

    public void setSchedulable(boolean schedulable)
    {
      this.schedulable = schedulable;
    }

    public boolean isScheduled()
    {
      return this.scheduled;
    }

    public void setScheduled(boolean scheduled)
    {
      this.scheduled = scheduled;
    }

    public boolean isEmailable()
    {
      return this.emailable;
    }

    public void setEmailable(boolean emailable)
    {
      this.emailable = emailable;
    }

    public String getSchedulingPolicyId()
    {
      return this.schedulingPolicyId;
    }

    public void setSchedulingPolicyId(String schedulingPolicyId)
    {
      this.schedulingPolicyId = schedulingPolicyId;
    }
  }