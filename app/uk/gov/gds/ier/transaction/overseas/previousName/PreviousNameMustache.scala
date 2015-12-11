package uk.gov.gds.ier.transaction.overseas.previousName

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait PreviousNameMustache extends StepTemplate[InprogressOverseas] {

  case class PreviousNameModel(
                        question: Question,
                        firstName: Field,
                        middleNames: Field,
                        lastName: Field,
                        hasPreviousNameOption: FieldSet,
                        hasPreviousNameOptionFalse: Field,
                        hasPreviousNameOptionTrue: Field,
                        hasPreviousNameOptionOther: Field,
                        previousFirstName: Field,
                        previousMiddleNames: Field,
                        previousLastName: Field,
                        nameChangeReason: Field,
                        changedNameBeforeLeavingUKFlag: Boolean
                        ) extends MustacheData

  val mustache = MustacheTemplate("overseas/previousName") { (form, post) =>

    implicit val progressForm = form

    var title = "What was your previous name?"

    var changedNameBeforeLeavingSelected = false

    if (form(keys.previousName.changedNameBeforeLeavingUKOption).value.isDefined) {
      if (form(keys.previousName.changedNameBeforeLeavingUKOption).value.get == "true") {
        changedNameBeforeLeavingSelected = true
      }
    }

    //If the citizen changed their name AFTER leaving the UK (ie. FALSE), change the page title...
    if (!changedNameBeforeLeavingSelected) {
      title = "What was your name when you left the UK?"
    }

    PreviousNameModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = form.globalErrors.map { _.message }),

      firstName = TextField(key = keys.name.firstName),
      middleNames = TextField(key = keys.name.middleNames),
      lastName = TextField(key = keys.name.lastName),

      hasPreviousNameOption = FieldSet(
        classes = if (form(keys.previousName).hasErrors) "invalid" else ""
      ),
      hasPreviousNameOptionFalse = RadioField(
        key = keys.previousName.hasPreviousNameOption, value = "false"),
      hasPreviousNameOptionTrue = RadioField(
        key = keys.previousName.hasPreviousNameOption, value = "true"),
      hasPreviousNameOptionOther = RadioField(
        key = keys.previousName.hasPreviousNameOption, value = "other"),

      previousFirstName = TextField(
        key = keys.previousName.previousName.firstName),
      previousMiddleNames = TextField(
        key = keys.previousName.previousName.middleNames),
      previousLastName = TextField(
        key = keys.previousName.previousName.lastName),
      nameChangeReason = TextField(
        key = keys.previousName.reason),

      changedNameBeforeLeavingUKFlag = changedNameBeforeLeavingSelected
    )
  }
}