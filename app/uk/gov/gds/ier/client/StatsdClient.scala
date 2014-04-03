package uk.gov.gds.ier.client

import play.modules.statsd.api.Statsd
import java.net.InetAddress
import uk.gov.gds.common.config.Config

object StatsdClient {

  //private lazy val applicationName =  Config("gds.application.name", "not-configured")

  def timing(statName: String, timeInMs: Long) = Statsd.timing(fullStatPath(statName), timeInMs)

  private def fullStatPath(statName: String) =
   // "%s.%s.%s".format(applicationName, InetAddress.getLocalHost.getHostName, statName)
    "%s.%s.%s".format("frontend", InetAddress.getLocalHost.getHostName, statName)
}
