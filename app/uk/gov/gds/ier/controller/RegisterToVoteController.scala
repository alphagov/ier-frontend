package uk.gov.gds.ier.controller

import play.api.mvc.Controller
import play.api.mvc.Action
import uk.gov.gds.ier.model.IerForms
import com.google.inject.Inject
import uk.gov.gds.ier.service.IerApiService

class RegisterToVoteController @Inject() (ierApi:IerApiService) extends Controller with IerForms {
  def index = Action {
    Ok(views.html.index())
  }

  def registerToVote = Action {
    Ok(views.html.registerToVote(webApplicationForm))
  }

  def complete = Action {
    Ok(views.html.complete())
  }

  def errorRedirect(error:String) = Action {
    Redirect(controllers.routes.RegisterToVoteController.error()).flashing("error-type" -> error)
  }

  def error = Action {
    implicit request =>
      flash.get("error-type") match {
        case Some("exit-unknown-dob") => Ok(views.html.errors.exitUnknownDob())
        case Some("exit-nationality") => Ok(views.html.errors.exitNationality())
        case Some("exit-dob") => Ok(views.html.errors.exitDob())
      }
  }

  def submitApplication = Action {
    implicit request =>
      webApplicationForm.bindFromRequest().fold(
        errors => Ok(views.html.registerToVote(errors)),
        applicant => {
          val response = ierApi.submitApplication(applicant)
          Ok(views.html.confirmation(response))
        }
      )
  }
}
