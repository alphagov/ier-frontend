package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.{WaysToVoteType, InprogressOverseas, LastRegisteredType}
import controllers.step.overseas._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.validation.Key
import uk.gov.gds.ier.validation.InProgressForm
import org.joda.time.{YearMonth, Years, LocalDate}
import scala.util.Try
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.validation.DateValidator
import uk.gov.gds.ier.model.DOB
import uk.gov.gds.ier.model.DateLeft
import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkStep
import play.api.Logger

trait ConfirmationMustache {

  case class ConfirmationQuestion(
      content: String,
      title: String,
      editLink: String,
      changeName: String
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
        form: InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String) = {

      val confirmation = new ConfirmationBlocks(form)

      val parentData = List(
        confirmation.parentName,
        confirmation.parentPreviousName,
        confirmation.parentsAddress
      ).flatten

      val applicantData = List(
        confirmation.previouslyRegistered,
        confirmation.dateLeft,
        confirmation.lastUkAddress,
        confirmation.passport,
        confirmation.name,
        confirmation.previousName,
        confirmation.dateOfBirth,
        confirmation.nino,
        confirmation.address,
        confirmation.openRegister,
        confirmation.waysToVote,
        confirmation.postalOrProxyVote,
        confirmation.contact
      ).flatten

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

  class ConfirmationBlocks(form:InProgressForm[InprogressOverseas])
    extends StepMustache with Logging with OverseasFormImplicits {

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

      val iWas = "I was last registered as"

      val previouslyRegisteredContent = (renewer, prevRegType) match {
        case (true, _) => s"<p>$iWas an overseas voter</p>"
        case (_, Some(LastRegisteredType.Ordinary)) => s"<p>$iWas a UK resident</p>"
        case (_, Some(LastRegisteredType.Forces)) => s"<p>$iWas a member of the armed forces</p>"
        case (_, Some(LastRegisteredType.Crown)) => s"<p>$iWas a Crown servant</p>"
        case (_, Some(LastRegisteredType.Council)) => s"<p>$iWas a British council employee</p>"
        case (_, Some(LastRegisteredType.NotRegistered)) => "<p>I have never been registered</p>"
        case _ => completeThisStepMessage
      }

      val editCall = if(prevRegType.isDefined) {
        routes.LastRegisteredToVoteController.editGet
      } else {
        routes.PreviouslyRegisteredController.editGet
      }

      Some(ConfirmationQuestion(
        title = "Previously Registered",
        editLink = editCall.url,
        changeName = "previously registered",
        content = previouslyRegisteredContent
      ))
    }

    def lastUkAddress = {
      Some(ConfirmationQuestion(
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
      ))
    }
    
    def dateLeft = {
      val wasRegisteredOverseas = Some("true")
      val prevOverseas = form(keys.previouslyRegistered.hasPreviouslyRegistered).value
      val prevRegType = Try {
        form(keys.lastRegisteredToVote.registeredType).value.map { regType =>
          LastRegisteredType.parse(regType)
        }
      }.getOrElse(None)

      (prevRegType, prevOverseas) match {
        case (_, `wasRegisteredOverseas`) => dateLeftUk
        case (Some(LastRegisteredType.Ordinary), _) => dateLeftUk
        case (Some(LastRegisteredType.Forces), _) => dateLeftArmy
        case (Some(LastRegisteredType.Crown), _) => dateLeftCrown
        case (Some(LastRegisteredType.Council), _) => dateLeftCouncil
        case (Some(LastRegisteredType.NotRegistered), _) => dateLeftUk
        case _ => None
      }
    }

    def parentsAddress = {
      val dateLeft = for(
        month <- form(keys.dateLeftUk.month).value;
        year <- form(keys.dateLeftUk.year).value
      ) yield {
        new YearMonth().withYear(year.toInt).withMonthOfYear(month.toInt)
      }
      val dob = for(
        day <- form(keys.dob.day).value;
        month <- form(keys.dob.month).value;
        year <- form(keys.dob.year).value
      ) yield {
        new YearMonth().withYear(year.toInt).withMonthOfYear(month.toInt)
      }
      val ageWhenLeft = for(
        whenLeft <- dateLeft;
        dateOfBirth <- dob
      ) yield {
        Years.yearsBetween(dateOfBirth, whenLeft).getYears()
      }
      if (ageWhenLeft.exists(_ < 18)) {
        Some(ConfirmationQuestion(
          title = "Parents Last UK Address",
          editLink = if (form(keys.parentsAddress.manualAddress).value.isDefined) {
            routes.ParentsAddressManualController.editGet.url
          } else if (form(keys.parentsAddress.uprn).value.isDefined) {
            routes.ParentsAddressSelectController.editGet.url
          } else {
            routes.ParentsAddressController.editGet.url
          },
          changeName = "your parents' last UK address",
          content = ifComplete(keys.parentsAddress) {
            val addressLine = form(keys.parentsAddress.addressLine).value.orElse{
              form(keys.parentsAddress.manualAddress).value
            }.getOrElse("")
            val postcode = form(keys.parentsAddress.postcode).value.getOrElse("")
            s"<p>$addressLine</p><p>$postcode</p>"
          }
        ))
      } else {
        None
      }
    }

    def dateLeftUk = {
      Some(ConfirmationQuestion(
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
      ))
    }

    def dateLeftArmy = {
      Some(ConfirmationQuestion(
        title = "Date you cease to be a member of the armed forces",
        editLink = routes.DateLeftArmyController.editGet.url,
        changeName = "date you cease to be a member of the armed forces",
        content = ifComplete(keys.dateLeftSpecial) {
          val yearMonth = Try (new YearMonth (
            form(keys.dateLeftSpecial.year).value.map(year => year.toInt).getOrElse(-1),
            form(keys.dateLeftSpecial.month).value.map(month => month.toInt).getOrElse(-1)
          ).toString("MMMM, yyyy")).getOrElse {
            logger.error("error parsing the date (date-left-army step)")
            ""
          }
          s"<p>$yearMonth</p>"
        }
      ))
    }    
    
    def dateLeftCrown = {
      Some(ConfirmationQuestion(
        title = "Date you cease to be a Crown Servant",
        editLink = routes.DateLeftCrownController.editGet.url,
        changeName = "date you cease to be a Crown Servant",
        content = ifComplete(keys.dateLeftSpecial) {
          val yearMonth = Try (new YearMonth (
            form(keys.dateLeftSpecial.year).value.map(year => year.toInt).getOrElse(-1),
            form(keys.dateLeftSpecial.month).value.map(month => month.toInt).getOrElse(-1)
          ).toString("MMMM, yyyy")).getOrElse {
            logger.error("error parsing the date (date-left-crown step)")
            ""
          }
          s"<p>$yearMonth</p>"
        }
      ))
    }
    
    def dateLeftCouncil = {
      Some(ConfirmationQuestion(
        title = "Date you cease to be a British Council employee?",
        editLink = routes.DateLeftCrownController.editGet.url,
        changeName = "date you cease to be a British Council employee?",
        content = ifComplete(keys.dateLeftSpecial) {
          val yearMonth = Try (new YearMonth (
            form(keys.dateLeftSpecial.year).value.map(year => year.toInt).getOrElse(-1),
            form(keys.dateLeftSpecial.month).value.map(month => month.toInt).getOrElse(-1)
          ).toString("MMMM, yyyy")).getOrElse {
            logger.error("error parsing the date (date-left-council step)")
            ""
          }
          s"<p>$yearMonth</p>"
        }
      ))
    }
    
    def nino = {
      Some(ConfirmationQuestion(
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
      ))
    }

    def address = {
      Some(ConfirmationQuestion(
        title = "Where do you live?",
        editLink = AddressController.addressStep.routes.editGet.url,
        changeName = "where do you live?",
        content = ifComplete(keys.overseasAddress) {

          val result:StringBuilder = new StringBuilder
          result.append ("<p>")
          result.append (
            List (
              form(keys.overseasAddress.addressLine1).value,
              form(keys.overseasAddress.addressLine2).value,
              form(keys.overseasAddress.addressLine3).value,
              form(keys.overseasAddress.addressLine4).value,
              form(keys.overseasAddress.addressLine5).value)
            .filter(!_.getOrElse("").isEmpty).map(_.get).mkString("","<br/>",""))
          result.append ("</p>")
          result.append ("<p>" + form (keys.overseasAddress.country).value.getOrElse("") + "</p>")
          result.toString()
        }
      ))
    }

    def dateOfBirth = {
      Some(ConfirmationQuestion(
        title = "What is your date of birth?",
        editLink = DateOfBirthController.dateOfBirthStep.routes.editGet.url,
        changeName = "date of birth",
        content = ifComplete(keys.dob) {
                "<p>" + form(keys.dob.day).value.get + " "  +
                DateOfBirthConstants.monthsByNumber(form(keys.dob.month).value.get) + " " +
                form(keys.dob.year).value.get + "</p>"
            }
      ))
    }

    def openRegister = {
      Some(ConfirmationQuestion(
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
      ))
    }

    def name = {
      Some(ConfirmationQuestion(
        title = "What is your full name?",
        editLink = routes.NameController.editGet.url,
        changeName = "full name",
        content = ifComplete(keys.overseasName.name) {
          List(
            form(keys.overseasName.name.firstName).value,
            form(keys.overseasName.name.middleNames).value,
            form(keys.overseasName.name.lastName).value).flatten
            .mkString("<p>", " ", "</p>")
        }
      ))
    }

    def previousName = {
      Some(ConfirmationQuestion(
        title = "What is your previous name?",
        editLink = routes.NameController.editGet.url,
        changeName = "previous name",
        content = ifComplete(keys.overseasName.previousName) {
          if (form(keys.overseasName.previousName.hasPreviousName).value == Some("true")) {
            List(
              form(keys.overseasName.previousName.previousName.firstName).value,
              form(keys.overseasName.previousName.previousName.middleNames).value,
              form(keys.overseasName.previousName.previousName.lastName).value
            ).flatten.mkString("<p>", " ", "</p>")
          } else {
            "<p>I have not changed my name in the last 12 months</p>"
          }
        }
      ))
    }
    
    def parentName = {
      val under18 = Some(true)
      val withLimit = Some(true)

      (form.under18WhenLeft, form.within15YearLimit) match {
        case (`under18`, `withLimit`) => {
          Some(ConfirmationQuestion(
            title = "Parent or guardian's name",
            editLink = routes.ParentNameController.editGet.url,
            changeName = "full name",
            content = ifComplete(keys.overseasParentName.parentName) {
              List(
                form(keys.overseasParentName.parentName.firstName).value,
                form(keys.overseasParentName.parentName.middleNames).value,
                form(keys.overseasParentName.parentName.lastName).value).flatten
                .mkString("<p>", " ", "</p>")
            }
          ))
        }
        case _ => None
      }
    }

    def parentPreviousName = {
      val hasPreviousName = Some("true")
      val under18 = Some(true)
      val withLimit = Some(true)
      val previousName = form(keys.overseasParentName.parentPreviousName.hasPreviousName).value

      (form.under18WhenLeft, form.within15YearLimit, previousName) match {
        case (`under18`, `withLimit`, `hasPreviousName`) => {
          Some(ConfirmationQuestion(
            title = "Parent or guardian's previous name",
            editLink = routes.ParentNameController.editGet.url,
            changeName = "previous name",
            content = ifComplete(keys.overseasParentName.parentPreviousName) {
              List(
                form(keys.overseasParentName.parentPreviousName.previousName.firstName).value,
                form(keys.overseasParentName.parentPreviousName.previousName.middleNames).value,
                form(keys.overseasParentName.parentPreviousName.previousName.lastName).value
              ).flatten.mkString("<p>", " ", "</p>")
            }
          ))
        }
        case (`under18`, `withLimit`, _) => {
          Some(ConfirmationQuestion(
            title = "Parent or guardian's previous name",
            editLink = routes.ParentNameController.editGet.url,
            changeName = "previous name",
            content = ifComplete(keys.overseasParentName.parentPreviousName) {
              "<p>They haven't changed their name since they left the UK</p>"
            }
          ))
        }
        case _ => None
      }
    }

    def postalOrProxyVote = {
      val way = form(keys.postalOrProxyVote.voteType).value.map{ way => WaysToVoteType.parse(way) }
      val prettyWayName = way match {
        case Some(WaysToVoteType.ByPost) => "postal vote"
        case Some(WaysToVoteType.ByProxy) => "proxy vote"
        case _ => ""
      }
      val myEmail = form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value.getOrElse("")
      val deliveryMethod = form(keys.postalOrProxyVote.deliveryMethod.methodName).value
      val emailMe = form(keys.postalOrProxyVote.deliveryMethod.methodName).value == Some("email")
      val optIn = form(keys.postalOrProxyVote.optIn).value == Some("true")

      way.map { wayToVote =>
        ConfirmationQuestion(
          title = "Application form",
          editLink = wayToVote match {
            case WaysToVoteType.ByPost => routes.PostalVoteController.editGet.url
            case WaysToVoteType.ByProxy => routes.ProxyVoteController.editGet.url
            case _ => routes.WaysToVoteController.editGet.url
          },
          changeName = wayToVote match {
            case WaysToVoteType.ByPost => "your postal vote form"
            case WaysToVoteType.ByProxy => "your proxy vote form"
            case _ => "your method of voting"
          },
          content = ifComplete(keys.postalOrProxyVote) {
            (optIn, emailMe) match {
              case (true, true) => s"<p>Please email a ${prettyWayName} application form to:" +
                "<br/>$myEmail</p>"
              case (true, false) => s"<p>Please post me a ${prettyWayName} application form</p>"
              case (false, _) => s"<p>I do not need a ${prettyWayName} application form</p>"
            }
          }
        )
      }
    }

    def contact = {
      Some(ConfirmationQuestion(
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
      ))
    }

    def passport = {
      val isRenewer = Some("true")
      val notRenewer = Some("false")
      val hasPassport = Some("true")
      val noPassport = Some("false")
      val bornInUk = Some("true")
      val notBornInUk = Some("false")
      val notBornBefore1983 = Some(false)

      val renewer = form(keys.previouslyRegistered.hasPreviouslyRegistered).value
      val passport = form(keys.passport.hasPassport).value
      val birth = form(keys.passport.bornInsideUk).value

      (renewer, passport, birth, form.bornBefore1983) match {
        case (`isRenewer`, _, _, _) => None
        case (`notRenewer`, `hasPassport`, _, _) => passportDetails
        case (`notRenewer`, `noPassport`, `notBornInUk`, _) => citizenDetails
        case (`notRenewer`, `noPassport`, `bornInUk`, `notBornBefore1983`) => citizenDetails
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
      val dateBecameCitizen = form.dateBecameCitizen.map { date =>
        s"${date.getDayOfMonth} ${date.getMonthOfYear} ${date.getYear}"
      }

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

      Some(ConfirmationQuestion(
        title = "British Citizenship Details",
        editLink = route.url,
        changeName = "your citizenship details",
        content = ifComplete(keys.passport) { citizenContent.getOrElse(completeThisStepMessage) }
      ))
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

      Some(ConfirmationQuestion(
        title = "British Passport Details",
        editLink = route.url,
        changeName = "your passport details",
        content = ifComplete(keys.passport) { passportContent.getOrElse(completeThisStepMessage) }
      ))
    }

    def waysToVote = {
      val way = form(keys.waysToVote.wayType).value.map{ way => WaysToVoteType.parse(way) }

      Some(ConfirmationQuestion(
        title = "How do you want to vote",
        editLink = routes.WaysToVoteController.editGet.url,
        changeName = "way to vote",
        content = ifComplete(keys.waysToVote) {
           way match {
            case Some(WaysToVoteType.ByPost) => "<p>By post</p>"
            case Some(WaysToVoteType.ByProxy) => "<p>By proxy (someone else voting for you)</p>"
            case Some(WaysToVoteType.InPerson) => "<p>In the UK, at a polling station</p>"
            case _ => ""
          }
        }
      ))
    }
  }
}
