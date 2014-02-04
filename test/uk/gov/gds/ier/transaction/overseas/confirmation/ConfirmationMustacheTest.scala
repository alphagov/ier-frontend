package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{PreviousName, Name, InprogressOverseas}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{InProgressForm, ErrorMessages, FormKeys}
import uk.gov.gds.guice.GuiceContainer

class ConfirmationMustacheTest
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  GuiceContainer.initialize()

  val confirmationMustache = new ConfirmationMustache {}

  "In-progress application form with filled name and previous name" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        previousName = Some(Name(
          firstName = "Jan",
          middleNames = None,
          lastName = "Kovar"))
      ))
    ))
    val model = confirmationMustache.Confirmation.confirmationModel(
      InProgressForm(partiallyFilledApplicationForm),
      "http://backUrl",
      "http://postUrl")

    filterRelevant(model, "/register-to-vote/overseas/edit/name") should be("" +
      "<p>John Smith</p>|<p>Jan Kovar</p>")
  }


  "In-progress application form with filled name and previous name with middle names" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      name = Some(Name(
        firstName = "John",
        middleNames = Some("Walker Junior"),
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        previousName = Some(Name(
          firstName = "Jan",
          middleNames = Some("Janko Janik"),
          lastName = "Kovar"))
      ))
    ))
    val model = confirmationMustache.Confirmation.confirmationModel(
      InProgressForm(partiallyFilledApplicationForm),
      "http://backUrl",
      "http://postUrl")

    filterRelevant(model, "/register-to-vote/overseas/edit/name") should be("" +
      "<p>John Walker Junior Smith</p>|<p>Jan Janko Janik Kovar</p>")
  }

  def filterRelevant(model: confirmationMustache.Confirmation.ConfirmationModel, url: String): String = {
    model.questions.filter(_.editLink == url).map(_.content).mkString("", "|", "")
  }
}