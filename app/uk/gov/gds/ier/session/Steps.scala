package uk.gov.gds.ier.session

import views.html
import play.api.mvc._
import play.api.data.Form
import play.api.templates.Html
import uk.gov.gds.ier.validation.{InProgressForm, IerForms}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{Addresses, InprogressApplication}

trait Steps extends IerForms {
  self: SessionHandling with WithSerialiser =>

  case class Step(page: InProgressForm => Html,
                  editPage: InProgressForm => Html,
                  validation: Form[InprogressApplication],
                  next:String)

  val contactStep = Step(
    form => html.steps.contact(form),
    form => html.edit.contact(form),
    contactForm,
    "confirmation"
  )
  val confirmationStep = Step(
    form => html.confirmation(form),
    form => html.confirmation(form),
    inprogressForm,
    "confirmation"
  )

  object Step {
    def getStep(step:String): Step = {
      step match {
        case "contact" => contactStep
        case "confirmation" => confirmationStep
        case "edit" => confirmationStep
      }
    }
    def apply(step:String)(block: Step => Result):Result = {
      block(getStep(step))
    }
  }

  def firstStep() = "nationality"
}
