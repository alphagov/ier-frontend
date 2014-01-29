package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import controllers.step.overseas._
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.validation.Key
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some

trait ConfirmationMustache {

  object Confirmation extends StepMustache {

    case class ConfirmationQuestion(content: String,
                                    title: String,
                                    editLink: String,
                                    changeName: String)

    case class ConfirmationModel(questions: List[ConfirmationQuestion],
                                 backUrl: String,
                                 postUrl: String)

    def confirmationModel(form: InProgressForm[InprogressOverseas],
                          backUrl: String,
                          postUrl: String) = {
      def ifComplete(key: Key)(confirmationHtml: String) = {
        if (form(key).hasErrors) {
          "<div class=\"validation-message visible\">Please complete this step</div>"
        } else {
          confirmationHtml
        }
      }

      ConfirmationModel(
        questions = List(
          ConfirmationQuestion(
            title = "Previously Registered",
            editLink = PreviouslyRegisteredController.previouslyRegisteredStep.routes.editGet.url,
            changeName = "previously registered",
            content = ifComplete(keys.previouslyRegistered) {
              if (form(keys.previouslyRegistered.hasPreviouslyRegistered).value == Some("true")) {
                "<p>I was last registered as an overseas voter</p>"
              } else {
                "<p>I wasn't last registered as an overseas voter</p>"
              }
            }
          ),
          ConfirmationQuestion(
            title = "What is your full name?",
            editLink = NameController.nameStep.routes.editGet.url,
            changeName = "full name",
            content = ifComplete(keys.name) {
              List(
                form(keys.name.firstName).value,
                form(keys.name.middleNames).value,
                form(keys.name.lastName).value).flatten
                .mkString("<p>", " ", "</p>")
            }
          ),
          ConfirmationQuestion(
            title = "What is your previous name?",
            editLink = NameController.nameStep.routes.editGet.url,
            changeName = "previous name",
            content = ifComplete(keys.previousName) {
              if (form(keys.previousName.hasPreviousName).value == Some("true")) {
                List(
                  form(keys.previousName.previousName.firstName).value,
                  form(keys.previousName.previousName.middleNames).value,
                  form(keys.previousName.previousName.lastName).value).flatten
                  .mkString("<p>", " ", "</p>")
              } else {
                "<p>I have not changed my name in the last 12 months</p>"
              }
            }
          )
        ),
        backUrl = backUrl,
        postUrl = postUrl
      )
    }

    def confirmationPage(form: InProgressForm[InprogressOverseas],
                         backUrl: String,
                         postUrl: String) = {
      val content = Mustache.render("overseas/confirmation", confirmationModel(form, backUrl, postUrl))
      MainStepTemplate(content, "Confirm your details - Register to vote", contentClasses = Some("confirmation"))
    }
  }
}
