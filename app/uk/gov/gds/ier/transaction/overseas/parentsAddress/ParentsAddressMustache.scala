package uk.gov.gds.ier.transaction.overseas.parentsAddress

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{InprogressOverseas, PossibleAddress}
import uk.gov.gds.ier.validation.InProgressForm

trait ParentsAddressMustache {
  self: WithSerialiser =>

  object ParentsAddressMustache extends StepMustache {

    val title = "What was your parent or guardian's last UK address?"
    val questionNumber = ""

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
        maCity: Field,
        maCounty: Field
    )

    def lookupData(
        form:InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String) = {
     LookupModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = title,
          errorMessages = form.form.globalErrors.map(_.message)
        ),
        postcode = Field(
          id = keys.parentsAddress.postcode.asId(),
          name = keys.parentsAddress.postcode.key,
          value = form(keys.parentsAddress.postcode).value.getOrElse(""),
          classes = if (form(keys.parentsAddress.postcode).hasErrors) {
            "invalid"
          } else {
            ""
          }
        )
      )
    }

    def lookupPage(
        form:InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String) = {

      val content = Mustache.render(
        "overseas/parentsAddressLookup",
        lookupData(form, backUrl, postUrl)
      )
      MainStepTemplate(content, title)
    }

    def selectData(
        form: InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {

      implicit val progressForm = form.form

      val selectedUprn = form(keys.parentsAddress.uprn).value

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

      val hasAddresses = maybePossibleAddress.exists { poss =>
        !poss.jsonList.addresses.isEmpty
      }

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
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = title,
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = lookupUrl,
        manualUrl = manualUrl,
        postcode = TextField(keys.parentsAddress.postcode),
        address = addressSelectWithError,
        possibleJsonList = TextField(keys.possibleAddresses.jsonList).copy(
          value = maybePossibleAddress.map { poss =>
            serialiser.toJson(poss.jsonList)
          }.getOrElse("")
        ),
        possiblePostcode = TextField(keys.possibleAddresses.postcode).copy(
          value = form(keys.parentsAddress.postcode).value.getOrElse("")
        ),
        hasAddresses = hasAddresses
      )
    }

    def selectPage(
        form: InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {

      val content = Mustache.render(
        "overseas/parentsAddressSelect",
        selectData(form, backUrl, postUrl, lookupUrl, manualUrl, maybePossibleAddress)
      )
      MainStepTemplate(content, title)
    }

    def manualData(
        form: InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {

      implicit val progressForm = form.form

      ManualModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = title,
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = lookupUrl,
        postcode = TextField(keys.parentsAddress.postcode),
        maLineOne = TextField(keys.parentsAddress.manualAddress.lineOne),
        maLineTwo = TextField(keys.parentsAddress.manualAddress.lineTwo),
        maCity = TextField(keys.parentsAddress.manualAddress.city),
        maCounty = TextField(keys.parentsAddress.manualAddress.country)
      )
    }

    def manualPage(
        form: InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {

      val content = Mustache.render(
        "overseas/parentsAddressManual",
        manualData(form, backUrl, postUrl, lookupUrl)
      )
      MainStepTemplate(content, title)
    }
  }
}
