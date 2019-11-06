package org.kerala.core.runtime.client.consumer

import org.apache.log4j.Logger
import org.kerala.core.consensus.messages.Entry
import org.kerala.core.runtime.client.broker.ClusterSetInfo
import org.kerala.core.runtime.client.command.ClientCommand
import org.kerala.core.runtime.client.command.ClientCommandType
import org.kerala.core.runtime.client.command.asCommand
import org.kerala.core.runtime.topic.TopicModule
import org.kerala.core.server.cluster.Node
import org.kerala.shared.schemes.URI

/**
 * SyslogConsumer consumes all entries on the @system Topic.
 *
 * The entry comes in the form of a decomposed ClientCommand object,
 * serialized as KVS.  This component will extract it and
 * execute it appropriate against the runtime.
 *
 * The invocation is purely asynchronous.  The entries have
 * already been committed to the logFacade so the failure mode here
 * it simply to logFacade.error and continue.
 *
 * It is a best-effort strategy to ensure an invalid type
 * is not issued to the logFacade in the first place, so it is rare
 * that you will encounter a runtime failure.  It will likely
 * be some kind of systemic error as opposed to an invalid
 * state of some sorts.
 */
class SyslogConsumer(private val topicModule: TopicModule,
                     private val clusterSetInfo: ClusterSetInfo) {
  fun consume(entry: Entry) {
    val command = entry.asCommand()

    when (ClientCommandType.fromId(command.command)) {
      ClientCommandType.CREATE_TOPIC -> createTopic(command.CreateTopicClientCommand())
      ClientCommandType.DELETE_TOPIC -> deleteTopic(command.DeleteTopicClientCommand())
      ClientCommandType.CONSENSUS_CHANGE -> try {
        clusterSetInfo.leader = Node(URI.parseURIString(command.LeaderChangeClientCommand().leaderNode))
      } catch (e: Exception) { }
    }
  }

  private fun createTopic(command: ClientCommand.CreateTopicClientCommand) {
    topicModule.topicRegistry.getByNamespace(command.namespace)?.let {
      LOGGER.info("topic creation ignored `${command.namespace}` already exists")
    } ?: run {
      val newTopic = topicModule.provisionTopic(command.id, command.namespace)
      LOGGER.info("topic provisioned $newTopic")
    }
  }

  private fun deleteTopic(command: ClientCommand.DeleteTopicClientCommand) {
    topicModule.topicRegistry.getByNamespace(command.namespace)?.let {
      topicModule.topicRegistry.remove(it)
    } ?: LOGGER.info("topic deletion ignored `${command.namespace}` does not exist.")
  }

  companion object {
    private var LOGGER = Logger.getLogger(SyslogConsumer::class.java)
  }
}
