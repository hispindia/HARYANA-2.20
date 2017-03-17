package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version InventoryType_AttributeService.java Jun 14, 2012 2:30:47 PM	
 */
public interface InventoryType_AttributeService
{
    String ID = InventoryType_AttributeService.class.getName();
    
    void addInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute );
    
    void updateInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute );

    void deleteInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute );
    
    InventoryType_Attribute getInventoryTypeAttribute( InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute );

    Collection<InventoryType_Attribute> getAllInventoryTypeAttributes();

    Collection<InventoryType_Attribute> getAllInventoryTypeAttributesByInventoryType( InventoryType inventoryType );
    
    Collection<InventoryTypeAttribute> getListInventoryTypeAttribute( InventoryType inventoryType );
    
    InventoryType_Attribute getInventoryTypeAttributeForDisplay( InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, boolean display);
    
    Collection<InventoryType_Attribute> getAllInventoryTypeAttributeForDisplay( InventoryType inventoryType, boolean display );
    
    Map<String, String> getOrgUnitAttributeDataValue( String orgUnitIdsByComma, String orgUnitAttribIdsByComma );
    
    Collection<OrganisationUnit> searchOrgUnitByAttributeValue( String orgUnitIdsByComma, Attribute attribute, String searchText );
    
    Map<Integer, String> getEquipmentCountByOrgUnitList( String orgUnitIdsByComma );
    
    String getSumInventoryTypeAttributeValue( String orgUnitIdsByComma, Integer inventoryTypeId, String inventoryTypeAttributeIdsByComma );
    
    
}
