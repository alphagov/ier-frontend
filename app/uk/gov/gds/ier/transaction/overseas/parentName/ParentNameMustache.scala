package uk.gov.gds.ier.transaction.overseas.parentName

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait ParentNameMustache extends StepTemplate[InprogressOverseas] {

  val title = "Parent or guardian's registration details"

  case class ParentNameModel(
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
  )

  val mustache = MustacheTemplate("overseas/parentName") { (form, post) =>

    implicit val progressForm = form

    val data = ParentNameModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = form.globalErrors.map { _.message }
      ),
      firstName = TextField(
        key = keys.overseasParentName.parentName.firstName),
      middleNames = TextField(
        key = keys.overseasParentName.parentName.middleNames),
      lastName = TextField(
        key = keys.overseasParentName.parentName.lastName),
      hasPreviousName = FieldSet(
        classes = if (form(keys.overseasParentName.parentPreviousName).hasErrors) "invalid" else ""
      ),
      hasPreviousNameTrue = RadioField(
        key = keys.overseasParentName.parentPreviousName.hasPreviousName, value = "true"),
      hasPreviousNameFalse = RadioField(
        key = keys.overseasParentName.parentPreviousName.hasPreviousName, value = "false"),

      previousFirstName = TextField(
        key = keys.overseasParentName.parentPreviousName.previousName.firstName),
      previousMiddleNames = TextField(
        key = keys.overseasParentName.parentPreviousName.previousName.middleNames),
      previousLastName = TextField(
        key = keys.overseasParentName.parentPreviousName.previousName.lastName)
    )
    MustacheData(data, title)
  }
}
