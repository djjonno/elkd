package org.kerala.core.server.cluster

import org.kerala.shared.schemes.URI
import java.util.Objects

class Node(private val uri: URI) {
  val id: String
    get() = uri.toString()

  val host: String
    get() = uri.host

  val port: Int
    get() = uri.port

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other == null || javaClass != other.javaClass) {
      return false
    }
    val node = other as Node?
    return uri == node!!.uri
  }

  override fun hashCode(): Int {
    return Objects.hash(uri)
  }

  override fun toString(): String {
    return id
  }
}
