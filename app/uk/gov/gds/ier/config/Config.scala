package uk.gov.gds.ier.config

import com.google.inject.Singleton
import uk.gov.gds.ier.logging.Logging

@Singleton
class Config extends Logging {
  private lazy val configuration = play.Play.application().configuration()

  def apiTimeout = configuration.getInt("api.timeout", 10).toInt
  def placesUrl = configuration.getString("places.url")
  def locateUrl = configuration.getString("locate.url")
  def locateApiAuthorizationToken = configuration.getString("locate.api.authorization.token")
  def fakeIer = configuration.getBoolean("ier.fake")
  def fakePlaces = configuration.getBoolean("places.fake")
  def fakeLocate = configuration.getBoolean("locate.fake")
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
    println(s"apiTimeout:$apiTimeout")
    println(s"placesUrl:$placesUrl")
    println(s"locateUrl:$locateUrl")
    println(s"locateApiAuthorizationToken:$locateApiAuthorizationToken")
    println(s"fakeIer:$fakeIer")
    println(s"fakePlaces:$fakePlaces")
    println(s"fakeLocate:$fakeLocate")
    println(s"ierApiUrl:$ierApiUrl")
    println(s"stripNino:$stripNino")
    println(s"buildDate:$buildDate")
    println(s"buildNumber:$buildNumber")
    println(s"revision:$revision")
    println(s"branch:$branch")
    println(s"cookiesSecured:$cookiesSecured")
  }
}
