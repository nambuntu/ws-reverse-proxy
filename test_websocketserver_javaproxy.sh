#!/bin/bash

# install node modules
npm install

# build java proxy
mvn clean install

# start the node websocket server and java websocket proxy
node websocketserver.js &
java -jar target/nf-reverse-proxy-0.0.1-SNAPSHOT.jar &
sleep 4

# use wscat to issue data
(echo -n; sleep 1; echo hello world; sleep 1; echo how are you?; sleep 1; ) | ./node_modules/wscat/bin/wscat --connect ws://localhost:7777

# kill the servers
pkill -f websocketserver.js
pkill -f nf-reverse-proxy
