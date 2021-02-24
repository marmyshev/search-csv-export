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

import org.eclipse.core.resources.IFile;
import org.eclipse.search.internal.ui.text.FileMatch;
import org.eclipse.search.internal.ui.text.LineElement;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * This is the default {@ISearchResultProvider} implementation of
 * {@link AbstractTextSearchResult}s that contributes via
 * {@code net.sf.csv_export.searchResultProvider} extension point following
 * columns to the CSV table to export:
 * <ol>
 *   <li>Project</li>
 *   <li>Path</li>
 *   <li>Location</li>
 *   <li>Line</li>
 *   <li>Text before</li>
 *   <li>Match</li>
 *   <li>Text after</li>
 * </ol>
 */
@SuppressWarnings("restriction")
public class TextSearchResultLabelProvider implements ISearchResultLabelProvider {

    private final static String[] TITLES = new String[] {Messages.project,
                                                         Messages.path,
                                                         Messages.location,
                                                         Messages.line,
                                                         Messages.textBefore,
                                                         Messages.match,
                                                         Messages.textAfter};

    @Override
    public boolean isApplicable(ISearchResult searchResult) {
        return searchResult instanceof AbstractTextSearchResult;
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public String[][] getLabels(ISearchResult searchResult, Object resource) {
        String fileProject = Messages.not_available;
        String filePath = Messages.not_available;
        String fileLocation = Messages.not_available;

        // file specific data (if available)
        if (resource instanceof IFile) {
            IFile file = (IFile) resource;

            // project
            if (file.getProject() != null) {
                fileProject = file.getProject().getName();
            }

            // path
            if (file.getFullPath() != null) {
                filePath = file.getFullPath().toString();
            }

            // location
            if (file.getLocation() != null) {
                fileLocation = file.getLocation().toString();
            }

        }

        // for each matches
        AbstractTextSearchResult textSearchResult =
                (AbstractTextSearchResult) searchResult;
        Match[] matchesPerFile = textSearchResult.getMatches(resource);
        String[][] result = new String[matchesPerFile.length][TITLES.length];
        for (int match = 0; match < result.length; match++) {
            Match currentMatch = matchesPerFile[match];

            // project, path, location
            result[match][0] = fileProject;
            result[match][1] = filePath;
            result[match][2] = fileLocation;

            // line containing the match (if the match contains
            // line breaks then it's the first line)
            LineElement lineElement =
                ((FileMatch) currentMatch).getLineElement();

            // line number
            int lineNumber = lineElement.getLine();
            result[match][3] = "" + lineNumber; //$NON-NLS-1$

            // text before, match, text after
            String line = lineElement.getContents();
            int start =   currentMatch.getOffset()
                        - lineElement.getOffset();
            int end = start + currentMatch.getLength();
            if (end > lineElement.getLength()) {
                end = lineElement.getLength();
            }
            result[match][4] = line.substring(0, start);   // text before
            result[match][5] = line.substring(start, end); // match
            result[match][6] = line.substring(end);        // text after
        }
        return result;
    }

}
