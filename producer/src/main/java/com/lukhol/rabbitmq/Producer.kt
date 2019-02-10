package com.lukhol.rabbitmq

import com.rabbitmq.client.ConnectionFactory

fun main(args: Array<String>) {
    val conFactory = ConnectionFactory()
    conFactory.setUri("amqp://guest:guest@localhost")
    conFactory.connectionTimeout = 300000

    val connection = conFactory.newConnection()
    val channel = connection.createChannel()

    channel.queueDeclare("my-queue", true, false, false, null)

    var count = 0

    while(count < 5000) {
        val msg = "Message nr. ${count++}"

        channel.basicPublish("", "my-queue", null, msg.toByteArray())
        println("Published messgae $msg")

        Thread.sleep(5000)
    }
}