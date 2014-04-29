package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait AddressLookupMustache extends StepTemplate[InprogressForces] {

  val questionNumber = "2"

  case class LookupModel (
      question: Question,
      postcode: Field)

  val mustache = MustacheTemplate("forces/addressLookup") { (form, postUrl) =>
    implicit val progressForm = form

    val title = "What is your UK address?"

    val data = LookupModel(
      question = Question(
        postUrl = postUrl.url,
        number = questionNumber,
        title = title,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = Field(
        id = keys.address.address.postcode.asId(),
        name = keys.address.address.postcode.key,
        value = form(keys.address.address.postcode).value.getOrElse(""),
        classes = if (form(keys.address.address.postcode).hasErrors) {
          "invalid"
        } else {
          ""
        }
      )

    )

    MustacheData(data, title)
  }
}

