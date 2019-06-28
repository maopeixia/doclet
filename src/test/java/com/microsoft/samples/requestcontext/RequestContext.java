// -----------------------------------------------------------------------
// <copyright file="RequestContext.java" company="Microsoft">
//      Copyright (c) Microsoft Corporation. All rights reserved.
// </copyright>
// -----------------------------------------------------------------------

package com.microsoft.samples.requestcontext;

import java.text.MessageFormat;
import java.util.UUID;


/**
 * Request context implementation.
 */
public class RequestContext
    implements IRequestContext
{
    /**
     * Initializes a new instance of the RequestContext class. Correlation Id will be generated. The request Id
     * will be automatically generated for each service API call.
     */
 
  

    private UUID __RequestId;
    
    /**
     * Gets the request identifier. Uniquely identifies the operation.
     * 
     * @return the request identifier.
     */
    @Override
    public UUID getRequestId()
    {
        return __RequestId;
    }

    /**
     * Sets the request identifier.
     * 
     * @param value The request identifier.
     */
    public void setRequestId( UUID value )
    {
        __RequestId = value;
    }

    private UUID __CorrelationId;

    /**
     * Gets the correlation identifier. This identifier is used to group logical operations together.
     */
    @Override
    public UUID getCorrelationId()
    {
        return __CorrelationId;
    }

    /**
     * Sets the correlation identifier.
     * 
     * @param value The correlation identifier.
     */
    public void setCorrelationId( UUID value )
    {
        __CorrelationId = value;
    }

    private String __Locale;

    /**
     * Gets the locale.
     * 
     * @return The locale.
     */
    @Override
    public String getLocale()
    {
        return __Locale;
    }

    /**
     * Sets the locale.
     * 
     * @param value The locale.
     */
    public void setLocale( String value )
    {
        __Locale = value;
    }

    /**
     * Returns a string representation of the request context.
     * 
     * @return A string representation of the request context.
     */
    @Override
    public String toString()
    {
        return MessageFormat.format( "Request Id: {0}, Correlation Id: {1}, Locale: {2}"
            + this.getRequestId().toString(), this.getCorrelationId().toString(), this.getLocale() );
    }
}