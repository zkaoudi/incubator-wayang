/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.wayang.spark.operators;

import org.apache.wayang.core.platform.ChannelInstance;
import org.apache.wayang.core.types.DataSetType;
import org.apache.wayang.core.util.WayangCollections;
import org.apache.wayang.java.channels.CollectionChannel;
import org.apache.wayang.spark.channels.RddChannel;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for {@link SparkShufflePartitionSampleOperator}.
 */
class SparkShufflePartitionSampleOperatorTest extends SparkOperatorTestBase {

    @Test
    void testExecution() {
        // Prepare test data.
        RddChannel.Instance input = this.createRddChannelInstance(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        CollectionChannel.Instance output = this.createCollectionChannelInstance();
        int sampleSize = 3;

        // Build the sample operator.
        SparkShufflePartitionSampleOperator<Integer> sampleOperator =
                new SparkShufflePartitionSampleOperator<>(
                        iterationNumber -> sampleSize,
                        DataSetType.createDefaultUnchecked(Integer.class),
                        iterationNumber -> 42L
                );
        sampleOperator.setDatasetSize(10);

        // Set up the ChannelInstances.
        final ChannelInstance[] inputs = new ChannelInstance[]{input};
        final ChannelInstance[] outputs = new ChannelInstance[]{output};

        // Execute.
        this.evaluate(sampleOperator, inputs, outputs);

        // Verify the outcome.
        final List<Integer> result = WayangCollections.asList(output.provideCollection());
        System.out.println(result);
        assertEquals(sampleSize, result.size());

    }

    @Test
    void testExecutionWithUnknownDatasetSize() {
        // Prepare test data.
        RddChannel.Instance input = this.createRddChannelInstance(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        CollectionChannel.Instance output = this.createCollectionChannelInstance();
        int sampleSize = 3;

        // Build the operator.
        SparkShufflePartitionSampleOperator<Integer> sampleOperator =
                new SparkShufflePartitionSampleOperator<>(
                        iterationNumber -> sampleSize,
                        DataSetType.createDefaultUnchecked(Integer.class),
                        iterationNumber -> 42L
                );
        // Set up the ChannelInstances.
        final ChannelInstance[] inputs = new ChannelInstance[]{input};
        final ChannelInstance[] outputs = new ChannelInstance[]{output};

        // Execute.
        this.evaluate(sampleOperator, inputs, outputs);

        // Verify the outcome.
        final List<Integer> result = WayangCollections.asList(output.provideCollection());
        System.out.println(result);
        assertEquals(sampleSize, result.size());

    }

}
