package uk.gov.gds.ier.config

import com.google.inject.Singleton

@Singleton
class Config {
  private lazy val configuration = play.Play.application().configuration()
  def apiTimeout = configuration.getInt("api.timeout", 10).toInt
  def placesUrl = configuration.getString("places.url")
  def fakeIer = configuration.getBoolean("ier.fake")
  def fakePlaces = configuration.getBoolean("places.fake")
  def ierApiUrl = configuration.getString("ier.api.url")
  def ierApiToken = configuration.getString("ier.api.token")
  def stripNino = configuration.getBoolean("ier.nino.strip", false)

  def buildDate = configuration.getString("gds.BuildTime", "unknown")
  def buildNumber = configuration.getString("gds.BuildNumber", "unknown")
  def revision = configuration.getString("gds.GitCommit", "unknown")
  def branch = configuration.getString("gds.GitBranch", "unknown")
}
