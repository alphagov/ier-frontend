package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{InprogressOrdinary, PossibleAddress}
import uk.gov.gds.ier.validation.{InProgressForm, Key}

trait PreviousAddressMustache {
  self: WithSerialiser =>

  object PreviousAddressMustache extends StepMustache {

    val title = "Have you moved out from another UK address in the last 12 months?"
    val questionNumber = "8 of 11"

    case class PostcodeModel (
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
        manualAddress: Field
    )

    def postcodeData(
        form:InProgressForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String) = {
      implicit val progressForm = form.form
      val modelData = PostcodeModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = title,
          errorMessages = form.form.globalErrors.map(_.message)
        ),
        postcode = TextField(keys.previousAddress.postcode)
      )
      println(modelData)
      modelData
    }

    def postcodePage(
        form: InProgressForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String) = {

      val content = Mustache.render(
        "ordinary/previousAddressPostcode",
        postcodeData(form, backUrl, postUrl)
      )
      MainStepTemplate(content, title)
    }

    def selectData(
        form: InProgressForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {

      implicit val progressForm = form.form

      val selectedUprn = form(keys.previousAddress.previousAddress.uprn).value

      val options = for (address <- maybePossibleAddress.map(_.jsonList.addresses).toList.flatten)
      yield SelectOption(
          value = address.uprn.getOrElse(""),
          text = address.addressLine.getOrElse(""),
          selected = if (address.uprn == selectedUprn) {
            "selected=\"selected\""
          } else ""
        )

      val hasAddresses = maybePossibleAddress.exists(!_.jsonList.addresses.isEmpty)

      val addressSelect = SelectField(
        key = keys.previousAddress.uprn,
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
        postcode = TextField(keys.previousAddress.postcode),
        address = addressSelectWithError,  // this is model data for <select>
        possibleJsonList = HiddenField(
          key = keys.possibleAddresses.jsonList,
          value = maybePossibleAddress.map { poss =>
            serialiser.toJson(poss.jsonList)
          }.getOrElse("")
        ),
        possiblePostcode = HiddenField(
          key = keys.possibleAddresses.postcode,
          value = form(keys.previousAddress.postcode).value.getOrElse("")
        ),
        hasAddresses = hasAddresses
      )
    }

    def selectPage(
        form: InProgressForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {

      val content = Mustache.render(
        "ordinary/previousAddressSelect",
        selectData(form, backUrl, postUrl, lookupUrl, manualUrl, maybePossibleAddress)
      )
      MainStepTemplate(content, title)
    }

    def manualData(
        form: InProgressForm[InprogressOrdinary],
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
        postcode = TextField(keys.previousAddress.postcode),
        manualAddress = TextField(keys.previousAddress.manualAddress)
      )
    }

    def manualPage(
        form: InProgressForm[InprogressOrdinary],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {

      val content = Mustache.render(
        "ordinary/previousAddressManual",
        manualData(form, backUrl, postUrl, lookupUrl)
      )
      MainStepTemplate(content, title)
    }
  }
}
