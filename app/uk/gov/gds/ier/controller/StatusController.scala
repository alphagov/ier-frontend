package uk.gov.gds.ier.controller

import play.api.mvc.{Action, Controller}
import sys.process._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import java.lang.management.ManagementFactory._
import scala.Some

class StatusController @Inject() (serialiser: JsonSerialiser, config: Config) extends Controller {

  def status = Action {
    Ok(
      serialiser.toJson(
        Map(
          "status" -> "up",
          "process id" -> pidAsString,
          "uptime" -> upTime,
          "started" -> startTime,
          "build date" -> config.buildDate,
          "build number" -> config.buildNumber,
          "revision" -> config.revision,
          "branch" -> config.branch
        )
      )
    ).withHeaders(CONTENT_TYPE -> JSON)
  }

  private def pid = getRuntimeMXBean.getName.split('@').headOption

  private def pidAsString = pid match {
    case Some(p) => p
    case None => "PID Unavailable"
  }

  private def upTime = pid match {
    case Some(p) => "ps -o etime= %s".format(p).!!.trim
    case None => "Uptime Unavailable"
  }

  private def startTime = pid match {
    case Some(p) => "ps -o lstart= %s".format(p).!!.trim
    case None => "Uptime Unavailable"
  }
}
