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

  val previousNameStep = Step(
    form => html.steps.previousName(form),
    form => html.edit.previousName(form),
    previousNameForm,
    "nino"
  )
  val ninoStep = Step(
    form => html.steps.nino(form),
    form => html.edit.nino(form),
    ninoForm,
    "address"
  )
  val addressStep = Step(
    form => html.steps.address(form,
      form(keys.possibleAddresses.jsonList).value match {
        case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
          Some(serialiser.fromJson[Addresses](possibleAddressJS))
        }
        case _ => None
      }
    ),
    form => html.edit.address(form),
    addressForm,
    "previous-address"
  )
  val previousAddressStep = Step(
    form => html.steps.previousAddress(form,
      form(keys.possibleAddresses.jsonList).value match {
        case Some(possibleAddressJS) if !possibleAddressJS.isEmpty => {
          Some(serialiser.fromJson[Addresses](possibleAddressJS))
        }
        case _ => None
      }
    ),
    form => html.edit.previousAddress(form),
    previousAddressForm,
    "other-address"
  )
  val otherAddressStep = Step(
    form => html.steps.otherAddress(form),
    form => html.edit.otherAddress(form),
    otherAddressForm,
    "open-register"
  )
  val openRegisterStep = Step(
    form => html.steps.openRegister(form),
    form => html.edit.openRegister(form),
    openRegisterForm,
    "postal-vote"
  )
  val postalVoteStep = Step(
    form => html.steps.postalVote(form),
    form => html.edit.postalVote(form),
    postalVoteForm,
    "contact"
  )
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
        case "nationality" => nationalityStep
        case "date-of-birth" => dateOfBirthStep
        case "previous-name" => previousNameStep
        case "nino" => ninoStep
        case "address" => addressStep
        case "previous-address" => previousAddressStep
        case "other-address" => otherAddressStep
        case "open-register" => openRegisterStep
        case "postal-vote" => postalVoteStep
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
