package org.hisp.dhis.coldchain.inventory;

import java.io.Serializable;
import java.util.Set;

import org.hisp.dhis.coldchain.catalog.Catalog;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class EquipmentInstance implements Serializable
{

    private int id;
    
    private InventoryType inventoryType;
    
    private OrganisationUnit organisationUnit;
    
    private Catalog catalog;
    
    private boolean working = false;
    
    private Set<EquipmentStatus> equipmentStatusUpdates;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public EquipmentInstance()
    {
        
    }
    
    public EquipmentInstance( InventoryType inventoryType, OrganisationUnit organisationUnit )
    {
        this.inventoryType = inventoryType;
        this.organisationUnit = organisationUnit;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

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

        if ( !(o instanceof EquipmentInstance) )
        {
            return false;
        }

        final EquipmentInstance other = (EquipmentInstance) o;

        return inventoryType.equals( other.getInventoryType() ) && organisationUnit.equals( other.getOrganisationUnit() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + inventoryType.hashCode();
        result = result * prime + organisationUnit.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public InventoryType getInventoryType()
    {
        return inventoryType;
    }

    public void setInventoryType( InventoryType inventoryType )
    {
        this.inventoryType = inventoryType;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public boolean isWorking()
    {
        return working;
    }

    public void setWorking( boolean working )
    {
        this.working = working;
    }

    public Set<EquipmentStatus> getEquipmentStatusUpdates()
    {
        return equipmentStatusUpdates;
    }

    public void setEquipmentStatusUpdates( Set<EquipmentStatus> equipmentStatusUpdates )
    {
        this.equipmentStatusUpdates = equipmentStatusUpdates;
    }

    public Catalog getCatalog()
    {
        return catalog;
    }

    public void setCatalog( Catalog catalog )
    {
        this.catalog = catalog;
    }
    
}
