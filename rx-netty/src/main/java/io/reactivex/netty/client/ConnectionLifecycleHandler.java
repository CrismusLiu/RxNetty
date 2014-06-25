/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.reactivex.netty.channel.ObservableConnection;

public class ConnectionLifecycleHandler<I, O> extends ChannelInboundHandlerAdapter {

    private ObservableConnection<I, O> connection;

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (null != connection) {
            connection.close();
        }
        super.channelUnregistered(ctx);
    }

    /*package private to set the connection*/ void setConnection(ObservableConnection<I, O> newConnection) {
        if (!newConnection.getChannelHandlerContext().channel().isRegistered()) {
            connection.close();
        } else {
            connection = newConnection;
        }
    }
}
