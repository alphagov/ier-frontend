package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.{InprogressOverseas, PossibleAddress}
import uk.gov.gds.ier.validation.{InProgressForm, Key}

trait LastUkAddressMustache {

  object LastUkAddressMustache extends StepMustache {

    val title = "What was the UK address where you were last registered to vote?"

    case class LookupModel (
        question: Question,
        postcode: Field
    )

    case class SelectModel (
        question: Question,
        lookupUrl: String,
        manualUrl: String,
        postcode: Field,
        address: Field
    )

    case class ManualModel (
        question: Question,
        lookupUrl: String,
        postcode: Field,
        manualAddress: Field
    )

    def lookupData(
        form:InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String) = {
     LookupModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = "5 or 6",
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

      SelectModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = "5 or 6",
          title = title,
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = lookupUrl,
        manualUrl = manualUrl,
        postcode = TextField(keys.lastUkAddress.postcode),
        address = SelectField(
          key = keys.lastUkAddress.uprn,
          optionList = options,
          default = SelectOption(value = "", text = s"${options.size} addresses found")
        )
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
          number = "5 or 6",
          title = title,
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = lookupUrl,
        postcode = TextField(keys.lastUkAddress.postcode),
        manualAddress = TextField(keys.lastUkAddress.manualAddress)
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
