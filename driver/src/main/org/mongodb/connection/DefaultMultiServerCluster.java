/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.connection;

import org.mongodb.MongoClientOptions;
import org.mongodb.MongoCredential;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.mongodb.assertions.Assertions.isTrue;
import static org.mongodb.assertions.Assertions.notNull;
import static org.mongodb.connection.MonitorDefaults.SLAVE_ACCEPTABLE_LATENCY_MS;

abstract class DefaultMultiServerCluster extends DefaultCluster {
    private final Map<ServerAddress, ServerDescription> serverAddressToDescriptionMap = new HashMap<ServerAddress, ServerDescription>();
    private final ConcurrentMap<ServerAddress, Server> addressToServerMap = new ConcurrentHashMap<ServerAddress, Server>();

    protected DefaultMultiServerCluster(final List<ServerAddress> seedList, final List<MongoCredential> credentialList,
                                        final MongoClientOptions options, final BufferPool<ByteBuffer> bufferPool,
                                        final ServerFactory serverFactory) {
        super(bufferPool, credentialList, options, serverFactory);

        notNull("seedList", seedList);
        for (ServerAddress serverAddress : seedList) {
            addServer(serverAddress);
        }
    }

    @Override
    public Server getServer(final ServerAddress serverAddress) {
        isTrue("open", !isClosed());

        Server connection = addressToServerMap.get(serverAddress);
        if (connection == null) {
            throw new MongoServerNotFoundException("The requested server is not available: " + serverAddress);
        }
        return connection;
    }

    @Override
    public void close() {
        if (!isClosed()) {
            for (Server server : addressToServerMap.values()) {
                server.close();
            }
            super.close();
        }
    }

    protected abstract ServerStateListener createServerStateListener(final ServerAddress serverAddress);

    protected Map<ServerAddress, ServerDescription> getServerAddressToDescriptionMap() {
        return serverAddressToDescriptionMap;
    }

    protected synchronized void addServer(final ServerAddress serverAddress) {
        if (!addressToServerMap.containsKey(serverAddress)) {
            Server mongoServer = createServer(serverAddress, createServerStateListener(serverAddress));
            addressToServerMap.put(serverAddress, mongoServer);
        }
    }

    protected synchronized void removeServer(final ServerAddress serverAddress) {
        isTrue("open", !isClosed());

        Server server = addressToServerMap.remove(serverAddress);
        server.close();
        unmapDescriptionFromServerAddress(serverAddress);
    }


    protected void invalidateAll() {
        isTrue("open", !isClosed());

        for (Server server : addressToServerMap.values()) {
            server.invalidate();
        }
    }

    protected void updateDescription() {
        updateDescription(new ClusterDescription(new ArrayList<ServerDescription>(getServerAddressToDescriptionMap().values()),
                SLAVE_ACCEPTABLE_LATENCY_MS));
    }

    protected void mapDescriptionToServerAddress(final ServerDescription serverDescription) {
        getServerAddressToDescriptionMap().put(serverDescription.getAddress(), serverDescription);
    }

    protected ServerDescription unmapDescriptionFromServerAddress(final ServerAddress key) {
        return getServerAddressToDescriptionMap().remove(key);
    }
}