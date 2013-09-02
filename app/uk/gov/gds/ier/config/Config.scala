package uk.gov.gds.ier.config

import com.google.inject.Singleton

@Singleton
class Config {
  private lazy val configuration = play.Play.application().configuration()
  def apiTimeout = configuration.getInt("api.timeout", 10).toInt
  def paUrl = configuration.getString("pa.url")
  def paKey = configuration.getString("pa.key")
}
