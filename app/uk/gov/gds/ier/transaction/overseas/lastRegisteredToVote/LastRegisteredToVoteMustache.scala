package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import uk.gov.gds.ier.model.LastRegisteredType
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait LastRegisteredToVoteMustache extends StepTemplate[InprogressOverseas] {

  val title = "How were you last registered to vote?"

  case class LastRegisteredModel(
      question:Question,
      registeredType:Field,
      ukResident:Field,
      armedForces:Field,
      crownServant:Field,
      britishCouncil:Field,
      notRegistered:Field
  )

  val mustache = MustacheTemplate("overseas/lastRegisteredToVote") { (form, post, back) =>

    implicit val progressForm = form

    val data = LastRegisteredModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map(_.url).getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = title
      ),
      registeredType = Field(
        classes = if(form(keys.lastRegisteredToVote.registeredType).hasErrors) {
          "invalid"
        } else ""
      ),
      ukResident = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.Ordinary.name
      ),
      armedForces = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.Forces.name
      ),
      crownServant = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.Crown.name
      ),
      britishCouncil = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.Council.name
      ),
      notRegistered = RadioField(
        key = keys.lastRegisteredToVote.registeredType,
        value = LastRegisteredType.NotRegistered.name
      )
    )

    MustacheData(data, title)
  }
}
