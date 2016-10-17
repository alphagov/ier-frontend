package uk.gov.gds.ier.client

import play.modules.statsd.api.Statsd
import java.net.InetAddress

object StatsdClient {
  private var hostName = InetAddress.getLocalHost.getHostName

  def timing(statName: String, timeInMs: Long) = {
    if (!statName.contains("assets"))
      Statsd.timing(fullStatPath(statName), timeInMs)
  }

  private def fullStatPath(statName: String) =
    "%s.%s".format(hostName, statName)
}
