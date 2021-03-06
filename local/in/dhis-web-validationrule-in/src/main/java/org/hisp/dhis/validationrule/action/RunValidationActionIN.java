package org.hisp.dhis.validationrule.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.validation.ValidationResult;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author Margrethe Store
 * @author Lars Helge Overland
 * @version $Id: RunValidationActionIN.java 5730 2008-09-20 14:32:22Z brajesh $
 */
public class RunValidationActionIN
    extends ActionSupport
{
    private static final String KEY_VALIDATIONRESULT = "validationResult";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }
        
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------    
    
    private String startDate;

    public String getStartDate()
    {
        return startDate;
    }
    
    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }
    
    private Boolean includeChildren;

    public void setIncludeChildren( Boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }  
    
    private Collection<ValidationResult> validationResults = new HashSet<ValidationResult>();

    public Collection<ValidationResult> getValidationResults()
    {
        return validationResults;
    }

    private Map<String,ValidationResult> finalValidationResults = new HashMap<String,ValidationResult>();
    
    public Map<String,ValidationResult> getFinalValidationResults()
    {
    	return finalValidationResults;
    }    
    
    private Map<Integer, Set<ValidationRule>> validationRulesInOu = new HashMap<Integer, Set<ValidationRule>>();
    
    public Map<Integer, Set<ValidationRule>> getValidationRulesInOu()
    {
    	return validationRulesInOu;
    }
    
    private Set<Source> organisationUnitsWithVR = new HashSet<Source>();
    
    public Set<Source> getOrganisationUnitsWithVR()
    {
    	return organisationUnitsWithVR;
    }
    
    private Collection<Period> periods = new ArrayList<Period>();
    
    public Collection<Period> getPeriods()
    {
    	return periods;
    }
        
    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------
    
    @SuppressWarnings( "unchecked" )    
    public String execute()
    {
        Collection<? extends Source> sources = selectionTreeManager.getSelectedOrganisationUnits();      
        
        if ( includeChildren )
        {        	
            Collection<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>(); 
            
            for ( Source source : sources )
            {
                organisationUnits.addAll( organisationUnitService.getOrganisationUnitWithChildren( source.getId() ) );
            }
            
            sources = organisationUnits;
        }            
        
        validationResults = validationRuleService.validate( format.parseDate( startDate ), format.parseDate( endDate ), sources );         
        
        Set<Period> tempPeriods = new HashSet<Period>();        
       
        for ( ValidationResult valRes : validationResults )
        {         	
        	
        	finalValidationResults.put( valRes.getSource().getId() + ":" + valRes.getValidationRule().getId() + ":" + valRes.getPeriod().getId(), valRes );
        	
        	organisationUnitsWithVR.add( valRes.getSource() );      	
        	
        	tempPeriods.add( valRes.getPeriod() );       	
        	
        	Set<ValidationRule> temp = new HashSet<ValidationRule>();
        	
        	if ( validationRulesInOu.containsKey( valRes.getSource().getId() ) )
        	{
        		temp = validationRulesInOu.get( valRes.getSource().getId() ) ;
        	}
        	
        	temp.add( valRes.getValidationRule() );
        	
        	validationRulesInOu.put( valRes.getSource().getId(), temp );
        	
        }       
        
        Map<Date, Period> sortedPeriod = new TreeMap<Date,Period>();
        
        for( Period period : tempPeriods )
        {
        	sortedPeriod.put(period.getEndDate(), period);
        }
        
        periods = sortedPeriod.values();
                
        ActionContext.getContext().getSession().put( KEY_VALIDATIONRESULT, validationResults );        
               
        return SUCCESS;        
    }
}
