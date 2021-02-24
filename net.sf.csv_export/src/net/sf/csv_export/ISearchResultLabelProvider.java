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
 * Implementations of this interface could contribute via the
 * {@code net.sf.csv_export.searchResultProvider} extension points columns to
 * the CSV table to export.
 *
 * @see TextSearchResultLabelProvider
 */
public interface ISearchResultLabelProvider {

    /**
     * @param searchResult current search result
     * @return {@code true} if and only if this class is able to contribute
     *         value columns to the specified search result to export as CSV
     *         table
     */
    boolean isApplicable(ISearchResult searchResult);

    /**
     * @return column headers; must not be {@code null} and the length of the
     *         returned array must be equal to the length of each inner array
     *         returned by {@link #getLabels(ISearchResult, Object)}.
     */
    String[] getTitles();

    /**
     * @param searchResult current search result with all hits
     * @param resource only values of hits contained in the specified resource
     *        must be returned
     * @return array of arrays of values: for each hit (outer array) of the
     *         specified resource the values for all columns (inner array) are
     *         returned; the length of each inner array must be equal to the
     *         length of the array returned by {@link #getTitles()}.
     */
    String[][] getLabels(ISearchResult searchResult, Object resource);

}
