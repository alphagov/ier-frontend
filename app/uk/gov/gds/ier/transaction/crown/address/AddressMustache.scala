package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{InprogressCrown, PossibleAddress}
import uk.gov.gds.ier.validation.InProgressForm

trait AddressMustache {
  self: WithSerialiser =>

  object AddressMustache extends StepMustache {

    val questionNumber = "2"

    case class LookupModel (
        question: Question,
        postcode: Field,
        hasUkAddress: Field
    )

    case class SelectModel (
        question: Question,
        lookupUrl: String,
        manualUrl: String,
        postcode: Field,
        address: Field,
        possibleJsonList: Field,
        possiblePostcode: Field,
        hasAddresses: Boolean,
        hasUkAddress: Field
    )

    case class ManualModel (
      question: Question,
      lookupUrl: String,
      postcode: Field,
      maLineOne: Field,
      maLineTwo: Field,
      maLineThree: Field,
      maCity: Field,
      hasUkAddress: Field
    )

    def lookupData(
        form:InProgressForm[InprogressCrown],
        backUrl: String,
        postUrl: String) = {

      implicit val progressForm = form.form

      LookupModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = showAddressPageTitle(form(keys.hasUkAddress).value),
          errorMessages = form.form.globalErrors.map(_.message)
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
        ),
        hasUkAddress = HiddenField(
          key = keys.hasUkAddress,
          value = form(keys.hasUkAddress).value.getOrElse("")
        )
      )
    }

    def lookupPage(
        form:InProgressForm[InprogressCrown],
        backUrl: String,
        postUrl: String) = {

      val content = Mustache.render(
        "crown/addressLookup",
        lookupData(form, backUrl, postUrl)
      )
      MainStepTemplate(content, showAddressPageTitle(form(keys.hasUkAddress).value))
    }

    def selectData(
        form: InProgressForm[InprogressCrown],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {

      implicit val progressForm = form.form

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

      val hasAddresses = maybePossibleAddress.exists { poss =>
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
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = showAddressPageTitle(form(keys.hasUkAddress).value),
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = lookupUrl,
        manualUrl = manualUrl,
        postcode = TextField(keys.address.postcode),
        address = addressSelectWithError,
        possibleJsonList = HiddenField(
          key = keys.possibleAddresses.jsonList,
          value = maybePossibleAddress.map { poss =>
            serialiser.toJson(poss.jsonList)
          }.getOrElse("")
        ),
        possiblePostcode = HiddenField(
          key = keys.possibleAddresses.postcode,
          value = form(keys.address.postcode).value.getOrElse("")
        ),
        hasAddresses = hasAddresses,
        hasUkAddress = HiddenField(
          key = keys.hasUkAddress,
          value = form(keys.hasUkAddress).value.getOrElse("")
        )      
      )
    }

    def selectPage(
        form: InProgressForm[InprogressCrown],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {

      val content = Mustache.render(
        "crown/addressSelect",
        selectData(form, backUrl, postUrl, lookupUrl, manualUrl, maybePossibleAddress)
      )
      MainStepTemplate(content, showAddressPageTitle(form(keys.hasUkAddress).value))
    }

    def manualData(
        form: InProgressForm[InprogressCrown],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {

      implicit val progressForm = form.form

      ManualModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = showAddressPageTitle(form(keys.hasUkAddress).value),
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = lookupUrl,
        postcode = TextField(keys.address.postcode),
        maLineOne = TextField(keys.address.manualAddress.lineOne),
        maLineTwo = TextField(keys.address.manualAddress.lineTwo),
        maLineThree = TextField(keys.address.manualAddress.lineThree),
        maCity = TextField(keys.address.manualAddress.city),
        hasUkAddress = HiddenField(
          key = keys.hasUkAddress,
          value = form(keys.hasUkAddress).value.getOrElse("")
        )       
      )
    }

    def manualPage(
        form: InProgressForm[InprogressCrown],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {

      val content = Mustache.render(
        "crown/addressManual",
        manualData(form, backUrl, postUrl, lookupUrl)
      )
      MainStepTemplate(content, showAddressPageTitle(form(keys.hasUkAddress).value))
    }

    private def showAddressPageTitle (hasUkAddress: Option[String]): String = {
      hasUkAddress match {
        case Some(hasUkAddress) if (hasUkAddress.toBoolean) => "What is your UK address?"
        case _ => "What is your last UK address?"
      }
    }
  }
}
