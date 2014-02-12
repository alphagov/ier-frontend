package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.LastRegisteredType

trait LastRegisteredToVoteMustache {

  object LastRegisteredToVoteMustache extends StepMustache {
    case class LastRegisteredModel(
        question:Question,
        registeredType:Field,
        ukResident:Field,
        armedForces:Field,
        crownServant:Field,
        britishCouncil:Field,
        notRegistered:Field
    )

    def lastRegisteredData(
        form:ErrorTransformForm[InprogressOverseas],
        postEndpoint: Call,
        backEndpoint: Option[Call]) = {

      implicit val progressForm = form

      LastRegisteredModel(
        question = Question(
          postUrl = postEndpoint.url,
          backUrl = backEndpoint.map(_.url).getOrElse(""),
          errorMessages = form.globalErrors.map{ _.message },
          number = "?",
          title = "How were you last registered to vote?"
        ),
        registeredType = Field(
          classes = if(form(keys.lastRegisteredToVote.registeredType.key).hasErrors) {
            "invalid"
          } else ""
        ),
        ukResident = RadioField(
          key = keys.lastRegisteredToVote.registeredType,
          value = LastRegisteredType.UK.toString
        ),
        armedForces = RadioField(
          key = keys.lastRegisteredToVote.registeredType,
          value = LastRegisteredType.Army.toString
        ),
        crownServant = RadioField(
          key = keys.lastRegisteredToVote.registeredType,
          value = LastRegisteredType.Crown.toString
        ),
        britishCouncil = RadioField(
          key = keys.lastRegisteredToVote.registeredType,
          value = LastRegisteredType.Council.toString
        ),
        notRegistered = RadioField(
          key = keys.lastRegisteredToVote.registeredType,
          value = LastRegisteredType.NotRegistered.toString
        )
      )
    }

    def lastRegisteredPage(
        form: ErrorTransformForm[InprogressOverseas],
        postEndpoint: Call,
        backEndpoint: Option[Call]) = {
      val data = lastRegisteredData(form, postEndpoint, backEndpoint)
      val content = Mustache.render("overseas/lastRegisteredToVote", data)
      MainStepTemplate(content, data.question.title)
    }
  }
}
