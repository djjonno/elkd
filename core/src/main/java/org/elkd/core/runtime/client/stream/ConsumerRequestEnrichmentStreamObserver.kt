package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.elkd.core.runtime.client.consumer.ConsumerACK
import org.elkd.core.runtime.client.consumer.ConsumerRequest
import org.elkd.core.runtime.topic.TopicModule
import org.elkd.core.server.client.RpcConsumerRequest
import org.elkd.core.server.client.RpcConsumerResponse

class ConsumerRequestEnrichmentStreamObserver(
    private val next: StreamObserver<ConsumerRequest>,
    private val back: StreamObserver<RpcConsumerResponse>,
    private val topicModule: TopicModule
) : StreamObserver<RpcConsumerRequest> {
  override fun onNext(value: RpcConsumerRequest) {
    topicModule.topicRegistry.getByNamespace(value.topic)?.apply {

      /* Construct ConsumerRequest */
      val field = RpcConsumerRequest.getDescriptor().findFieldByName("index")
      next.onNext(ConsumerRequest(this, if (value.hasField(field)) value.index else null))
    } ?: back.onNext(ConsumerACK.Rpcs.UNKNOWN_TOPIC)
  }

  override fun onError(t: Throwable?) {
    next.onError(t)
  }

  override fun onCompleted() {
    next.onCompleted()
  }
}
