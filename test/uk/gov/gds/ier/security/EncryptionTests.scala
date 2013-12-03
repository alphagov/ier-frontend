package uk.gov.gds.ier.security

import org.scalatest.{GivenWhenThen, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import sun.security.rsa.{RSAPublicKeyImpl, RSAPrivateCrtKeyImpl}
import java.security.{SecureRandom, KeyPair}
import javax.crypto.KeyGenerator

class EncryptionTests extends FunSuite with GivenWhenThen with ShouldMatchers {

  private val jsonToEncrypt = """{key:"value"}"""

  private def testPublic = """MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCD08bgDGQPznuSFLDgxmPdADXYZSD9U7imRIJOMFT5+31PSaE+wITsuPiQWxP7XOsItm2TgjRY2XN2wRi0hNnLZaLiDf4hL0VxOiuxuAlcFg/cg0NQHjaVtQGNdBP/EQZbZ2ga4gaNRMcXoH+ju2iWVVxFN92A2SnZx21jkD6ccQIDAQAB"""
  private def testPrivate = """MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIPTxuAMZA/Oe5IUsODGY90ANdhlIP1TuKZEgk4wVPn7fU9JoT7AhOy4+JBbE/tc6wi2bZOCNFjZc3bBGLSE2ctlouIN/iEvRXE6K7G4CVwWD9yDQ1AeNpW1AY10E/8RBltnaBriBo1Exxegf6O7aJZVXEU33YDZKdnHbWOQPpxxAgMBAAECgYAanCwf28BzBF4jPbP6m3FbEgjMdpVzLWwve9LFz7HHiEFiON2omhn64teh5BCjg70Z1CFITlccvEA3b4D6J3sC7oK/s4BIWWXg4zkI/WLbz8SlT6S5pFez4TZoxL7j+Y5+L8Xk4sq/Gs7ic6B8Rc8XS9n6fwN1s4G9HNFHgTmgAQJBALsZzwh9Bqc8cmtUhUX75GKrmJhda+G0GlDejQReDSonGV7h+23+Y+QEzKMJs9KrumLq5qIHYoWV80g+9MxVngECQQC0X0VoY/Gl49XH7v25GDkgHlPlcLGcminxUbJydxjPFISdzJt7jB75u2//3IpT6xF+WSxoPsu8OMM1WR2db95xAkEAuRgFKr6VEGjKQMfyuJNDEyHy6fixuy1zQ0GHfCSXHXZksOsa02vw4iilUT1N+kINN8JuuyhXHRSFApnVjze8AQJAEoZyb31MVVhoHYe7QWZuf5D91uPTKh1fT2yvojf/MU2PLVHVakQC7m1E3Id/IY1UF6D7AZ3peORvkCwd8YyK0QJAL+FYYEgScxSe4o7LJkxife3mtM1IX2+NWQcx0a7w+X/l59nJD4G4rXLs+q+ULUgb/REVtvFA9erCvMT38WYl7A=="""
  private def testKeyPair = EncryptionKeys.createKeyPair(testPublic, testPrivate)

  private val testKeys = new KeyPair(
    new RSAPublicKeyImpl(Base64EncodingService.decode( """MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJ8TE333H+lI340Xo6dl9UEbBVUBIjKsw4pgmoR4kJCgT5LxKkXNPVb2QUW5B2eWL4ifyWHhloVlVep9z95JhQ0WoVQOBDxmOavZdPpLAlZ2P8TpRCzpEFRFx99MpxOs+v75kQJG5tbtfVovRUeNyJKhCtVQ4186/43I+yXInZCwIDAQAB""")),
    RSAPrivateCrtKeyImpl.newKey(Base64EncodingService.decode( """MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAInxMTffcf6UjfjRejp2X1QRsFVQEiMqzDimCahHiQkKBPkvEqRc09VvZBRbkHZ5YviJ/JYeGWhWVV6n3P3kmFDRahVA4EPGY5q9l0+ksCVnY/xOlELOkQVEXH30ynE6z6/vmRAkbm1u19Wi9FR43IkqEK1VDjXzr/jcj7JcidkLAgMBAAECgYBJjVsiILoG9i1RjFjxTWb9S8VLaDuTluF4H0is+x/duwL135zAGWM2voONu1knYwhb4C/X18rScJ5qr3VNyBNS1AI4kXmSUe0qqWXShLR3dX8cgXB3UCN7RR9BOLWRDvR/I9G5lD70qAZqW+K2aNcJdqpzE609n6mf5OFAcVF5aQJBAMVdtI/o48/aJUf5lqyy4tIdQcb7ZyQo9F+akkI4z6AR619bnnbPQXIA78SEcKljTpLt95/WNRB7V3d0ffApEacCQQCy7B40Xjzl6yEe54fSyfxSV/daC6XWrYTVpdphLkmvCXjP99a3QFNQC5qumV3Lqw6FQuL3OqzYv/aJ+7dUyEH9AkEAkkpnqPfFzG30jkn5Hh8mMvnOpK/5/nqA0FBhMsarVwmRPkhJx+TNrLP3BOHqJBPgzNWoYwhCounZpkhphNbcJwJAKzTdrmO7bQI0w1PB9uMT7YaUksgRSiAo3bbpX2JgJMayx/Xfge0ksUW8GsGqZs5t+TxHttASgV0J2hRRF0YsuQJAbc8pJejIbY906XZITz+c38T4y3nbfDX/pDBN/d9FlIctxRTfoa6nPdce1EFzH7ySRyLZFGDY1cfwRuDtYfbWKA=="""))
  )

  test("Should be able to decrypt a block using key") {
    val encryptionOutput = RsaEncryptionService.encrypt(jsonToEncrypt, testKeyPair.getPublic)
    val decrypted = RsaEncryptionService.decrypt(encryptionOutput, testKeyPair.getPrivate)

    decrypted should be(jsonToEncrypt)
  }

  test("Should not be able to decrypt a block with another key") {
    val encryptionOutput = RsaEncryptionService.encrypt(jsonToEncrypt, testKeyPair.getPublic)
    intercept[DecryptionFailedException] {
      RsaEncryptionService.decrypt(encryptionOutput, testKeys.getPrivate)
    }
  }

  test("Should be able to encrypt/decrypt a block using an AES key") {
    val (encryptionOutput, secretKey) = AesEncryptionService.encryptWithAES(jsonToEncrypt)
    AesEncryptionService.decryptWithAES(encryptionOutput, secretKey) should be(jsonToEncrypt)
  }

  test("Should be not be able to encrypt/decrypt a block using another AES key") {
    val kg = KeyGenerator.getInstance("AES")
    kg.init(new SecureRandom())

    val (encryptionOutput, correctSecretKey) = AesEncryptionService.encryptWithAES(jsonToEncrypt)
    AesEncryptionService.decryptWithAES(encryptionOutput, correctSecretKey) should be(jsonToEncrypt)

    intercept[DecryptionFailedException] {
      AesEncryptionService.decryptWithAES(encryptionOutput, kg.generateKey())
    }
  }

  test("Should be able to 2-stage encrypt a payload through RSA/AES encryption") {
    val (content, aesKey) = EncryptionService.encrypt(jsonToEncrypt, testKeyPair.getPublic)
    EncryptionService.decrypt(content, aesKey, testKeyPair.getPrivate) should be(jsonToEncrypt)
  }

  test("Should be not be able to 2-stage encrypt a payload through RSA/AES encryption if you use another ero key") {
    val (content, aesKey) = EncryptionService.encrypt(jsonToEncrypt, testKeyPair.getPublic)
    intercept[DecryptionFailedException] {
      EncryptionService.decrypt(content, aesKey, testKeys.getPrivate) should be(jsonToEncrypt)
    }
  }

}
