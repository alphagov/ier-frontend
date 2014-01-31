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
        postcode: Field,
        address: Field
    )

    def lookupPage(
        form:InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String
    ) = {
      val data = LookupModel(
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
      val content = Mustache.render("overseas/lastUkAddressLookup", data)
      MainStepTemplate(content, title)
    }

    def selectPage(
        form: InProgressForm[InprogressOverseas],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        maybePossibleAddress:Option[PossibleAddress]
    ) = {
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

      val data = SelectModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = "5 or 6",
          title = title,
          errorMessages = form.form.globalErrors.map(_.message)
        ),
        lookupUrl = lookupUrl,
        postcode = Field(
          id = keys.lastUkAddress.postcode.asId(),
          name = keys.lastUkAddress.postcode.key,
          value = form(keys.lastUkAddress.postcode).value.getOrElse("")
        ),
        address = SelectField(
          key = keys.lastUkAddress.uprn,
          optionList = options,
          default = SelectOption(
            value = "",
            text = s"${options.size} addresses found"
          )
        )
      )
      val content = Mustache.render("overseas/lastUkAddressSelect", data)
      MainStepTemplate(content, title)
    }
  }
}
