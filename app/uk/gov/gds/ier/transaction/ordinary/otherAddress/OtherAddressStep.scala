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
import uk.gov.gds.ier.transaction.ordinary.otherAddress.OtherAddressMustacheTransformer
import views.html.layouts.{stepsBodyEnd, head}
import org.jba.Mustache

class OtherAddressStep @Inject ()(val serialiser: JsonSerialiser,
                                        val config: Config,
                                        val encryptionService : EncryptionService,
                                        val encryptionKeys : EncryptionKeys,
                                        val otherAddressTransformer: OtherAddressMustacheTransformer)
  extends OrdinaryStep
  with OtherAddressForms {

  val validation = otherAddressForm
  val editPostRoute = OtherAddressController.editPost
  val stepPostRoute = OtherAddressController.post

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    val data = otherAddressTransformer.transformFormStepToMustacheData(form, call.url).getOrElse(None)
    views.html.layouts.main(
      title = Some("Register to Vote - Do you spend part of your time living at another UK address?"),
      stylesheets = head(),
      scripts = stepsBodyEnd()
    )(
      Mustache.render("ordinary/otherAddress", data)
    )
  }

  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    Redirect(OpenRegisterController.get)
  }
}

