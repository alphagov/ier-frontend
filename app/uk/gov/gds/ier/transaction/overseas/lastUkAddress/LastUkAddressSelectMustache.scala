package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.crown.address.WithAddressService
import controllers.step.overseas.routes._
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.model.PossibleAddress

trait LastUkAddressSelectMustache extends StepTemplate[InprogressOverseas] {
  self:WithAddressService
    with WithSerialiser =>

    val title = "What was the UK address where you were last registered to vote?"
    val questionNumber = ""

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

    val mustache = MustacheTemplate("overseas/lastUkAddressSelect") { (form, post, back) =>

      implicit val progressForm = form

      val selectedUprn = form(keys.lastUkAddress.uprn).value

      val storedAddresses = for(
        jsonList <- form(keys.possibleAddresses.jsonList).value;
        postcode <- form(keys.possibleAddresses.postcode).value
      ) yield {
        PossibleAddress(
          jsonList = serialiser.fromJson[Addresses](jsonList),
          postcode = postcode
        )
      }

      val maybeAddresses = storedAddresses.orElse {
        lookupAddresses(form(keys.lastUkAddress.postcode).value)
      }

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

      val addressSelect = SelectField(
        key = keys.lastUkAddress.uprn,
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
          backUrl = back.map{ _.url }.getOrElse(""),
          number = questionNumber,
          title = title,
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = LastUkAddressController.get.url,
        manualUrl = LastUkAddressManualController.get.url,
        postcode = TextField(keys.lastUkAddress.postcode),
        address = addressSelectWithError,
        possibleJsonList = TextField(keys.possibleAddresses.jsonList).copy(
          value = maybeAddresses.map { poss =>
            serialiser.toJson(poss.jsonList)
          }.getOrElse("")
        ),
        possiblePostcode = TextField(keys.possibleAddresses.postcode).copy(
          value = form(keys.lastUkAddress.postcode).value.getOrElse("")
        ),
        hasAddresses = hasAddresses
      )

      MustacheData(data, title)
    }

    private[lastUkAddress] def lookupAddresses(
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
