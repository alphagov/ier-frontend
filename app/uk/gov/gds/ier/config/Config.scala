package uk.gov.gds.ier.config

import com.google.inject.Singleton
import uk.gov.gds.ier.logging.Logging
import scala.collection.JavaConversions._

@Singleton
class Config extends Logging {
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

  def cookiesRsaPublicKey = configuration.getString("ier.cookies.Rsa.PublicKey","MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJIF36IAQz2t7WJtUDYqID1TnLQH4vhdS7ozVEYo67TyhkZM4NVa96rkKF6TjTHvfBmI/ZFK6jSPk6dLLyz2CkUCAwEAAQ==")
  def cookiesRsaPrivateKey = configuration.getString("ier.cookies.Rsa.PrivateKey","MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAkgXfogBDPa3tYm1QNiogPVOctAfi+F1LujNURijrtPKGRkzg1Vr3quQoXpONMe98GYj9kUrqNI+Tp0svLPYKRQIDAQABAkAiFeDn7evEQA9DhITOv/KaniPcGmMu2ohMxKKNZgvvRrlrq181aqxoBvqu78ErM0fS+fvlz20aaUa3DkTEBZjBAiEA66dXoIAyM4FXh+5rHOC7SkNkjtad7pqdmvHVYwhfp/0CIQCeoWvh1ozW/kjxEKG5/6rStwUXadxb2/jY3/LWpatJ6QIgUIMZDi4eeLhtJnUPxYsGkkXaOm8bAGV1CXYseKxouiUCIC9xlgOQmMUAfq5izAwGNIAbLxGmnrp2mwG6UTXzjLxpAiEA1IW3FJujVWO+emtkvq5B0v+0n7o8O2urLrRZGRTRidU=")

  def logConfiguration() = {
    logger.debug(s"apiTimeout:$apiTimeout")
    logger.debug(s"placesUrl:$placesUrl")
    logger.debug(s"fakeIer:$fakeIer")
    logger.debug(s"fakePlaces:$fakePlaces")
    logger.debug(s"ierApiUrl:$ierApiUrl")
    logger.debug(s"stripNino:$stripNino")
    logger.debug(s"buildDate:$buildDate")
    logger.debug(s"buildNumber:$buildNumber")
    logger.debug(s"revision:$revision")
    logger.debug(s"branch:$branch")
  }
}
