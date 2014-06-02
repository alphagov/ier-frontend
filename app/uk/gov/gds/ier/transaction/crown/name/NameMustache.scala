package uk.gov.gds.ier.transaction.crown.name

import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.step.StepTemplate

trait NameMustache extends StepTemplate[InprogressCrown] {

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

  val pageTitle = "What is your full name?"

  val mustache = MustacheTemplate("crown/name") { (form, postUrl) =>
    implicit val progressForm = form
    NameModel(
      question = Question(
        postUrl = postUrl.url,
        title = pageTitle,
        errorMessages = form.globalErrors.map ( _.message )
      ),
      firstName = TextField(key = keys.name.firstName),
      middleNames = TextField(key = keys.name.middleNames),
      lastName = TextField(key = keys.name.lastName),
      hasPreviousName = FieldSet(
        classes = if (form(keys.previousName).hasErrors) "invalid" else ""
      ),
      hasPreviousNameTrue = RadioField(
        key = keys.previousName.hasPreviousName,
        value = "true"
      ),
      hasPreviousNameFalse = RadioField(
        key = keys.previousName.hasPreviousName,
        value = "false"
      ),
      previousFirstName = TextField(
        key = keys.previousName.previousName.firstName
      ),
      previousMiddleNames = TextField(
        key = keys.previousName.previousName.middleNames
      ),
      previousLastName = TextField(
        key = keys.previousName.previousName.lastName
      )
    )
  }
}
