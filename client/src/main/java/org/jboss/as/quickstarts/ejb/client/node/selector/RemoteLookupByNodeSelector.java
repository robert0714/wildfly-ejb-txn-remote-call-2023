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

package org.jboss.as.quickstarts.ejb.client.node.selector;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
 
  
/**
 * Methods for remote EJB lookup by Node Selector.
 */
public class RemoteLookupByNodeSelector {
    private static final String JNDI_PKG_PREFIXES = "org.jboss.ejb.client.naming";
    // this is name of the deployment on the second server. The project is named 'server' and the built war is the 'server.war'
    private static final String REMOTE_DEPLOYMENT_NAME = System.getProperty("remote.deployment.name", "server");

    private RemoteLookupByNodeSelector() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class, cannot be instantiated");
    }

     

    /**
     * Method which creates a remote proxy endpoint defined by the EJB class name and the EJB remote interface.
     * The method first defines the JNDI properties (put to {@link Properties} data structure) and then constructs
     * the string lookup representation of the remote bean as explained at
     * <a href="https://docs.wildfly.org/18/Developer_Guide.html#writing-a-remote-client-application-for-accessing-and-invoking-the-ejbs-deployed-on-the-server">WildFly Developer Guide</a>.
     *
     * This method connects to the particular host and port with the provided credentials.
     *
     * This method does not rely on remote outbound connection. The proxy is created directly from the data
     * provided as arguments.
     *
     * @param beanImplName name of the bean implemented at the remote server which implements the remote interface
     * @param remoteInterface remote interface with definition of metods that could be called against the remote EJB implementation
     * @param isStateful true if the remote EJB bean (endpoint) is the stateful bean, if it's stateless then use 'false'
     * @return bean which is casted to the remoteInterface
     * @throws NamingException is thrown when a trouble on InitialContext lookup occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T lookupRemoteEJBDirect(String beanImplName, Class<T> remoteInterface, boolean isStateful,
    		String[] hostsAndPorts, String user, String password ) throws NamingException {
        if(hostsAndPorts == null || hostsAndPorts.length == 0 || user == null || password == null) {
            throw new NullPointerException("host, port or credentials are null or undefined");
        }

        Properties jndiProperties = new Properties();
        jndiProperties.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");

        
        jndiProperties.put(Context.URL_PKG_PREFIXES, JNDI_PKG_PREFIXES);

        jndiProperties.put("endpoint.name", "client-endpoint");
        jndiProperties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
        jndiProperties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
        
        jndiProperties.put("invocation.timeout", "9000");
        jndiProperties.put("reconnect.tasks.timeout", "2000"); 
        jndiProperties.put("deployment.node.selector", RoundRobinDeploymentNodeSelector.class.getCanonicalName());
  

//        jndiProperties.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
//        jndiProperties.put("jboss.naming.client.connect.options.org.xnio.Options.SSL_STARTTLS", "false");
//        jndiProperties.put("jboss.naming.client.connect.options.org.xnio.Options.SSL_ENABLED", "false");
//        jndiProperties.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "true");
//        jndiProperties.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
        final StringBuilder connections = new StringBuilder();
        int sCount = 0;
        for (final String host : hostsAndPorts) { 
        	jndiProperties.put("remote.connection.s" + sCount + ".host", host.split(":")[0]);
        	jndiProperties.put("remote.connection.s" + sCount + ".port", host.split(":")[1]);
        	jndiProperties.put("remote.connection.s" + sCount + ".connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS",  "false");
            jndiProperties.put("remote.connection.s" + sCount + ".connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
            jndiProperties.put("remote.connection.s" + sCount + ".connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT",  "false");
            jndiProperties.put("remote.connection.s" + sCount + ".connect.timeout", "2000");
            
        	jndiProperties.put("remote.connection.s" + sCount + ".clusternode.selector", RoundRobinClusterNodeSelector.class.getCanonicalName());
            
            // fill connections list
            if (connections.length() > 0) {
                connections.append(",");
            }
            connections.append("s" + sCount);
            // prepare for next server
            sCount++;
        }
        System.out.println("---------------------------------");
        System.out.println("hostsAndPorts size: "+hostsAndPorts.length);
        
     // connections-list
        jndiProperties.put("remote.connections", connections.toString());
//        jndiProperties.put("jboss.naming.client.ejb.context", true);
        
        jndiProperties.put(Context.SECURITY_PRINCIPAL, user);
        jndiProperties.put(Context.SECURITY_CREDENTIALS, password);

        final Context context = new InitialContext(jndiProperties);

        return (T) context.lookup("ejb:/" + REMOTE_DEPLOYMENT_NAME + "/" + beanImplName + "!"
                + remoteInterface.getName() + (isStateful ? "?stateful" : ""));
    }
}
