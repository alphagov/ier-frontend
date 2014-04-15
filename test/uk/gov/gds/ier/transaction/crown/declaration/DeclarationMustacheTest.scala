package uk.gov.gds.ier.transaction.crown.declaration

import org.scalatest.{GivenWhenThen, Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import controllers.step.crown.routes._
import uk.gov.gds.ier.service.PlacesService
import uk.gov.gds.common.model.{Ero, LocalAuthority}
import org.specs2.mock.Mockito
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import uk.gov.gds.ier.model.{Job, PartialAddress, LastUkAddress}

class DeclarationMustacheTest
  extends FlatSpec
  with Matchers
  with GivenWhenThen
  with Mockito
  with DeclarationPdfForms
  with TestHelpers
  with DeclarationPdfMustache
  with WithSerialiser
  with WithPlacesService {

  val serialiser = mock[JsonSerialiser]
  val placesService = mock[PlacesService]
  placesService.lookupAuthority("WR26NJ") returns Some(
    new LocalAuthority(name = "Haringey Borough Council", ero = Ero(), opcsId = ""))

  it should "construct model for declaration step with election authority details from mocked service" in {
    val emptyApplicationForm = declarationPdfForm
    val emptyApplication = InprogressCrown()
    val model: DeclarationPdfModel = mustache.data(
      declarationPdfForm.fill(inprogressApplicationWithPostcode("WR26NJ")),
      DeclarationPdfController.post,
      Some(NinoController.get),
      emptyApplication
    ).data.asInstanceOf[DeclarationPdfModel]

    model.question.title should be("Download your service declaration form")
    model.question.postUrl should be("/register-to-vote/crown/declaration-pdf")
    model.question.backUrl should be("/register-to-vote/crown/nino")

    model.authorityName should be("Haringey Borough Council electoral registration office")
    model.showAuthorityUrl should be(false)
  }

  private def inprogressApplicationWithPostcode(postcode: String) = {
    InprogressCrown().copy(
        address = Some(LastUkAddress(
          hasUkAddress = Some(true),
          address = Some(PartialAddress(
            addressLine = None,
            uprn = None,
            manualAddress = None,
            postcode = postcode)))))
  }
}
