package uk.gov.gds.ier.transaction.country

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CountryConstraints
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{InprogressOrdinary, Country}
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes, Exit}
import controllers.step.ordinary.NationalityController
import controllers.step.routes.CountryController
import controllers.routes.ExitController

class CountryStep @Inject ()(val serialiser: JsonSerialiser,
                             val config:Config,
                             val encryptionService : EncryptionService,
                             val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with CountryConstraints
  with CountryForms
  with CountryMustache {

  val validation = countryForm
  val editPostRoute = CountryController.editPost
  val stepPostRoute = CountryController.post

  val routes = Routes(
    get = CountryController.get,
    post = CountryController.post,
    edit = CountryController.editGet,
    editPost = CountryController.editPost
  )

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    countryMustache(form.form, call)
  }

  def nextStep(currentState: InprogressOrdinary) = {
    currentState.country match {
      case Some(Country("Northern Ireland")) => Exit(ExitController.northernIreland)
      case Some(Country("Scotland")) => Exit(ExitController.scotland)
      case _ => NationalityController.nationalityStep
    }
  }
}
