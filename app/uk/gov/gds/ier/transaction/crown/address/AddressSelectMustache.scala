package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import controllers.step.crown.routes.{AddressController, AddressManualController}
import uk.gov.gds.ier.model.{HasAddressOption, PossibleAddress, Addresses}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.service.WithAddressService

trait AddressSelectMustache extends StepTemplate[InprogressCrown] {
    self:WithAddressService
    with WithSerialiser =>

  private def pageTitle(hasAddress: Option[String]): String = {
    HasAddressOption.parse(hasAddress.getOrElse("")) match{
      case HasAddressOption.YesAndLivingThere | HasAddressOption.YesAndNotLivingThere => "What is your UK address?"
      case _ => "What was your last UK address?"
    }
  }

  case class SelectModel (
      question: Question,
      lookupUrl: String,
      manualUrl: String,
      postcode: Field,
      address: Field,
      possibleJsonList: Field,
      possiblePostcode: Field,
      hasAddresses: Boolean,
      hasAddress: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/addressSelect") { (form, postUrl) =>
    implicit val progressForm = form
  
    val title = pageTitle(form(keys.hasAddress).value)

    val selectedUprn = form(keys.address.uprn).value
    val postcode = form(keys.address.postcode).value.orElse {
      form(keys.possibleAddresses.postcode).value
    }

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

    SelectModel(
      question = Question(
        postUrl = postUrl.url,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = AddressController.get.url,
      manualUrl = AddressManualController.get.url,
      postcode = TextField(keys.address.postcode, default = postcode),
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
      hasAddresses = hasAddresses,
      hasAddress = HiddenField(
        key = keys.hasAddress,
        value = form(keys.hasAddress).value.getOrElse("")
      )
    )
  }
}

