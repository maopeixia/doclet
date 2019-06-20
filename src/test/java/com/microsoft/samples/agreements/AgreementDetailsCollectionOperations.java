// -----------------------------------------------------------------------
// <copyright file="AgreementDetailsCollectionOperations.java" company="Microsoft">
//      Copyright (c) Microsoft Corporation. All rights reserved.
// </copyright>
// -----------------------------------------------------------------------

package com.microsoft.samples.agreements;

import com.fasterxml.jackson.core.type.TypeReference;
import com.microsoft.samples.BasePartnerComponentString;
import com.microsoft.samples.IPartner;



/**
 * Agreement details collection operations implementation class.
 */
public class AgreementDetailsCollectionOperations
        extends BasePartnerComponentString
        implements IAgreementDetailsCollection
      
{
    /**
     * Initializes a new instance of the AgreementDetailsCollectionOperations class.
     *
     * @param rootPartnerOperations The root partner operations instance.
     */
    public AgreementDetailsCollectionOperations( IPartner rootPartnerOperations )
    {
        super( rootPartnerOperations );
    }

    /**
     * Retrieves the agreement details.
     *
     * @return A list of agreement details.
     */
    public ResourceCollection<AgreementMetaData> get()
    {
        return null;
    }
}