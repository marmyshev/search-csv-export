/*******************************************************************************
 * Copyright (c) 2012 Holger Voormann <http://voormann.de>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Holger Voormann - initial implementation
 *******************************************************************************/
package net.sf.csv_export;

import org.eclipse.search.ui.ISearchResult;

/**
 * Implementations of this interface returns all resources of a specific
 * {@link ISearchResult} that contain hits.
 *
 * @see TextSearchResultProvider
 */
public interface ISearchResultProvider {

    /**
     * @param searchResult current search result
     * @return {@code true} if and only if this class is able to process
     *         specified search result
     */
    boolean isApplicable(ISearchResult searchResult);

    /**
     * @param searchResult current search result
     * @return all resources that contain one or more hits
     */
    Object[] getResourcesWithHits(ISearchResult searchResult);

}
