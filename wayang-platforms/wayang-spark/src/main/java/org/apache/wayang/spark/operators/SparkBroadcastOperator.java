package org.apache.wayang.spark.operators;

import org.apache.spark.broadcast.Broadcast;
import org.apache.wayang.core.optimizer.OptimizationContext;
import org.apache.wayang.core.plan.wayangplan.ExecutionOperator;
import org.apache.wayang.core.plan.wayangplan.UnaryToUnaryOperator;
import org.apache.wayang.core.platform.ChannelDescriptor;
import org.apache.wayang.core.platform.ChannelInstance;
import org.apache.wayang.core.platform.lineage.ExecutionLineageNode;
import org.apache.wayang.core.types.DataSetType;
import org.apache.wayang.core.util.Tuple;
import org.apache.wayang.java.channels.CollectionChannel;
import org.apache.wayang.spark.channels.BroadcastChannel;
import org.apache.wayang.spark.execution.SparkExecutor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Takes care of creating a {@link Broadcast} that can be used later on.
 */
public class SparkBroadcastOperator<Type> extends UnaryToUnaryOperator<Type, Type> implements SparkExecutionOperator {

    public SparkBroadcastOperator(DataSetType<Type> type) {
        super(type, type, false);
    }

    public SparkBroadcastOperator(SparkBroadcastOperator<Type> that) {
        super(that);
    }

    @Override
    public Tuple<Collection<ExecutionLineageNode>, Collection<ChannelInstance>> evaluate(
            ChannelInstance[] inputs,
            ChannelInstance[] outputs,
            SparkExecutor sparkExecutor,
            OptimizationContext.OperatorContext operatorContext) {
        assert inputs.length == this.getNumInputs();
        assert outputs.length == this.getNumOutputs();

        final CollectionChannel.Instance input = (CollectionChannel.Instance) inputs[0];
        final BroadcastChannel.Instance output = (BroadcastChannel.Instance) outputs[0];

        final Collection<?> collection = input.provideCollection();
        final Broadcast<?> broadcast = sparkExecutor.sc.broadcast(collection);
        output.accept(broadcast);

        return ExecutionOperator.modelEagerExecution(inputs, outputs, operatorContext);
    }

    @Override
    public boolean containsAction() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public DataSetType<Type> getType() {
        return (DataSetType<Type>) this.getInput(0).getType();
    }

    @Override
    protected ExecutionOperator createCopy() {
        return new SparkBroadcastOperator<>(this);
    }


    @Override
    public String getLoadProfileEstimatorConfigurationKey() {
        return "wayang.spark.broadcast.load";
    }

    @Override
    public List<ChannelDescriptor> getSupportedInputChannels(int index) {
        return Collections.singletonList(CollectionChannel.DESCRIPTOR);
    }

    @Override
    public List<ChannelDescriptor> getSupportedOutputChannels(int index) {
        assert index == 0;
        return Collections.singletonList(BroadcastChannel.DESCRIPTOR);
    }

}