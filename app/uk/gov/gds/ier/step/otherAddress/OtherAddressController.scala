package uk.gov.gds.ier.step.otherAddress

import controllers.step._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import org.jba.Mustache
import views.html.layouts.{stepsBodyEnd, head}

class OtherAddressController @Inject()(val serialiser: JsonSerialiser,
                                       val config: Config,
                                       val encryptionService: EncryptionService,
                                       val encryptionKeys: EncryptionKeys,
                                       val otherAddressTransformer: OtherAddressMustacheTransformer)
  extends StepController
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with OtherAddressForms {

  val validation = otherAddressForm
  val editPostRoute = routes.OtherAddressController.editPost
  val stepPostRoute = routes.OtherAddressController.post

  def template(form: InProgressForm, call: Call): Html = {
    val data = otherAddressTransformer.transformFormStepToMustacheData(form, call.url).getOrElse(None)
    views.html.layouts.main(
      title = Some("Register to Vote - Do you spend part of your time living at another UK address?"),
      stylesheets = head(),
      scripts = stepsBodyEnd()
    )(
      Mustache.render("ordinary/otherAddress", data)
    )
  }

  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.OpenRegisterController.get)
  }
}

