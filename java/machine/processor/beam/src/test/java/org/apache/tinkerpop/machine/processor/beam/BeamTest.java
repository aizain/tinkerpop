/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.machine.processor.beam;

import org.apache.tinkerpop.language.gremlin.Gremlin;
import org.apache.tinkerpop.language.gremlin.P;
import org.apache.tinkerpop.language.gremlin.Traversal;
import org.apache.tinkerpop.language.gremlin.TraversalSource;
import org.apache.tinkerpop.language.gremlin.TraversalUtil;
import org.apache.tinkerpop.language.gremlin.common.__;
import org.apache.tinkerpop.machine.coefficient.LongCoefficient;
import org.apache.tinkerpop.machine.strategy.optimization.IdentityStrategy;
import org.apache.tinkerpop.machine.structure.blueprints.BlueprintsStructure;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.apache.tinkerpop.language.gremlin.core.__.constant;
import static org.apache.tinkerpop.language.gremlin.core.__.incr;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class BeamTest {

    @Test
    public void doStuff() {
        final TraversalSource<Long> g = Gremlin.<Long>traversal()
                .withCoefficient(LongCoefficient.class)
                .withProcessor(BeamProcessor.class)
                .withStructure(BlueprintsStructure.class)
                .withStrategy(IdentityStrategy.class);

        Traversal<Long, ?, ?> traversal = g.V();
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal.toList());
        System.out.println("\n----------\n");
        traversal = g.V().identity().union(__.count(), __.count()).map(__.<Long, Long>count().identity()).explain();
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal.next());
        System.out.println("\n----------\n");
    }

    @Test
    public void shouldWork() {
        final TraversalSource<Long> g = Gremlin.<Long>traversal()
                //.withCoefficient(LongCoefficient.class)
                .withProcessor(BeamProcessor.class)
                .withStrategy(IdentityStrategy.class);

        Traversal<Long, ?, ?> traversal = g.inject(Arrays.asList(1L, 1L)).<Long>unfold().map(incr()).c(4L).repeat(incr()).until(__.is(__.constant(8L).incr().incr())).sum();
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(traversal.toList());
        System.out.println("\n----------\n");
        traversal = g.inject(Arrays.asList(1L, 2L)).<Long>unfold().map(incr()).is(P.lt(constant(3L)));
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(traversal.toList());
        System.out.println("\n----------\n");
        traversal = g.inject(1L).times(10).repeat(__.incr()).emit();
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(traversal.toList());
        System.out.println("\n----------\n");
        traversal = g.inject(1L).repeat(incr()).emit(__.constant(true)).until(__.is(5L));
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(traversal.toList());
        System.out.println("\n----------\n");
        traversal = g.inject(1L).emit(__.constant(true)).until(__.is(5L)).repeat(incr());
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(traversal.toList());
        System.out.println("\n----------\n");
        traversal = g.inject(1L).until(__.is(11L)).repeat(__.<Long>incr().incr()).emit(__.constant(true));
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(traversal.toList());
        System.out.println("\n----------\n");
        traversal = g.inject(7L).choose(__.is(7L), __.incr(), __.<Long>incr().incr());
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(traversal.toList());
        System.out.println("\n----------\n");
        traversal = g.inject(10L).choose(__.is(7L), __.incr(), __.<Long>incr().incr());
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(traversal.toList());
        System.out.println("\n----------\n");
        traversal = g.inject(7L).union(__.incr(), __.<Long>incr().incr().union(__.incr(), __.incr()));
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(traversal.toList());

        traversal = g.inject(8L).choose(__.is(7L), __.incr());
        System.out.println(TraversalUtil.getBytecode(traversal));
        System.out.println(traversal);
        System.out.println(traversal.toList());
    }
}
