package uk.gov.gds.ier.transaction.overseas.previouslyRegistered

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait PreviousRegisteredMustache extends StepMustache {

  case class PreviouslyRegisteredModel(postUrl:String = "",
                                       globalErrors:Seq[String] = List.empty,
                                       backUrl:String = "")

  def previousRegisteredMustache(form:ErrorTransformForm[InprogressOverseas],
                                 post: Call,
                                 back: Option[Call]): Html = {
    val data = PreviouslyRegisteredModel(
      post.url,
      form.globalErrors.map{ _.message },
      back.map { call => call.url }.getOrElse("")
    )
    Mustache.render("overseas/previouslyRegistered", data)
  }
}
