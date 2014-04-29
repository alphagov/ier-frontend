package uk.gov.gds.ier.transaction.ordinary.address

import controllers.step.ordinary.routes.{AddressController, AddressManualController}
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.model.{PossibleAddress, Addresses}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait AddressSelectMustache extends StepTemplate[InprogressOrdinary] {
  val serialiser:JsonSerialiser
  val addressService:AddressService

  val title = "What is your address?"
  val questionNumber = "6 of 11"

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

  val mustache = MustacheTemplate("ordinary/addressSelect") {
    (form, post) =>

    implicit val progressForm = form

    val selectedUprn = form(keys.address.uprn).value
    val postcode = form(keys.address.postcode).value.orElse {
      form(keys.possibleAddresses.postcode).value
    }

    val storedAddresses = for(
      jsonList <- form(keys.possibleAddresses.jsonList).value;
      postcode <- postcode
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

    val options = possibleAddresses.map { possibleAddress =>
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

    val hasAddresses = possibleAddresses.exists (!_.jsonList.addresses.isEmpty)

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

    val data = SelectModel(
      question = Question(
        postUrl = post.url,
        number = questionNumber,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = AddressController.get.url,
      manualUrl = AddressManualController.get.url,
      postcode = TextField(keys.address.postcode, default = postcode),
      address = addressSelectWithError,
      possibleJsonList = TextField(keys.possibleAddresses.jsonList).copy(
        value = possibleAddresses.map { poss =>
          serialiser.toJson(poss.jsonList)
        }.getOrElse("")
      ),
      possiblePostcode = TextField(keys.possibleAddresses.postcode).copy(
        value = form(keys.address.postcode).value.getOrElse("")
      ),
      hasAddresses = hasAddresses
    )

    MustacheData(data, title)
  }
}
