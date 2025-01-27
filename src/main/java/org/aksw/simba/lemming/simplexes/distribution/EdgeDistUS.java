package org.aksw.simba.lemming.simplexes.distribution;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;
import com.carrotsearch.hppc.ObjectObjectOpenHashMap;

public class EdgeDistUS {

	/**
	 * Map for storing count of head and tail colours for 1-simplexes.
	 */
	private ObjectObjectOpenHashMap<BitSet, ObjectDoubleOpenHashMap<BitSet>> mHeadColoTailColoCount;
	
	/**
	 * Number of input graphs.
	 */
	//private int iNoOfVersions;
	
	/**
	 * Object of Random class
	 */
	//private Random mRandom;
	
	/**
	 * Head Color proposer for 1-simplex.
	 */
	private Set<BitSet> potentialVertColoProposer;
	
	/**
	 * Map for storing probability distributions for different head colors
	 */
	private ObjectObjectOpenHashMap<BitSet, Set<BitSet>> mVertColoProbDist;

	public EdgeDistUS(ObjectDoubleOpenHashMap<BitSet> mVertColoCount1Simplex, ObjectObjectOpenHashMap<BitSet, ObjectDoubleOpenHashMap<BitSet>> mHeadColoTailColoCount, int iNoOfVersions, Random mRandom) {
		// initialization
		this.mHeadColoTailColoCount = mHeadColoTailColoCount;
		//this.iNoOfVersions = iNoOfVersions;
		//this.mRandom = mRandom;
		mVertColoProbDist = new ObjectObjectOpenHashMap<BitSet, Set<BitSet>>();
		
		// create head color proposer
		createVertColoProposer(mVertColoCount1Simplex);
	}
	
	/**
	 * The method initializes the head color proposer for 1-simplexes. It utilizes map storing count of head colors for input graphs. 
	 * @param mVertColoCount1Simplex - Map storing Head Color has keys and their count in input graphs as values.
	 * @param iNoOfVersions - Number of input graphs.
	 * @param mRandom - Random generator object.
	 */
	public void createVertColoProposer(ObjectDoubleOpenHashMap<BitSet> mVertColoCount1Simplex) {
		
		potentialVertColoProposer = new HashSet<BitSet>();
		
		//iterate list of head colors and add values to sample space and values
		Object[] keysVertColo = mVertColoCount1Simplex.keys;
		for(int i=0; i < keysVertColo.length; i++) {
			if (mVertColoCount1Simplex.allocated[i]) {
				BitSet coloVert = (BitSet) keysVertColo[i];
				potentialVertColoProposer.add(coloVert);
					
			}
		}
		
		
	}
	
	public Set<BitSet> proposeVertColo(BitSet headColo) {
		
		Set<BitSet> potentialTailColoProposer = mVertColoProbDist.get(headColo);
		
		if(potentialTailColoProposer == null) {
			//create a new distribution if distribution does not already exist for head color
			
			potentialTailColoProposer = new HashSet<BitSet>();
		
			// get map of tail color and count for the input head color
			ObjectDoubleOpenHashMap<BitSet> mtailColoCount = mHeadColoTailColoCount.get(headColo);
			
			
			//iterate list of head colors and add values to sample space and values
			Object[] keysVertColo = mtailColoCount.keys;
			for(int i = 0; i < keysVertColo.length; i++) {
				if (mtailColoCount.allocated[i]) {
					BitSet tailColo = (BitSet) keysVertColo[i];
					potentialTailColoProposer.add(tailColo);
				}
			}
			
			
			
		} 
		
		//return potentialTailColoProposer.getPotentialItem(); //changing return type so that the proposer could be filtered
		return potentialTailColoProposer;
	}

	public Set<BitSet> getPotentialHeadColoProposer() {
		return potentialVertColoProposer;
	}
}
