package uk.gov.gds.ier.transaction.overseas.previouslyRegistered

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import org.jba.Mustache
import controllers.step.routes._

trait PreviousRegisteredMustache {

  case class PreviouslyRegisteredModel(postUrl:String = "", globalErrors:Seq[String] = List.empty, backUrl:String = "")

  def previousRegisteredMustache(form:ErrorTransformForm[InprogressOverseas], call: Call): Html = {
    val data = PreviouslyRegisteredModel(
      call.url,
      form.globalErrors map{ _.message },
      CountryController.get.url
    )
    Mustache.render("overseas/previouslyRegistered", data)
  }
}
