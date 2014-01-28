package uk.gov.gds.ier.transaction.overseas.address

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.model.{PreviouslyRegistered, InprogressOverseas}
import uk.gov.gds.ier.validation.{ErrorTransformForm, InProgressForm}
import play.api.mvc.Call
import play.api.templates.Html
import play.api.mvc.SimpleResult
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import controllers.step.overseas.routes.{AddressController, NinoController}
import controllers.step.overseas.{OpenRegisterController}
import uk.gov.gds.ier.model.OverseasAddress
import uk.gov.gds.ier.transaction.overseas.address.OverseasAddressMustache

class OverseasAddressStep @Inject() (val serialiser: JsonSerialiser,
                                                val config: Config,
                                                val encryptionService: EncryptionService,
                                                val encryptionKeys: EncryptionKeys)
  extends OverseaStep
  with OverseasAddressForms
  with OverseasAddressMustache {

  val validation = addressForm
  val routes = Routes(
    get = AddressController.get,
    post = AddressController.post,
    editGet = AddressController.editGet,
    editPost = AddressController.editPost
  )
  val previousRoute = Some(NinoController.get)

  def nextStep(currentState: InprogressOverseas) = {
    currentState.address match {
      case Some(OverseasAddress(country, address)) => OpenRegisterController.openRegisterStep
      case _ => this
    }
  }

  def template(form: InProgressForm[InprogressOverseas],
               postEndpoint: Call,
               backEndpoint:Option[Call]): Html = {
    addressMustache(form.form, postEndpoint, backEndpoint)
  }
}
