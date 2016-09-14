package uk.gov.gds.ier.controller

import play.api.mvc.{Action, Controller}

import sys.process._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import uk.gov.gds.ier.config.Config
import java.lang.management.ManagementFactory._
import scala.Some
import uk.gov.gds.ier.client.{ApiResults, IerApiClient}
import com.google.inject.Singleton
import uk.gov.gds.ier.model.{Fail, LocalAuthority, Success}
import uk.gov.gds.ier.service.{ApiException, LocateService}
import uk.gov.gds.ier.service.apiservice.IerApiService

@Singleton
class StatusController @Inject() (
                                   val serialiser: JsonSerialiser,
                                   config: Config,
                                   locateService: LocateService,
                                   apiClient: IerApiClient
                                 ) extends Controller with ApiResults with WithSerialiser {

  def status = Action {
    okResult(Map(
      "status" -> "up",
      "process id" -> pidAsString,
      "uptime" -> upTime,
      "started" -> startTime,
      "build date" -> config.buildDate,
      "build number" -> config.buildNumber,
      "revision" -> config.revision,
      "branch" -> config.branch,
      "mongo" -> testMongo,
      "postgres" -> testPostgres
    ))
  }

  private def pid = getRuntimeMXBean.getName.split('@').headOption
  private def address = locateService.lookupAddress("x11xx")
  private def localAuthority = getLocalAuthorityByGssCode("E99999999").toString

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

  private def testMongo = address.mkString(",") match {
    case address if address.contains("property") => "up"
    case _ => "Mongo Unavailable"
  }

  private def testPostgres = localAuthority match {
    case localAuthority if localAuthority.contains("E99999999") => "up"
    case _ => "Postgres Unavailable"
  }

  def getLocalAuthorityByGssCode(gssCode: String): LocalAuthority = {
    apiClient.get(config.ierLocalAuthorityLookupUrl + gssCode,
      ("Authorization", "BEARER " + config.ierApiToken)) match {
      case Success(body,timeTakenMs) => {
        serialiser.fromJson[LocalAuthority](body)
      }
      case Fail(error,timeTakenMs) => {
        throw new ApiException(error)
      }
    }
  }
}
