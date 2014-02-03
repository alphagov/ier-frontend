package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.{InProgressForm, Key}
import uk.gov.gds.ier.model.InprogressOverseas
import controllers.step.overseas.routes

trait ConfirmationMustache {

  case class ConfirmationQuestion(
      content:String,
      title:String,
      editLink:String,
      changeName:String
  )

  case class ConfirmationModel(
      questions:List[ConfirmationQuestion],
      backUrl: String,
      postUrl: String
  )

  object Confirmation extends StepMustache {
    def confirmationPage(
        form:InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String) = {

      val confirmation = new ConfirmationBlocks(form)

      val foo:ConfirmationModel = ConfirmationModel(
        questions = List(
          confirmation.previouslyRegistered,
          confirmation.lastUkAddress
        ),
        backUrl = backUrl,
        postUrl = postUrl
      )

      val content = Mustache.render("overseas/confirmation", foo)
      MainStepTemplate(
        content,
        "Confirm your details - Register to vote",
        contentClasses = Some("confirmation")
      )
    }
  }

  class ConfirmationBlocks(form:InProgressForm[InprogressOverseas])
    extends StepMustache {

    def ifComplete(key:Key)(confirmationHtml:String) = {
      if (form(key).hasErrors) {
        "<div class=\"validation-message visible\">" +
          "Please complete this step" +
          "</div>"
      } else {
        confirmationHtml
      }
    }

    def previouslyRegistered = {
      ConfirmationQuestion(
        title = "Previously Registered",
        editLink = routes.PreviouslyRegisteredController.editGet.url,
        changeName = "previously registered",
        content = ifComplete(keys.previouslyRegistered) {
          if (form(keys.previouslyRegistered.hasPreviouslyRegistered).value == Some("true")) {
            "<p>I was last registered as an overseas voter</p>"
          } else {
            "<p>I wasn't last registered as an overseas voter</p>"
          }
        }
      )
    }

    def lastUkAddress = {
      ConfirmationQuestion(
        title = "Last UK Address",
        editLink = if (form(keys.lastUkAddress.manualAddress).value.isDefined) {
          routes.LastUkAddressManualController.editGet.url
        } else {
          routes.LastUkAddressSelectController.editGet.url
        },
        changeName = "your last UK address",
        content = ifComplete(keys.lastUkAddress) {
          val addressLine = form(keys.lastUkAddress.addressLine).value.orElse{
            form(keys.lastUkAddress.manualAddress).value
          }.getOrElse("")
          val postcode = form(keys.lastUkAddress.postcode).value.getOrElse("")

          s"<p>$addressLine</p><p>$postcode</p>"
        }
      )
    }
  }
}
