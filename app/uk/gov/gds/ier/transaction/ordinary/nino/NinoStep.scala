package uk.gov.gds.ier.transaction.ordinary.nino

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.OrdinaryStep

class NinoStep @Inject ()(val serialiser: JsonSerialiser,
                                val config: Config,
                                val encryptionService : EncryptionService,
                                val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with NinoForms {

  val validation = ninoForm
  val editPostRoute = NinoController.editPost
  val stepPostRoute = NinoController.post
  val previousRoute = Some(NameController.get)

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    views.html.steps.nino(form, call, backUrl.map(_.url))
  }
  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    Redirect(AddressController.get)
  }
}

