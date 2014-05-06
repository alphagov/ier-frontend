package uk.gov.gds.ier.test
import org.specs2.mock.Mockito
import uk.gov.gds.ier.service.AddressService
import org.mockito.Mockito._

trait WithMockAddressService
  extends Mockito{

  val addressService = {

    val mockService = mock[AddressService]
    when (mockService.isNothernIreland("bt7 1aa")).thenReturn(true)
    mockService
  }

}
