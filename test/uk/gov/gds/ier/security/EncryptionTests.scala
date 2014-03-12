package uk.gov.gds.ier.security

import org.scalatest.{GivenWhenThen, FunSuite}
import org.scalatest.Matchers
import uk.gov.gds.ier.config.Config
import org.specs2.mock.Mockito

class EncryptionTests extends FunSuite with GivenWhenThen with Matchers with Mockito {

  private val jsonToEncrypt = """{key:"value"}"""
  private val mockedConfig = mock[Config]
  mockedConfig.cookiesAesKey returns "J1gs7djvi9/ecFHj0gNRbHHWIreobplsWmXnZiM2reo="

  test("Should be able to encrypt/decrypt a block using an AES key") {
      val (encryptionOutput, encryptionIV) =
        new EncryptionService(new Base64EncodingService, mockedConfig).
          encrypt(jsonToEncrypt)
      new EncryptionService(new Base64EncodingService, mockedConfig).
        decrypt(encryptionOutput, encryptionIV) should be(jsonToEncrypt)
  }

}
