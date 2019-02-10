#!/usr/bin/env bash

myInterruptHandler()
{
    echo "Cleanup..."
    sudo kill -9 $(ps -aux | grep producer-1 | grep -v grep | awk '{print $2}')
    sudo kill -9 $(ps -aux | grep consumer-1 | grep -v grep | awk '{print $2}')
    exit 1;
}

runRabbitMqDockerContainer()
{
    result=$( sudo docker images -q some-rabbit )

    if [[ -n "$result" ]]; then
      echo "Container exists"
    else
      sudo docker run -d --hostname my-rabbit --name some-rabbit -p 8080:15672 -p 5672:5672 rabbitmq:3-management
    fi

    curl -i -u guest:guest -H "content-type:application/json" \
    -XPUT -d'{"durable":true}' \
    http://localhost:8080/api/queues/%2f/my-queue
}

trap myInterruptHandler INT

runRabbitMqDockerContainer

mvn clean package
java -jar producer/target/producer-1.0-jar-with-dependencies.jar &
java -jar consumer/target/consumer-1.0-jar-with-dependencies.jar &

while true ; do continue ; done