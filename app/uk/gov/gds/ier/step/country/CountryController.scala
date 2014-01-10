package uk.gov.gds.ier.step.country

import controllers.step.routes._
import controllers.routes._
import controllers.step.ordinary.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CountryConstraints
import uk.gov.gds.ier.controller.OrdinaryController
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{InprogressOrdinary, Country}
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class CountryController @Inject ()(val serialiser: JsonSerialiser,
                                   val config:Config,
                                   val encryptionService : EncryptionService,
                                   val encryptionKeys : EncryptionKeys)
  extends OrdinaryController
  with CountryConstraints
  with CountryForms {

  val validation = countryForm
  val editPostRoute = CountryController.editPost
  val stepPostRoute = CountryController.post

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.country(form, call)
  }
  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    currentState.country match {
      case Some(Country("Northern Ireland")) => Redirect(ExitController.northernIreland)
      case Some(Country("Scotland")) => Redirect(ExitController.scotland)
      case _ => Redirect(NationalityController.get)
    }
  }
}

