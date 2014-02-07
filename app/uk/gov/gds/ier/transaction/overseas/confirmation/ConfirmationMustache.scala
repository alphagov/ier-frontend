package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.{WaysToVoteType, InprogressOverseas}
import controllers.step.overseas._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.validation.Key
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some
import org.joda.time.YearMonth
import scala.util.Try
import uk.gov.gds.ier.logging.Logging

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

      val data = ConfirmationModel(
        questions = List(
          confirmation.dateOfBirth,
          confirmation.previouslyRegistered,
          confirmation.lastUkAddress,
          confirmation.dateLeftUk,
          confirmation.nino,
          confirmation.openRegister,
          confirmation.name,
          confirmation.previousName,
          confirmation.contact,
          confirmation.waysToVote
        ),
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

  class ConfirmationBlocks(form:InProgressForm[InprogressOverseas])
    extends StepMustache with Logging {

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

    def dateLeftUk = {
      ConfirmationQuestion(
        title = "Date you left the UK",
        editLink = routes.DateLeftUkController.editGet.url,
        changeName = "date you left the UK",
        content = ifComplete(keys.dateLeftUk) {
          val yearMonth = Try (new YearMonth (
            form(keys.dateLeftUk.year).value.map(year => year.toInt).getOrElse(-1),
            form(keys.dateLeftUk.month).value.map(month => month.toInt).getOrElse(-1)
          ).toString("MMMM, yyyy")).getOrElse {
            logger.error("error parsing the date (date-left-uk step)")
            ""
          }
          s"<p>$yearMonth</p>"
        }
      )
    }

    def nino = {
      ConfirmationQuestion(
        title = "National Insurance number",
        editLink = routes.NinoController.editGet.url,
        changeName = "national insurance number",
        content = ifComplete(keys.nino) {
          if(form(keys.nino.nino).value.isDefined){
            s"<p>${form(keys.nino.nino).value.getOrElse("")}</p>"
          } else {
            "<p>I cannot provide my national insurance number because:</p>" +
              s"<p>${form(keys.nino.noNinoReason).value.getOrElse("")}</p>"
          }
        }
      )
    }

    def dateOfBirth = {
      ConfirmationQuestion(
        title = "What is your date of birth?",
        editLink = DateOfBirthController.dateOfBirthStep.routes.editGet.url,
        changeName = "date of birth",
        content = ifComplete(keys.dob) {
                "<p>" + form(keys.dob.day).value.get + " "  + DateOfBirthConstants.monthsByNumber(form(keys.dob.month).value.get) + " " + form(keys.dob.year).value.get + "</p>"
            }
      )
    }
    def openRegister = {
      ConfirmationQuestion(
        title = "Open register",
        editLink = routes.OpenRegisterController.editGet.url,
        changeName = "open register",
        content = ifComplete(keys.openRegister) {
          if(form(keys.openRegister.optIn).value == Some("true")){
            "<p>I want to include my details on the open register</p>"
          }else{
            "<p>I don’t want to include my details on the open register</p>"
          }
        }
      )
    }

    def name = {
      ConfirmationQuestion(
        title = "What is your full name?",
        editLink = routes.NameController.editGet.url,
        changeName = "full name",
        content = ifComplete(keys.name) {
          List(
            form(keys.name.firstName).value,
            form(keys.name.middleNames).value,
            form(keys.name.lastName).value).flatten
            .mkString("<p>", " ", "</p>")
        }
      )
    }

    def previousName = {
      ConfirmationQuestion(
        title = "What is your previous name?",
        editLink = routes.NameController.editGet.url,
        changeName = "previous name",
        content = ifComplete(keys.previousName) {
          if (form(keys.previousName.hasPreviousName).value == Some("true")) {
            List(
              form(keys.previousName.previousName.firstName).value,
              form(keys.previousName.previousName.middleNames).value,
              form(keys.previousName.previousName.lastName).value
            ).flatten.mkString("<p>", " ", "</p>")
          } else {
            "<p>I have not changed my name in the last 12 months</p>"
          }
        }
      )
    }

    def contact = {
      ConfirmationQuestion(
        title = "How we should contact you",
        editLink = ContactController.contactStep.routes.editGet.url,
        changeName = "how we should contact you",
        content = ifComplete(keys.contact) {
          val post = if(form(keys.contact.post.contactMe).value == Some("true")){
            "<p>By post</p>"
          } else ""

          val phone = if(form(keys.contact.phone.contactMe).value == Some("true")){
            s"<p>By phone: ${form(keys.contact.phone.detail).value.getOrElse("")}</p>"
          } else ""

          val email = if(form(keys.contact.email.contactMe).value == Some("true")){
            s"<p>By email: ${form(keys.contact.email.detail).value.getOrElse("")}</p>"
          } else ""

          s"$post $phone $email"
        }
      )
    }

    def waysToVote = {
      ConfirmationQuestion(
        title = "How do you want to vote",
        editLink = routes.WaysToVoteController.editGet.url,
        changeName = "way to vote",
        content = ifComplete(keys.waysToVote) {
          form(keys.waysToVote.wayType).value match {
            case Some(wayToVote) => {
              val wayToVoteLabel = WaysToVoteType.withName(wayToVote) match {
                case WaysToVoteType.ByPost => "By post"
                case WaysToVoteType.ByProxy => "By proxy (someone else voting for you)"
                case WaysToVoteType.InPerson => "In the UK, at a polling station"
              }
              s"<p>$wayToVoteLabel</p>"
            }
            case None => "???"
          }
        }
      )
    }
  }
}
