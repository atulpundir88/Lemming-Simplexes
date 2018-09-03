package org.aksw.simba.lemming.metrics.single.edgemanipulation;

import java.util.ArrayList;
import java.util.List;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.metrics.single.SingleValueMetric;
import org.aksw.simba.lemming.mimicgraph.constraints.TripleBaseSingleID;

import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;

public class EdgeModifier {

	private EdgeModification mEdgeModification;

	private List<SingleValueMetric> mLstMetrics;
	private ObjectDoubleOpenHashMap<String> mMapMetricValues;
	private ObjectDoubleOpenHashMap<String> mMapOrignalMetricValues;
	private List<TripleBaseSingleID> mLstRemovedEdges;
	private List<TripleBaseSingleID> mLstAddedEdges;
	private boolean isCoutingEdgeTriangles = false;
	private boolean isCountingNodeTriangles = false;
	
	public EdgeModifier(ColouredGraph clonedGraph, List<SingleValueMetric> lstMetrics){
		//list of metric
		mLstMetrics = lstMetrics;
		//initialize two list removed edges and added edges
		mLstRemovedEdges = new ArrayList<TripleBaseSingleID>();
		mLstAddedEdges = new ArrayList<TripleBaseSingleID>();
		//compute metric values
		computeMetricValues(clonedGraph, lstMetrics);
		//initialize EdgeModification
		mEdgeModification= new EdgeModification(clonedGraph,(int) mMapMetricValues.get("#nodetriangles"),(int) mMapMetricValues.get("#edgetriangles"));
	}
	
	private void computeMetricValues(ColouredGraph clonedGraph, List<SingleValueMetric> lstMetrics){
		
		mMapMetricValues  = new ObjectDoubleOpenHashMap<String>();
		if(lstMetrics != null && lstMetrics.size()> 0 ){
			
			for(SingleValueMetric metric : lstMetrics){
				if(metric.getName().equalsIgnoreCase("#edgetriangles")){
					isCoutingEdgeTriangles = true;
				}else if(metric.getName().equalsIgnoreCase("#nodetriangles")){
					isCountingNodeTriangles = true;
				}
				
				//compute value for each of metrics
				mMapMetricValues.put(metric.getName(), metric.apply(clonedGraph));
			}
		}
		if(!isCountingNodeTriangles){
			mMapMetricValues.put("#nodetriangles", 0);
		}
		if(!isCoutingEdgeTriangles){
			mMapMetricValues.put("#edgetriangles", 0);
		}

		//create a backup map metric values
		mMapOrignalMetricValues = mMapMetricValues.clone();
	}
	
	public ColouredGraph getGraph(){
		return mEdgeModification.getGraph();
	}
	
	public ObjectDoubleOpenHashMap<String> tryToRemoveAnEdge(TripleBaseSingleID triple){
		
		if(triple != null && triple.edgeId != -1){
			
			//add to list of removed edges
			mLstRemovedEdges.add(triple);
			ObjectDoubleOpenHashMap<String> mapMetricValues = new ObjectDoubleOpenHashMap<String>();
			mEdgeModification.removeEdgeFromGraph(triple.edgeId);
			if(isCountingNodeTriangles){
				int newNodeTri = mEdgeModification.getNewNodeTriangles();
				mapMetricValues.put("#nodetriangles", newNodeTri);	
			}
			
			if(isCoutingEdgeTriangles){
		        int newEdgeTri = mEdgeModification.getNewEdgeTriangles();
		        mapMetricValues.put("#edgetriangles", newEdgeTri);				
			}

	        ColouredGraph graph = mEdgeModification.getGraph();
	        
	        for(SingleValueMetric metric: mLstMetrics){
	        	if(!metric.getName().equalsIgnoreCase("#edgetriangles") &&
	        			!metric.getName().equalsIgnoreCase("#nodetriangles")){
	        		double metVal = metric.apply(graph);
	        		mapMetricValues.put(metric.getName(), metVal);        		
	        	}
	        }
	        
	        //reverse the graph
	        mEdgeModification.addEdgeToGraph(triple.tailId, triple.headId, triple.edgeColour);
	        
	        return mapMetricValues;
		}else{
			return mMapMetricValues;
		}
	}
	
	public ObjectDoubleOpenHashMap<String> tryToAddAnEdge(TripleBaseSingleID triple){
		if(triple!= null && triple.edgeColour != null && triple.headId!= -1 && triple.tailId != -1){
			//add to list of added edges
			mLstAddedEdges.add(triple);
			
			ObjectDoubleOpenHashMap<String> mapMetricValues = new ObjectDoubleOpenHashMap<String>();
			triple.edgeId = mEdgeModification.addEdgeToGraph(triple.tailId,triple.headId, triple.edgeColour);
			
			if(isCountingNodeTriangles){
				int newNodeTri = mEdgeModification.getNewNodeTriangles();
				mapMetricValues.put("#nodetriangles", newNodeTri);
			}
			
			if(isCoutingEdgeTriangles){
				int newEdgeTri = mEdgeModification.getNewEdgeTriangles();
		        mapMetricValues.put("#edgetriangles", newEdgeTri);
			}
		    
		    ColouredGraph graph = mEdgeModification.getGraph();
		    for(SingleValueMetric metric: mLstMetrics){
	        	if(!metric.getName().equalsIgnoreCase("#edgetriangles") &&
	        			!metric.getName().equalsIgnoreCase("#nodetriangles")){
	        		double metVal = metric.apply(graph);
	        		mapMetricValues.put(metric.getName(), metVal);        		
	        	}
	        }
		    
		    mEdgeModification.removeEdgeFromGraph(triple.edgeId);
			return mapMetricValues;
		}else{
			return mMapMetricValues;
		}
	}
	
	public void executeRemovingAnEdge(){
		if(mLstRemovedEdges.size() > 0 ){
			TripleBaseSingleID lastTriple = mLstRemovedEdges.get(mLstRemovedEdges.size() -1);
			mEdgeModification.removeEdgeFromGraph(lastTriple.edgeId);
		}
	}
	
	public void executeAddingAnEdge(){
		if(mLstAddedEdges.size() > 0 ){
			TripleBaseSingleID lastTriple = mLstAddedEdges.get(mLstAddedEdges.size() -1);
			mEdgeModification.addEdgeToGraph(lastTriple.tailId, lastTriple.headId, lastTriple.edgeColour);
		}
	}
	
	public void updateMapMetricValues(ObjectDoubleOpenHashMap<String> newMetricValues){
		mMapMetricValues = newMetricValues;
	}
	
	public ObjectDoubleOpenHashMap<String> getOriginalMetricValues(){
		return mMapOrignalMetricValues;
	}
	
	public ObjectDoubleOpenHashMap<String> getOptimizedMetricValues(){
		return mMapMetricValues;
	}
}
