package org.kerala.ctl.commands.producer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.runBlocking
import org.kerala.core.server.client.ClientServiceGrpc
import org.kerala.core.server.client.RpcKV
import org.kerala.ctl.Context
import org.kerala.ctl.asChannel
import org.kerala.ctl.leader
import org.kerala.shared.client.ProducerACK

class ConsoleProducerCommand : CliktCommand(name = "console-producer"),
    CoroutineScope by MainScope() {

  private val topic: String by argument(name = "namespace", help = "namespace of topic to consume from")
  private val ctx by requireObject<Context>()

  override fun run() = runBlocking {
    echo("producing -> Topic($topic)")
    echo("-")

    val producer = StreamProducer(ClientServiceGrpc.newStub(ctx.cluster!!.leader()!!.asChannel()))
    loop@do {
      val prompt = TermUi.prompt("> ")
      val response = producer.batch(topic, listOf(RpcKV.newBuilder().setValue(prompt).build()))
      when (response.status) {
        ProducerACK.Codes.OK.id -> echo("committed ✓")
        else -> {
          echo("error status=$response")
          break@loop
        }
      }
    } while (true)
    producer.complete()
  }
}
