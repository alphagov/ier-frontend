package uk.gov.gds.ier.transaction.crown.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.WaysToVoteType
import controllers.step.crown._
import uk.gov.gds.ier.validation.constants.{NationalityConstants, DateOfBirthConstants}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.validation.Key
import uk.gov.gds.ier.model.InprogressCrown
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.form.AddressHelpers

trait ConfirmationMustache {

  case class ConfirmationQuestion(
     content:String,
     title:String,
     editLink:String,
     changeName:String
  )

  case class ConfirmationModel(
      applicantDetails: List[ConfirmationQuestion],
      partnerDetails: List[ConfirmationQuestion],
      completeApplicantDetails: List[ConfirmationQuestion],
      displayPartnerBlock: Boolean,
      backUrl: String,
      postUrl: String
  )

  object Confirmation extends StepMustache {

    def confirmationPage(
        form:InProgressForm[InprogressCrown],
        backUrl: String,
        postUrl: String) = {

      val confirmation = new ConfirmationBlocks(form)

      val partnerData = List(
        confirmation.jobTitle,
        confirmation.govDepartment
      ).flatten

      val applicantData = List(
        confirmation.name,
        confirmation.dateOfBirth,
        confirmation.nationality,
        confirmation.nino,
        confirmation.address,
        confirmation.contactAddress,
        confirmation.openRegister,
        confirmation.waysToVote,
        confirmation.postalOrProxyVote,
        confirmation.contact
      ).flatten

      val completeApplicantData = List(
        confirmation.name,
        confirmation.dateOfBirth,
        confirmation.nationality,
        confirmation.nino,
        confirmation.jobTitle,
        confirmation.govDepartment,
        confirmation.address,
        confirmation.contactAddress,
        confirmation.openRegister,
        confirmation.waysToVote,
        confirmation.postalOrProxyVote,
        confirmation.contact
      ).flatten

      val data = ConfirmationModel(
        partnerDetails = partnerData,
        applicantDetails = applicantData,
        completeApplicantDetails = completeApplicantData,
        displayPartnerBlock = displayPartnerBlock(form),
        backUrl = backUrl,
        postUrl = postUrl
      )

      val content = Mustache.render("crown/confirmation", data)
      MainStepTemplate(
        content,
        "Confirm your details - Register to vote",
        contentClasses = Some("confirmation")
      )
    }

    def displayPartnerBlock (form:InProgressForm[InprogressCrown]): Boolean = {

      val isPartner = Some("true")
      val isNotMember = Some("false")

      val displayCrownPartner = (
        form(keys.statement.partnerCrownMember).value,
        form(keys.statement.crownMember).value
      ) match {
        case (`isPartner`, `isNotMember`) => true
        case (`isPartner`, None) => true
        case _ => false
      }

      val displayBritisthCouncilPartner = (
        form(keys.statement.partnerBritishCouncilMember).value,
        form(keys.statement.britishCouncilMember).value
        ) match {
        case (`isPartner`, `isNotMember`) => true
        case (`isPartner`, None) => true
        case _ => false
      }

      (displayCrownPartner || displayBritisthCouncilPartner)

    }

  }

  class ConfirmationBlocks(form:InProgressForm[InprogressCrown])
    extends StepMustache with AddressHelpers with Logging {

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

    def name = {
      Some(ConfirmationQuestion(
        title = "Name",
        editLink = routes.NameController.editGet.url,
        changeName = "full name",
        content = ifComplete(keys.name) {
          List(
            form(keys.name.firstName).value,
            form(keys.name.middleNames).value,
            form(keys.name.lastName).value).flatten
            .mkString("<p>", " ", "</p>")
        }
      ))
    }

    def dateOfBirth = {

      val dobContent =
        if (form(keys.dob.dob.day).value.isDefined) {
          val day = form(keys.dob.dob.day).value.getOrElse("")
          val month = DateOfBirthConstants.monthsByNumber(form(keys.dob.dob.month).value.get)
          val year = form(keys.dob.dob.year).value.getOrElse("")
          "<p>" + day + " " + month + " " + year + "</p>"
        } else {
          val excuseReason = if (form(keys.dob.noDob.reason).value.isDefined) {
            "<p>You are unable to provide your date of birth because: " +
              form(keys.dob.noDob.reason).value.getOrElse("") + "</p>"
          }
          val ageRange = form(keys.dob.noDob.range).value match {
            case Some("under18") => "<p>I am roughly under 18</p>"
            case Some("18to70") => "<p>I am over 18 years old</p>"
            case Some("over70") => "<p>I am over 70 years old</p>"
            case Some("dontKnow") => "<p>I don't know my age</p>"
            case _ => ""
          }
          excuseReason + ageRange
        }

      Some(ConfirmationQuestion(
        title = "Date of birth",
        editLink = routes.DateOfBirthController.editGet.url,
        changeName = "date of birth",
        content = ifComplete(keys.dob) {
          dobContent
        }
      ))
    }

    def nationality = {
      Some(ConfirmationQuestion(
        title = "Nationality",
        editLink = routes.NationalityController.editGet.url,
        changeName = "nationality",
        content = ifComplete(keys.nationality) {
          if (nationalityIsFilled) {
            "<p>I am " + confirmationNationalityString + "</p>"
          } else {
            "<p>I cannot provide my nationality because:</p><p>"+
              form(keys.nationality.noNationalityReason).value.getOrElse("") + "</p>"
          }
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

    def jobTitle = {
      Some(ConfirmationQuestion(
        title = "Job title",
        editLink = routes.JobController.editGet.url,
        changeName = "job title",
        content = ifComplete(keys.job) {
            "<p>"+form(keys.job.jobTitle).value.getOrElse("")+"</p>"
        }
      ))
    }

    def govDepartment = {
      Some(ConfirmationQuestion(
        title = "Department",
        editLink = routes.JobController.editGet.url,
        changeName = "department",
        content = ifComplete(keys.job) {
          "<p>"+form(keys.job.govDepartment).value.getOrElse("")+"</p>"
        }
      ))
    }

    def address = {
      Some(ConfirmationQuestion(
        title = "UK registration address",
        editLink = if (form(keys.address.addressLine).value.isDefined) {
          routes.AddressSelectController.editGet.url
        } else if (isManualAddressDefined(form, keys.address.manualAddress)) {
          routes.AddressManualController.editGet.url
        } else {
          routes.AddressController.editGet.url
        },
        changeName = "your UK registration address",
        content = ifComplete(keys.address) {
          val addressLine = form(keys.address.addressLine).value.orElse{
            manualAddressToOneLine(form, keys.address.manualAddress)
          }.getOrElse("")
          val postcode = form(keys.address.postcode).value.getOrElse("")
          s"<p>$addressLine</p><p>$postcode</p>"
        }
      ))
    }

    def contactAddress = {
      Some(ConfirmationQuestion(
        title = "Polling card address",
        editLink = routes.ContactAddressController.editGet.url,
        changeName = "polling card address",
        content = ifComplete(keys.contactAddress) {

          val result:StringBuilder = new StringBuilder
          result.append ("<p>")
          result.append (
            List (
              form(keys.contactAddress.addressLine1).value,
              form(keys.contactAddress.addressLine2).value,
              form(keys.contactAddress.addressLine3).value,
              form(keys.contactAddress.addressLine4).value,
              form(keys.contactAddress.addressLine5).value)
              .filter(!_.getOrElse("").isEmpty).map(_.get).mkString("","<br/>",""))
          result.append ("</p>")
          result.append ("<p>" + form (keys.contactAddress.country).value.getOrElse("") + "</p>")
          result.toString()
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

    def waysToVote = {
      val way = form(keys.waysToVote.wayType).value.map{ way => WaysToVoteType.parse(way) }

      Some(ConfirmationQuestion(
        title = "How you want to vote",
        editLink = routes.WaysToVoteController.editGet.url,
        changeName = "voting",
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

    def postalOrProxyVote = {
      val way = form(keys.postalOrProxyVote.voteType).value.map{ way => WaysToVoteType.parse(way) }
      val prettyWayName = way match {
        case Some(WaysToVoteType.ByPost) => "postal vote"
        case Some(WaysToVoteType.ByProxy) => "proxy vote"
        case _ => ""
      }
      val myEmail = form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value.getOrElse("")
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
              case (true, true) => s"<p>Please email a ${prettyWayName} application form to:</p>" +
                s"<p>${myEmail}</p>"
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
        editLink = routes.ContactController.editGet.url,
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

    def getNationalities = {
      val british = form(keys.nationality.british).value
      val irish =form(keys.nationality.irish).value
      british.toList.filter(_ == "true").map(brit => "United Kingdom") ++
        irish.toList.filter(_ == "true").map(isIrish => "Ireland")
    }

    def confirmationNationalityString = {
      val allCountries = getNationalities ++ obtainOtherCountriesList
      val nationalityString = List(allCountries.dropRight(1).mkString(", "),
        allCountries.takeRight(1).mkString("")).filter(_.nonEmpty)
      s"a citizen of ${nationalityString.mkString(" and ")}"
    }

    def nationalityIsFilled:Boolean = {
      val british = form(keys.nationality.british).value.getOrElse("false").toBoolean
      val irish = form(keys.nationality.irish).value.getOrElse("false").toBoolean
      val otherCountries = obtainOtherCountriesList
      (british || irish || !otherCountries.isEmpty)
    }

    def obtainOtherCountriesList:List[String] = {
      (
        for (i <- 0 until NationalityConstants.numberMaxOfOtherCountries
             if (form(otherCountriesKey(i)).value.isDefined)
               && !form(otherCountriesKey(i)).value.get.isEmpty)
        yield form(otherCountriesKey(i)).value.get
        ).toList
    }

    def otherCountriesKey(i:Int) = keys.nationality.otherCountries.key + "["+i+"]"
  }
}
