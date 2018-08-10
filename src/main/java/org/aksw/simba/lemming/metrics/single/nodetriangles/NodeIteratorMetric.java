package org.aksw.simba.lemming.metrics.single.nodetriangles;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.metrics.AbstractMetric;
import org.aksw.simba.lemming.metrics.single.SingleValueClusteringCoefficientMetric;
import org.aksw.simba.lemming.metrics.single.SingleValueMetric;

import com.carrotsearch.hppc.cursors.IntCursor;

import grph.Grph;
import grph.in_memory.InMemoryGrph;
import toools.set.IntSet;
import toools.set.IntSets;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models an algorithm for counting the amount of node triangles in a given graph. This
 * is done using the so called node-iterator algorithm explained by Schank and Wagner in their work
 * "Finding, Counting and Listing all Triangles in Large Graphs, An Experimental Study".
 *
 * @see <a href=
 *      "https://www.researchgate.net/publication/221131490_Finding_Counting_and_Listing_All_Triangles_in_Large_Graphs_an_Experimental_Study">https://www.researchgate.net/publication/221131490_Finding_Counting_and_Listing_All_Triangles_in_Large_Graphs_an_Experimental_Study</a>).
 *
 * @author Tanja Tornede
 * https://github.com/BlackHawkLex/Lemming/blob/master/src/main/java/org/aksw/simba/lemming/metrics/single/triangle/NodeIteratorNumberOfTrianglesMetric.java
 *
 */
public class NodeIteratorMetric extends AbstractMetric implements SingleValueMetric, SingleValueClusteringCoefficientMetric {

    private IntSet highDegreeVertices;
    private Boolean calculateClusteringCoefficient = false;
    private List<Double> clusteringCoefficient = new ArrayList<>();

    public NodeIteratorMetric() {
        super("node-iterator #node triangles");
        this.highDegreeVertices = IntSets.emptySet;
    }

    public NodeIteratorMetric(Boolean calculateClusteringCoefficient) {
        super("node-iterator #node triangles");
        this.highDegreeVertices = IntSets.emptySet;
        this.calculateClusteringCoefficient = calculateClusteringCoefficient;
    }

    public NodeIteratorMetric(IntSet highDegreeVertices) {
        this();
        this.highDegreeVertices = highDegreeVertices;
    }

    public List<Double> getClusteringCoefficient() {
        return clusteringCoefficient;
    }

    @Override
    public double apply(ColouredGraph graph) {
        IntSet visitedVertices = IntSets.from(new int[] {});
        Grph grph = getUndirectedGraph(graph.getGraph());

        int numberOfTriangles = 0;
        for (IntCursor vertex : graph.getVertices()) {
            int triangleCount = 0;
            IntSet neighbors = IntSets.difference(IntSets.union(grph.getInNeighbors(vertex.value), grph.getOutNeighbors(vertex.value)),
                    visitedVertices);
            for (IntCursor neighbor1 : neighbors) {
                IntSet neighbors1 = IntSets
                        .difference(IntSets.union(grph.getInNeighbors(neighbor1.value), grph.getOutNeighbors(neighbor1.value)), visitedVertices);
                for (IntCursor neighbor2 : neighbors) {
                    if (vertex.value != neighbor1.value && vertex.value != neighbor2.value && neighbor1.value < neighbor2.value
                            && neighbors1.contains(neighbor2.value)) {
                        if (!highDegreeVertices.contains(vertex.value) || !highDegreeVertices.contains(neighbor1.value)
                                || !highDegreeVertices.contains(neighbor2.value)) {
                            numberOfTriangles++;
                            triangleCount++;
                        }
                    }
                }
            }

            if (!this.calculateClusteringCoefficient)
                visitedVertices.add(vertex.value);

            if (triangleCount > 0 && this.calculateClusteringCoefficient) {
                IntSet vertexNeighbors = grph.getInNeighbors(vertex.value);
                vertexNeighbors.addAll(grph.getOutNeighbors(vertex.value));
                double degree = vertexNeighbors.size();
                clusteringCoefficient.add((2 * triangleCount) / (degree * (degree - 1)));
            }
        }

        return numberOfTriangles;
    }


    private Grph getUndirectedGraph(Grph graph) {
        Grph undirectedGraph = new InMemoryGrph();
        for (IntCursor e : graph.getEdges()) {
            int sourceNode = graph.getOneVertex(e.value);
            int targetNode = graph.getTheOtherVertex(e.value, sourceNode);
            undirectedGraph.addUndirectedSimpleEdge(sourceNode, targetNode);
        }
        return undirectedGraph;
    }

}

