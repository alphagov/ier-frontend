package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.{
  WaysToVoteType,
  InprogressOverseas,
  LastRegisteredType,
  DOB,
  DateLeft}
import controllers.step.overseas._
import controllers.routes.RegisterToVoteController
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.validation.{Key, ErrorTransformForm, DateValidator}
import org.joda.time.{YearMonth, Years, LocalDate}
import scala.util.Try
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkStep
import play.api.Logger
import uk.gov.gds.ier.transaction.overseas.confirmation.blocks.{
  ConfirmationQuestion,
  ConfirmationBlocks}

trait ConfirmationMustache {

  case class ErrorModel(
      startUrl: String
  )

  case class ConfirmationModel(
      applicantDetails: List[ConfirmationQuestion],
      parentDetails: List[ConfirmationQuestion],
      displayParentBlock: Boolean,
      backUrl: String,
      postUrl: String
  )

  object Confirmation extends StepMustache {
    def confirmationPage(
        form: ErrorTransformForm[InprogressOverseas],
        backUrl: String,
        postUrl: String) = {

      val confirmation = new ConfirmationBlocks(form)
      val parentData = confirmation.parentBlocks()
      val applicantData = confirmation.applicantBlocks()

      val data = ConfirmationModel(
        parentDetails = parentData,
        applicantDetails = applicantData,
        displayParentBlock = !parentData.isEmpty,
        backUrl = backUrl,
        postUrl = postUrl
      )

      val content = Mustache.render("overseas/confirmation", data)

      MainStepTemplate(
        content,
        "Confirm your details - Register to vote",
        contentClasses = Some("confirmation")
      )
    }
  }
}
