package org.jboss.as.quickstarts.ejb.server.experiment.nested.node.selectos;

import org.jboss.ejb.client.DeploymentNodeSelector;

public class RoundRobinDeploymentNodeSelector implements DeploymentNodeSelector
{
    private int nodeIndex = 0;

    @Override
    public String selectNode(String[] eligibleNodes, String appName, String moduleName,
                             String distinctName)
    {
    	if(eligibleNodes!=null && eligibleNodes.length>0) {
    		System.out.println("=========== RoundRobinDeploymentNodeSelector.selectNode =========== ");
    		for(String nodeName: eligibleNodes) {
    			System.out.println(nodeName);
    		}
    		System.out.println("=========== RoundRobinDeploymentNodeSelector.selectNode =========== ");
    	}
        String selectedNode = eligibleNodes[nodeIndex++ % eligibleNodes.length];
        
        System.out.println("RoundRobinDeploymentNodeSelector.selectNode => " + selectedNode);
        return selectedNode;
    }
}
