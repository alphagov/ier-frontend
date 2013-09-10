package uk.gov.gds.ier.controller

import play.api.mvc.Controller
import play.api.mvc.Action
import uk.gov.gds.ier.model.IerForms
import com.google.inject.Inject
import uk.gov.gds.ier.service.IerApiService
import views._

class RegisterToVoteController @Inject() (ierApi:IerApiService) extends Controller with IerForms {
  def index = Action {
    Ok(html.start())
  }

  def registerToVote = Action {
    Ok(html.registerToVote(webApplicationForm))
  }

  def registerStep(step:String) = Action {
    Ok(html.registerToVote(webApplicationForm))
  }

  def complete = Action {
    Ok(html.complete())
  }

  def errorRedirect(error:String) = Action {
    Redirect(controllers.routes.RegisterToVoteController.error()).flashing("error-type" -> error)
  }

  def error = Action {
    implicit request =>
      flash.get("error-type") match {
        case Some("exit-unknown-dob") => Ok(html.errors.exitUnknownDob())
        case Some("exit-nationality") => Ok(html.errors.exitNationality())
        case Some("exit-dob") => Ok(html.errors.exitDob())
      }
  }

  def confirmation = Action {
    implicit request =>
      Ok()
  }

  def submitApplication = Action {
    implicit request =>
      webApplicationForm.bindFromRequest().fold(
        errors => Ok(html.registerToVote(errors)),
        applicant => {
          val response = ierApi.submitApplication(applicant)
          Ok(html.complete())
        }
      )
  }
}
