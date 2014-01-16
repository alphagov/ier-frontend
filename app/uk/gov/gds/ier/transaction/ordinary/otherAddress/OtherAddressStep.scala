package uk.gov.gds.ier.transaction.ordinary.otherAddress

import controllers.step.ordinary.OpenRegisterController
import controllers.step.ordinary.routes.{OtherAddressController, PreviousAddressController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}

class OtherAddressStep @Inject ()(val serialiser: JsonSerialiser,
                                        val config: Config,
                                        val encryptionService : EncryptionService,
                                        val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with OtherAddressForms {

  val validation = otherAddressForm
  val previousRoute = Some(PreviousAddressController.get)

  val routes = Routes(
    get = OtherAddressController.get,
    post = OtherAddressController.post,
    editGet = OtherAddressController.editGet,
    editPost = OtherAddressController.editPost
  )

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    views.html.steps.otherAddress(form, call, backUrl.map(_.url))
  }
  def nextStep(currentState: InprogressOrdinary) = {
    OpenRegisterController.openRegisterStep
  }
}

