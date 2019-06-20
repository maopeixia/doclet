// -----------------------------------------------------------------------
// <copyright file="IAgreementDetailsCollection.java" company="Microsoft">
//      Copyright (c) Microsoft Corporation. All rights reserved.
// </copyright>
// -----------------------------------------------------------------------

package com.microsoft.samples.agreements;



/**
 * Encapsulates the operations on the agreement metadata collection.
 */
public interface IAgreementDetailsCollection
{
    /**
     * Retrieves all current agreement metadata.
     *
     * @return The current agreement metadata.
     */
    ResourceCollection<AgreementMetaData> get();
}