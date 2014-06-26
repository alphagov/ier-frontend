package uk.gov.gds.ier.transaction.overseas.parentsAddress

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.service.WithAddressService
import controllers.step.overseas.routes._
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.model.PossibleAddress

trait ParentsAddressSelectMustache extends StepTemplate[InprogressOverseas] {
  self:WithAddressService
    with WithSerialiser =>

  val title = "What was your parent or guardian's last UK address?"
  val questionNumber = ""

  case class SelectModel (
      question: Question,
      lookupUrl: String,
      manualUrl: String,
      postcode: Field,
      address: Field,
      possibleJsonList: Field,
      possiblePostcode: Field,
      hasAddresses: Boolean,
      hasAuthority: Boolean
  ) extends MustacheData


  val mustache = MustacheTemplate("overseas/parentsAddressSelect") { (form, post) =>

    implicit val progressForm = form

    val selectedUprn = form(keys.parentsAddress.uprn).value
    val postcode = form(keys.parentsAddress.postcode).value.orElse {
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

    val maybeAddresses = storedAddresses orElse lookupAddresses(postcode)

    val options = maybeAddresses.map { possibleAddress =>
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

    val hasAddresses = maybeAddresses.exists { poss =>
      !poss.jsonList.addresses.isEmpty
    }

    val hasAuthority = hasAddresses || addressService.validAuthority(postcode)

    val addressSelect = SelectField(
      key = keys.parentsAddress.uprn,
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
      lookupUrl = ParentsAddressController.get.url,
      manualUrl = ParentsAddressManualController.get.url,
      postcode = TextField(keys.parentsAddress.postcode),
      address = addressSelectWithError,
      possibleJsonList = TextField(keys.possibleAddresses.jsonList).copy(
        value = maybeAddresses.map { poss =>
          serialiser.toJson(poss.jsonList)
        }.getOrElse("")
      ),
      possiblePostcode = TextField(keys.possibleAddresses.postcode).copy(
        value = form(keys.parentsAddress.postcode).value.getOrElse("")
      ),
      hasAddresses = hasAddresses,
      hasAuthority = hasAuthority
    )
  }

  private[parentsAddress] def lookupAddresses(
      maybePostcode:Option[String]): Option[PossibleAddress] = {

    maybePostcode.map { postcode =>
      val addresses = addressService.lookupPartialAddress(postcode)
      PossibleAddress(
        jsonList = Addresses(addresses),
        postcode = postcode
      )
    }
  }

}
