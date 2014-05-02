package uk.gov.gds.ier.transaction.overseas.name

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait NameMustache extends StepTemplate[InprogressOverseas] {

  val title = "What is your full name?"

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

  val mustache = MustacheTemplate("overseas/name") { (form, post) =>

    implicit val progressForm = form

    NameModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = form.globalErrors.map { _.message }),
      firstName = TextField(
        key = keys.overseasName.name.firstName),
      middleNames = TextField(
        key = keys.overseasName.name.middleNames),
      lastName = TextField(
        key = keys.overseasName.name.lastName),
      hasPreviousName = FieldSet(
        classes = if (form(keys.overseasName.previousName).hasErrors) "invalid" else ""
      ),
      hasPreviousNameTrue = RadioField(
        key = keys.overseasName.previousName.hasPreviousName, value = "true"),
      hasPreviousNameFalse = RadioField(
        key = keys.overseasName.previousName.hasPreviousName, value = "false"),

      previousFirstName = TextField(
        key = keys.overseasName.previousName.previousName.firstName),
      previousMiddleNames = TextField(
        key = keys.overseasName.previousName.previousName.middleNames),
      previousLastName = TextField(
        key = keys.overseasName.previousName.previousName.lastName)
    )
  }
}
