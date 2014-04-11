package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{PossibleAddress, MovedHouseOption}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PreviousAddressMustache {
  self: WithSerialiser =>

  object PreviousAddressMustache extends StepMustache {

    val questionNumber = "8 of 11"

    case class PostcodeModel (
        question: Question,
        postcode: Field
    )

    case class SelectModel (
        question: Question,
        lookupUrl: String,
        manualUrl: String,
        postcode: Field,
        address: Field,
        possibleJsonList: Field,
        possiblePostcode: Field,
        hasAddresses: Boolean
    )

    case class ManualModel (
        question: Question,
        lookupUrl: String,
        postcode: Field,
        maLineOne: Field,
        maLineTwo: Field,
        maLineThree: Field,
        maCity: Field
    )

    def selectData(
        title: String,
        form: ErrorTransformForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {

      implicit val progressForm = form

      val selectedUprn = form(keys.previousAddress.previousAddress.uprn).value

      val options = for (address <- maybePossibleAddress.map(_.jsonList.addresses).toList.flatten)
      yield SelectOption(
          value = address.uprn.getOrElse(""),
          text = address.addressLine.getOrElse(""),
          selected = if (address.uprn == selectedUprn) {
            "selected=\"selected\""
          } else ""
        )

      val hasAddresses = maybePossibleAddress.exists(!_.jsonList.addresses.isEmpty)

      val addressSelect = SelectField(
        key = keys.previousAddress.previousAddress.uprn,
        optionList = options,
        default = SelectOption(
          value = "",
          text = s"${options.size} addresses found"
        )
      )
      val addressSelectWithError = addressSelect.copy(
        classes = if (!hasAddresses) {
          "invalid"
        } else {
          addressSelect.classes
        }
      )

      SelectModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = title,
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = lookupUrl,
        manualUrl = manualUrl,
        postcode = TextField(keys.previousAddress.previousAddress.postcode),
        address = addressSelectWithError,  // this is model data for <select>
        possibleJsonList = HiddenField(
          key = keys.possibleAddresses.jsonList,
          value = maybePossibleAddress.map { poss =>
            serialiser.toJson(poss.jsonList)
          }.getOrElse("")
        ),
        possiblePostcode = HiddenField(
          key = keys.possibleAddresses.postcode,
          value = form(keys.previousAddress.previousAddress.postcode).value.getOrElse("")
        ),
        hasAddresses = hasAddresses
      )
    }

    def selectPage(
        form: ErrorTransformForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {
      val movedRecently = form(keys.previousAddress.movedRecently).value.map {
        str => MovedHouseOption.parse(str)
      }
      val title = movedRecently match {
        case Some(MovedHouseOption.MovedFromAbroad) => "What was your last UK address before moving abroad?"
        case _ => "What was your previous address?"
      }

      val content = Mustache.render(
        "ordinary/previousAddressSelect",
        selectData(title, form, backUrl, postUrl, lookupUrl, manualUrl, maybePossibleAddress)
      )
      MainStepTemplate(content, title)
    }

    def manualData(
        title: String,
        form: ErrorTransformForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {

      implicit val progressForm = form

      ManualModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = title,
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = lookupUrl,
        postcode = TextField(keys.previousAddress.previousAddress.postcode),
        maLineOne = TextField(keys.previousAddress.previousAddress.manualAddress.lineOne),
        maLineTwo = TextField(keys.previousAddress.previousAddress.manualAddress.lineTwo),
        maLineThree = TextField(keys.previousAddress.previousAddress.manualAddress.lineThree),
        maCity = TextField(keys.previousAddress.previousAddress.manualAddress.city)
      )
    }

    def manualPage(
        form: ErrorTransformForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {
      val movedRecently = form(keys.previousAddress.movedRecently).value.map {
        str => MovedHouseOption.parse(str)
      }
      val title = movedRecently match {
        case Some(MovedHouseOption.MovedFromAbroad) => "What was your last UK address before moving abroad?"
        case _ => "What was your previous address?"
      }

      val content = Mustache.render(
        "ordinary/previousAddressManual",
        manualData(title, form, backUrl, postUrl, lookupUrl)
      )
      MainStepTemplate(content, title)
    }
  }
}
