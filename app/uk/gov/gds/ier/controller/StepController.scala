package uk.gov.gds.ier.controller

import play.api.mvc._
import com.google.inject.Inject
import uk.gov.gds.ier.service.{PlacesService, IerApiService}
import views._
import controllers._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation.{WithErrorTransformer, ErrorTransformer, InProgressForm, IerForms}
import scala.Some
import uk.gov.gds.common.model.{Ero, LocalAuthority}
import org.slf4j.LoggerFactory
import BodyParsers.parse._
import uk.gov.gds.ier.session.{Steps, SessionHandling}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.data.Form
import play.api.templates.Html

trait StepController extends Controller with SessionHandling {
  self: WithSerialiser
    with WithErrorTransformer =>

  val validation: Form[InprogressApplication]
  val template:(InProgressForm, Call) => Html
  val editPostRoute: Call
  val stepPostRoute: Call
  def goToNext(currentState: InprogressApplication):SimpleResult

  //Can override this method if you like
  def goToConfirmation(currentState: InprogressApplication):SimpleResult = {
    Redirect(routes.RegisterToVoteController.confirmApplication())
  }

  def editPage:InProgressForm => Html = {
    form:InProgressForm => template(form, editPostRoute)
  }

  def stepPage:InProgressForm => Html = {
    form:InProgressForm => template(form, stepPostRoute)
  }

  def get = ValidSession requiredFor {
    request => application =>
      Ok(stepPage(InProgress(application)))
  }

  def post = ValidSession withParser urlFormEncoded storeAfter {
    implicit request => application =>
      validation.bindFromRequest().fold(
        hasErrors => {
          val errorsTransformed = errorTransformer.transform(hasErrors)
          (Ok(stepPage(InProgressForm(errorsTransformed))), application)
        },
        success => {
          val mergedApplication = merge(application, success)
          (goToNext(mergedApplication), mergedApplication)
        }
      )
  }

  def editGet = ValidSession requiredFor {
    request => application =>
      Ok(editPage(InProgress(application)))
  }

  def editPost = ValidSession withParser urlFormEncoded storeAfter {
    implicit request => application =>
      validation.bindFromRequest().fold(
        hasErrors => {
          val errorsTransformed = errorTransformer.transform(hasErrors)
          (Ok(editPage(InProgressForm(errorsTransformed))), application)
        },
        success => {
          val mergedApplication = merge(application, success)
          (goToConfirmation(mergedApplication), mergedApplication)
        }
      )
  }

  private def merge(fromCookieApplication: InprogressApplication, application: InprogressApplication):InprogressApplication = {
    fromCookieApplication.copy(
      name = application.name.orElse(fromCookieApplication.name),
      previousName = application.previousName.orElse(fromCookieApplication.previousName),
      dob = application.dob.orElse(fromCookieApplication.dob),
      nationality = application.nationality.orElse(fromCookieApplication.nationality),
      nino = application.nino.orElse(fromCookieApplication.nino),
      address = application.address.orElse(fromCookieApplication.address),
      previousAddress = application.previousAddress.orElse(fromCookieApplication.previousAddress),
      otherAddress = application.otherAddress.orElse(fromCookieApplication.otherAddress),
      openRegisterOptin = application.openRegisterOptin.orElse(fromCookieApplication.openRegisterOptin),
      postalVoteOptin = application.postalVoteOptin.orElse(fromCookieApplication.postalVoteOptin),
      contact = application.contact.orElse(fromCookieApplication.contact),
      possibleAddresses = None
    )
  }
}
