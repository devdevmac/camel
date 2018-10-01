/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.main;

import org.apache.camel.CamelContext;
import org.apache.camel.VetoCamelContextStartException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.LifecycleStrategySupport;
import org.junit.Assert;
import org.junit.Test;


/**
 */
public class MainVetoTest extends Assert {

    @Test
    public void testMain() throws Exception {
        // lets make a simple route
        Main main = new Main();
        main.setDuration(30);
        main.setDurationHitExitCode(99);
        main.addRouteBuilder(new MyRoute());
        main.addMainListener(new MainListenerSupport() {
            @Override
            public void configure(CamelContext context) {
                context.addLifecycleStrategy(new MyVetoLifecycle());
            }
        });

        // should not hang as we veto fail
        main.run();

        // should complete normally due veto
        assertEquals(0, main.getExitCode());
    }

    private class MyRoute extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("timer:foo").to("mock:foo");
        }
    }

    private class MyVetoLifecycle extends LifecycleStrategySupport {
        @Override
        public void onContextStart(CamelContext context) throws VetoCamelContextStartException {
            throw new VetoCamelContextStartException("We do not like this route", context, false);
        }
    }

}
