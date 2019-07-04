// -----------------------------------------------------------------------
// <copyright file="Link.java" company="Microsoft">
//      Copyright (c) Microsoft Corporation. All rights reserved.
// </copyright>
// -----------------------------------------------------------------------

package com.microsoft.samples;


import java.util.Collection;


import com.microsoft.samples.KeyValuePair;


/**
 * Link represents a URI and the HTTP method which indicates the desired action for accessing the resource.
 */
public class Link
{
    public Link()
    {
    }

    /**
     * Initializes a new instance of the Link class.
     * 
     * @param uri The URI.
     */


    /**
     * Gets the URI.


    /**
     * Gets the method.
     */
    private String method;

    public String getMethod()
    {
        return method;
    }

    public void setMethod( String value )
    {
        method = value;
    }

    /**
     * Gets the link headers.
     */
    private Collection<KeyValuePair<String, String>> headers;

    public Collection<KeyValuePair<String, String>> getHeaders()
    {
        return headers;
    }

    public void setHeaders( Collection<KeyValuePair<String, String>> value )
    {
        headers = value;
    }
}