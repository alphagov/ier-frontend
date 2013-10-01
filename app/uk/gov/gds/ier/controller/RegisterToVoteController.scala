package uk.gov.gds.ier.controller

import play.api.mvc.{Session, BodyParsers, Controller, Action}
import uk.gov.gds.ier.model.{InProgressSession, Steps}
import com.google.inject.Inject
import uk.gov.gds.ier.service.IerApiService
import views._
import controllers._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation.{InProgressForm, IerForms}

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

  def registerStep(step:String) = Action { implicit request =>
    Step(step) { stepDetail =>
      Ok(stepDetail.page(InProgressForm(request.session.getApplication)))
    }
  }

  def next(step:String) = Action(BodyParsers.parse.urlFormEncoded) { implicit request =>
    Step(step) { stepDetail =>
      println(request.body)
      stepDetail.validation.bindFromRequest().fold(
        errors => {
          Ok(stepDetail.page(InProgressForm(errors)))
        },
        form => {
          val application = session.merge(form)
          Redirect(routes.RegisterToVoteController.registerStep(stepDetail.next)).withSession(application.toSession)
        }
      )
    }
  }

  def edit(step:String) = Action {
    implicit request =>
      Ok(editPageFor(step))
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

  def submitApplication = Action {
    implicit request =>
      Redirect(controllers.routes.RegisterToVoteController.complete()).withNewSession
  }
}
