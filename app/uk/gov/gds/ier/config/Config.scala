package uk.gov.gds.ier.config

import com.google.inject.Singleton
import uk.gov.gds.ier.logging.Logging

@Singleton
class Config extends Logging {
  private lazy val configuration = play.Play.application().configuration()

  def apiTimeout = configuration.getInt("api.timeout", 10).toInt
  def placesUrl = configuration.getString("places.url")
  def locateUrl = configuration.getString("locate.url")
  def fakeIer = configuration.getBoolean("ier.fake")
  def fakePlaces = configuration.getBoolean("places.fake")
  def ierApiUrl = configuration.getString("ier.api.url")
  def ierApiToken = configuration.getString("ier.api.token")
  def stripNino = configuration.getBoolean("ier.nino.strip", false)
  def sessionTimeout = configuration.getInt("session.timeout", 20).toInt

  def buildDate = configuration.getString("gds.BuildTime", "unknown")
  def buildNumber = configuration.getString("gds.BuildNumber", "unknown")
  def revision = configuration.getString("gds.GitCommit", "unknown")
  def branch = configuration.getString("gds.GitBranch", "unknown")

  def cookiesAesKey = configuration.getString("ier.cookies.aes.encryptionKey")
  def cookiesSecured = configuration.getBoolean("ier.cookies.secured", false)

  def logConfiguration() = {
    logger.debug(s"apiTimeout:$apiTimeout")
    logger.debug(s"placesUrl:$placesUrl")
    logger.debug(s"locateUrl:$locateUrl")
    logger.debug(s"fakeIer:$fakeIer")
    logger.debug(s"fakePlaces:$fakePlaces")
    logger.debug(s"ierApiUrl:$ierApiUrl")
    logger.debug(s"stripNino:$stripNino")
    logger.debug(s"buildDate:$buildDate")
    logger.debug(s"buildNumber:$buildNumber")
    logger.debug(s"revision:$revision")
    logger.debug(s"branch:$branch")
    logger.debug(s"cookiesSecured:$cookiesSecured")
  }
}
