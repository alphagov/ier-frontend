package uk.gov.gds.ier.controller

import play.api.mvc._
import com.google.inject.Inject
import uk.gov.gds.ier.service.PlacesService
import views._
import controllers._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import scala.Some
import uk.gov.gds.common.model.{Ero, LocalAuthority}
import org.slf4j.LoggerFactory
import uk.gov.gds.ier.session.SessionHandling

class CompleteController @Inject() (val serialiser: JsonSerialiser,
                                    placesService:PlacesService)
    extends Controller
    with WithSerialiser
    with SessionHandling {

  def logger = LoggerFactory.getLogger(this.getClass)

  def complete = NewSession requiredFor {
    implicit request =>
      val authority = request.flash.get("postcode") match {
        case Some("") => None
        case Some(postCode) => placesService.lookupAuthority(postCode)
        case None => None
      }
      val refNum = request.flash.get("refNum")

      Ok(html.complete(authority, refNum))
  }

  def fakeComplete = Action {
    val authority = Some(LocalAuthority("Tower Hamlets Borough Council", Ero(), "00BG", "E09000030"))
    Ok(html.complete(authority, Some("123456")))
  }

}
