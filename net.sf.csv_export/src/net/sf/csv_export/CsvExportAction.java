/*******************************************************************************
 * Copyright (c) 2012 Tobias Althoff, Ji-Seung Shin, Susanne Könning.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Tobias Althoff, Ji-Seung Shin, Susanne Könning - initial implementation
 *     Holger Voormann - enhancements
 *******************************************************************************/
package net.sf.csv_export;

import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search2.internal.ui.SearchView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

import com.csvreader.CsvWriter;

/** Exports the file search results as comma-separated values (CSV). */
@SuppressWarnings("restriction")
public class CsvExportAction extends ActionDelegate implements
        IViewActionDelegate {

    private IViewPart view;

    @Override
    public void init(IViewPart view) {
        this.view = view;
    }

    @Override
    public void run(IAction action) {

        if (!(view instanceof SearchView))
            return; // impossible (because in plugin.xml:
                    // targetID="org.eclipse.search.ui.views.SearchView")

        SearchView searchView = (SearchView) view;
        ISearchResult searchResult = searchView.getCurrentSearchResult();

        if (searchResult == null) {
            // TODO show dialog: "No search result to export."
            return;
        }

        ISearchResultProvider resultProvider = new TextSearchResultProvider();
        ISearchResultLabelProvider[] labelProviders =
                CsvExportPlugin.computeLabelProviders(searchResult);

        // TODO show dialog: "This kind of search is not supported.
        // Currently results of File Search can be exported"
        if (   !resultProvider.isApplicable(searchResult)
            || labelProviders == null
            || labelProviders.length == 0) return;

        // show export dialog
        Shell shell = view.getViewSite().getShell();
        FileDialog exportDialog = new FileDialog(shell, SWT.SAVE);
        exportDialog.setFilterNames(new String[] { "Exported Files (*.csv)",
                                                   "All Files (*)" });
        exportDialog.setFilterExtensions(new String[] { "*.csv", "*" });
        exportDialog.setFileName("*.csv");
        exportDialog.setOverwrite(true);

        if (exportDialog.open() == null)
            return;

        // path of the file that was choose in the export dialog
        String path =   exportDialog.getFilterPath()
                      + "/"
                      + exportDialog.getFileName();

        // write to file
        Object[] resources = resultProvider.getResourcesWithHits(searchResult);
        try {
            CsvWriter writer = new CsvWriter(path);

            // TODO make following as preferences
            // writer.setDelimiter(';');

            // column headers
            int[] lengths = new int[labelProviders.length];
            for (int i = 0; i < labelProviders.length; i++) {
                String[] titles = labelProviders[i].getTitles();
                lengths[i] = titles.length;
                for (String currentTitle : titles) {
                    writer.write(currentTitle);
                }
            }
            writer.endRecord();

            // data
            for (Object currentResource : resources) {

                // labels per resource
                String[][][] labels = new String[labelProviders.length][][];
                for (int i = 0; i < labelProviders.length; i++) {
                    labels[i] = labelProviders[i].getLabels(searchResult,
                                                            currentResource);
                }

                // TODO check: label length == title length
                // TODO check: same number of hits per label provider

                for (int row = 0; row < labels[0].length; row++) {
                    for (int i = 0; i < labels.length; i++) {
                        for (String cell : labels[i][row]) {
                            writer.write(cell);
                        }
                    }
                    writer.endRecord();
                }

            }

            // finished
            writer.flush();
            writer.close();

        } catch (IOException e) {

            // in case the file that was choose in the export dialog is
            // opened a MessageBox opens that informs the user about this
            MessageBox errorDialog = new MessageBox(shell);
            errorDialog.setMessage(Messages.error_message_file_opened);
            errorDialog.open();

        }

    }

}
