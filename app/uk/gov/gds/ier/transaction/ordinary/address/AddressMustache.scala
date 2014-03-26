package uk.gov.gds.ier.transaction.ordinary.address

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{InprogressOrdinary, PossibleAddress}
import uk.gov.gds.ier.validation.ErrorTransformForm

trait AddressMustache {
  self: WithSerialiser =>

  object AddressMustache extends StepMustache {

    val title = "What is your address?"
    val questionNumber = "6 of 11"

    case class LookupModel (
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

    def lookupData(
        form: ErrorTransformForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String) = {
     LookupModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = title,
          errorMessages = form.globalErrors.map(_.message)
        ),
        postcode = Field(
          id = keys.address.postcode.asId(),
          name = keys.address.postcode.key,
          value = form(keys.address.postcode).value.getOrElse(""),
          classes = if (form(keys.address.postcode).hasErrors) {
            "invalid"
          } else {
            ""
          }
        )
      )
    }

    def lookupPage(
        form: ErrorTransformForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String) = {

      val content = Mustache.render(
        "ordinary/addressLookup",
        lookupData(form, backUrl, postUrl)
      )
      MainStepTemplate(content, title)
    }

    def selectData(
        form: ErrorTransformForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {

      implicit val progressForm = form

      val selectedUprn = form(keys.address.uprn).value

      val options = maybePossibleAddress.map { possibleAddress =>
        possibleAddress.jsonList.addresses
      }.getOrElse(List.empty).map { address =>
        SelectOption(
          value = address.uprn.getOrElse(""),
          text = address.addressLine.getOrElse(""),
          selected = if (address.uprn == selectedUprn) {
            "selected=\"selected\""
          } else ""
        )
      }

      val hasAddresses = maybePossibleAddress.exists (!_.jsonList.addresses.isEmpty)

      val addressSelect = SelectField(
        key = keys.address.uprn,
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
        postcode = TextField(keys.address.postcode),
        address = addressSelectWithError,
        possibleJsonList = TextField(keys.possibleAddresses.jsonList).copy(
          value = maybePossibleAddress.map { poss =>
            serialiser.toJson(poss.jsonList)
          }.getOrElse("")
        ),
        possiblePostcode = TextField(keys.possibleAddresses.postcode).copy(
          value = form(keys.address.postcode).value.getOrElse("")
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

      val content = Mustache.render(
        "ordinary/addressSelect",
        selectData(form, backUrl, postUrl, lookupUrl, manualUrl, maybePossibleAddress)
      )
      MainStepTemplate(content, title)
    }

    def manualData(
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
        postcode = TextField(keys.address.postcode),
        maLineOne = TextField(keys.address.manualAddress.lineOne),
        maLineTwo = TextField(keys.address.manualAddress.lineTwo),
        maLineThree = TextField(keys.address.manualAddress.lineThree),
        maCity = TextField(keys.address.manualAddress.city)
      )
    }

    def manualPage(
        form: ErrorTransformForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {

      val content = Mustache.render(
        "ordinary/addressManual",
        manualData(form, backUrl, postUrl, lookupUrl)
      )
      MainStepTemplate(content, title)
    }
  }
}
