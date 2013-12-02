/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package sample;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import static org.jboss.netty.channel.Channels.*;

public class HttpHelloWorldServerPipelineFactory implements ChannelPipelineFactory {
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("cors", new CorsFilter(createPolicy()));
        pipeline.addLast("handler", new HttpHelloWorldServerHandler());
        return pipeline;
    }

     private Map<String, CorsPolicy> createPolicy(){
         CorsPolicy corsPolicy;
         TreeSet<String> origin = new TreeSet<String>();
         origin.add("*");
         TreeSet<String> header = new TreeSet<String>();
         header.add("x-HeaderA");
         header.add("x-HeaderB");
         TreeSet<String> method = new TreeSet<String>();
         method.add("GET");
         method.add("POST");
         corsPolicy = new DefaultCorsPolicy(origin,
                 true,header,
                 100,
                 method,
                 header
         );
         CorsPolicy corsPolicy2;
         TreeSet<String> origin2 = new TreeSet<String>();
         origin2.add("*");
         TreeSet<String> header2 = new TreeSet<String>();
         header2.add("x-HeaderA");
         header2.add("x-HeaderB");
         TreeSet<String> method2 = new TreeSet<String>();
         method2.add("GET");
         method2.add("PUT");
         corsPolicy2 = new DefaultCorsPolicy(origin2,
                 true,header2,
                 100,
                 method2,
                 header2
         );
         Map<String, CorsPolicy> m = new HashMap<String, CorsPolicy>();
         m.put("/", corsPolicy);
         m.put("/[1-9]/", corsPolicy2);
         return m;
     }
}
