package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.constants.{NationalityConstants, DateOfBirthConstants}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.validation.{Key, ErrorTransformForm}
import uk.gov.gds.ier.model.{OtherAddress, MovedHouseOption}
import scala.Some
import controllers.step.ordinary.routes
import uk.gov.gds.ier.form.AddressHelpers
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.shared.{BlockContent, BlockError, EitherErrorOrContent}

trait ConfirmationMustache {

  case class ConfirmationQuestion(
      content: EitherErrorOrContent,
      title: String,
      editLink: String,
      changeName: String
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

    val completeThisStepMessage = "Please complete this step"

    def ifComplete(key:Key)(confirmationHtml: => List[String]): EitherErrorOrContent = {
      if (form(key).hasErrors) {
        BlockError(completeThisStepMessage)
      } else {
        BlockContent(confirmationHtml)
      }
    }

    def name = {
      Some(ConfirmationQuestion(
        title = "Name",
        editLink = routes.NameController.editGet.url,
        changeName = "full name",
        content = ifComplete(keys.name) {
          List(List(
            form(keys.name.firstName).value,
            form(keys.name.middleNames).value,
            form(keys.name.lastName).value).flatten.mkString(" "))
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
          List(prevNameStr)
        }
      ))
    }

    def dateOfBirth = {

      val dobContent =
        if (form(keys.dob.dob.day).value.isDefined) {
          val day = form(keys.dob.dob.day).value.getOrElse("")
          val month = DateOfBirthConstants.monthsByNumber(form(keys.dob.dob.month).value.get)
          val year = form(keys.dob.dob.year).value.getOrElse("")
          List(day + " " + month + " "  + year)
        } else {
          val excuseReason = form(keys.dob.noDob.reason).value match {
            case Some(reasonDescription) =>
              s"You are unable to provide your date of birth because: $reasonDescription"
            case _ => ""
          }
          val ageRange = form(keys.dob.noDob.range).value match {
            case Some("under18") => "I am roughly under 18"
            case Some("18to70") => "I am over 18 years old"
            case Some("over70") => "I am over 70 years old"
            case Some("dontKnow") => "I don't know my age"
            case _ => ""
          }
          List(excuseReason, ageRange)
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
            List(confirmationNationalityString)
          } else {
            List("I cannot provide my nationality because:",
              form(keys.nationality.noNationalityReason).value.getOrElse(""))
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
            List(form(keys.nino.nino).value.getOrElse(""))
          } else {
            List("I have not moved in the last 12 months")
            List("I cannot provide my national insurance number because:",
              form(keys.nino.noNinoReason).value.getOrElse(""))
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
          List(addressLine, postcode)
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
                  List("I have a second address")
              case _ => List("I don't have a second address")
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
      val address = addressLine orElse manualAddress getOrElse("")
      val postcode = form(keys.previousAddress.previousAddress.postcode).value.getOrElse("")
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
            case Some(MovedHouseOption.MovedFromAbroadNotRegistered) =>
              List("I moved from abroad, but I was not registered to vote there")
            case Some(moveOption) if moveOption.hasPreviousAddress =>
              List(address, postcode)
            case _ =>
              List("I have not moved in the last 12 months")
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
            List("I want to include my name and address on the open register")
          }else{
            List("I don't want my name and address on the open register")
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
              List("I want you to email a postal vote application form to:",
                form(keys.postalVote.deliveryMethod.emailAddress).value.getOrElse(""))
            }
            else {
              List("I want you to mail me a postal vote application form")
            }

          if(form(keys.postalVote.optIn).value == Some("true")){
            deliveryMethod
          }
          else {
            List("I don’t want to apply for a postal vote")
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
          val post = if (form(keys.contact.post.contactMe).value == Some("true")) {
            Some("By post")
          } else None

          val phone = if (form(keys.contact.phone.contactMe).value == Some("true")) {
            form(keys.contact.phone.detail).value.map( phone => s"By phone: $phone")
          } else None

          val email = if( form(keys.contact.email.contactMe).value == Some("true")) {
            form(keys.contact.email.detail).value.map {email => s"By email: $email"}
          } else None

          List(post, phone, email).flatten
        }
      ))
    }

    private def getNationalities: List[String] = {
      val british = form(keys.nationality.british).value
      val irish =form(keys.nationality.irish).value
      british.toList.filter(_ == "true").map(brit => "British") ++
      irish.toList.filter(_ == "true").map(isIrish => "Irish")
    }

    private[confirmation] def confirmationNationalityString = {
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

    private def nationalityIsFilled: Boolean = {
      val isBritish = form(keys.nationality.british).value.getOrElse("false").toBoolean
      val isIrish = form(keys.nationality.irish).value.getOrElse("false").toBoolean
      val otherCountries = obtainOtherCountriesList
      (isBritish || isIrish || !otherCountries.isEmpty)
    }

    private def obtainOtherCountriesList: List[String] = {
      val otherCountries = (
        for (i <- 0 until NationalityConstants.numberMaxOfOtherCountries
             if (form(otherCountriesKey(i)).value.isDefined)
               && !form(otherCountriesKey(i)).value.get.isEmpty)
        yield form(otherCountriesKey(i)).value.get
      )
      otherCountries.toList
    }

    private def otherCountriesKey(i:Int) = keys.nationality.otherCountries.item(i)
  }
}
