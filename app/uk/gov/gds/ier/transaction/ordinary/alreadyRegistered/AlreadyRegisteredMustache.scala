package uk.gov.gds.ier.transaction.ordinary.alreadyRegistered

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait AlreadyRegisteredMustache extends StepTemplate[InprogressOrdinary] {

  case class AlreadyRegisteredModel(
                                question:Question
                                ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/alreadyRegistered") { implicit lang =>
    (form, postEndpoint) =>

      implicit val progressForm = form

      AlreadyRegisteredModel(
        question = Question(
          postUrl = postEndpoint.url,
          errorMessages = Messages.translatedGlobalErrors(form),
          title = Messages("ordinary_alreadyRegistered_title")
        )
      )
  }
}

