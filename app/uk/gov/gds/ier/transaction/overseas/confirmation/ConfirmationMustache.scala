package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.{InProgressForm, Key}
import uk.gov.gds.ier.model.InprogressOverseas
import controllers.step.overseas._
import org.joda.time.{YearMonth, Months}
import scala.util.Try
import uk.gov.gds.ier.logging.Logging

trait ConfirmationMustache {
  object Confirmation
    extends StepMustache
    with Logging {

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
              val yearMonth = Try (new YearMonth (
                form(keys.dateLeftUk.year).value.map(year => year.toInt).getOrElse(-1),
                form(keys.dateLeftUk.month).value.map(month => month.toInt).getOrElse(-1)
              ).toString("MMMM, yyyy")).getOrElse {
                logger.error("error parsing the date (date-left-uk step)")
                ""
              }
              "<p>"+yearMonth+"</p>"
            }
          ),
          ConfirmationQuestion(
            title = "National Insurance number",
            editLink = NinoController.ninoStep.routes.editGet.url,
            changeName = "national insurance number",
            content = ifComplete(keys.nino) {
              if(form(keys.nino.nino).value.isDefined){
                "<p>" + form(keys.nino.nino).value.getOrElse("") +"</p>"
              } else {
                "<p>I cannot provide my national insurance number because:</p>" +
                "<p>" + form(keys.nino.noNinoReason).value.getOrElse("")+"</p>"
              }
            }
          ),
          ConfirmationQuestion(
            title = "How we should contact you",
            editLink = ContactController.contactStep.routes.editGet.url,
            changeName = "how we should contact you",
            content = ifComplete(keys.contact) {
              val contactResult = new StringBuilder
              if(form(keys.contact.post.contactMe).value == Some("true")){
                contactResult ++= "<p>By post</p>"
              }
              if(form(keys.contact.phone.contactMe).value == Some("true")){
                contactResult ++= "<p>By phone: " + form(keys.contact.phone.detail).value.getOrElse("")+"</p>"
              }
              if(form(keys.contact.email.contactMe).value == Some("true")){
                contactResult ++= "<p>By email: " + form(keys.contact.email.detail).value.getOrElse("")+"</p>"
              }
              contactResult.toString()
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
