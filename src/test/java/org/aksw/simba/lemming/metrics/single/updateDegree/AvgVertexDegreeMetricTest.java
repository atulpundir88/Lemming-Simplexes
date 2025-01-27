package org.aksw.simba.lemming.metrics.single.updateDegree;

import org.aksw.simba.lemming.AddEdgeDecorator;
import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.ColouredGraphDecorator;
import org.aksw.simba.lemming.RemoveEdgeDecorator;
import org.aksw.simba.lemming.metrics.single.AvgVertexDegreeMetric;
import org.aksw.simba.lemming.metrics.single.UpdatableMetricResult;
import org.aksw.simba.lemming.metrics.single.edgemanipulation.Operation;
import org.aksw.simba.lemming.mimicgraph.constraints.TripleBaseSingleID;
import org.junit.Test;

import junit.framework.Assert;

public class AvgVertexDegreeMetricTest extends UpdateMetricTest {

    @Test
    public void testcase1() {
        // Test case for Avg Vertex degree metric
        AvgVertexDegreeMetric metric = new AvgVertexDegreeMetric();
        ColouredGraph buildGraph1 = buildGraph1();

        double result = metric.apply(buildGraph1);
        Assert.assertEquals(4.0 / 3.0, result);

        ColouredGraphDecorator decorator = new ColouredGraphDecorator(buildGraph1);
        UpdatableMetricResult prevResult = metric.applyUpdatable(decorator);

        // delete an edge 0 = (0, 1)
        TripleBaseSingleID triple = new TripleBaseSingleID(0, null, 1, null, 0, null);
        ColouredGraphDecorator remDecorator = new RemoveEdgeDecorator(buildGraph1, false);
        remDecorator.setTriple(triple);
        buildGraph1 = removeEdge(buildGraph1, triple.edgeId);
        UpdatableMetricResult newResult = metric.update(remDecorator, triple, Operation.REMOVE, prevResult);
        Assert.assertEquals((4.0 - 1.0) / 3.0, newResult.getResult());

        // delete an edge 2 = (1, 2)
        triple = new TripleBaseSingleID(1, null, 2, null, 2, null);
        remDecorator.setGraph(buildGraph1);
        remDecorator.setTriple(triple);
        buildGraph1 = removeEdge(buildGraph1, triple.edgeId);
        newResult = metric.update(remDecorator, triple, Operation.REMOVE, newResult);
        Assert.assertEquals((4.0 - 1.0 - 1.0) / 3.0, newResult.getResult());

        // add an edge 4 = (1, 2);
        ColouredGraphDecorator addDecorator = new AddEdgeDecorator(buildGraph1, false);
        buildGraph1 = addEdge(buildGraph1, 1, 2);
        triple = new TripleBaseSingleID(1, null, 2, null, 4, null);
        addDecorator.setTriple(triple);
        newResult = metric.update(addDecorator, triple, Operation.ADD, newResult);
        Assert.assertEquals((4.0 - 1.0 - 1.0 + 1.0) / 3.0, newResult.getResult());

        // add an edge 4 = (2, 2);
        addDecorator.setGraph(buildGraph1);
        buildGraph1 = addEdge(buildGraph1, 2, 2);
        triple = new TripleBaseSingleID(2, null, 2, null, 4, null);
        addDecorator.setTriple(triple);
        newResult = metric.update(addDecorator, triple, Operation.ADD, newResult);
        Assert.assertEquals((4.0 - 1.0 - 1.0 + 1.0 + 1.0) / 3.0, newResult.getResult());

    }

    @Test
    public void testcase2() {
        // test case for in-degree with graph 2
        AvgVertexDegreeMetric metric = new AvgVertexDegreeMetric();
        ColouredGraph buildGraph2 = buildGraph2();

        double result = metric.apply(buildGraph2);
        Assert.assertEquals(3.0 / 2.0, result);

        ColouredGraphDecorator decorator = new ColouredGraphDecorator(buildGraph2);
        UpdatableMetricResult prevResult = metric.applyUpdatable(decorator);

        // delete an edge 0 = (0, 1)
        TripleBaseSingleID triple = new TripleBaseSingleID(0, null, 1, null, 1, null);
        ColouredGraphDecorator remDecorator = new RemoveEdgeDecorator(buildGraph2, false);
        remDecorator.setTriple(triple);
        buildGraph2 = removeEdge(buildGraph2, triple.edgeId);
        UpdatableMetricResult newResult = metric.update(remDecorator, triple, Operation.REMOVE, prevResult);
        Assert.assertEquals((3.0 - 1.0) / 2.0, newResult.getResult());

        // add an edge 0 = (0, 1);
        ColouredGraphDecorator addDecorator = new AddEdgeDecorator(buildGraph2, false);
        buildGraph2 = addEdge(buildGraph2, 0, 1);
        triple = new TripleBaseSingleID(0, null, 1, null, 1, null);
        addDecorator.setTriple(triple);
        newResult = metric.update(addDecorator, triple, Operation.ADD, newResult);
        Assert.assertEquals((3.0 - 1.0 + 1.0) / 2.0, newResult.getResult());

        // add an edge 4 = (1, 1);
        addDecorator.setGraph(buildGraph2);
        buildGraph2 = addEdge(buildGraph2, 1, 1);
        triple = new TripleBaseSingleID(1, null, 1, null, 4, null);
        addDecorator.setTriple(triple);
        newResult = metric.update(addDecorator, triple, Operation.ADD, newResult);
        Assert.assertEquals((3.0 - 1.0 + 1.0 + 1.0) / 2.0, newResult.getResult());

    }
}
