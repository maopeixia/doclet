// -----------------------------------------------------------------------
// <copyright file="PartnerOperations.java" company="Microsoft">
//      Copyright (c) Microsoft Corporation. All rights reserved.
// </copyright>
// -----------------------------------------------------------------------

package com.microsoft.samples;


import com.microsoft.samples.agreements.IAgreementDetailsCollection;


/**
 * The partner implementation class.
 */
public class PartnerOperations
	implements IPartner
{
	// /**
	// * The resource collection enumerator container.
	// */


	/**
	 * The agreement metadata collection operations.
	 */
	private IAgreementDetailsCollection agreements;

	/**
	 * The partner service used for performing HTTP operations.
	 */
	
	/**
	 * Gets the partner credentials.
	 */
	private IPartnerCredentials credentials;
	
	public String getCredentials()
	{
		return "";
		
	}

	
}