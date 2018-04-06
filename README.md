
# OVSDB Client Library

## Overview
This is a schema-independent OVSDB client implementation of OVSDB Management Protocol 
([RFC 7047](https://tools.ietf.org/html/rfc7047)). All RPC methods defined in the protocol are 
implemented.

## Getting Started

### Dependency
Currently, only 1.0-SNAPSHOT version is available and we temporarily use github as a maven 
repository. In order to use this library you have to add github repository and dependency to 
the pom file:

```xml
<repositories>
    <repository>
        <id>ovsdb-client-repo</id>
        <url>https://raw.github.com/vmware/ovsdb-client-library/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.vmware.ovsdb</groupId>
        <artifactId>ovsdb-client</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Passive Connection
When the OVSDB client uses passive connection mode, it implies that the OVSDB server is running on 
active connection mode. In other words, the client listens on certain port (6640 by default) and 
waits for the server to connects. For more information, see [ovsdb-server(1)](http://www.openvswitch.org/support/dist-docs/ovsdb-server.1.html)

Note: Currently this library only supports this mode. The active connection will be supported in near future.

The following example uses hardware_vtep database on an OVSDB server. The server is started by 
command:

```bash
$ ovsdb-server --pidfile --log-file --remote punix:/var/run/openvswitch/db.sock --remote=db:hardware_vtep,Global,managers /etc/openvswitch/vtep.db
``` 

For the meaning of the params, see [ovsdb-server(1)](http://www.openvswitch.org/support/dist-docs/ovsdb-server.1.html).

```java
ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);    
OvsdbPassiveConnectionListener listener = new OvsdbPassiveConnectionListenerImpl(executorService);  // (1)

CompletableFuture<OvsdbClient> ovsdbClientFuture = new CompletableFuture<>();
ConnectionCallback connectionCallback = new ConnectionCallback() {      // (2)
    public void connected(OvsdbClient ovsdbClient) {
        System.out.println(ovsdbClient + " connected");
        ovsdbClientFuture.complete(ovsdbClient);
    }
    public void disconnected(OvsdbClient ovsdbClient) {
        System.out.println(ovsdbClient + " disconnected");
    }
};
listener.startListening(6640, connectionCallback);       // (3)

OvsdbClient ovsdbClient = ovsdbClientFuture.get(3, TimeUnit.SECONDS);   // (4)
CompletableFuture<String[]> f = ovsdbClient.listDatabases();
String[] dbs = f.get(3, TimeUnit.SECONDS);
System.out.println(Arrays.toString(dbs));

```

From above example we can see the steps of getting an `OvsdbClient` object.

(1) Construct a `OvsdbPassiveConnectionListener`. The `OvsdbPassiveConnectionListenerImpl`
takes a `ScheduledExecutorService` for asynchronous operations. The thread pool has to contain at 
least 2 idle threads because 1 thread will be used to listen on the port.  
(2) Implement the `ConnectionCallback` interface and construct a callback object.  
(3) Start listening on the port.  
(4) Get the `OvsdbClient` object from the callback and use it for operations on the OVSDB server.

Note: 
* All the interfaces provided by `OvsdbClient` are asynchronous and return a `CompletableFuture`.
See [OvsdbClient.java](ovsdb-client/src/main/java/com/vmware/ovsdb/service/OvsdbClient.java).
* Exception handling is omitted in this example.

### Active Connection (TBD)

## TODOs
1. **Active connection needs to be implemented**. The [ovsdb-server](http://www.openvswitch.org/support/dist-docs/ovsdb-server.1.html) 
implementation supports connections over both active and passive TCP/IP sockets. Currently this 
library only supports passive connections, which means the user needs to listen on certain port and 
wait for the server to connect.
2. **ORM layer**. Currently, to perform a transaction, the user has to construct the `Row` object. 
Ideally, there should be an ORM layer through which the user only needs to define the entity object 
annotated with certain annotations. And one entity object can be used to represent one row.
This is similar to JPA.
3. **Integration tests with containers and CI/CD pipeline**. 
4. **Use central maven repo**. After the first release, upload the jar to central maven repo. 
See [Guide to uploading artifacts to the Central Repository](https://maven.apache.org/guides/mini/guide-central-repository-upload.html)
5. **Use Wiki for documentation.**

## Contributing

The ovsdb-client-library project team welcomes contributions from the community. Before you start working with ovsdb-client-library, please read our [Developer Certificate of Origin](https://cla.vmware.com/dco). All contributions to this repository must be signed as described on that page. Your signature certifies that you wrote the patch or have the right to pass it on as an open-source patch. For more detailed information, refer to [CONTRIBUTING.md](CONTRIBUTING.md).

## License
* [BSD-2](https://opensource.org/licenses/BSD-2-Clause)
