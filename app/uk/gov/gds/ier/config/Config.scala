package uk.gov.gds.ier.config

object Config {
  private lazy val configuration = play.Play.application().configuration()
  def apiTimeout = configuration.getInt("api.timeout", 10).toInt
}
