/*******************************************************************************
 * Copyright (c) 2012 Holger Voormann, Tobias Althoff, Ji-Seung Shin,
 *                    Susanne Könning
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Holger Voormann, Tobias Althoff, Ji-Seung Shin, Susanne Könning
 *         - initial implementation
 *******************************************************************************/
package net.sf.csv_export;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.resources.IFile;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

/**
 * This is the default {@ISearchResultProvider} implementation for
 * {@link AbstractTextSearchResult}s.
 */
public class TextSearchResultProvider implements ISearchResultProvider {

    @Override
    public boolean isApplicable(ISearchResult searchResult) {
        return searchResult instanceof AbstractTextSearchResult;
    }

    @Override
    public Object[] getResourcesWithHits(ISearchResult searchResult) {
        Object[] files = ((AbstractTextSearchResult) searchResult).getElements();
        Arrays.sort(files, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                if (!(o1 instanceof IFile) && !(o2 instanceof IFile))
                    return 0;
                if (!(o1 instanceof IFile)) return -1;
                if (!(o2 instanceof IFile)) return 1;

                IFile f1 = (IFile) o1;
                IFile f2 = (IFile) o2;

                if (f1.getLocation() == null && f2.getLocation() == null)
                    return 0;
                if (f1.getLocation() == null) return -1;
                if (f2.getLocation() == null) return 1;

                return f1.getLocation().toString().compareTo(
                           f2.getLocation().toString());
            }
        });
        return files;
    }

}
