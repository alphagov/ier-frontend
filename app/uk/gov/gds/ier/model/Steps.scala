package uk.gov.gds.ier.model

import views.html
import play.api.mvc.RequestHeader
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form

trait Steps extends IerForms {
  self: InProgressSession =>

  def nextStep(step:String) = {
    step match {
      case "nationality" => "date-of-birth"
      case "date-of-birth" => "name"
      case "name" => "previous-name"
      case "previous-name" => "nino"
      case "nino" => "address"
      case "address" => "previous-address"
      case "previous-address" => "other-address"
      case "other-address" => "open-register"
      case "open-register" => "contact"
      case "contact" => "confirmation"
      case "confirmation" => "complete"
      case "edit" => "confirmation"
      case _ => "nationality"
    }
  }

  def firstStep() = "nationality"

  def editPageFor(step:String)(implicit request: RequestHeader) = {
    step match {
      case "nationality" => html.edit.nationality(InProgressForm(request.session.getApplication))
      case "date-of-birth" => html.edit.dateOfBirth(request.session.getApplication)
      case "name" => html.edit.name(request.session.getApplication)
      case "previous-name" => html.edit.previousName(request.session.getApplication)
      case "nino" => html.edit.nino(request.session.getApplication)
      case "address" => html.edit.address(request.session.getApplication)
      case "previous-address" => html.edit.previousAddress(request.session.getApplication)
      case "other-address" => html.edit.otherAddress(request.session.getApplication)
      case "open-register" => html.edit.openRegister(request.session.getApplication)
      case "contact" => html.edit.contact(request.session.getApplication)
    }
  }

  def pageFor(step:String, form:Form[InprogressApplication])(implicit request: RequestHeader) = {
    step match {
      case "nationality" => html.steps.nationality(InProgressForm(form))
      case "date-of-birth" => html.steps.dateOfBirth(request.session.getApplication)
      case "name" => html.steps.name(request.session.getApplication)
      case "previous-name" => html.steps.previousName(request.session.getApplication)
      case "nino" => html.steps.nino(request.session.getApplication)
      case "address" => html.steps.address(request.session.getApplication)
      case "previous-address" => html.steps.previousAddress(request.session.getApplication)
      case "other-address" => html.steps.otherAddress(request.session.getApplication)
      case "open-register" => html.steps.openRegister(request.session.getApplication)
      case "contact" => html.steps.contact(request.session.getApplication)
      case "confirmation" => html.confirmation(request.session.getApplication)
    }
  }
}
