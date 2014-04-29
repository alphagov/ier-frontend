package uk.gov.gds.ier.transaction.forces.confirmation

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.WaysToVoteType
import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.validation.constants.{NationalityConstants, DateOfBirthConstants}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.validation.{ErrorTransformForm, Key}
import scala.Some
import controllers.step.forces.routes
import uk.gov.gds.ier.form.AddressHelpers
import uk.gov.gds.ier.transaction.forces.InprogressForces

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
    postUrl: String
  )

  object Confirmation extends StepMustache {

    def confirmationPage(
        form: ErrorTransformForm[InprogressForces],
        postUrl: String) = {

      val confirmation = new ConfirmationBlocks(form)

      val partnerData = List(
        confirmation.service(true),
        confirmation.rank
      ).flatten

      val applicantData = List(
        confirmation.name,
        confirmation.previousName,
        confirmation.dateOfBirth,
        confirmation.nationality,
        confirmation.nino,
        confirmation.address,
        confirmation.previousAddress,
        confirmation.contactAddress,
        confirmation.openRegister,
        confirmation.waysToVote,
        confirmation.contact
      ).flatten

      val completeApplicantData = List(
        confirmation.name,
        confirmation.previousName,
        confirmation.dateOfBirth,
        confirmation.nationality,
        confirmation.nino,
        confirmation.service(false),
        confirmation.rank,
        confirmation.address,
        confirmation.previousAddress,
        confirmation.contactAddress,
        confirmation.openRegister,
        confirmation.waysToVote,
        confirmation.contact
      ).flatten

      val data = ConfirmationModel(
        partnerDetails = partnerData,
        applicantDetails = applicantData,
        completeApplicantDetails = completeApplicantData,
        displayPartnerBlock = displayPartnerBlock(form),
        postUrl = postUrl
      )

      val content = Mustache.render("forces/confirmation", data)
      MainStepTemplate(
        content,
        "Confirm your details - Register to vote",
        contentClasses = Some("confirmation")
      )
    }

    def displayPartnerBlock (form: ErrorTransformForm[InprogressForces]): Boolean = {

      val isForcesPartner = Some("true")
      val isNotForcesMember = Some("false")

      (
        form(keys.statement.partnerForcesMember).value,
        form(keys.statement.forcesMember).value
      ) match {
        case (`isForcesPartner`, `isNotForcesMember`) => true
        case (`isForcesPartner`, None) => true
        case _ => false
      }
    }

  }

  class ConfirmationBlocks(form: ErrorTransformForm[InprogressForces])
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

    def service(isPartner:Boolean) = {
      Some(ConfirmationQuestion(
        title = "Service",
        editLink = routes.ServiceController.editGet.url,
        changeName = "service",
        content = ifComplete(keys.service) {
           val serviceName = form(keys.service.serviceName).value match {
             case Some("Royal Navy") => "Royal Navy"
             case Some("British Army") => "British Army"
             case Some("Royal Air Force") => "Royal Air Force"
             case _ => ""
           }

           val memberOf = if (isPartner)
                            "<p>Your partner is a member of the "+serviceName+"</p>"
                          else
                            "<p>I am a member of the "+serviceName+"</p>"
           val regiment = form(keys.service.regiment).value match {
             case Some(regiment) => s"<p>Regiment: ${regiment}</p>"
             case None => ""
           }

           memberOf + regiment
        }
      ))
    }

    def rank = {
      Some(ConfirmationQuestion(
        title = "Service number and rank",
        editLink = routes.RankController.editGet.url,
        changeName = "service number and rank",
        content = ifComplete(keys.rank) {

          val serviceNumber = form(keys.rank.serviceNumber).value match {
            case Some(serviceNumber) => s"<p>Service number: ${serviceNumber}</p>"
            case None => ""
          }
          val rank = form(keys.rank.rank).value match {
            case Some(rank) => s"<p>Rank: ${rank}</p>"
            case None => ""
          }
          serviceNumber + rank
        }
      ))
    }

    def address = {
      Some(ConfirmationQuestion(
        title = "UK registration address",
        editLink = if (isManualAddressDefined(form, keys.address.manualAddress)) {
          routes.AddressManualController.editGet.url
        } else {
          routes.AddressSelectController.editGet.url
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

    def previousAddress = {
      Some(ConfirmationQuestion(
        title = "UK previous registration address",
        editLink = routes.PreviousAddressFirstController.editGet.url,
        changeName = "your UK previous registration address",
        content = ifComplete(keys.previousAddress) {
          val moved = form(keys.previousAddress.movedRecently).value.map { str =>
            MovedHouseOption.parse(str).hasPreviousAddress
          }.getOrElse(false)

          if(moved) {
            val address = if(form(keys.previousAddress.previousAddress.addressLine).value.isDefined) {
              form(keys.previousAddress.previousAddress.addressLine).value.map(
                addressLine => "<p>" + addressLine + "</p>"
              ).getOrElse("")
            } else {
              manualAddressToOneLine(form, keys.previousAddress.previousAddress.manualAddress).map(
                addressLine => "<p>" + addressLine + "</p>"
              ).getOrElse("")
            }

            val postcode = form(keys.previousAddress.previousAddress.postcode).value.map(
              postcode => "<p>" + postcode + "</p>"
            ).getOrElse("")

            address + postcode

          } else {
            "<p>I have not moved in the last 12 months</p>"
          }
        }
      ))
    }

    def contactAddress = {
      Some(ConfirmationQuestion(
        title = "Polling card address",
        editLink = routes.ContactAddressController.editGet.url,
        changeName = "polling card address",
        content = ifComplete(keys.contactAddress) {

          val addressTypeKey = form(keys.contactAddress.contactAddressType).value match {
            case Some("uk") => keys.ukContactAddress
            case Some("bfpo") => keys.bfpoContactAddress
            case Some("other") => keys.otherContactAddress
            case _ => throw new IllegalArgumentException
          }

          if (addressTypeKey.equals(keys.ukContactAddress)) {
            ifComplete(keys.address) {
              val addressLine = form(keys.address.addressLine).value.orElse{
                manualAddressToOneLine(form, keys.address.manualAddress)
              }.getOrElse("")
              val postcode = form(keys.address.postcode).value.getOrElse("")
              s"<p>$addressLine</p><p>$postcode</p>"
            }
          }
          else {
            val contactAddressKey = keys.contactAddress.prependNamespace(addressTypeKey)
            val result:StringBuilder = new StringBuilder
            result.append ("<p>")
            result.append (
              List (
                form(contactAddressKey.prependNamespace(keys.addressLine1)).value,
                form(contactAddressKey.prependNamespace(keys.addressLine2)).value,
                form(contactAddressKey.prependNamespace(keys.addressLine3)).value,
                form(contactAddressKey.prependNamespace(keys.addressLine4)).value,
                form(contactAddressKey.prependNamespace(keys.addressLine5)).value)
                .filter(!_.getOrElse("").isEmpty).map(_.get).mkString("","<br/>",""))
            result.append ("</p>")
            result.append ("<p>" +
              form (contactAddressKey.prependNamespace(keys.postcode)).value.getOrElse("") + "</p>")
            result.append ("<p>" +
              form (contactAddressKey.prependNamespace(keys.country)).value.getOrElse("") + "</p>")
            result.toString()
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

    def waysToVote = {
      val way = form(keys.waysToVote.wayType).value.map(WaysToVoteType.parse(_))
      val prettyWayName = way match {
        case Some(WaysToVoteType.ByPost) => "a postal vote"
        case Some(WaysToVoteType.ByProxy) => "a proxy vote"
        case _ => "an"
      }
      val myEmail = form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value.getOrElse("")
      val emailMe = form(keys.postalOrProxyVote.deliveryMethod.methodName).value == Some("email")
      val optIn = form(keys.postalOrProxyVote.optIn).value
      val ways = way match {
            case Some(WaysToVoteType.ByPost) => "<p>I want to vote by post</p>"
            case Some(WaysToVoteType.ByProxy) => "<p>I want to vote by proxy (someone else voting for me)</p>"
            case Some(WaysToVoteType.InPerson) => "<p>I want to vote in person, at a polling station</p>"
            case _ => ""
      }
      val postalOrProxyVote = (optIn, emailMe) match {
              case (Some("true"), true) => s"<p>Send an application form to:</p>" +
                s"<p>${myEmail}</p>"
              case (Some("true"), false) => s"<p>Send me an application form in the post</p>"
              case (Some("false"), _) => s"<p>I do not need ${prettyWayName} application form</p>"
              case (_, _) => ""
      }

      Some(ConfirmationQuestion(
        title = "Voting options",
        editLink = routes.WaysToVoteController.editGet.url,
        changeName = "voting",
        content = if (form(keys.waysToVote).hasErrors ||
             (way.exists(_ == WaysToVoteType.InPerson) && form(keys.postalOrProxyVote).hasErrors)) {
           completeThisStepMessage
         }
         else ways + postalOrProxyVote
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
        str match {
          case "" => ""
          case content => s"$prepend$content$append"
        }
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

    def otherCountriesKey(i:Int) = keys.nationality.otherCountries.item(i)
  }
}
