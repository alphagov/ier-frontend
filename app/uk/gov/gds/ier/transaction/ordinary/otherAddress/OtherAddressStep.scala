package uk.gov.gds.ier.transaction.ordinary.otherAddress

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.OrdinaryStep
import uk.gov.gds.ier.transaction.ordinary.otherAddress.OtherAddressMustache
import views.html.layouts.{stepsBodyEnd, head}
import org.jba.Mustache

class OtherAddressStep @Inject ()(val serialiser: JsonSerialiser,
                                        val config: Config,
                                        val encryptionService : EncryptionService,
                                        val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with OtherAddressForms
  with OtherAddressMustache {

  val validation = otherAddressForm
  val editPostRoute = OtherAddressController.editPost
  val stepPostRoute = OtherAddressController.post

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    otherAddressMustache(form.form, call)
  }

  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    Redirect(OpenRegisterController.get)
  }
}

