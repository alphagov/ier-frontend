package uk.gov.gds.ier.localAuthority

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import uk.gov.gds.ier.validation.ErrorMessages
import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.test.WithMockConfig
import uk.gov.gds.ier.test.WithMockRemoteAssets
import uk.gov.gds.ier.serialiser.WithSerialiser

class LocalAuthorityMustacheTests
  extends FlatSpec
  with Matchers
  with LocalAuthorityLookupForm
  with LocalAuthorityMustache
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with WithMockConfig
  with WithMockRemoteAssets
  with WithSerialiser{

  val serialiser = jsonSerialiser

  it should "" in runningApp {
    val emptyLookupForm = localAuthorityLookupForm
  }

}