package uk.gov.gds.ier.security

import org.scalatest.{GivenWhenThen, FunSuite}
import org.scalatest.Matchers
import uk.gov.gds.ier.config.Config
import org.specs2.mock.Mockito

class EncryptionTests extends FunSuite with GivenWhenThen with Matchers with Mockito {

  private val jsonToEncrypt = """{key:"value"}"""
  private val mockedConfig = mock[Config]
  mockedConfig.cookiesAesKey returns "J1gs7djvi9/ecFHj0gNRbHHWIreobplsWmXnZiM2reo="
  private val encryptionService = new EncryptionService(new Base64EncodingService, mockedConfig)

  test("Should be able to encrypt/decrypt a block using an AES key") {
    val (encryptionOutput, encryptionIV) = encryptionService.encrypt(jsonToEncrypt)
    encryptionService.decrypt(encryptionOutput, encryptionIV) should be(jsonToEncrypt)
  }

  test("encrypt with AES returns message different than original") {
    val (encryptionOutput, encryptionIV) = encryptionService.encrypt(jsonToEncrypt)
    jsonToEncrypt should not be(encryptionOutput)
  }

  test("encrypt twice with AES returns different encrypted content") {
    val (encryptionOutput1, encryptionIV1) = encryptionService.encrypt(jsonToEncrypt)
    val (encryptionOutput2, encryptionIV2) = encryptionService.encrypt(jsonToEncrypt)
    encryptionOutput1 should not be (encryptionOutput2)
    encryptionIV1 should not be (encryptionIV2)
  }

}
