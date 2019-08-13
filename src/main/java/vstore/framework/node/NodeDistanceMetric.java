package vstore.framework.node;

import java.util.ArrayList;
import java.util.List;

import vstore.framework.context.ContextManager;
import vstore.framework.context.types.location.VLocation;

/**
 * Distance metric class which calculates the distance metric for a storage node.
 * Provides additional methods for sorting a list of storage nodes by the distance metric
 * and others.
 */
public class NodeDistanceMetric {

    /**
     * Calculates the distance metric of a storage node.
     * Currently it is a simple metric based on the type of the node.
     *
     * @param n The node object for which to calculate the distance metric
     * @return The distance metric (lower values are better)
     */
    public static float getDistanceMetric(NodeInfo n) {
        switch(n.getNodeType()) {
            case CLOUDLET:
                return 1;
            case GATEWAY:
                return 2;
            case CORENET:
                return 3;
            case CLOUD:
                return 4;
        }
        return 4;
    }
    
    /**
     * Calculates the distance between the currently provided LocationContext and a storage node.
     * Return value is inversed to enable sorted list to keep its sorting order:
     * The lower the distance, the higher the returned value.
     * @param n the storage node 
     * @return Float.MAX_VALUE - (distance in meters)
     */
    public static float getDistance(NodeInfo n) {
    	VLocation currentPos = ContextManager.get().getCurrentContext().getLocationContext();
    	
    	// if no location context is provided, fallback to distance metric
    	if (currentPos == null) {
    		return getDistanceMetric(n);
    	}
    	
    	double nodeLat = n.getLatLng().getLatitude();
    	double nodeLng = n.getLatLng().getLongitude();
    	double currentLat = currentPos.getLatLng().getLatitude();
    	double currentLng = currentPos.getLatLng().getLongitude();
    	
    	// use haversine formula to calculate distance between both LatLng pairs
    	final int R = 6371000;	// earth radius in meters
		double rho1 = Math.toRadians(currentLat);
		double rho2 = Math.toRadians(nodeLat);
		double deltaRho = Math.toRadians(currentLat - nodeLat);
		double deltaLambda = Math.toRadians(currentLng - nodeLng);
		
		double a = Math.sin(deltaRho / 2) * Math.sin(deltaRho / 2) 
					+ Math.cos(rho1) * Math.cos(rho2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		
		return (float) (R * c);
    	
    }

    /**
     * Sorts the given list by the distance metric.
     * @param nodeInfos The list of storage nodes to sort by the distance metric
     * @return The sorted list
     */
    public static List<NodeInfo> sortNodesByDistanceMetric(List<NodeInfo> nodeInfos) {
        List<NodeInfo> sortedNodes = new ArrayList<>();
        for(NodeInfo n : nodeInfos)
        {
            //Calculate distance metric for each node
//            float distanceMetric = getDistanceMetric(n);
        	
        	// Instead: Use actual distance instead of metric value to select spatially nearest node
        	float distanceMetric = getDistance(n);	
        	
            n.setDistanceMetric(distanceMetric);

            int pos = 0;
            
            //Add it sorted into the list 
            if (ContextManager.get().getCurrentContext().getLocationContext() == null) {
            	
            	// no LocationContext available, so we sort by distance metric - high to low
            	while(pos < sortedNodes.size())
                {
                    if(sortedNodes.get(pos).getDistanceMetric() < distanceMetric) break;
                    ++pos;
                }
                sortedNodes.add(pos, n);
            
            } else {
            	
            	// LocationContext is available, so we sort by actual distance - low to high
            	while(pos < sortedNodes.size())
                {
                    if(sortedNodes.get(pos).getDistanceMetric() > distanceMetric) break;
                    ++pos;
                }
                sortedNodes.add(pos, n);
            
            }
        }
        
        return sortedNodes;
    }

    /**
     * Returns the position of the best corenet or cloud in the given list
     * @param nodes The list of storage nodes
     * @param start The start index from where to search in the list
     * @param end The end index up to where to search in the list
     * @return The best cloud or corenet node corresponding to the bandwidth.
     */
    public static int getBestDlCloud(List<NodeInfo> nodes, int start, int end) {
        if(start > nodes.size() || end > nodes.size()) { return -1; }
        NodeInfo bestNode = null;
        int bestNodeIndex = -1;
        for(int i = start; i < end; ++i)
        {
            NodeInfo n = nodes.get(i);
            if(!(n.getNodeType().equals(NodeType.CLOUD) || n.getNodeType().equals(NodeType.CORENET)))
            {
                //Node is no cloud or corenet
                continue;
            }
            //No cloud/corenet node found yet. This is the first one.
            if(bestNode == null) { bestNode = n; bestNodeIndex = i; continue; }

            if(n.getBandwidthDown() > bestNode.getBandwidthDown()) {
                bestNode = n;
                bestNodeIndex = i;
            }
        }
        if(bestNode == null) { return -1; }
        return bestNodeIndex;
    }
}
