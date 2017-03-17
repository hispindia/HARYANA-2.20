package org.hisp.dhis.pbf.api;

import java.io.Serializable;

import org.hisp.dhis.common.BaseNameableObject;

@SuppressWarnings("serial")
public class Lookup extends BaseNameableObject implements Serializable
{
    public final static String PAYMENT_ADJUSTMENT_AMOUNT_DE = "PAYMENT_ADJUSTMENT_AMOUNT_DE";
    public final static String TARIFF_SETTING_AUTHORITY = "TARIFF_SETTING_AUTHORITY";

    public static final String DS_PBF_TYPE = "DS_PBF_TYPE";    
    public static final String DS_QUALITY_TYPE = "DS_QUALITY_TYPE";
    public static final String DS_PAYMENT_TYPE = "DS_PAYMENT_TYPE";
    
    public static final String OC_TARIFF = "OC_TARIFF";    
    public static final String QV_TARIFF = "QV_TARIFF";     
    public static final String PBF_AGG_TYPE = "PBF_AGG_TYPE";    
    public static final String BANK = "BANK";
    
    public static final String PBF_AGG_TYPE_OVERALL_QUALITY_SCORE = "OVERALL QUALITY SCORE";
    public static final String PBF_AGG_TYPE_OVERALL_UNADJUSTED_PBF_AMOUNT = "OVERALL UNADJUSTED PBF AMOUNT";
    
    public static final String ORGUNITID_BY_COMMA = "ORGUNITID_BY_COMMA";
    public static final String PERIODID_BY_COMMA = "PERIODID_BY_COMMA";
    
    
    private String type;
    
    private String value;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public Lookup()
    {
        
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof Lookup) )
        {
            return false;
        }

        final Lookup other = (Lookup) o;

        return name.equals( other.getName() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }


    
}
