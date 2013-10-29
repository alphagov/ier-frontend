package uk.gov.gds.ier.controller

import play.api.mvc._
import uk.gov.gds.ier.model.{InProgressSession, Steps}
import com.google.inject.Inject
import uk.gov.gds.ier.service.{PlacesService, IerApiService}
import views._
import controllers._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation.{InProgressForm, IerForms}
import scala.Some
import uk.gov.gds.common.model.{Ero, LocalAuthority}

class RegisterToVoteController @Inject() (ierApi:IerApiService, serialiser: JsonSerialiser, placesService:PlacesService)
    extends Controller
    with IerForms
    with WithSerialiser
    with Steps
    with InProgressSession {

  def toJson(obj: AnyRef): String = serialiser.toJson(obj)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson[T](json)

  def index = Action { implicit request =>
    Ok(html.start()).withFreshSession()
  }

  def registerToVote = ValidSession requiredFor Action {
    Redirect(controllers.routes.RegisterToVoteController.registerStep(firstStep()))
  }

  def registerStep(step:String) = ValidSession requiredFor Action { implicit request =>
    Step(step) { stepDetail =>
      Ok(stepDetail.page(request.session.getForm))
    }
  }

  def validateStep(step:String) = ValidSession requiredFor Action(BodyParsers.parse.urlFormEncoded) { implicit request =>
    Step(step) { stepDetail =>
      stepDetail.validation.bindFromRequest().fold(
        errors => Ok(stepDetail.page(InProgressForm(errors))),
        form => Redirect(routes.RegisterToVoteController.registerStep(stepDetail.next)).mergeWithSession(form)
      )
    }
  }

  def edit(step:String) = ValidSession requiredFor Action {
    implicit request =>
      Step(step) { stepDetail =>
        Ok(stepDetail.editPage(request.session.getForm))
      }
  }

  def validateEdit(step:String) = ValidSession requiredFor Action(BodyParsers.parse.urlFormEncoded) { implicit request =>
    Step(step) { stepDetail =>
      stepDetail.validation.bindFromRequest().fold(
        errors => Ok(stepDetail.page(InProgressForm(errors))),
        form => Redirect(routes.RegisterToVoteController.confirmApplication()).mergeWithSession(form)
      )
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
    implicit request =>
      val authority = request.flash.get("postcode") match {
        case Some("") => None
        case Some(postCode) => placesService.lookupAuthority(postCode)
        case None => None
      }
      val refNum = request.flash.get("refNum")

      Ok(html.complete(authority, refNum)).withNewSession
  }

  def fakeComplete = Action {
    val authority = Some(LocalAuthority("Tower Hamlets Borough Council", Ero(), "00BG", "E09000030"))
    Ok(html.complete(authority, Some("123456")))
  }

  def confirmApplication = ValidSession requiredFor Action {
    implicit request => Step("confirmation") { stepDetail =>
      Ok(stepDetail.page(InProgressForm(stepDetail.validation.fillAndValidate(request.session.getApplication))))
    }
  }

  def submitApplication = ValidSession requiredFor Action {
    implicit request =>
      inprogressForm.fillAndValidate(request.session.getApplication).fold(
        errors => {
          Ok(Step.getStep("confirmation").page(InProgressForm(errors)))
        },
        validApplication => {
          val refNum = ierApi.generateReferenceNumber(validApplication)
          ierApi.submitApplication(validApplication, Some(refNum))
          Redirect(controllers.routes.RegisterToVoteController.complete()).flashing(
            "refNum" -> refNum,
            "postcode" -> validApplication.address.map(_.postcode).getOrElse("")
          )
        }
      )
  }
}
