package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{InprogressOverseas, PossibleAddress}
import uk.gov.gds.ier.validation.{InProgressForm, Key}

trait LastUkAddressMustache {
  self: WithSerialiser =>

  object LastUkAddressMustache extends StepMustache {

    val title = "What was the UK address where you were last registered to vote?"
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
        maLineThree: Field,
        maCity: Field
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
          id = keys.lastUkAddress.postcode.asId(),
          name = keys.lastUkAddress.postcode.key,
          value = form(keys.lastUkAddress.postcode).value.getOrElse(""),
          classes = if (form(keys.lastUkAddress.postcode).hasErrors) {
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
        "overseas/lastUkAddressLookup",
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

      val selectedUprn = form(keys.lastUkAddress.uprn).value

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
        postcode = TextField(keys.lastUkAddress.postcode),
        address = addressSelectWithError,
        possibleJsonList = TextField(keys.possibleAddresses.jsonList).copy(
          value = maybePossibleAddress.map { poss =>
            serialiser.toJson(poss.jsonList)
          }.getOrElse("")
        ),
        possiblePostcode = TextField(keys.possibleAddresses.postcode).copy(
          value = form(keys.lastUkAddress.postcode).value.getOrElse("")
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
        "overseas/lastUkAddressSelect",
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
        postcode = TextField(keys.lastUkAddress.postcode),
        maLineOne = TextField(keys.lastUkAddress.manualAddress.lineOne),
        maLineTwo = TextField(keys.lastUkAddress.manualAddress.lineTwo),
        maLineThree = TextField(keys.lastUkAddress.manualAddress.lineThree),
        maCity = TextField(keys.lastUkAddress.manualAddress.city)
      )
    }

    def manualPage(
        form: InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {

      val content = Mustache.render(
        "overseas/lastUkAddressManual",
        manualData(form, backUrl, postUrl, lookupUrl)
      )
      MainStepTemplate(content, title)
    }
  }
}
