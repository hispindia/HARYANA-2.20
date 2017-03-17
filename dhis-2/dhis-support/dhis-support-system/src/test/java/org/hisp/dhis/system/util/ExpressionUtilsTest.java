package org.hisp.dhis.system.util;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.commons.util.ExpressionUtils;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class ExpressionUtilsTest
{
    private static final double DELTA = 0.01;
    
    @Test
    public void testEvaluateToDouble()
    {
        assertEquals( 3d, ExpressionUtils.evaluateToDouble( "3", null ), DELTA );
        assertEquals( 3.45, ExpressionUtils.evaluateToDouble( "3.45", null ), DELTA );
        assertEquals( 5d, ExpressionUtils.evaluateToDouble( "2 + 3", null ), DELTA );
        assertEquals( 15.6, ExpressionUtils.evaluateToDouble( "12.4 + 3.2", null ), DELTA );        
        assertEquals( 3d, ExpressionUtils.evaluateToDouble( "d2:zing(3)", null ), DELTA );        
        assertEquals( 2d, ExpressionUtils.evaluateToDouble( "d2:zing(-3) + 2.0", null ), DELTA );
        assertEquals( 4d, ExpressionUtils.evaluateToDouble( "d2:zing(-1) + 4 + d2:zing(-2)", null ), DELTA );
        assertEquals( 0d, ExpressionUtils.evaluateToDouble( "d2:oizp(-4)", null ), DELTA );
        assertEquals( 1d, ExpressionUtils.evaluateToDouble( "d2:oizp(0)", null ), DELTA );
        assertEquals( 2d, ExpressionUtils.evaluateToDouble( "d2:oizp(-4) + d2:oizp(0) + d2:oizp(3.0)", null ), DELTA );
        assertEquals( 2d, ExpressionUtils.evaluateToDouble( "d2:zpvc(1,3)", null ), DELTA );
        assertEquals( 3d, ExpressionUtils.evaluateToDouble( "d2:zpvc(1,-1,2,-3,0)", null ), DELTA );
    }
    
    @Test
    public void testEvaluateToDoubleWithVars()
    {
        Map<String, Object> vars = new HashMap<String, Object>();
        
        vars.put( "v1", 4d );
        vars.put( "v2", -5d );
        
        assertEquals( 7d, ExpressionUtils.evaluateToDouble( "v1 + 3", vars ), DELTA );
        assertEquals( 4d, ExpressionUtils.evaluateToDouble( "d2:zing(v1)", vars ), DELTA );
        assertEquals( 0d, ExpressionUtils.evaluateToDouble( "d2:zing(v2)", vars ), DELTA );
        assertEquals( 4d, ExpressionUtils.evaluateToDouble( "d2:zing(v1) + d2:zing(v2)", vars ), DELTA );
    }
    
    @Test
    public void testIsTrue()
    {
        assertTrue( ExpressionUtils.isTrue( "2 > 1", null ) );
        assertTrue( ExpressionUtils.isTrue( "(2 * 3) == 6", null ) );
        assertTrue( ExpressionUtils.isTrue( "\"a\" == \"a\"", null ) );
        assertTrue( ExpressionUtils.isTrue( "'b' == 'b'", null ) );
        assertTrue( ExpressionUtils.isTrue( "('b' == 'b') && ('c' == 'c')", null ) );
        assertTrue( ExpressionUtils.isTrue( "'goat' == 'goat'", null ) );
        
        assertFalse( ExpressionUtils.isTrue( "2 < 1", null ) );
        assertFalse( ExpressionUtils.isTrue( "(2 * 3) == 8", null ) );
        assertFalse( ExpressionUtils.isTrue( "\"a\" == \"b\"", null ) );
        assertFalse( ExpressionUtils.isTrue( "'b' == 'c'", null ) );
        assertFalse( ExpressionUtils.isTrue( "'goat' == 'cow'", null ) );
    }
    
    @Test
    public void testIsTrueWithVars()
    {
        Map<String, Object> vars = new HashMap<String, Object>();
        
        vars.put( "v1", "4" );
        vars.put( "v2", "12" );
        vars.put( "v3", "goat" );
        vars.put( "v4", "horse" );
        
        assertTrue( ExpressionUtils.isTrue( "v1 > 1", vars ) );
        assertTrue( ExpressionUtils.isTrue( "v2 < 18", vars ) );
        assertTrue( ExpressionUtils.isTrue( "v2 < '23'", vars ) );
        assertTrue( ExpressionUtils.isTrue( "v3 == 'goat'", vars ) );
        assertTrue( ExpressionUtils.isTrue( "v4 == 'horse'", vars ) );
        assertTrue( ExpressionUtils.isTrue( "v4 == \"horse\"", vars ) );

        assertFalse( ExpressionUtils.isTrue( "v1 < 1", vars ) );
        assertFalse( ExpressionUtils.isTrue( "v2 > 18", vars ) );
        assertFalse( ExpressionUtils.isTrue( "v2 > '23'", vars ) );
        assertFalse( ExpressionUtils.isTrue( "v3 == 'cow'", vars ) );
        assertFalse( ExpressionUtils.isTrue( "v4 == 'goat'", vars ) );
        assertFalse( ExpressionUtils.isTrue( "v4 == \"goat\"", vars ) );
    }
    
    @Test
    public void testIsBoolean()
    {
        Map<String, Object> vars = new HashMap<String, Object>();
        
        vars.put( "uA2hsh8j26j", "FEMALE" );
        vars.put( "v2", "12" );
        
        assertTrue( ExpressionUtils.isBoolean( "2 > 1", null ) );
        assertTrue( ExpressionUtils.isBoolean( "(2 * 3) == 6", null ) );
        assertTrue( ExpressionUtils.isBoolean( "\"a\" == \"a\"", null ) );
        assertTrue( ExpressionUtils.isBoolean( "'b' == 'b'", null ) );
        assertTrue( ExpressionUtils.isBoolean( "('b' == 'b') && ('c' == 'c')", null ) );
        assertTrue( ExpressionUtils.isBoolean( "'goat' == 'goat'", null ) );

        assertFalse( ExpressionUtils.isBoolean( "4", null ) );
        assertFalse( ExpressionUtils.isBoolean( "3 + 2", null ) );
        assertFalse( ExpressionUtils.isBoolean( "someinvalid expr", null ) );
    }
    
    @Test
    public void testAsSql()
    {
        assertEquals( "2 > 1 and 3 < 4", ExpressionUtils.asSql( "2 > 1 && 3 < 4" ) );
        assertEquals( "2 > 1 or 3 < 4", ExpressionUtils.asSql( "2 > 1 || 3 < 4" ) );
        assertEquals( "'a' = 1", ExpressionUtils.asSql( "'a' == 1" ) );
        assertEquals( "\"oZg33kd9taw\" = 'Female'", ExpressionUtils.asSql( "\"oZg33kd9taw\" == 'Female'" ) );
    }
        
    @Test
    public void testIsValid()
    {
        Map<String, Object> vars = new HashMap<String, Object>();
        
        vars.put( "v1", "12" );
        
        assertTrue( ExpressionUtils.isValid( "2 + 8", null ) );
        assertTrue( ExpressionUtils.isValid( "3 - v1", vars ) );
        assertTrue( ExpressionUtils.isValid( "d2:zing(1)", null ) );
        assertTrue( ExpressionUtils.isValid( "(d2:zing(1)+d2:zing(1))*50/1", null ) );
        assertTrue( ExpressionUtils.isValid( "1/(1/100)", null ) );
        assertTrue( ExpressionUtils.isValid( "SUM(1)", null ) );
        assertTrue( ExpressionUtils.isValid( "average(2+1)", null ) );
        
        assertFalse( ExpressionUtils.isValid( "2 a 3", null ) );
        assertFalse( ExpressionUtils.isValid( "v2 + 3", vars ) );
        assertFalse( ExpressionUtils.isValid( "4 + abc", vars ) );
        assertFalse( ExpressionUtils.isValid( "'goat' == goat", null ) );
        assertFalse( ExpressionUtils.isValid( "aver(2+1)", null ) );
    }
}
