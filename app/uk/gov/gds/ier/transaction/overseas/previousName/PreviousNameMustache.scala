package uk.gov.gds.ier.transaction.overseas.previousName

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait PreviousNameMustache extends StepTemplate[InprogressOverseas] {

  case class PreviousNameModel(
                        question: Question,
                        previousFirstName: Field,
                        previousMiddleNames: Field,
                        previousLastName: Field,
                        nameChangeReason: Field,
                        changedNameBeforeLeavingUKFlag: Boolean
                        ) extends MustacheData

  val mustache = MustacheTemplate("overseas/previousName") { (form, post) =>

    implicit val progressForm = form

    var title = "What was your name when you left the UK?"

    var changedNameBeforeLeavingSelected = false
    if(form(keys.previousName.changedNameBeforeLeavingUKOption).value.isDefined) {
      if(form(keys.previousName.changedNameBeforeLeavingUKOption).value.getOrElse("").equals("true")) {
        //If the citizen changed their name BEFORE leaving the UK (ie. TRUE), change the page title...
        changedNameBeforeLeavingSelected = true
        title = "What was your previous name?"
      }
    }

    PreviousNameModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = form.globalErrors.map { _.message }),

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