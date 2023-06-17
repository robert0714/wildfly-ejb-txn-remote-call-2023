/*
 * JBoss, Home of Professional Open Source
 * Copyright 2021, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.as.quickstarts.ejb.server.experiment.nested;

import javax.ejb.Stateless; 
import javax.naming.NamingException; 
  
import org.jboss.as.quickstarts.ejb.server.experiment.nested.ejbcall.RemoteBeanEx01EndInterface;
import org.jboss.as.quickstarts.ejb.server.experiment.nested.node.selectos.RemoteLookupByNodeSelector;
import org.jboss.logging.Logger;
 
import java.util.Arrays;
import java.util.List;

/**
 * EJB which runs the remote calls to the EJBs.
 * We use the EJB here for benefit of automatic transaction management.
 */
@Stateless
public class SelfBeanCaller {
    private static final Logger log = Logger.getLogger(SelfBeanCaller.class);
 
    /**
     * What happen to calling myself  ejbs  when using Node Selector for EJB load balancing calls ?  
     *
     * @return list of strings as return values from the remote beans,
     *         in this case the return values are hostname and the jboss node names of the remote application server
     * @throws NamingException when remote lookup fails
     */
    public List<String> directLookupStatelessBeanOverEjbRemotingCallByNodeSelector() throws NamingException {
        log.debugf("Calling direct lookup with transaction to StatelessBean.successOnCall()");

        String[] hostsAndPorts = RemoteLookupByNodeSelector.getHostsAndPortsFromJavaOpts();
        String remoteUsername = System.getProperty("remote.server.username");
        String remotePassword = System.getProperty("remote.server.password");

        RemoteBeanEx01EndInterface bean = RemoteLookupByNodeSelector.lookupRemoteEJBDirect("StatelessBeanEx01End", RemoteBeanEx01EndInterface.class, false,
        		hostsAndPorts, remoteUsername, remotePassword );
        return Arrays.asList(
                bean.successOnCall(),
                bean.successOnCall()
        );
    }
 
}
