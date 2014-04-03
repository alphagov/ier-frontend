package uk.gov.gds.ier.client

import play.modules.statsd.api.Statsd
import java.net.InetAddress

object StatsdClient {

  def timing(statName: String, timeInMs: Long) = Statsd.timing(fullStatPath(statName), timeInMs)

  private def fullStatPath(statName: String) =
    "%s.%s".format(InetAddress.getLocalHost.getHostName, statName)
}
