package uk.gov.gds.ier.model

import views.html
import play.api.mvc._
import play.api.data.Form
import play.api.templates.Html

trait Steps extends IerForms {
  self: InProgressSession =>

  case class Step(page: InProgressForm => Html,
                  editPage: InProgressForm => Html,
                  validation: Form[InprogressApplication],
                  next:String)

  val nationalityStep = Step(
    form => html.steps.nationality(form),
    form => html.edit.nationality(form),
    nationalityForm,
    "date-of-birth")

  val dateOfBirthStep = Step(
    form => html.steps.dateOfBirth(form),
    form => html.edit.dateOfBirth(form),
    dateOfBirthForm,
    "name")

  val nameStep = Step(
    form => html.steps.name(form),
    form => html.edit.name(form),
    nameForm,
    "previous-name")

  val previousNameStep = Step(
    form => html.steps.previousName(form),
    form => html.edit.previousName(form),
    previousNameForm,
    "nino"
  )

  object Step {
    def apply(step:String)(block: Step => Result):Result = {
      step match {
        case "nationality" => block(nationalityStep)
        case "name" => block(nameStep)
        case "date-of-birth" => block(dateOfBirthStep)
        case "previous-name" => block(previousNameStep)
      }
    }
  }

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
      case "nino" => html.edit.nino(request.session.getApplication)
      case "address" => html.edit.address(request.session.getApplication)
      case "previous-address" => html.edit.previousAddress(request.session.getApplication)
      case "other-address" => html.edit.otherAddress(request.session.getApplication)
      case "open-register" => html.edit.openRegister(request.session.getApplication)
      case "contact" => html.edit.contact(request.session.getApplication)
    }
  }

  def validationFor(step:String) = {
    step match {
      case "nationality" => nationalityForm
    }
  }

  def pageFor(step:String, form:Form[InprogressApplication])(implicit request: RequestHeader) = {
    step match {
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
