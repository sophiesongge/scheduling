/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d.util;


import java.io.IOException;
import java.util.ArrayList;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeAdapter;
import org.objectweb.proactive.core.runtime.http.HttpProActiveRuntime;
import org.objectweb.proactive.core.util.UrlBuilder;



public class HttpHostRTFinder implements HostRTFinder {
    private IC2DMessageLogger logger;

    public HttpHostRTFinder(IC2DMessageLogger logger) {
        this.logger = logger;
    }

    /**
     * @see org.objectweb.proactive.ic2d.util.HostRTFinder#findPARuntimes(java.lang.String, int)
     */
    public ArrayList findPARuntimes(String host, int port)
        throws IOException {
        logger.log("Exploring " + host + " with HTTP on port " + port);
        ArrayList runtimeArray = new ArrayList();
        ProActiveRuntimeAdapter adapter;
        try {
            adapter = new ProActiveRuntimeAdapter(new HttpProActiveRuntime(UrlBuilder.buildUrl(
                        host, "", "http:", port)));
            runtimeArray.add(adapter);
        } catch (ProActiveException e) {
            logger.log(e.getMessage(), e);
        }
        
        return runtimeArray;
    }
}
