package org.hisp.dhis.util;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.joda.time.DateTime;

public class ObjectUtils
{
    public static final Map<String, Class<?>> VALUE_TYPE_JAVA_CLASS_MAP = new HashMap<String, Class<?>>() { {
        put( DataElement.VALUE_TYPE_INT, Double.class );
        put( DataElement.VALUE_TYPE_STRING, String.class );
        put( DataElement.VALUE_TYPE_BOOL, Boolean.class );
        put( DataElement.VALUE_TYPE_TRUE_ONLY, Boolean.class );
        put( DataElement.VALUE_TYPE_DATE, Date.class );
        put( DataElement.VALUE_TYPE_DATETIME, DateTime.class );
        put( DataElement.VALUE_TYPE_UNIT_INTERVAL, Double.class );
        put( DataElement.VALUE_TYPE_PERCENTAGE, Double.class );
        put( TrackedEntityAttribute.TYPE_NUMBER, Double.class );
        put( TrackedEntityAttribute.TYPE_LETTER, String.class );
        put( TrackedEntityAttribute.TYPE_OPTION_SET, String.class );
        put( TrackedEntityAttribute.TYPE_EMAIL, String.class );
        put( TrackedEntityAttribute.TYPE_PHONE_NUMBER, String.class );
    } };
    
    /**
     * Returns the first non-null argument. Returns null if all arguments are null.
     * 
     * @param objects the objects.
     * @return the first non-null argument.
     */
    @SafeVarargs
    public static final <T> T firstNonNull( T... objects )
    {
        if ( objects != null )
        {
            for ( T object : objects )
            {
                if ( object != null )
                {
                    return object;
                }
            }
        }
        
        return null;
    }
}
