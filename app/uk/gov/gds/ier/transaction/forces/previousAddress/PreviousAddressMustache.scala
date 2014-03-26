package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{InprogressForces, PossibleAddress}
import uk.gov.gds.ier.validation.ErrorTransformForm

trait PreviousAddressMustache {
  self: WithSerialiser =>

  object PreviousAddressMustache extends StepMustache {

    val title = "Have you moved from another UK address in the last 12 months?"
    val questionNumber = "3"

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
      maLineOne: Field,
      maLineTwo: Field,
      maLineThree: Field,
      maCity: Field
      )

    def postcodeData(
        form: ErrorTransformForm[InprogressForces],
        backUrl: String,
        postUrl: String) = {
      implicit val progressForm = form
      val modelData = PostcodeModel(
        question = Question(
          postUrl = postUrl,
          backUrl = backUrl,
          number = questionNumber,
          title = title,
          errorMessages = form.globalErrors.map(_.message)
        ),
        postcode = TextField(keys.previousAddress.postcode)
      )
      modelData
    }

    def postcodePage(
        form: ErrorTransformForm[InprogressForces],
        backUrl: String,
        postUrl: String) = {

      val content = Mustache.render(
        "forces/previousAddressPostcode",
        postcodeData(form, backUrl, postUrl)
      )
      MainStepTemplate(content, title)
    }

    def selectData(
        form: ErrorTransformForm[InprogressForces],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {

      implicit val progressForm = form

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
        form: ErrorTransformForm[InprogressForces],
        backUrl: String,
        postUrl: String,
        lookupUrl: String,
        manualUrl: String,
        maybePossibleAddress:Option[PossibleAddress]) = {

      val content = Mustache.render(
        "forces/previousAddressSelect",
        selectData(form, backUrl, postUrl, lookupUrl, manualUrl, maybePossibleAddress)
      )
      MainStepTemplate(content, title)
    }

    def manualData(
        form: ErrorTransformForm[InprogressForces],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {

      implicit val progressForm = form

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
        maLineOne = TextField(keys.previousAddress.manualAddress.lineOne),
        maLineTwo = TextField(keys.previousAddress.manualAddress.lineTwo),
        maLineThree = TextField(keys.previousAddress.manualAddress.lineThree),
        maCity = TextField(keys.previousAddress.manualAddress.city)
      )
    }

    def manualPage(
        form: ErrorTransformForm[InprogressForces],
        backUrl: String,
        postUrl: String,
        lookupUrl: String) = {

      val content = Mustache.render(
        "forces/previousAddressManual",
        manualData(form, backUrl, postUrl, lookupUrl)
      )
      MainStepTemplate(content, title)
    }
  }
}
