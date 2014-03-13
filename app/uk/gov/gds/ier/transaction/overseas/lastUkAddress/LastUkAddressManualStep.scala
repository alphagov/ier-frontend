package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import controllers.step.overseas.routes.{
  LastUkAddressController,
  LastUkAddressManualController,
  DateLeftUkController}
import controllers.step.overseas.{
  NameController,
  PassportCheckController}
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{
  InprogressOverseas,
  ApplicationType}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.form.OverseasFormImplicits

class LastUkAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService)
  extends OverseaStep
  with LastUkAddressMustache
  with LastUkAddressForms
  with OverseasFormImplicits {

  val validation = manualAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = LastUkAddressManualController.get,
    post = LastUkAddressManualController.post,
    editGet = LastUkAddressManualController.editGet,
    editPost = LastUkAddressManualController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.identifyApplication match {
      case ApplicationType.RenewerVoter => NameController.nameStep
      case ApplicationType.DontKnow => this
      case _ => PassportCheckController.passportCheckStep
    }
  }

  def template(
      form: InProgressForm[InprogressOverseas],
      call: Call,
      backUrl: Option[Call]) = {
    LastUkAddressMustache.manualPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url,
      LastUkAddressController.get.url
    )
  }
}
