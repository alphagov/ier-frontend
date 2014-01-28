package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import play.api.templates.Html
import controllers.step.routes.CountryController
import controllers.step.overseas.RegisteredAddressController
import uk.gov.gds.ier.step.OverseaStep
import controllers.step.overseas.routes._
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some


class DateLeftUkStep @Inject() (val serialiser: JsonSerialiser,
                                val config: Config,
                                val encryptionService: EncryptionService,
                                val encryptionKeys: EncryptionKeys)
  extends OverseaStep
    with DateLeftUkForms
    with DateLeftUkMustache {

  val validation = dateLeftUkForm
  val routes = Routes(
    get = DateLeftUkController.get,
    post = DateLeftUkController.post,
    editGet = DateLeftUkController.editGet,
    editPost = DateLeftUkController.editPost
  )
  val previousRoute = Some(CountryController.get)

  def nextStep(currentState: InprogressOverseas) = {
    RegisteredAddressController.registeredAddressStep
  }

  def template(form: InProgressForm[InprogressOverseas],
               postEndpoint: Call,
               backEndpoint:Option[Call]): Html = {
    dateLeftUkMustache(form.form, postEndpoint, backEndpoint)
  }
}
