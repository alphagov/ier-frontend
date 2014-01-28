package uk.gov.gds.ier.transaction.overseas.address

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait OverseasAddressMustache extends StepMustache {

  case class OverseasAddressModel(question:Question, country: Field, address: Field)

  def addressMustache(form:ErrorTransformForm[InprogressOverseas],
                                 post: Call,
                                 back: Option[Call]): Html = {

    val data = OverseasAddressModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "11",
        title = "Where do you live?"
      ),
      country = Field(
        name = keys.country.key,
        id = keys.country.asId(),
        classes = if (form(keys.country.key).hasErrors) "invalid" else ""
      ),
      address = Field(
        name = keys.address.key,
        id = keys.address.asId(),
        classes = if (form(keys.address.key).hasErrors) "invalid" else ""
      )
    )
    val content = Mustache.render("overseas/address", data)
    MainStepTemplate(content, data.question.title)
  }
}
