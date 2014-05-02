package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{PossibleAddress, Addresses}
import controllers.step.forces.routes.{PreviousAddressManualController, PreviousAddressPostcodeController}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.service.WithAddressService

trait PreviousAddressSelectMustache
  extends StepTemplate[InprogressForces] {
    self: WithAddressService
    with WithSerialiser =>

  val title = "What was your previous UK address?"
  val questionNumber = "3"

  case class SelectModel (
    question: Question,
    lookupUrl: String,
    manualUrl: String,
    postcode: Field,
    address: Field,
    possibleJsonList: Field,
    possiblePostcode: Field,
    hasAddresses: Boolean
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/previousAddressSelect") {
    (form, post, application) =>

    implicit val progressForm = form

    val selectedUprn = form(keys.previousAddress.uprn).value
    val postcode = form(keys.previousAddress.postcode).value

    val storedAddresses = for(
      jsonList <- form(keys.possibleAddresses.jsonList).value;
      postcode <- form(keys.possibleAddresses.postcode).value
    ) yield {
      PossibleAddress(
        jsonList = serialiser.fromJson[Addresses](jsonList),
        postcode = postcode
      )
    }

    val possibleAddresses = storedAddresses orElse postcode.map { pc =>
      val addresses = addressService.lookupPartialAddress(pc)
      PossibleAddress(
        jsonList = Addresses(addresses),
        postcode = pc
      )
    }

    val options = for (
      address <- possibleAddresses.map {_.jsonList.addresses}.toList.flatten
    ) yield {
      SelectOption(
        value = address.uprn.getOrElse(""),
        text = address.addressLine.getOrElse(""),
        selected = if (address.uprn == selectedUprn) {
          "selected=\"selected\""
        } else ""
      )
    }

    val hasAddresses = possibleAddresses.exists { possible =>
      !possible.jsonList.addresses.isEmpty
    }

    val addressSelect = SelectField(
      key = keys.previousAddress.uprn,
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
        postUrl = post.url,
        number = questionNumber,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = PreviousAddressPostcodeController.get.url,
      manualUrl = PreviousAddressManualController.get.url,
      postcode = TextField(keys.previousAddress.postcode),
      address = addressSelectWithError,  // this is model data for <select>
      possibleJsonList = HiddenField(
        key = keys.possibleAddresses.jsonList,
        value = possibleAddresses.map { poss =>
          serialiser.toJson(poss.jsonList)
        }.getOrElse("")
      ),
      possiblePostcode = HiddenField(
        key = keys.possibleAddresses.postcode,
        value = form(keys.previousAddress.postcode).value.getOrElse("")
      ),
      hasAddresses = hasAddresses
    )
  }
}
