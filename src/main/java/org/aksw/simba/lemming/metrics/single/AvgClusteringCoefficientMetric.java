package org.aksw.simba.lemming.metrics.single;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.metrics.AbstractMetric;

/**
 * This metric is the average clustering coefficient of the graph.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class AvgClusteringCoefficientMetric extends AbstractMetric implements SingleValueMetric {

    public AvgClusteringCoefficientMetric() {
        super("avgClusterCoefficient");
    }

    @Override
    public double apply(ColouredGraph graph) {
        return graph.getGraph().getAverageClusteringCoefficient();
    }

}
