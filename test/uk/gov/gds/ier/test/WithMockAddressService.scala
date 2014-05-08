package uk.gov.gds.ier.test
import org.specs2.mock.Mockito
import uk.gov.gds.ier.service.AddressService
import org.mockito.Mockito._

trait WithMockAddressService
  extends Mockito{

  val addressService = {

    val mockService = mock[AddressService]
    when (mockService.isNothernIreland("BT7 1AA")).thenReturn(true)
    when (mockService.isNothernIreland("bt7 1aa")).thenReturn(true)
    when (mockService.isNothernIreland("BT71AA")).thenReturn(true)
    when (mockService.isNothernIreland("bt71aa")).thenReturn(true)
    mockService
  }

}
