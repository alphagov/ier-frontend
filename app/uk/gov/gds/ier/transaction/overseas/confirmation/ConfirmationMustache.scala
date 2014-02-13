package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.{WaysToVoteType, InprogressOverseas, LastRegisteredType}
import controllers.step.overseas._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.validation.Key
import uk.gov.gds.ier.validation.InProgressForm
import org.joda.time.{YearMonth, Months, LocalDate}
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
          confirmation.address,
          confirmation.openRegister,
          confirmation.name,
          confirmation.previousName,
          confirmation.contact,
          confirmation.waysToVote,
          confirmation.postalVote,
          confirmation.contact
        ) ++ confirmation.passport.toList,
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

    val completeThisStepMessage = "<div class=\"validation-message visible\">" +
      "Please complete this step" +
      "</div>"

    def ifComplete(key:Key)(confirmationHtml: => String) = {
      if (form(key).hasErrors) {
        completeThisStepMessage
      } else {
        confirmationHtml
      }
    }

    def previouslyRegistered = {
      val renewer = form(keys.previouslyRegistered.hasPreviouslyRegistered).value == Some("true")
      val prevRegType = Try {
        form(keys.lastRegisteredToVote.registeredType).value.map { regType =>
          LastRegisteredType.parse(regType)
        }
      }.getOrElse(None)

      val iAm = "I was last registered as"

      val previouslyRegisteredContent = (renewer, prevRegType) match {
        case (true, _) => "an overseas voter"
        case (_, Some(LastRegisteredType.UK)) => s"<p>$iAm a UK resident</p>"
        case (_, Some(LastRegisteredType.Army)) => s"<p>$iAm a member of the armed forces</p>"
        case (_, Some(LastRegisteredType.Crown)) => s"<p>$iAm a Crown servant</p>"
        case (_, Some(LastRegisteredType.Council)) => s"<p>$iAm a British council employee</p>"
        case (_, Some(LastRegisteredType.NotRegistered)) => "<p>I have never been registered</p>"
        case _ => completeThisStepMessage
      }

      ConfirmationQuestion(
        title = "Previously Registered",
        editLink = routes.PreviouslyRegisteredController.editGet.url,
        changeName = "previously registered",
        content = ifComplete(keys.previouslyRegistered) {
          previouslyRegisteredContent
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

    def address = {
      ConfirmationQuestion(
        title = "Where do you live?",
        editLink = AddressController.addressStep.routes.editGet.url,
        changeName = "where do you live?",
        content = ifComplete(keys.overseasAddress) {
          "<p>" + form (keys.overseasAddress.overseasAddressDetails).value.getOrElse("") + "</p>" +
          "<p>" + form (keys.overseasAddress.country).value.getOrElse("") + "</p>"
        }
      )
    }

    def dateOfBirth = {
      ConfirmationQuestion(
        title = "What is your date of birth?",
        editLink = DateOfBirthController.dateOfBirthStep.routes.editGet.url,
        changeName = "date of birth",
        content = ifComplete(keys.dob) {
                "<p>" + form(keys.dob.day).value.get + " "  +
                DateOfBirthConstants.monthsByNumber(form(keys.dob.month).value.get) + " " +
                form(keys.dob.year).value.get + "</p>"
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

    def postalVote = {
      ConfirmationQuestion(
        title = "Application form",
        editLink = form(keys.postalOrProxyVote.voteType).value match {
            case Some("postal") =>  PostalVoteController.postalVoteStep.routes.editGet.url
            case Some("proxy") =>   ProxyVoteController.proxyVoteStep.routes.editGet.url
            case _ => throw new IllegalArgumentException()
        },
        changeName = "application form",
        content = ifComplete(keys.postalOrProxyVote) {
          val wayToVote = form(keys.postalOrProxyVote.voteType).value.getOrElse("")
          if(form(keys.postalOrProxyVote.optIn).value == Some("true")){
            if(form(keys.postalOrProxyVote.deliveryMethod.methodName).value == Some("email")){
              "<p>Please email a "+wayToVote+" vote application form to:<br/>"+
                form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value.getOrElse("")+"</p>"
            }else{
              "<p>Please post me a "+wayToVote+" vote application form</p>"
            }
          }else{
            "<p>I do not need a "+wayToVote+" vote application form</p>"
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

    def passport:Option[ConfirmationQuestion] = {
      val isRenewer = Some("true")
      val notRenewer = Some("false")
      val hasPassport = Some("true")
      val noPassport = Some("false")
      val bornInUk = Some("true")
      val notBornInUk = Some("false")
      val notBornBefore1983 = Some(false)

      val jan1st1983 = new LocalDate()
        .withYear(1983)
        .withMonthOfYear(1)
        .withDayOfMonth(1)

      val dob = for(
        day <- form(keys.dob.day).value;
        month <- form(keys.dob.month).value;
        year <- form(keys.dob.year).value
      ) yield {
        new LocalDate()
          .withYear(year.toInt)
          .withMonthOfYear(month.toInt)
          .withDayOfMonth(day.toInt)
      }

      val before1983 = dob map { dateOfBirth =>
        dateOfBirth.isBefore(jan1st1983)
      }

      val renewer = form(keys.previouslyRegistered.hasPreviouslyRegistered).value
      val passport = form(keys.passport.hasPassport).value
      val birth = form(keys.passport.bornInsideUk).value

      (renewer, passport, birth, before1983) match {
        case (`isRenewer`, _, _, _) => None
        case (`notRenewer`, `hasPassport`, _, _) => Some(passportDetails)
        case (`notRenewer`, `noPassport`, `notBornInUk`, _) => Some(citizenDetails)
        case (`notRenewer`, `noPassport`, `bornInUk`, `notBornBefore1983`) => Some(citizenDetails)
        case _ => Some(
          ConfirmationQuestion(
            title = "British Passport Details",
            editLink = routes.PassportCheckController.editGet.url,
            changeName = "your passport details",
            content = completeThisStepMessage
          )
        )
      }
    }

    def citizenDetails = {
      val howBecameCitizen = form(keys.passport.citizenDetails.howBecameCitizen).value
      val dateBecameCitizen = for (
        day <- form(keys.passport.citizenDetails.dateBecameCitizen.day).value;
        month <- form(keys.passport.citizenDetails.dateBecameCitizen.month).value;
        year <- form(keys.passport.citizenDetails.dateBecameCitizen.year).value
      ) yield s"$day $month $year"

      val citizenContent = for (
        how <- howBecameCitizen;
        date <- dateBecameCitizen
      ) yield {
        s"<p>How you became a citizen: $how</p>"+
          s"<p>Date you became a citizen: $date</p>"
      }

      val route = if(form(keys.passport).hasErrors) {
        routes.PassportCheckController.editGet
      } else {
        routes.CitizenDetailsController.editGet
      }

      ConfirmationQuestion(
        title = "British Citizenship Details",
        editLink = route.url,
        changeName = "your citizenship details",
        content = ifComplete(keys.passport) { citizenContent.getOrElse(completeThisStepMessage) }
      )
    }

    def passportDetails = {
      val passportNumber = form(keys.passport.passportDetails.passportNumber).value
      val authority = form(keys.passport.passportDetails.authority).value
      val issueDate = for(
        day <- form(keys.passport.passportDetails.issueDate.day).value;
        month <- form(keys.passport.passportDetails.issueDate.month).value;
        year <- form(keys.passport.passportDetails.issueDate.year).value
      ) yield s"$day $month $year"

      val passportContent = for(
        num <- passportNumber;
        auth <- authority;
        date <- issueDate
      ) yield {
        s"<p>Passport Number: $num</p>" +
          s"<p>Authority: $auth</p>" +
          s"<p>Issue Date: $date</p>"
      }

      val route = if(form(keys.passport).hasErrors) {
        routes.PassportCheckController.editGet
      } else {
        routes.PassportDetailsController.editGet
      }

      ConfirmationQuestion(
        title = "British Passport Details",
        editLink = route.url,
        changeName = "your passport details",
        content = ifComplete(keys.passport) { passportContent.getOrElse(completeThisStepMessage) }
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
            case None => ""
          }
        }
      )
    }
  }
}
