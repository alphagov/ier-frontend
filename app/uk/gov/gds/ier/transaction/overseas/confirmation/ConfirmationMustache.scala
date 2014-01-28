package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.{InProgressForm, Key}
import uk.gov.gds.ier.model.InprogressOverseas
import controllers.step.overseas._
import org.joda.time.{YearMonth, Months}

trait ConfirmationMustache {
  object Confirmation extends StepMustache {

    case class ConfirmationQuestion(content:String,
                                    title:String,
                                    editLink:String,
                                    changeName:String)

    case class ConfirmationModel(questions:List[ConfirmationQuestion],
                                 backUrl: String,
                                 postUrl: String)

    def confirmationPage(form:InProgressForm[InprogressOverseas],
                         backUrl: String,
                         postUrl: String) = {


      def ifComplete(key:Key)(confirmationHtml:String) = {
        if (form(key).hasErrors) {
          "<div class=\"validation-message visible\">Please complete this step</div>"
        } else {
          confirmationHtml
        }
      }

      val foo:ConfirmationModel = ConfirmationModel(
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
            title = "Date you left the UK",
            editLink = DateLeftUkController.dateLeftUkStep.routes.editGet.url,
            changeName = "date you left the UK",
            content = ifComplete(keys.dateLeftUk) {
              val yearMonth = new YearMonth (
                form(keys.dateLeftUk.year).value.map(year => year.toInt).getOrElse(-1),
                form(keys.dateLeftUk.month).value.map(month => month.toInt).getOrElse(-1)
              ).toString("MMMM, yyyy")
              "<p>"+yearMonth+"</p>"
            }
          )
        ),
        backUrl = backUrl,
        postUrl = postUrl
      )

      val content = Mustache.render("overseas/confirmation", foo)
      MainStepTemplate(content, "Confirm your details - Register to vote", contentClasses = Some("confirmation"))
    }
  }
}
