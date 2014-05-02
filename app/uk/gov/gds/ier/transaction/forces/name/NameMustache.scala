package uk.gov.gds.ier.transaction.forces.name

import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait NameMustache extends StepTemplate[InprogressForces] {

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

  val mustache = MustacheTemplate("forces/name") { (form, postUrl) =>
    implicit val progressForm = form
    NameModel(
      question = Question(
        postUrl = postUrl.url,
        number = "6",
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
