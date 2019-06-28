// -----------------------------------------------------------------------
// <copyright file="StandardPartnerFactory.java" company="Microsoft">
//      Copyright (c) Microsoft Corporation. All rights reserved.
// </copyright>
// -----------------------------------------------------------------------

package com.microsoft.samples.factory;

import com.microsoft.samples.*;


/**
 * Standard implementation of the partner factory.
 */
public class StandardPartnerFactory
    implements IPartnerFactory
{
    /**
     * Builds an IAggregatePartner instance and configures it using the provided partner credentials.
     * 
     * @param credentials The partner credentials. Use the extensions to obtain these.
     * @return A configured partner object.
     */
    @Override
    public IAggregatePartner build( IPartnerCredentials credentials )
    {
        return new AggregatePartnerOperations( credentials );
    }
}