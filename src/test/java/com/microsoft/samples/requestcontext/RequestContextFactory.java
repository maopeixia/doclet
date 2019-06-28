//-----------------------------------------------------------------------
// <copyright file="RequestContextFactory.java" company="Microsoft">
//   Copyright (c) Microsoft Corporation. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------

package com.microsoft.samples.requestcontext;


public class RequestContextFactory
{
    /**
     * A singleton instance of the request context factory.
     */
    private static RequestContextFactory instance = new RequestContextFactory();

    /**
     * Prevents a default instance of the <see cref="RequestContextFactory"/> class from being created.
     */
    private RequestContextFactory()
    {
    }

    /**
     * Gets an instance of the request context factory.
     * 
     * @return An instance of the request context factory.
     */
    public static RequestContextFactory getInstance()
    {
        return RequestContextFactory.instance;
    }

    /**
     * Creates a request context object which will use a randomly generated correlation Id and a unique request Id for
     * each partner API call.
     * 
     * @return A request context object.
     */
    public IRequestContext create()
    {
        return new RequestContext();
    }



}