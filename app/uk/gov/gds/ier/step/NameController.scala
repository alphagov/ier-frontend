package uk.gov.gds.ier.step

import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.controller.StepController
import uk.gov.gds.ier.validation.{InProgressForm, ErrorTransformer, WithErrorTransformer}
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import com.google.inject.Inject
import uk.gov.gds.ier.model.InprogressApplication
import play.api.mvc.{Call, SimpleResult}
import play.api.templates.Html
import play.api.data.Form
import play.api.data.Forms._
import controllers._
import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.validation.ErrorMessages
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.validation.InProgressForm

class NameController @Inject ()(val serialiser: JsonSerialiser,
                                val errorTransformer: ErrorTransformer) extends StepController
                                                                        with WithSerialiser
                                                                        with WithErrorTransformer
                                                                        with ErrorMessages
                                                                        with FormKeys
                                                                        with NameForms {

  val validation: Form[InprogressApplication] = nameForm
  val editPostRoute: Call = step.routes.NameController.editPost
  val stepPostRoute: Call = step.routes.NameController.post

  def template(form:InProgressForm, call:Call): Html = views.html.steps.name(form, call)
  def goToNext(currentState: InprogressApplication): SimpleResult = Redirect(routes.RegisterToVoteController.registerStep("previous-name"))
}

trait NameForms {
  self:  FormKeys
    with ErrorMessages =>

  val nameMapping = mapping(
    keys.firstName.key -> optional(text.verifying(firstNameMaxLengthError, _.size <= maxTextFieldLength))
      .verifying("Please enter your first name", _.nonEmpty),
    keys.middleNames.key -> optional(nonEmptyText.verifying(middleNameMaxLengthError, _.size <= maxTextFieldLength)),
    keys.lastName.key -> optional(text.verifying(lastNameMaxLengthError, _.size <= maxTextFieldLength))
      .verifying("Please enter your last name", _.nonEmpty)
  ) (
    (firstName, middleName, lastName) => Name(firstName.get, middleName, lastName.get)
  ) (
    name => Some(Some(name.firstName), name.middleNames, Some(name.lastName))
  )

  val nameForm = Form(
    mapping(
      keys.name.key -> optional(nameMapping)
        .verifying("Please enter your full name", _.isDefined)
    ) (
      name => InprogressApplication(name = name)
    ) (
      inprogress => Some(inprogress.name)
    )
  )
}
