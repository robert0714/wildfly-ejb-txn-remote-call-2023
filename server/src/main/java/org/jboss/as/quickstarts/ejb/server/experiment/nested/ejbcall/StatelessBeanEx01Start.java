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

package org.jboss.as.quickstarts.ejb.server.experiment.nested.ejbcall;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.apache.commons.lang3.StringUtils;
import org.jboss.as.quickstarts.ejb.entity.CalleeUser;
import org.jboss.as.quickstarts.ejb.mock.MockXAResource;
import org.jboss.as.quickstarts.ejb.server.InfoUtils;
import org.jboss.as.quickstarts.ejb.server.experiment.nested.SelfBeanCaller;
import org.jboss.logging.Logger;

/**
 * Stateless bean to be called.
 */
@Stateless
@Remote (RemoteBeanEx01StartInterface.class)
public class StatelessBeanEx01Start implements RemoteBeanEx01StartInterface {
    private static final Logger log = Logger.getLogger(StatelessBeanEx01Start.class);

    @Resource(lookup = "java:/TransactionManager")
    private TransactionManager tm;

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private SelfBeanCaller selfCaller;

    /**
     * Stateless remote ejb method to be called from the client side.
     *
     * @return information about the host that this EJB resides on
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String successOnCall() {
        log.debugf("Called '%s.successOnCall()' with transaction status %s",
                this.getClass().getName(), InfoUtils.getTransactionStatus());
        StringBuffer sbf = new StringBuffer();
        // transaction enlists XAResource #1
        try {
        	List<String> endRepsonses = selfCaller.directLookupStatelessBeanOverEjbRemotingCallByNodeSelector();
			if (endRepsonses != null && endRepsonses.size() > 0) {
				sbf.append( StringUtils.join(endRepsonses,".") );
			}
            tm.getTransaction().enlistResource(new MockXAResource());
        } catch(SystemException | RollbackException | NamingException sre) {
            throw new IllegalStateException("Cannot enlist a " + MockXAResource.class.getName() + " to the current transaction", sre);
        }
        // transaction enlists XAResource #2
        em.persist(new CalleeUser("Bard", "The Bowman"));

        sbf.append( InfoUtils.getHostInfo() );
        
        return sbf.toString();
    }

    /**
     * Failure during the commit processing does not mean an exception is thrown by transaction manager processing.
     * The business method is marked as succesfully finished.
     * The uncommitted {@link javax.transaction.xa.XAResource}s will be committed later by periodic recovery.
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String failOnCall() {
        log.debugf("Called '%s.failOnCall()' with transaction status %s",
                this.getClass().getName(), InfoUtils.getTransactionStatus());

        // transaction enlists XAResource #1 with forcing to fail
        try {
            tm.getTransaction().enlistResource(new MockXAResource(MockXAResource.TestAction.COMMIT_THROW_XAER_RMFAIL));
        } catch(SystemException | RollbackException sre) {
            throw new IllegalStateException("Cannot enlist a " + MockXAResource.class.getName() + " to the current transaction", sre);
        }
        // transaction enlists XAResource #2
        em.persist(new CalleeUser("Bard", "The Bowman"));

        return InfoUtils.getHostInfo();
    }
}
