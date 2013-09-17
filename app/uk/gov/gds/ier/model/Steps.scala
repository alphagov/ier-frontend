package uk.gov.gds.ier.model

import views.html
import play.api.mvc.RequestHeader
import uk.gov.gds.ier.serialiser.WithSerialiser

trait Steps extends IerForms{
  self: WithSerialiser =>

  def nextStep(step:String) = {
    step match {
      case "nationality" => "date-of-birth"
      case "date-of-birth" => "name"
      case "name" => "previous-name"
      case "previous-name" => "nino"
      case "nino" => "confirmation"
      case "confirmation" => "complete"
      case _ => "nationality"
    }
  }

  def firstStep() = "nationality"

  def pageFor(step:String)(implicit request: RequestHeader) = {
    step match {
      case "nationality" => html.steps.nationality(request.session.getApplication)
      case "date-of-birth" => html.steps.dateOfBirth(request.session.getApplication)
      case "name" => html.steps.name(request.session.getApplication)
      case "previous-name" => html.steps.previousName(request.session.getApplication)
      case "nino" => html.steps.nino(request.session.getApplication)
      case "confirmation" => html.steps.confirmation(request.session.getApplication)
    }
  }
}
