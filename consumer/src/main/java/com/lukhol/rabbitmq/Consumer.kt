package com.lukhol.rabbitmq

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.QueueingConsumer
import java.nio.charset.StandardCharsets

fun main(args: Array<String>) {
    val factory = ConnectionFactory()
    factory.setUri("amqp://guest:guest@localhost")
    factory.connectionTimeout = 300000

    val connection = factory.newConnection()
    val channel = connection.createChannel()
    channel.queueDeclare("my-queue", true, false, false, null)

    val consumer = QueueingConsumer(channel)
    channel.basicConsume("my-queue", false, consumer)

    while(true) {
        val delivery = consumer.nextDelivery()

        delivery?.let {
            try {
                val msg = String(delivery.body, StandardCharsets.UTF_8)
                println("Message consumed: $msg")

                channel.basicAck(delivery.envelope.deliveryTag, false)
            } catch (e: Exception) {
                channel.basicReject(delivery.envelope.deliveryTag, true)
            }
        }

        Thread.sleep(1000)
    }
}