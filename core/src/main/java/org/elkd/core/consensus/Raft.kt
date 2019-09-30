package org.elkd.core.consensus

import org.elkd.core.config.Config
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.Request
import org.elkd.core.consensus.states.RaftStateFactory
import org.elkd.core.consensus.states.State
import org.elkd.core.log.ds.Log
import org.elkd.core.log.LogCommandExecutor
import org.elkd.core.runtime.TopicModule
import org.elkd.core.server.cluster.ClusterMessenger
import org.elkd.core.server.cluster.ClusterSet
import org.elkd.shared.annotations.Mockable

/**
 * Raft module performs consensus of the topicModule over the cluster, using the [ClusterMessenger]
 * as a means of communication. This module registers as a delegate to the [org.elkd.core.server.cluster.ClusterService]
 * and acts as a state machine between the various consensus states.
 *
 * @see [https://raft.github.io/raft.pdf](https://raft.github.io/raft.pdf)
 * @see RaftFollowerState
 * @see RaftCandidateState
 * @see RaftLeaderState
 */
@Mockable
class Raft
internal constructor(val config: Config,
                     val clusterMessenger: ClusterMessenger,
                     val raftContext: RaftContext,
                     val topicModule: TopicModule) {

  val log: Log<Entry>
    get() = topicModule.syslog.logFacade.log

  val logCommandExecutor: LogCommandExecutor<Entry>
    get() = topicModule.syslog.logFacade.commandExecutor

  val clusterSet: ClusterSet
    get() = clusterMessenger.clusterSet

  val supportedOps: Set<OpCategory>
    get() = delegator.supportedOps

  val delegator: RaftDelegator = RaftDelegator(RaftStateFactory(this), listOf(
      object: TransitionContract {

        override fun isTransitionRequired(request: Request): Boolean {
          return request.term > raftContext.currentTerm
        }

        override val transitionTo: State
          get() = State.FOLLOWER

        override val transitionPreHook: (request: Request) -> Unit
          get() = { request ->
            raftContext.currentTerm = request.term
            raftContext.votedFor = null
          }

        override val transitionPostHook: (request: Request) -> Unit
          get() = { /* no op */ }
      }
  ))

  fun initialize() {
    delegator.transition(State.FOLLOWER)
  }
}
