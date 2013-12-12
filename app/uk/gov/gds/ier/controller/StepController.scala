package uk.gov.gds.ier.controller

import play.api.mvc._
import controllers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.session.SessionHandling
import uk.gov.gds.ier.model.InprogressApplication
import play.api.data.Form
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}

trait StepController
  extends Controller
  with SessionHandling
  with ErrorMessages
  with FormKeys 
  with Logging {
    self: WithSerialiser
      with WithConfig
      with WithEncryption =>

  val validation: ErrorTransformForm[InprogressApplication]
  val editPostRoute: Call
  val stepPostRoute: Call
  def template(form: InProgressForm, call: Call):Html
  def goToNext(currentState: InprogressApplication):SimpleResult

  //Can override this method if you like
  def goToConfirmation(currentState: InprogressApplication):SimpleResult = {
    Redirect(routes.ConfirmationController.get())
  }

  def editPage:InProgressForm => Html = {
    form:InProgressForm => template(form, editPostRoute)
  }

  def stepPage:InProgressForm => Html = {
    form:InProgressForm => template(form, stepPostRoute)
  }

  def get = ValidSession requiredFor {
    request => application =>
      logger.info(s"GET request for ${request.path}")
      Ok(stepPage(InProgressForm(validation.fill(application))))
  }

  def post = ValidSession storeAfter {
    implicit request => application =>
      logger.info(s"POST request for ${request.path}")
      validation.bindFromRequest().fold(
        hasErrors => {
          logger.info(s" - Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          (Ok(stepPage(InProgressForm(hasErrors))), application)
        },
        success => {
          logger.info(s" - Form binding successful")
          val mergedApplication = merge(application, success)
          (goToNext(mergedApplication), mergedApplication)
        }
      )
  }

  def editGet = ValidSession requiredFor {
    request => application =>
      logger.info(s"GET edit request for ${request.path}")
      Ok(editPage(InProgressForm(validation.fill(application))))
  }

  def editPost = ValidSession storeAfter {
    implicit request => application =>
      logger.info(s"POST edit request for ${request.path}")
      validation.bindFromRequest().fold(
        hasErrors => {
          logger.info(s" - Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          (Ok(editPage(InProgressForm(hasErrors))), application)
        },
        success => {
          logger.info(s" - Form binding successful")
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
      possibleAddresses = None,
      country = application.country.orElse(fromCookieApplication.country)
    )
  }
}
