# Trade Client

## Introduction

The purpose of this example is to show the NewOrderSingle / Execution Report FIX message dialogue.

The Application presents a very basic page that allows the submission of Orders and shows the returned messages.  
Behind the scenes it connects to Nadex and sends messages using the FIX protocol.

The front end is implemented with WebSocket and STOMP.

## Run the example

### Specify custom Spring configuration file location

One can update the `application.yml` in the project and build the project or \
add `--spring.config.location=<path to Spring configuraion>` to the command, for example:

`java -jar target/simple-trade-client-spring-3.jar --spring.config.location=../../application.yml`

### Example Trade Client
`cd simple-trade-client-spring-3 && java -jar target/simple-trade-client-spring-3-spring-3.jar` or

`cd simple-trade-client-spring-3 && mvn spring-boot:run`

## Connect the example client to CDNA environments

Update the QuickFIX/J configuration options in the `application.yml` file using the details provided by CDNA and run the client as above.

## Submit an Order

Run the Simple Trade Client as above.\
Check the Trade Example Client logs to confirm that it has established a FIX connected to CDNA and successfully logged on.

Determine a `symbol` and `price` by running the __*PreTrade*__ example (simple-trade-client-spring-3) and reviewing the logs.

Open http://localhost:8080/ in your browser.

First use the "Connect" button to connect the WebSocket from the browser to the running Trade Example Client application.

Fill in all the fields in the form and use the "Send" button to send the Order from the browser to the application.

The application will create a FIX message and send it to the CDNA Trade FIX Gateway.
When a response is received from the CDNA Trade FIX Gateway, it should be presented on the page.\
Also check the Trade Example Client logs.
If messages are rejected at the session level (35=3) this will only appear in the logs.

## About the code

The code is for example purposes only. This is not intended to be production quality code.
It does not represent all the trading functionality available via FIX. 

### Conventions

The objects that are received and sent on the WebSocket are in the "domain" package. 
These correspond to object defined by the FIX Protocol. 
The datatypes of fields correspond to those defined by the FIX Protocol. 

The objects received on the WebSocket endpoints are translated to FIX Protocol Messages \
and relayed to the exchange FIX Gateways.

Relevant application level messages received over FIX Protocol are translated to \ 
Plain Old Java (Data Transfer) Objects and relayed via WebSocket.  

### The project structure

<pre>
├── src
│     └── main
│         ├── java
│         │     └── com
│         │         └── nadex
│         │             └── quickfixj
│         │                 └── spring
│         │                     └── boot
│         │                         └── starters
│         │                             └── examples
│         │                                 └── trade
│         │                                     └── client
│         │                                         ├──[WebSocket and FIX Protocol handling, Spring Config]
│         │                                         └── domain
│         │                                             ├── [Plain Old Java Data Transfer Objects]
│         │                                             └── from
│         │                                                 ├── fix
│         │                                                 │   └──[Translation from FIX to WebSocket]
│         │                                                 └── websocket
│         │                                                     └──[Translation from WebSocket to FIX ]
│         └── resources
│             ├── application.yml [the application configuration]
│             └── static 
|                 └──[Contains the static web resources]
</pre>
