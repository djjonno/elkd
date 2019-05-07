package org.elkd.core.client

import org.elkd.core.client.consumer.Consumer
import org.elkd.core.client.producer.Producer


interface TopicRegistry {

  fun consumersFor(topic: Topic): List<Consumer>

  fun producersFor(topic: Topic): List<Producer>

  fun registerConsumer(topic: Topic, consumer: Consumer)

  fun deregisterConsumer(topic: Topic, consumer: Consumer)

  fun registerProducer(topic: Topic, producer: Producer)

  fun deregisterProducer(topic: Topic, producer: Producer)

}
