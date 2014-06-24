package uk.gov.gds.ier.transaction.complete

import play.api.mvc._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.assets.RemoteAssets
import scala.util.Try
import uk.gov.gds.ier.logging.Logging

class CompleteStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets
) extends SessionHandling[CompleteStepCookie]
  with Controller
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets
  with Logging
  with CompleteMustache {

  def complete = ValidSession requiredFor {
    implicit request => completeData =>
      Ok(Complete.CompletePage(
        completeData.authority,
        completeData.refNum,
        completeData.hasOtherAddress,
        completeData.backToStartUrl,
        completeData.showEmailConfirmation))
  }

  def factoryOfT() = CompleteStepCookie()
}
