package uk.gov.gds.ier.transaction.ordinary.name

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait NameMustache extends StepTemplate[InprogressOrdinary] {

  val pageTitle = "What is your full name?"

  case class NameModel(
    question: Question,
    firstName: Field,
    middleNames: Field,
    lastName: Field,
    hasPreviousName: FieldSet,
    hasPreviousNameTrue: Field,
    hasPreviousNameFalse: Field,
    previousFirstName: Field,
    previousMiddleNames: Field,
    previousLastName: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("ordinary/name") { (form, post) =>
    implicit val progressForm = form

    NameModel(
      question = Question(
        postUrl = post.url,
        number = "4 of 11",
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
      firstName = TextField(
        key = keys.name.firstName),
      middleNames = TextField(
        key = keys.name.middleNames),
      lastName = TextField(
        key = keys.name.lastName),
      hasPreviousName = FieldSet(
        classes = if (form(keys.previousName).hasErrors) "invalid" else ""
      ),
      hasPreviousNameTrue = RadioField(
        key = keys.previousName.hasPreviousName, value = "true"),
      hasPreviousNameFalse = RadioField(
        key = keys.previousName.hasPreviousName, value = "false"),

      previousFirstName = TextField(
        key = keys.previousName.previousName.firstName),
      previousMiddleNames = TextField(
        key = keys.previousName.previousName.middleNames),
      previousLastName = TextField(
        key = keys.previousName.previousName.lastName)
    )
  }
}

