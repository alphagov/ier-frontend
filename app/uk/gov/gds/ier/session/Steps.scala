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

  object Step {
    def getStep(step:String): Step = ???

    def apply(step:String)(block: Step => Result):Result = {
      block(getStep(step))
    }
  }

  def firstStep() = "nationality"
}
