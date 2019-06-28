// -----------------------------------------------------------------------
// <copyright file="IPartnerCredentials.java" company="Microsoft">
//      Copyright (c) Microsoft Corporation. All rights reserved.
// </copyright>
// -----------------------------------------------------------------------

package com.microsoft.samples;



import com.microsoft.samples.requestcontext.IRequestContext;

/**
 * The credentials needed to access the partner service.
 */
public interface IPartnerCredentials
{
    /**
     * Gets the token needed to authenticate with the partner service.
     * 
     * @return The token needed to authenticate with the partner service. 
     */
    String getPartnerServiceToken();

    /**
     * Gets the expiry time in UTC for the token.
     * 
     * @return The expiry time in UTC for the token.
     */

    /**
     * Indicates whether the partner credentials have expired or not.
     * 
     * @return true if credentials have expired; otherwise false. 
     */
    boolean isExpired();

    /**
     * Called when a partner credentials needs to be refreshed.
     * 
     * @param credentials The outdated partner credentials.
     * @param context The request context.
     */
    void onCredentialsRefreshNeeded( IPartnerCredentials credentials, IRequestContext context );
}