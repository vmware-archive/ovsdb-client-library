

# OVSDB Client Library

## Overview
This is a schema-independent OVSDB client implementation of OVSDB Management Protocol 
([RFC 7047](https://tools.ietf.org/html/rfc7047)). All RPC methods defined in the protocol are 
implemented.

## Try it out

### Prerequisites

* JDK 1.8+
* Maven 3+

### Build & Use

1. Build the jar.
```bash
mvn clean package
```
After this step, you will find a jar called `ovsdb-client-{version}.jar` under ovsdb-client/target/

2. Use the jar.
Currently, due to the lack of a maven repository, you will have to first build the jar and then copy 
it to your project. Besides, you have to add other 3rd party dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>com.vmeare.ovsdb</groupId>
        <artifactId>ovsdb-client</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/ovsdb-client-1.0-SNAPSHOT.jar</systemPath>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-core</artifactId>
        <version>${jackson.version}</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>${jackson.version}</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-annotations</artifactId>
        <version>${jackson.version}</version>
    </dependency>
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-all</artifactId>
        <version>${netty.version}</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>${sl4j.version}</version>
    </dependency>
</dependencies>
```

## Getting Started

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
4. **Have a maven repository on Github**. See [Hosting a Maven repository on github
](https://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github)
The final goal should be uploading it to the central maven repository. See 
[Guide to uploading artifacts to the Central Repository](https://maven.apache.org/guides/mini/guide-central-repository-upload.html)
5. **Use Wiki for documentation.**
6. **Add checkstyle plugin**

## Contributing

The ovsdb-client-library project team welcomes contributions from the community. If you wish to contribute code and you have not
signed our contributor license agreement (CLA), our bot will update the issue when you open a Pull Request. For any
questions about the CLA process, please refer to our [FAQ](https://cla.vmware.com/faq). For more detailed information,
refer to [CONTRIBUTING.md](CONTRIBUTING.md).

## License
* [BSD-2](https://opensource.org/licenses/BSD-2-Clause)
