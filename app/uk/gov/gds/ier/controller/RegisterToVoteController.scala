package uk.gov.gds.ier.controller

import play.api.mvc._
import uk.gov.gds.ier.model.{InProgressSession, Steps}
import com.google.inject.Inject
import uk.gov.gds.ier.service.IerApiService
import views._
import controllers._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation.{InProgressForm, IerForms}
import scala.Some

class RegisterToVoteController @Inject() (ierApi:IerApiService, serialiser: JsonSerialiser)
    extends Controller
    with IerForms
    with WithSerialiser
    with Steps
    with InProgressSession {

  def toJson(obj: AnyRef): String = serialiser.toJson(obj)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson[T](json)

  def index = Action {
    Ok(html.start()).withNewSession
  }

  def registerToVote = Action {
    Redirect(controllers.routes.RegisterToVoteController.registerStep(firstStep()))
  }

  def validateAndRedirect(step:Step, redirectOnSuccess:Call)(implicit request: play.api.mvc.Request[_]) = {
    step.validation.bindFromRequest().fold(
      errors => {
        Ok(step.page(InProgressForm(errors)))
      },
      form => {
        val application = session.merge(form)
        Redirect(redirectOnSuccess).withSession(application.toSession)
      }
    )
  }

  def registerStep(step:String) = Action { implicit request =>
    Step(step) { stepDetail =>
      Ok(stepDetail.page(InProgressForm(request.session.getApplication)))
    }
  }

  def validateStep(step:String) = Action(BodyParsers.parse.urlFormEncoded) { implicit request =>
    Step(step) { stepDetail =>
      validateAndRedirect(stepDetail, routes.RegisterToVoteController.registerStep(stepDetail.next))
    }
  }

  def edit(step:String) = Action {
    implicit request =>
      Step(step) { stepDetail =>
        Ok(stepDetail.editPage(InProgressForm(request.session.getApplication)))
      }
  }

  def validateEdit(step:String) = Action(BodyParsers.parse.urlFormEncoded) { implicit request =>
    Step(step) { stepDetail =>
      validateAndRedirect(stepDetail, routes.RegisterToVoteController.confirmApplication())
    }
  }

  def errorRedirect(error:String) = Action {
    Redirect(routes.RegisterToVoteController.error()).flashing("error-type" -> error)
  }

  def error = Action {
    implicit request =>
      flash.get("error-type") match {
        case Some("exit-unknown-dob") => Ok(html.errors.exitUnknownDob())
        case Some("exit-nationality") => Ok(html.errors.exitNationality())
        case Some("exit-dob") => Ok(html.errors.exitDob())
        case Some("exit-error") => Ok(html.errors.exitError())
      }
  }

  def complete = Action {
    Ok(html.complete()).withNewSession
  }

  def confirmApplication = Action {
    implicit request => Step("confirmation") { stepDetail =>
      Ok(stepDetail.page(InProgressForm(stepDetail.validation.fillAndValidate(request.session.getApplication))))
    }
  }

  def submitApplication = Action {
    implicit request =>
      inprogressForm.bindFromRequest().fold(
        errors => Ok(Step.getStep("confirmation").page(InProgressForm(errors))),
        validApplication => {
          //Post to Api
          Redirect(controllers.routes.RegisterToVoteController.complete()).withNewSession
        }
      )
  }
}
