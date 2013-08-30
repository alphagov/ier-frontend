package uk.gov.gds.ier.controller

import com.google.inject.Inject
import uk.gov.gds.ier.service.PostcodeAnywhereService
import play.api.mvc.Controller
import play.api.mvc.Action
import uk.gov.gds.ier.client.ApiResults
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import uk.gov.gds.common.http.ApiResponseException
import uk.gov.gds.ier.exception.PostcodeLookupFailedException

class PostcodeController @Inject()(postcodeAnywhere: PostcodeAnywhereService, serialiser: JsonSerialiser)
  extends Controller with ApiResults with WithSerialiser {

  def toJson(obj: AnyRef): String = serialiser.toJson(obj)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson[T](json)

  def lookup(postcode: String) = Action {
    implicit request =>
      try {
        okResult("addresses" -> postcodeAnywhere.lookup(postcode))
      } catch {
        case e:PostcodeLookupFailedException => serverErrorResult("error" -> e.getMessage)
      }
  }
}
