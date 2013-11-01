package uk.gov.gds.ier.controller

import com.google.inject.Inject
import uk.gov.gds.ier.service.PlacesService
import play.api.mvc.Controller
import play.api.mvc.Action
import uk.gov.gds.ier.client.ApiResults
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import uk.gov.gds.common.http.ApiResponseException
import uk.gov.gds.ier.exception.PostcodeLookupFailedException
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.model.{CrossField, AllErrorsForm, AllErrors}

class PostcodeController @Inject()(postcodeAnywhere: PlacesService, val serialiser: JsonSerialiser)
  extends Controller with ApiResults with WithSerialiser with IerForms {

  def lookupAddress(postcode: String) = Action {
    implicit request =>
      postcodeForm.bind(Map("postcode" -> postcode)).fold(
        errors => badResult("errors" -> errors.errorsAsMap),
        postcode =>
          try {
            val addresses = postcodeAnywhere.lookupAddress(postcode)
            okResult("addresses" -> addresses)
          } catch {
            case e:PostcodeLookupFailedException => serverErrorResult("error" -> e.getMessage)
          }
      )
  }

  def lookupAuthority(postcode: String) = Action {
    implicit request =>
      postcodeForm.bind(Map("postcode" -> postcode)).fold(
        errors => badResult("errors" -> errors.errorsAsMap),
        postcode =>
          try {
            okResult(postcodeAnywhere.lookupAuthority(postcode))
          } catch {
            case e:PostcodeLookupFailedException => serverErrorResult("error" -> e.getMessage)
          }
      )
  }
}
