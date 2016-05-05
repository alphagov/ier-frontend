package uk.gov.gds.ier.transaction.ordinary.soleOccupancy

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.model.SoleOccupancyOption

trait SoleOccupancyMustache extends StepTemplate[InprogressOrdinary] {

  case class SoleOccupancyModel(
                              question: Question,
                              soleOccupancyYes: Field,
                              soleOccupancyNo: Field,
                              soleOccupancyNotSure: Field,
                              soleOccupancySkipThisQuestion: Field
                              ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/soleOccupancy") { implicit lang =>
    (form, postUrl) =>

      implicit val progressForm = form

      SoleOccupancyModel(
        question = Question(
          postUrl = postUrl.url,
          number = s"11 ${Messages("step_of")} 12",
          title = Messages("ordinary_soleOccupancy_title"),
          errorMessages = Messages.translatedGlobalErrors(form)),

        soleOccupancyYes = RadioField(
          key = keys.soleOccupancy.optIn,
          value = SoleOccupancyOption.Yes.name),
        soleOccupancyNo = RadioField(
          key = keys.soleOccupancy.optIn,
          value = SoleOccupancyOption.No.name),
        soleOccupancyNotSure = RadioField(
          key = keys.soleOccupancy.optIn,
          value = SoleOccupancyOption.NotSure.name),
        soleOccupancySkipThisQuestion = RadioField(
          key = keys.soleOccupancy.optIn,
          value = SoleOccupancyOption.SkipThisQuestion.name)
      )
  }
}

