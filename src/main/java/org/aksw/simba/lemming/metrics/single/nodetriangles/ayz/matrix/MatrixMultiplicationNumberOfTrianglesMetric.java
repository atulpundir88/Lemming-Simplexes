package org.aksw.simba.lemming.metrics.single.nodetriangles.ayz.matrix;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.metrics.AbstractMetric;
import org.aksw.simba.lemming.metrics.single.SingleValueMetric;

import toools.math.IntMatrix;


/**
 * This class models an algorithm for computing the amount of node triangles in a given graph. This
 * is done using a matrix multiplication approach. The idea is to compute the cubic adjacency matrix
 * (A^3) of the graph. Doing so, one can compute the amount of node triangles present in the graph
 * by summing over the diagonal of the matrix. Note that due to the way we compute the adjacency
 * matrix, this approach only works for graphs where no two pairwise distinct nodes are connected by
 * more than one edge.
 *
 * @see <a href=
 *      "https://www.geeksforgeeks.org/number-of-triangles-in-a-undirected-graph/">https://www.geeksforgeeks.org/number-of-triangles-in-a-undirected-graph/</a>.
 *
 * @author Alexander Hetzer
 * https://github.com/BlackHawkLex/Lemming/blob/master/src/main/java/org/aksw/simba/lemming/metrics/single/triangle/matrix/MatrixMultiplicationNumberOfTrianglesMetric.java
 *
 */
public class MatrixMultiplicationNumberOfTrianglesMetric extends AbstractMetric implements SingleValueMetric {

    public MatrixMultiplicationNumberOfTrianglesMetric() {
        super("#nodetriangles");
    }


    @Override
    public double apply(ColouredGraph graph) {
        if (graph.getVertices().size() < 3 || graph.getGraph().getEdges().size() < 3) {
            return 0;
        }
        // NOTE: This implementation only works under the assumption that no two nodes are connected
        // by more than one edge

        IntMatrix cubicAdjacencyMatrix = getCubicAdjacencyMatrix(graph);

        double diagonalSumOfCubicAdjacencyMatrix = getSumOfDiagonal(cubicAdjacencyMatrix);
        return diagonalSumOfCubicAdjacencyMatrix / 6;
    }


    public IntMatrix getCubicAdjacencyMatrix(ColouredGraph graph) {
        MultiEdgeIgnoringAdjacencyMatrix adjacencyMatrix = new MultiEdgeIgnoringAdjacencyMatrix(graph.getGraph());
        return MultiEdgeIgnoringAdjacencyMatrix.power(adjacencyMatrix, 3);
    }


    private double getSumOfDiagonal(IntMatrix matrix) {
        double sum = 0;
        for (int i = 0; i < matrix.width; i++) {
            sum += matrix.get(i, i);
        }
        return sum;
    }
}
