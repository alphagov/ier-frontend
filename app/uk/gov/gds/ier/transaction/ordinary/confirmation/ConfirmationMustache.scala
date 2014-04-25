package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.constants.{NationalityConstants, DateOfBirthConstants}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.validation.{Key, ErrorTransformForm}
import uk.gov.gds.ier.model.{OtherAddress}
import uk.gov.gds.ier.model.{OtherAddress, MovedHouseOption}
import scala.Some
import controllers.step.ordinary.routes
import uk.gov.gds.ier.form.AddressHelpers
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait ConfirmationMustache {

  case class ConfirmationQuestion(
      content:String,
      title:String,
      editLink:String,
      changeName:String
  )

  case class ConfirmationModel(
    completeApplicantDetails: List[ConfirmationQuestion],
    postUrl: String
  )

  object Confirmation extends StepMustache {

    def confirmationPage(
        form: ErrorTransformForm[InprogressOrdinary],
        postUrl: String) = {

      val confirmation = new ConfirmationBlocks(form)

      val completeApplicantData = List(
        confirmation.name,
        confirmation.previousName,
        confirmation.dateOfBirth,
        confirmation.nationality,
        confirmation.nino,
        confirmation.address,
        confirmation.secondAddress,
        confirmation.previousAddress,
        confirmation.openRegister,
        confirmation.postalVote,
        confirmation.contact
      ).flatten

      val data = ConfirmationModel(
        completeApplicantDetails = completeApplicantData,
        postUrl = postUrl
      )

      val content = Mustache.render("ordinary/confirmation", data)
      MainStepTemplate(
        content,
        "Confirm your details - Register to vote",
        contentClasses = Some("confirmation")
      )
    }

  }

  class ConfirmationBlocks(form: ErrorTransformForm[InprogressOrdinary])
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

    def previousName = {
      val havePreviousName = form(keys.previousName.hasPreviousName).value
      val prevNameStr =  havePreviousName match {
        case Some("true") => {
          List(
            form(keys.previousName.previousName.firstName).value,
            form(keys.previousName.previousName.middleNames).value,
            form(keys.previousName.previousName.lastName).value
          ).flatten.mkString(" ")
        }
        case _ => "I have not changed my name in the last 12 months"
      }
      Some(ConfirmationQuestion(
        title = "What is your previous name?",
        editLink = routes.NameController.editGet.url,
        changeName = "previous name",
        content = ifComplete(keys.previousName) {
          s"<p>$prevNameStr</p>"
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
            "<p>" + confirmationNationalityString + "</p>"
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

    def address = {
      Some(ConfirmationQuestion(
        title = "Address",
        editLink = if (isManualAddressDefined(form, keys.address.manualAddress)) {
          routes.AddressManualController.editGet.url
        } else {
          routes.AddressSelectController.editGet.url
        },
        changeName = "your address",
        content = ifComplete(keys.address) {
          val addressLine = form(keys.address.addressLine).value.orElse{
            manualAddressToOneLine(form, keys.address.manualAddress)
          }.getOrElse("")
          val postcode = form(keys.address.postcode).value.getOrElse("").toUpperCase
          s"<p>$addressLine</p><p>$postcode</p>"
        }
      ))
    }

    def secondAddress = {
      Some(ConfirmationQuestion(
        title = "Second address",
        editLink = routes.OtherAddressController.editGet.url,
        changeName = "second address",
        content =
          ifComplete(keys.otherAddress) {
            form(keys.otherAddress.hasOtherAddress).value match {
              case Some(secAddrType)
                if OtherAddress.parse(secAddrType) != OtherAddress.NoOtherAddress =>
                  "<p>I have a second address</p>"
              case _ => "<p>I don't have a second address</p>"
            }
          }
      ))
    }

    def previousAddress = {
      val addressLine = form(keys.previousAddress.previousAddress.addressLine).value
      val manualAddress = manualAddressToOneLine(
        form,
        keys.previousAddress.previousAddress.manualAddress
      )
      val address = { addressLine orElse manualAddress }.map( line =>
        "<p>" + line + "</p>"
      ).getOrElse("")

      val postcode = form(keys.previousAddress.previousAddress.postcode).value.map(
        postcode => "<p>" + postcode + "</p>"
      ).getOrElse("")

      val movedHouse = form(keys.previousAddress.movedRecently).value.map {
        MovedHouseOption.parse(_)
      }

      val title = movedHouse match {
        case Some(MovedHouseOption.MovedFromAbroadRegistered) => "Last UK address"
        case _ => "Previous address"
      }

      Some(ConfirmationQuestion(
        title = title,
        editLink = routes.PreviousAddressFirstController.editGet.url,
        changeName = "your previous address",
        content = ifComplete(keys.previousAddress) {
          movedHouse match {
            case Some(MovedHouseOption.MovedFromAbroadNotRegistered) => "<p>I moved from abroad, but I was not registered to vote there</p>"
            case Some(moveOption) if moveOption.hasPreviousAddress => address + postcode
            case _ => "<p>I have not moved in the last 12 months</p>"
          }
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

    def postalVote = {
      Some(ConfirmationQuestion(
        title = "Postal vote",
        editLink = routes.PostalVoteController.editGet.url,
        changeName = "postal vote",
        content = ifComplete(keys.postalVote) {
          val deliveryMethod =
            if (form(keys.postalVote.deliveryMethod.methodName).value == Some("email")){
              "I want you to email a postal vote application form to: <br/>" +
              form(keys.postalVote.deliveryMethod.emailAddress).value.getOrElse("")
            }
            else {
              "I want you to mail me a postal vote application form"
            }

          if(form(keys.postalVote.optIn).value == Some("true")){
            "<p>" + deliveryMethod + "</p>"
          }
          else {
            "<p>I don’t want to apply for a postal vote</p>"
          }
        }
      ))
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

    def getNationalities:List[String] = {
      val british = form(keys.nationality.british).value
      val irish =form(keys.nationality.irish).value
      british.toList.filter(_ == "true").map(brit => "British") ++
      irish.toList.filter(_ == "true").map(isIrish => "Irish")
    }

    def confirmationNationalityString = {
      def concatCommaEndInAnd(
          list:List[String],
          prepend:String = "",
          append:String = "") = {
        val filteredList = list.filter(_.nonEmpty)
        val str = List(
          filteredList.dropRight(1).mkString(", "),
          filteredList.takeRight(1).mkString("")
        ).filter(_.nonEmpty).mkString(" and ")

        if (str.isEmpty) "" else s"$prepend$str$append"
      }

      val localNationalities = getNationalities
      val foreignNationalities = concatCommaEndInAnd(
        prepend = "a citizen of ",
        list = obtainOtherCountriesList
      )

      val nationalityString = concatCommaEndInAnd(
        prepend = "I am ",
        list = localNationalities :+ foreignNationalities
      )
      nationalityString
    }

    def nationalityIsFilled:Boolean = {
      val isBritish = form(keys.nationality.british).value.getOrElse("false").toBoolean
      val isIrish = form(keys.nationality.irish).value.getOrElse("false").toBoolean
      val otherCountries = obtainOtherCountriesList
      (isBritish || isIrish || !otherCountries.isEmpty)
    }

    def obtainOtherCountriesList:List[String] = {
      val otherCountries = (
        for (i <- 0 until NationalityConstants.numberMaxOfOtherCountries
             if (form(otherCountriesKey(i)).value.isDefined)
               && !form(otherCountriesKey(i)).value.get.isEmpty)
        yield form(otherCountriesKey(i)).value.get
      )
      otherCountries.toList
    }

    def otherCountriesKey(i:Int) = keys.nationality.otherCountries.item(i)
  }
}
