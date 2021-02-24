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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.search.ui.ISearchResult;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CsvExportPlugin implements BundleActivator {

    /** The ID of this plug-in/bundle. */
    private static final String ID = "net.sf.csv_export"; //$NON-NLS-1$

    /** The singleton instance of this class representing plug-in/bundle. */
    private static CsvExportPlugin plugin;

    private static final String PROVIDER_EXTENSION_POINT_NAME =
            "searchResultProvider";  //$NON-NLS-1$

    private static final String ELEMENT_LABEL_PROVIDER =
            "labelProvider";  //$NON-NLS-1$

    private static final String ATTRIBUTE_RANK = "rank";  //$NON-NLS-1$

    private static final String ATTRIBUTE_CLASS = "class";  //$NON-NLS-1$

    private static final Object PROVIDER_LOCK = new Object();

    private IExtension[] providerExtensions;

    private final IRegistryChangeListener providerListener =
            new IRegistryChangeListener() {

        @Override
        public void registryChanged(IRegistryChangeEvent event) {
            IExtensionDelta[] deltas =
                    event.getExtensionDeltas(ID, PROVIDER_EXTENSION_POINT_NAME);
            if (deltas.length == 0) return;

            synchronized (PROVIDER_LOCK) {
                providerExtensions = computeProviderExtensions();
            }
        }

    };

    @Override
    public void start(BundleContext context) throws Exception {
        if (plugin != null) {
            throw new RuntimeException("Bundle must be singleton"); //$NON-NLS-1$
        }
        plugin = this;

        // initialize extension point listener
        synchronized (PROVIDER_LOCK) {
            providerExtensions = computeProviderExtensions();
            IExtensionRegistry registry = Platform.getExtensionRegistry();
            registry.addRegistryChangeListener(providerListener);
        }

    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        synchronized (PROVIDER_LOCK) {
            registry.removeRegistryChangeListener(providerListener);
            providerExtensions = null;
        }
    }

    public static ISearchResultLabelProvider[] computeLabelProviders(
            ISearchResult searchResult) {
        synchronized (PROVIDER_LOCK) {
            List<LabelProviderExtension> results =
                    new ArrayList<LabelProviderExtension>();
            for (IExtension extension : plugin.providerExtensions) {
                IConfigurationElement[] ElementsOfExtension =
                        extension.getConfigurationElements();
                for (IConfigurationElement element : ElementsOfExtension) {
                    if (!ELEMENT_LABEL_PROVIDER.equals(element.getName()))
                        continue;

                    // class
                    ISearchResultLabelProvider provider = null;
                    try {
                        Object o = element.createExecutableExtension(ATTRIBUTE_CLASS);
                        if (o instanceof ISearchResultLabelProvider) {
                            provider = (ISearchResultLabelProvider) o;
                        }
                    } catch (CoreException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // rank
                    String rankAsString = element.getAttribute(ATTRIBUTE_RANK);
                    int rank = Integer.MIN_VALUE;
                    if (rankAsString != null) {
                        try {
                            rank = Integer.parseInt(rankAsString);
                        } catch (NumberFormatException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    // applicable?
                    if (   provider != null
                        && provider.isApplicable(searchResult)) {
                        results.add(new LabelProviderExtension(rank, provider));
                    }

                }
            }

            // sort by rank
            Collections.sort(results);
            ISearchResultLabelProvider[] labelProviders =
                    new ISearchResultLabelProvider[results.size()];
            for (int i = 0; i < labelProviders.length; i++) {
                labelProviders[i] = results.get(i).getLabelProvider();
            }
            return labelProviders;
        }
    }

    private IExtension[] computeProviderExtensions() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint =
                registry.getExtensionPoint(ID, PROVIDER_EXTENSION_POINT_NAME);
        return extensionPoint == null
               ? new IExtension[0]
               : extensionPoint.getExtensions();
    }

    private static class LabelProviderExtension
            implements Comparable<LabelProviderExtension> {

        private final int rank;

        private final ISearchResultLabelProvider labelProvider;

        public LabelProviderExtension(int rank,
                ISearchResultLabelProvider labelProvider) {
            this.rank = rank;
            this.labelProvider = labelProvider;
        }

        public ISearchResultLabelProvider getLabelProvider() {
            return labelProvider;
        }

        @Override
        public int compareTo(LabelProviderExtension other) {
            return other.rank - rank;
        }

    }

}
