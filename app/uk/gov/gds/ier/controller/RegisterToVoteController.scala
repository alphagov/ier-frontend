package uk.gov.gds.ier.controller

import uk.gov.gds.ier.guice.DelegatingController
import controllers.{RegisterToVoteController => PlayController}
import play.api.mvc.Controller
import play.api.mvc.Action
import uk.gov.gds.ier.model.{Fail, Success, WebApplication, IerForms}
import org.joda.time.DateTime
import com.google.inject.Inject
import uk.gov.gds.ier.service.IerApiService

class RegisterToVoteController @Inject() (ierApi:IerApiService) extends Controller with IerForms {
  def index = Action {
    Ok(views.html.index())
  }

  def registerToVote = Action {
    Ok(views.html.registerToVote(webApplicationForm))
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
