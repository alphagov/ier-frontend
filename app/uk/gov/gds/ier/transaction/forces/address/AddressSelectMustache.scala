package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.step.StepTemplate
import controllers.step.forces.routes.{AddressController, AddressManualController}
import uk.gov.gds.ier.model.{PossibleAddress, Addresses}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.service.WithAddressService

trait AddressSelectMustache extends StepTemplate[InprogressForces] {
    self:WithAddressService
    with WithSerialiser =>

  val questionNumber = "2"

  case class SelectModel (
     question: Question,
     lookupUrl: String,
     manualUrl: String,
     postcode: Field,
     address: Field,
     possibleJsonList: Field,
     possiblePostcode: Field,
     hasAddresses: Boolean)

  val mustache = MustacheTemplate("forces/addressSelect") { (form, postUrl) =>
    implicit val progressForm = form

    val title = "What is your UK address?"

    val selectedUprn = form(keys.address.uprn).value
    val postcode = form(keys.address.postcode).value

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

    val hasAddresses = possibleAddresses.exists { poss =>
      !poss.jsonList.addresses.isEmpty
    }

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
        postUrl = postUrl.url,
        number = questionNumber,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = AddressController.get.url,
      manualUrl = AddressManualController.get.url,
      postcode = TextField(keys.address.postcode),
      address = addressSelectWithError,
      possibleJsonList = HiddenField(
        key = keys.possibleAddresses.jsonList,
        value = possibleAddresses.map { poss =>
          serialiser.toJson(poss.jsonList)
        }.getOrElse("")
      ),
      possiblePostcode = HiddenField(
        key = keys.possibleAddresses.postcode,
        value = form(keys.address.postcode).value.getOrElse("")
      ),
      hasAddresses = hasAddresses
    )

    MustacheData(data, title)
  }
}

