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
import uk.gov.gds.ier.transaction.shared.{BlockContent, BlockError, EitherErrorOrContent}
import uk.gov.gds.ier.service.WithAddressService

trait ConfirmationMustache extends WithAddressService{

  case class ConfirmationQuestion(
      content: EitherErrorOrContent,
      title: String,
      editLink: String,
      changeName: String
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

    val completeThisStepMessage = "Please complete this step"

    def ifComplete(key:Key)(confirmationHtml: => List[String]): EitherErrorOrContent = {
      if (form(key).hasErrors) {
        BlockError(completeThisStepMessage)
      } else {
        BlockContent(confirmationHtml)
      }
    }

    def ifComplete(keys:Key*)(confirmationHtml: => List[String]): EitherErrorOrContent = {
      if (keys.exists(form(_).hasErrors)) {
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
            List("I cannot provide my national insurance number because:",
              form(keys.nino.noNinoReason).value.getOrElse(""))
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
           val memberOf = form(keys.service.serviceName).value map { serviceName =>
             if (isPartner)
               s"Your partner is a member of the ${serviceName}"
             else
               s"I am a member of the ${serviceName}"
           }
           val regiment = form(keys.service.regiment).value map {
             regiment => s"Regiment: ${regiment}"
           }
           List(memberOf, regiment).flatten
        }
      ))
    }

    def rank = {
      Some(ConfirmationQuestion(
        title = "Service number and rank",
        editLink = routes.RankController.editGet.url,
        changeName = "service number and rank",
        content = ifComplete(keys.rank) {
          val serviceNumber = form(keys.rank.serviceNumber).value map { serviceNumber =>
            s"Service number: ${serviceNumber}"
          }
          val rank = form(keys.rank.rank).value map { rank =>
            s"Rank: ${rank}"
          }
          List(serviceNumber, rank).flatten
        }
      ))
    }

    def address = {
      Some(ConfirmationQuestion(
        title = "UK registration address",
        editLink = routes.AddressFirstController.editGet.url,
        changeName = "your UK registration address",
        content = ifComplete(keys.address.address) {
          val addressLine = form(keys.address.address.addressLine).value.orElse{
            manualAddressToOneLine(form, keys.address.address.manualAddress)
          }
          val postcode = form(keys.address.address.postcode).value
          List(addressLine, postcode).flatten
        }
      ))
    }

    def previousAddress = {
      val hasCurrentUkAddress =
        form(keys.address.hasUkAddress).value match {
          case Some(hasUkAddress) if (hasUkAddress.toBoolean) => true
          case _ => false
        }
      if (hasCurrentUkAddress) {
        Some(ConfirmationQuestion(
          title = "UK previous registration address",
          editLink = routes.PreviousAddressFirstController.editGet.url,
          changeName = "your UK previous registration address",
          content = ifComplete(keys.previousAddress, keys.previousAddress.movedRecently) {
            val moved = form(keys.previousAddress.movedRecently).value
              .map(MovedHouseOption.parse(_).hasPreviousAddress)
              .getOrElse(false)

            if (moved) {
              val postcode = form(keys.previousAddress.previousAddress.postcode).value.getOrElse("")
              if (addressService.isNothernIreland(postcode)) {
                "<p>" + postcode + "</p>" +
                  "<p>I was previously registered in Northern Ireland</p>"

              } else {
                val address = if (form(keys.previousAddress.previousAddress.addressLine).value.isDefined) {
                  form(keys.previousAddress.previousAddress.addressLine).value
                } else {
                  manualAddressToOneLine(form, keys.previousAddress.previousAddress.manualAddress)
                }
                val postcode = form(keys.previousAddress.previousAddress.postcode).value
                List(address, postcode).flatten
              } else
              {
                List("I have not moved in the last 12 months")
              }
            } // FIXME: !!
          }
        ))
      }
      else None
    }

    def contactAddress = {
      Some(ConfirmationQuestion(
        title = "Polling card address",
        editLink = routes.ContactAddressController.editGet.url,
        changeName = "polling card address",
        content = {
          val addressTypeKey = form(keys.contactAddress.contactAddressType).value match {
            case Some("uk") => Some(keys.ukContactAddress)
            case Some("bfpo") => Some(keys.bfpoContactAddress)
            case Some("other") => Some(keys.otherContactAddress)
            case _ => None
          }

          if (!addressTypeKey.isDefined) {
            ifComplete(keys.contactAddress.contactAddressType) { List() }
          } else if (addressTypeKey.equals(Some(keys.ukContactAddress))) {
            ifComplete(keys.address) {
              //println(">>>" + form(keys.address.address.manualAddress.lineOne).value)
              //println(">>>" + form(keys.address.manualAddress.lineOne).value)
              val addressLine = form(keys.address.address.addressLine).value.orElse{
                manualAddressToOneLine(form, keys.address.address.manualAddress)
              }
              val postcode = form(keys.address.address.postcode).value
              List(addressLine, postcode).flatten
            }
          }
          else {
            ifComplete(keys.contactAddress) {
              val contactAddressKey = keys.contactAddress.prependNamespace(addressTypeKey.get)
              val addressLines = List(
                form(contactAddressKey.prependNamespace(keys.addressLine1)).value,
                form(contactAddressKey.prependNamespace(keys.addressLine2)).value,
                form(contactAddressKey.prependNamespace(keys.addressLine3)).value,
                form(contactAddressKey.prependNamespace(keys.addressLine4)).value,
                form(contactAddressKey.prependNamespace(keys.addressLine5)).value
              ).flatten.mkString(", ") match {
                case "" => None
                case a => Some(a)
                // FIXME: improve!
              }
              val postcode = form(contactAddressKey.prependNamespace(keys.postcode)).value
              val country = form(contactAddressKey.prependNamespace(keys.country)).value

              List(addressLines, postcode, country).flatten
            }
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
          if (form(keys.openRegister.optIn).value == Some("true")){
            List("I want to include my details on the open register")
          } else {
            List("I don’t want to include my details on the open register")
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
        case Some(WaysToVoteType.ByPost) => List("I want to vote by post")
        case Some(WaysToVoteType.ByProxy) => List("I want to vote by proxy (someone else voting for me)")
        case Some(WaysToVoteType.InPerson) => List("I want to vote in person, at a polling station")
        case _ => List()
      }
      val postalOrProxyVote = (optIn, emailMe) match {
        case (Some("true"), true) => List("Send an application form to:", myEmail)
        case (Some("true"), false) => List("Send me an application form in the post")
        case (Some("false"), _) => List(s"I do not need ${prettyWayName} application form")
        case (_, _) => List()
      }

      Some(ConfirmationQuestion(
        title = "Voting options",
        editLink = routes.WaysToVoteController.editGet.url,
        changeName = "voting",
        content = ifComplete(keys.waysToVote, keys.postalOrProxyVote) {
          ways ++ postalOrProxyVote
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
            Some(s"By phone: ${form(keys.contact.phone.detail).value.getOrElse("")}")
          } else None

          val email = if( form(keys.contact.email.contactMe).value == Some("true")) {
            Some(s"By email: ${form(keys.contact.email.detail).value.getOrElse("")}")
          } else None

          List(post, phone, email).flatten
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
