package org.hisp.dhis.organisationunit;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.OrganisationUnitSelectionMode;
import org.hisp.dhis.commons.filter.Filter;
import org.hisp.dhis.commons.filter.FilterUtils;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitLevelComparator;
import org.hisp.dhis.system.filter.OrganisationUnitPolygonCoveringCoordinateFilter;
import org.hisp.dhis.system.util.GeoUtils;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.version.VersionService;
import org.springframework.transaction.annotation.Transactional;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.i18n.I18nUtils.i18n;

/**
 * @author Torgeir Lorange Ostby
 */
@Transactional
public class DefaultOrganisationUnitService
    implements OrganisationUnitService
{
    private static final String LEVEL_PREFIX = "Level ";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitStore organisationUnitStore;

    public void setOrganisationUnitStore( OrganisationUnitStore organisationUnitStore )
    {
        this.organisationUnitStore = organisationUnitStore;
    }

    private OrganisationUnitLevelStore organisationUnitLevelStore;

    public void setOrganisationUnitLevelStore( OrganisationUnitLevelStore organisationUnitLevelStore )
    {
        this.organisationUnitLevelStore = organisationUnitLevelStore;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private VersionService versionService;

    public void setVersionService( VersionService versionService )
    {
        this.versionService = versionService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    @Override
    public int addOrganisationUnit( OrganisationUnit organisationUnit )
    {
        int id = organisationUnitStore.save( organisationUnit );

        if ( organisationUnit.getParent() == null && currentUserService.getCurrentUser() != null )
        {
            // Adding a new root node, add this node to the current user

            currentUserService.getCurrentUser().getOrganisationUnits().add( organisationUnit );
        }

        return id;
    }

    @Override
    public void updateOrganisationUnit( OrganisationUnit organisationUnit )
    {
        organisationUnitStore.update( organisationUnit );
    }

    @Override
    public void updateOrganisationUnitVersion()
    {
        versionService.updateVersion( VersionService.ORGANISATIONUNIT_VERSION );
    }

    @Override
    public void updateOrganisationUnit( OrganisationUnit organisationUnit, boolean updateHierarchy )
    {
        updateOrganisationUnit( organisationUnit );
    }

    @Override
    public void deleteOrganisationUnit( OrganisationUnit organisationUnit )
        throws HierarchyViolationException
    {
        organisationUnit = getOrganisationUnit( organisationUnit.getId() );

        if ( !organisationUnit.getChildren().isEmpty() )
        {
            throw new HierarchyViolationException( "Cannot delete an OrganisationUnit with children" );
        }

        OrganisationUnit parent = organisationUnit.getParent();

        if ( parent != null )
        {
            parent.getChildren().remove( organisationUnit );

            organisationUnitStore.update( parent );
        }

        organisationUnitStore.delete( organisationUnit );
    }

    @Override
    public OrganisationUnit getOrganisationUnit( int id )
    {
        return i18n( i18nService, organisationUnitStore.get( id ) );
    }

    @Override
    public List<OrganisationUnit> getAllOrganisationUnits()
    {
        return i18n( i18nService, organisationUnitStore.getAll() );
    }

    @Override
    public List<OrganisationUnit> getAllOrganisationUnitsByStatus( boolean status )
    {
        return i18n( i18nService, organisationUnitStore.getAllOrganisationUnitsByStatus( status ) );
    }

    @Override
    public List<OrganisationUnit> getAllOrganisationUnitsByLastUpdated( Date lastUpdated )
    {
        return i18n( i18nService, organisationUnitStore.getAllOrganisationUnitsByLastUpdated( lastUpdated ) );
    }

    @Override
    public List<OrganisationUnit> getAllOrganisationUnitsByStatusLastUpdated( boolean status, Date lastUpdated )
    {
        return i18n( i18nService, organisationUnitStore.getAllOrganisationUnitsByStatusLastUpdated( status, lastUpdated ) );
    }

    @Override
    public void searchOrganisationUnitByName( List<OrganisationUnit> orgUnits, String key )
    {
        Iterator<OrganisationUnit> iterator = orgUnits.iterator();

        while ( iterator.hasNext() )
        {
            if ( !iterator.next().getName().toLowerCase().contains( key.toLowerCase() ) )
            {
                iterator.remove();
            }
        }
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnits( Collection<Integer> identifiers )
    {
        return i18n( i18nService, organisationUnitStore.getById( identifiers ) );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsByUid( Collection<String> uids )
    {
        return i18n( i18nService, organisationUnitStore.getByUid( uids ) );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsByQuery( OrganisationUnitQueryParams params )
    {
        return organisationUnitStore.getOrganisationUnits( params );
    }

    @Override
    public OrganisationUnit getOrganisationUnit( String uid )
    {
        return i18n( i18nService, organisationUnitStore.getByUid( uid ) );
    }

    @Override
    public OrganisationUnit getOrganisationUnitByUuid( String uuid )
    {
        return i18n( i18nService, organisationUnitStore.getByUuid( uuid ) );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitByName( String name )
    {
        return new ArrayList<>( i18n( i18nService, organisationUnitStore.getAllEqName( name ) ) );
    }

    @Override
    public OrganisationUnit getOrganisationUnitByCode( String code )
    {
        return i18n( i18nService, organisationUnitStore.getByCode( code ) );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitByNameIgnoreCase( String name )
    {
        return organisationUnitStore.getAllEqNameIgnoreCase( name );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsByNames( Collection<String> names )
    {
        return i18n( i18nService, organisationUnitStore.getByNames( names ) );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsByCodes( Collection<String> codes )
    {
        return i18n( i18nService, organisationUnitStore.getByCodes( codes ) );
    }

    @Override
    public List<OrganisationUnit> getRootOrganisationUnits()
    {
        return i18n( i18nService, organisationUnitStore.getRootOrganisationUnits() );
    }

    @Override
    public int getLevelOfOrganisationUnit( OrganisationUnit unit )
    {
        return unit.getLevel() != 0 ? unit.getLevel() : unit.getOrganisationUnitLevel();
    }

    @Override
    public int getLevelOfOrganisationUnit( int id )
    {
        return getOrganisationUnit( id ).getOrganisationUnitLevel();
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnits( Collection<OrganisationUnitGroup> groups, Collection<OrganisationUnit> parents )
    {
        OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
        params.setParents( Sets.newHashSet( parents ) );
        params.setGroups( Sets.newHashSet( groups ) );

        return organisationUnitStore.getOrganisationUnits( params );
    }

    @Override
    public Set<String> getOrganisationUnitUids( Set<String> parents, OrganisationUnitSelectionMode ouMode )
    {
        List<OrganisationUnit> ouParents = new ArrayList<>( organisationUnitStore.getByUid( parents ) );
        Set<String> ou = new HashSet<>();

        if ( OrganisationUnitSelectionMode.ACCESSIBLE == ouMode )
        {
            User user = currentUserService.getCurrentUser();

            if ( user != null )
            {
                ouParents = new ArrayList<>( user.getDataViewOrganisationUnitsWithFallback() );
                ouMode = OrganisationUnitSelectionMode.DESCENDANTS;
            }
        }

        for ( OrganisationUnit organisationUnit : ouParents )
        {
            if ( OrganisationUnitSelectionMode.DESCENDANTS == ouMode )
            {
                ou.add( organisationUnit.getUid() );
                ou.addAll( getUids( getOrganisationUnitWithChildren( organisationUnit.getUid() ) ) );
            }
            else if ( OrganisationUnitSelectionMode.CHILDREN == ouMode )
            {
                ou.add( organisationUnit.getUid() );
                ou.addAll( getUids( organisationUnit.getChildren() ) );
            }
            else // SELECTED
            {
                ou.add( organisationUnit.getUid() );
            }
        }

        return ou;
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsWithChildren( Collection<String> parentUids )
    {
        return getOrganisationUnitsWithChildren( parentUids, null );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsWithChildren( Collection<String> parentUids, Integer maxLevels )
    {
        List<OrganisationUnit> units = new ArrayList<>();

        for ( String uid : parentUids )
        {
            units.addAll( getOrganisationUnitWithChildren( uid, maxLevels ) );
        }

        return units;
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitWithChildren( String uid )
    {
        return getOrganisationUnitWithChildren( uid, null );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitWithChildren( String uid, Integer maxLevels )
    {
        OrganisationUnit unit = getOrganisationUnit( uid );

        int id = unit != null ? unit.getId() : -1;

        return getOrganisationUnitWithChildren( id, maxLevels );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitWithChildren( int id )
    {
        return getOrganisationUnitWithChildren( id, null );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitWithChildren( int id, Integer maxLevels )
    {
        OrganisationUnit organisationUnit = getOrganisationUnit( id );

        if ( organisationUnit == null )
        {
            return new ArrayList<>();
        }

        if ( maxLevels != null && maxLevels <= 0 )
        {
            return new ArrayList<>();
        }

        List<OrganisationUnit> result = new ArrayList<>();

        int rootLevel = organisationUnit.getOrganisationUnitLevel();

        organisationUnit.setLevel( rootLevel );
        result.add( organisationUnit );

        final Integer maxLevel = maxLevels != null ? (rootLevel + maxLevels - 1) : null;

        addOrganisationUnitChildren( organisationUnit, result, rootLevel, maxLevel );

        return result;
    }

    /**
     * Support method for getOrganisationUnitWithChildren(). Adds all
     * OrganisationUnit children to a result collection.
     */
    private void addOrganisationUnitChildren( OrganisationUnit parent, List<OrganisationUnit> result, int level, final Integer maxLevel )
    {
        if ( parent.getChildren() != null && parent.getChildren().size() > 0 )
        {
            level++;
        }

        if ( maxLevel != null && level > maxLevel )
        {
            return;
        }

        List<OrganisationUnit> childList = parent.getSortedChildren();

        for ( OrganisationUnit child : childList )
        {
            child.setLevel( level );
            result.add( child );

            addOrganisationUnitChildren( child, result, level, maxLevel );
        }
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitBranch( int id )
    {
        OrganisationUnit organisationUnit = getOrganisationUnit( id );

        if ( organisationUnit == null )
        {
            return Collections.emptyList();
        }

        ArrayList<OrganisationUnit> result = new ArrayList<>();

        result.add( organisationUnit );

        OrganisationUnit parent = organisationUnit.getParent();

        while ( parent != null )
        {
            result.add( parent );

            parent = parent.getParent();
        }

        Collections.reverse( result ); // From root to target

        return result;
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsAtLevel( int level )
    {
        List<OrganisationUnit> roots = getRootOrganisationUnits();

        if ( level == 1 )
        {
            return roots;
        }

        return getOrganisationUnitsAtLevel( level, roots );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsAtLevel( int level, OrganisationUnit parent )
    {
        List<OrganisationUnit> parents = new ArrayList<>();
        parents.add( parent );

        return getOrganisationUnitsAtLevel( level, parent != null ? parents : null );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsAtLevel( int level, Collection<OrganisationUnit> parents )
    {
        Set<Integer> levels = new HashSet<>();
        levels.add( level );

        return getOrganisationUnitsAtLevels( levels, parents );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsAtLevels( Collection<Integer> levels, Collection<OrganisationUnit> parents )
    {
        if ( parents == null || parents.isEmpty() )
        {
            parents = new HashSet<>( getRootOrganisationUnits() );
        }

        List<OrganisationUnit> result = new ArrayList<>();

        for ( Integer level : levels )
        {
            if ( level < 1 )
            {
                throw new IllegalArgumentException( "Level must be greater than zero" );
            }

            for ( OrganisationUnit parent : parents )
            {
                int parentLevel = parent.getOrganisationUnitLevel();

                if ( level < parentLevel )
                {
                    throw new IllegalArgumentException(
                        "Level must be greater than or equal to level of parent organisation unit" );
                }

                if ( level == parentLevel )
                {
                    parent.setLevel( level );
                    result.add( parent );
                }
                else
                {
                    addOrganisationUnitChildrenAtLevel( parent, parentLevel + 1, level, result );
                }
            }
        }

        return result;
    }

    /**
     * Support method for getOrganisationUnitsAtLevel(). Adds all children at a
     * given targetLevel to a result collection. The parent's children are at
     * the current level.
     */
    private void addOrganisationUnitChildrenAtLevel( OrganisationUnit parent, int currentLevel, int targetLevel,
        List<OrganisationUnit> result )
    {
        if ( currentLevel == targetLevel )
        {
            for ( OrganisationUnit child : parent.getChildren() )
            {
                child.setLevel( currentLevel );
                result.add( child );
            }
        }
        else
        {
            for ( OrganisationUnit child : parent.getChildren() )
            {
                addOrganisationUnitChildrenAtLevel( child, currentLevel + 1, targetLevel, result );
            }
        }
    }

    @Override
    public int getNumberOfOrganisationalLevels()
    {
        int maxDepth = 0;
        int depth;

        for ( OrganisationUnit root : getRootOrganisationUnits() )
        {
            depth = getDepth( root, 1 );

            if ( depth > maxDepth )
            {
                maxDepth = depth;
            }
        }

        return maxDepth;
    }

    /**
     * Support method for getNumberOfOrganisationalLevels(). Finds the depth of
     * a given subtree. The parent is at the current level.
     */
    private int getDepth( OrganisationUnit parent, int currentLevel )
    {
        int maxDepth = currentLevel;
        int depth;

        for ( OrganisationUnit child : parent.getChildren() )
        {
            depth = getDepth( child, currentLevel + 1 );

            if ( depth > maxDepth )
            {
                maxDepth = depth;
            }
        }

        return maxDepth;
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsWithoutGroups()
    {
        return i18n( i18nService, organisationUnitStore.getOrganisationUnitsWithoutGroups() );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsByNameAndGroups( String query,
        Collection<OrganisationUnitGroup> groups, boolean limit )
    {
        return i18n( i18nService, organisationUnitStore.getOrganisationUnitsByNameAndGroups( query, groups, limit ) );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsByNameAndGroups( String name,
        Collection<OrganisationUnitGroup> groups, OrganisationUnit parent, boolean limit )
    {
        // Can only limit in query if parent is not set and we get all units

        boolean _limit = limit && parent == null;

        final Set<OrganisationUnit> result = new HashSet<>( organisationUnitStore.getOrganisationUnitsByNameAndGroups( name,
            groups, _limit ) );

        if ( parent == null )
        {
            return new ArrayList<OrganisationUnit>( result );
        }

        final Set<OrganisationUnit> subTree = new HashSet<>( getOrganisationUnitWithChildren( parent.getId() ) );

        List<OrganisationUnit> intersection = new ArrayList<OrganisationUnit>( Sets.intersection( subTree,
            result ) );

        return limit && intersection.size() > MAX_LIMIT ? intersection.subList( 0, MAX_LIMIT )
            : intersection;
    }

    @Override
    public OrganisationUnitDataSetAssociationSet getOrganisationUnitDataSetAssociationSet( Integer maxLevels )
    {
        Map<String, Set<String>> associationSet = Maps.newHashMap( organisationUnitStore.getOrganisationUnitDataSetAssocationMap() );

        filterUserDataSets( associationSet );
        filterChildOrganisationUnits( associationSet, maxLevels );

        OrganisationUnitDataSetAssociationSet set = new OrganisationUnitDataSetAssociationSet();

        for ( Map.Entry<String, Set<String>> entry : associationSet.entrySet() )
        {
            int index = set.getDataSetAssociationSets().indexOf( entry.getValue() );

            if ( index == -1 ) // Association set does not exist, add new
            {
                index = set.getDataSetAssociationSets().size();
                set.getDataSetAssociationSets().add( entry.getValue() );
            }

            set.getOrganisationUnitAssociationSetMap().put( entry.getKey(), index );
            set.getDistinctDataSets().addAll( entry.getValue() );
        }

        return set;
    }

    /**
     * Retains only the data sets from the map which the current user has access to.
     *
     * @param associationMap the associations between organisation unit and data sets.
     */
    private void filterUserDataSets( Map<String, Set<String>> associationMap )
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser != null && !currentUser.getUserCredentials().isSuper() )
        {
            Collection<String> userDataSets = IdentifiableObjectUtils.getUids( currentUser.getUserCredentials().getAllDataSets() );

            for ( Set<String> dataSets : associationMap.values() )
            {
                dataSets.retainAll( userDataSets );
            }
        }
    }

    /**
     * Retains only the organisation units in the sub-tree of the current user.
     *
     * @param associationMap the associations between organisation unit and data sets.
     * @param maxLevels      the maximum number of levels to include relative to
     *                       current user, inclusive.
     */
    private void filterChildOrganisationUnits( Map<String, Set<String>> associationMap, Integer maxLevels )
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser != null && currentUser.getOrganisationUnits() != null )
        {
            Collection<String> parentIds = getUids( currentUser.getOrganisationUnits() );

            Collection<OrganisationUnit> organisationUnitsWithChildren = getOrganisationUnitsWithChildren( parentIds, maxLevels );

            Collection<String> children = getUids( organisationUnitsWithChildren );

            associationMap.keySet().retainAll( children );
        }
    }

    @Override
    public void filterOrganisationUnitsWithoutData( Collection<OrganisationUnit> organisationUnits )
    {
        final Set<Integer> unitsWithoutData = organisationUnitStore.getOrganisationUnitIdsWithoutData();

        FilterUtils.filter( organisationUnits, new Filter<OrganisationUnit>()
        {
            @Override
            public boolean retain( OrganisationUnit unit )
            {
                return unit != null && (!unitsWithoutData.contains( unit.getId() ) || unit.hasChild());
            }
        } );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsBetween( int first, int max )
    {
        return i18n( i18nService, organisationUnitStore.getAllOrderedName( first, max ) );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, organisationUnitStore.getAllLikeName( name, first, max ) );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsBetweenByStatus( boolean status, int first, int max )
    {
        return i18n( i18nService, organisationUnitStore.getBetweenByStatus( status, first, max ) );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsBetweenByLastUpdated( Date lastUpdated, int first, int max )
    {
        return i18n( i18nService, organisationUnitStore.getBetweenByLastUpdated( lastUpdated, first, max ) );
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsBetweenByStatusLastUpdated( boolean status,
        Date lastUpdated, int first, int max )
    {
        return i18n( i18nService, organisationUnitStore.getBetweenByStatusLastUpdated( status, lastUpdated, first, max ) );
    }

    @Override
    public Map<String, OrganisationUnit> getUuidOrganisationUnitMap()
    {
        Map<String, OrganisationUnit> map = new HashMap<>();

        Collection<OrganisationUnit> organisationUnits = getAllOrganisationUnits();

        for ( OrganisationUnit organisationUnit : organisationUnits )
        {
            map.put( organisationUnit.getUuid(), organisationUnit );
        }

        return map;
    }

    @Override
    public boolean isInUserHierarchy( OrganisationUnit organisationUnit )
    {
        User user = currentUserService.getCurrentUser();
        
        if ( user == null || user.getOrganisationUnits() == null || user.getOrganisationUnits().isEmpty() )
        {
            return false;
        }

        return organisationUnit.isDescendant( user.getOrganisationUnits() );
    }

    @Override
    public boolean isInUserHierarchy( String uid, Set<OrganisationUnit> organisationUnits )
    {
        OrganisationUnit organisationUnit = organisationUnitStore.getByUid( uid );
        
        return organisationUnit != null ? organisationUnit.isDescendant( organisationUnits ) : false;
    }

    @Override
    public void setOrganisationUnitLevel( Collection<OrganisationUnit> organisationUnits )
    {
        for ( OrganisationUnit unit : organisationUnits )
        {
            if ( unit != null && !unit.hasLevel() )
            {
                unit.setLevel( getLevelOfOrganisationUnit( unit.getId() ) );
            }
        }
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitHierarchy
    // -------------------------------------------------------------------------

    @Override
    public OrganisationUnitHierarchy getOrganisationUnitHierarchy()
    {
        return organisationUnitStore.getOrganisationUnitHierarchy();
    }

    @Override
    public void updateOrganisationUnitParent( int organisationUnitId, int parentId )
    {
        organisationUnitStore.updateOrganisationUnitParent( organisationUnitId, parentId );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitLevel
    // -------------------------------------------------------------------------

    @Override
    public int addOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        return organisationUnitLevelStore.save( organisationUnitLevel );
    }

    @Override
    public void updateOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        organisationUnitLevelStore.update( organisationUnitLevel );
    }

    @Override
    public void addOrUpdateOrganisationUnitLevel( OrganisationUnitLevel level )
    {
        OrganisationUnitLevel existing = getOrganisationUnitLevelByLevel( level.getLevel() );

        if ( existing == null )
        {
            addOrganisationUnitLevel( level );
        }
        else
        {
            existing.setName( level.getName() );
            existing.setOfflineLevels( level.getOfflineLevels() );

            updateOrganisationUnitLevel( existing );
        }
    }

    @Override
    public void pruneOrganisationUnitLevels( Set<Integer> currentLevels )
    {
        for ( OrganisationUnitLevel level : getOrganisationUnitLevels() )
        {
            if ( !currentLevels.contains( level.getLevel() ) )
            {
                deleteOrganisationUnitLevel( level );
            }
        }
    }

    @Override
    public OrganisationUnitLevel getOrganisationUnitLevel( int id )
    {
        return organisationUnitLevelStore.get( id );
    }

    @Override
    public OrganisationUnitLevel getOrganisationUnitLevel( String uid )
    {
        return organisationUnitLevelStore.getByUid( uid );
    }

    @Override
    public List<OrganisationUnitLevel> getOrganisationUnitLevels( final Collection<Integer> identifiers )
    {
        List<OrganisationUnitLevel> objects = getOrganisationUnitLevels();

        return identifiers == null ? objects : FilterUtils.filter( objects, new Filter<OrganisationUnitLevel>()
        {
            @Override
            public boolean retain( OrganisationUnitLevel object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    @Override
    public void deleteOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        organisationUnitLevelStore.delete( organisationUnitLevel );
    }

    @Override
    public void deleteOrganisationUnitLevels()
    {
        organisationUnitLevelStore.deleteAll();
    }

    @Override
    public List<OrganisationUnitLevel> getOrganisationUnitLevels()
    {
        List<OrganisationUnitLevel> organisationUnitLevels = new ArrayList<>( i18n( i18nService,
            organisationUnitLevelStore.getAll() ) );

        Collections.sort( organisationUnitLevels, OrganisationUnitLevelComparator.INSTANCE );

        return organisationUnitLevels;
    }

    @Override
    public OrganisationUnitLevel getOrganisationUnitLevelByLevel( int level )
    {
        return i18n( i18nService, organisationUnitLevelStore.getByLevel( level ) );
    }

    @Override
    public List<OrganisationUnitLevel> getOrganisationUnitLevelByName( String name )
    {
        return new ArrayList<>( i18n( i18nService, organisationUnitLevelStore.getAllEqName( name ) ) );
    }

    @Override
    public List<OrganisationUnitLevel> getFilledOrganisationUnitLevels()
    {
        Map<Integer, OrganisationUnitLevel> levelMap = getOrganisationUnitLevelMap();

        List<OrganisationUnitLevel> levels = new ArrayList<>();

        for ( int i = 0; i < getNumberOfOrganisationalLevels(); i++ )
        {
            int level = i + 1;

            levels.add( levelMap.get( level ) != null ? levelMap.get( level ) : new OrganisationUnitLevel( level,
                LEVEL_PREFIX + level ) );
        }

        return levels;
    }

    @Override
    public Map<Integer, OrganisationUnitLevel> getOrganisationUnitLevelMap()
    {
        Map<Integer, OrganisationUnitLevel> levelMap = new HashMap<>();

        Collection<OrganisationUnitLevel> levels = getOrganisationUnitLevels();

        for ( OrganisationUnitLevel level : levels )
        {
            levelMap.put( level.getLevel(), level );
        }

        return levelMap;
    }

    @Override
    public int getNumberOfOrganisationUnits()
    {
        return organisationUnitStore.getCount();
    }

    @Override
    public int getMaxOfOrganisationUnitLevels()
    {
        return organisationUnitLevelStore.getMaxLevels();
    }

    @Override
    public int getOfflineOrganisationUnitLevels()
    {
        // ---------------------------------------------------------------------
        // Get level from organisation unit of current user
        // ---------------------------------------------------------------------

        User user = currentUserService.getCurrentUser();

        if ( user != null && user.hasOrganisationUnit() )
        {
            OrganisationUnit organisationUnit = user.getOrganisationUnit();

            int level = getLevelOfOrganisationUnit( organisationUnit.getId() );

            OrganisationUnitLevel orgUnitLevel = getOrganisationUnitLevelByLevel( level );

            if ( orgUnitLevel != null && orgUnitLevel.getOfflineLevels() != null )
            {
                return orgUnitLevel.getOfflineLevels();
            }
        }

        // ---------------------------------------------------------------------
        // Get level from system configuration
        // ---------------------------------------------------------------------

        OrganisationUnitLevel level = configurationService.getConfiguration().getOfflineOrganisationUnitLevel();

        if ( level != null )
        {
            return level.getLevel();
        }

        // ---------------------------------------------------------------------
        // Get max level
        // ---------------------------------------------------------------------

        int max = getOrganisationUnitLevels().size();

        OrganisationUnitLevel maxLevel = getOrganisationUnitLevelByLevel( max );

        if ( maxLevel != null )
        {
            return maxLevel.getLevel();
        }

        // ---------------------------------------------------------------------
        // Return 1 level as fall back
        // ---------------------------------------------------------------------

        return 1;
    }

    @Override
    public void updatePaths()
    {
        organisationUnitStore.updatePaths();
    }

    @Override
    public void forceUpdatePaths()
    {
        organisationUnitStore.forceUpdatePaths();
    }

    /**
     * Get all the Organisation Units within the distance of a coordinate.
     */
    @Override
    public List<OrganisationUnit> getOrganisationUnitWithinDistance( double longitude, double latitude,
        double distance )
    {
        List<OrganisationUnit> objects = organisationUnitStore.getWithinCoordinateArea( GeoUtils.getBoxShape(
            longitude, latitude, distance ) );

        // Go through the list and remove the ones located outside radius

        if ( objects != null && objects.size() > 0 )
        {
            Iterator<OrganisationUnit> iter = objects.iterator();

            Point2D centerPoint = new Point2D.Double( longitude, latitude );

            while ( iter.hasNext() )
            {
                OrganisationUnit orgunit = iter.next();

                double distancebetween = GeoUtils.getDistanceBetweenTwoPoints( centerPoint,
                    ValidationUtils.getCoordinatePoint2D( orgunit.getCoordinates() ) );

                if ( distancebetween > distance )
                {
                    iter.remove();
                }
            }
        }

        return objects;
    }

    /**
     * Get lowest level/target level Organisation Units that includes the coordinates.
     */
    @Override
    public List<OrganisationUnit> getOrganisationUnitByCoordinate( double longitude, double latitude,
        String topOrgUnitUid, Integer targetLevel )
    {
        List<OrganisationUnit> orgUnits = new ArrayList<>();

        if ( GeoUtils.checkGeoJsonPointValid( longitude, latitude ) )
        {
            OrganisationUnit topOrgUnit = null;

            if ( topOrgUnitUid != null && !topOrgUnitUid.isEmpty() )
            {
                topOrgUnit = getOrganisationUnit( topOrgUnitUid );
            }
            else
            {
                // Get top search point through top level org unit which contains coordinate

                Collection<OrganisationUnit> orgUnitsTopLevel = getTopLevelOrgUnitWithPoint( longitude, latitude, 1,
                    getNumberOfOrganisationalLevels() - 1 );

                if ( orgUnitsTopLevel.size() == 1 )
                {
                    topOrgUnit = orgUnitsTopLevel.iterator().next();
                }
            }

            // Search children org units to get the lowest level org unit that contains coordinate

            if ( topOrgUnit != null )
            {
                Collection<OrganisationUnit> orgUnitChildren = new ArrayList<>();

                if ( targetLevel != null )
                {
                    orgUnitChildren = getOrganisationUnitsAtLevel( targetLevel, topOrgUnit );
                }
                else
                {
                    orgUnitChildren = getOrganisationUnitWithChildren( topOrgUnit.getId() );
                }

                FilterUtils.filter( orgUnitChildren, new OrganisationUnitPolygonCoveringCoordinateFilter( longitude, latitude ) );

                // Get org units with lowest level

                int bottomLevel = topOrgUnit.getLevel();

                for ( OrganisationUnit ou : orgUnitChildren )
                {
                    if ( ou.getLevel() > bottomLevel )
                    {
                        bottomLevel = ou.getLevel();
                    }
                }

                for ( OrganisationUnit ou : orgUnitChildren )
                {
                    if ( ou.getLevel() == bottomLevel )
                    {
                        orgUnits.add( ou );
                    }
                }
            }
        }

        return orgUnits;
    }

    // -------------------------------------------------------------------------
    // Version
    // -------------------------------------------------------------------------

    @Override
    public void updateVersion()
    {
        versionService.updateVersion( VersionService.ORGANISATIONUNIT_VERSION );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Searches organisation units until finding one with polygon containing point.
     */
    private List<OrganisationUnit> getTopLevelOrgUnitWithPoint( double longitude, double latitude,
        int searchLevel, int stopLevel )
    {
        for ( int i = searchLevel; i <= stopLevel; i++ )
        {
            List<OrganisationUnit> unitsAtLevel = new ArrayList<>( getOrganisationUnitsAtLevel( i ) );
            FilterUtils.filter( unitsAtLevel, new OrganisationUnitPolygonCoveringCoordinateFilter( longitude, latitude ) );

            if ( unitsAtLevel.size() > 0 )
            {
                return unitsAtLevel;
            }
        }

        return new ArrayList<>();
    }
}
