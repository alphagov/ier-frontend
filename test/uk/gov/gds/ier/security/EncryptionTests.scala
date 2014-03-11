package uk.gov.gds.ier.security

import org.scalatest.{GivenWhenThen, FunSuite}
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import org.scalatest.Matchers
import uk.gov.gds.ier.config.Config
import org.specs2.mock.Mockito
import play.api.test.FakeApplication

class EncryptionTests extends FunSuite with GivenWhenThen with Matchers with Mockito {

  private val jsonToEncrypt = """{key:"value"}"""
  private val mockedConfig = mock[Config]
  mockedConfig.cookiesAesKey returns "QWVzS2V5QmVpbmdTb21lU3RyaW5nT2ZMZW5ndGgyNTY="

  test("Should be able to encrypt/decrypt a block using an AES key") {
      val encryptionOutput = new AesEncryptionService(new Base64EncodingService, mockedConfig).encryptWithAES(jsonToEncrypt)
      new AesEncryptionService(new Base64EncodingService, mockedConfig).decryptWithAES(encryptionOutput) should be(jsonToEncrypt)
  }

  test("Should be able to 2-stage encrypt a payload") {
    val content = new EncryptionService (new AesEncryptionService(new Base64EncodingService, mockedConfig)).encrypt(jsonToEncrypt)
    new EncryptionService (new AesEncryptionService(new Base64EncodingService, mockedConfig)).decrypt(content) should be(jsonToEncrypt)
  }

}
