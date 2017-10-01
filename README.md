# Distributed Transaction Sandbox
Simulates a back end which transactionally modifies a database and then sends a JMS message to a queue. The back end uses nFlow to guarantee the eventual consistency of the data.

## Parameters to run the application
`-Ddb.url=jdbc:mysql://localhost:3306/sandbox -Ddb.username=root -Ddb.password=root -Djms.broker.url=tcp://127.0.0.1:61616 -Djms.queue.name=TEST.FOO`, where URL, username and password are ones of a running MySQL database while JMS broker URL and JMS queue name are ones of a running JMS instance.

## cURL command to create a notification
```bash
curl -v -H "Content-Type: application/json" --data '{"type":"Some Type", "name":"Some Name"}' http://localhost:8080/notifications
```
