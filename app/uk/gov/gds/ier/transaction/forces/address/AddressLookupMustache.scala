package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait AddressLookupMustache extends StepTemplate[InprogressForces] {

  val questionNumber = "2"

  case class LookupModel (
      question: Question,
      postcode: Field)

  val mustache = MustacheTemplate("forces/addressLookup") { (form, postUrl, backUrl) =>
    implicit val progressForm = form

    val title = "What is your UK address?"

    val data = LookupModel(
      question = Question(
        postUrl = postUrl.url,
        backUrl = backUrl.map{ _.url }.getOrElse(""),
        number = questionNumber,
        title = title,
        errorMessages = form.globalErrors.map(_.message)
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
      )

    )

    MustacheData(data, title)
  }
}

