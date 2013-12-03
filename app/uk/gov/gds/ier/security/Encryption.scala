package uk.gov.gds.ier.security

import java.security._
import javax.crypto.{SecretKey, BadPaddingException, Cipher, KeyGenerator}
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}
import org.bouncycastle.jce.provider.BouncyCastleProvider
import sun.security.rsa.{RSAPublicKeyImpl, RSAPrivateCrtKeyImpl}
import Base64EncodingService.{decode, encode}
import uk.gov.gds.common.config.Config
import uk.gov.gds.ier.config.Config

object EncryptionKeys {

  val config = new Config
  private def cookiesRsaPublicKey = config.cookiesRsaPublicKey
  private def cookiesRsaPrivateKey = config.cookiesRsaPrivateKey

  lazy val cookies = createKeyPair(cookiesRsaPublicKey, cookiesRsaPrivateKey)

  def createKeyPair(publicKey: String, privateKey: String) = new KeyPair(createPublicKey(publicKey), createPrivateKey(privateKey))

  private def createPrivateKey(base64EncodedKey: String) = RSAPrivateCrtKeyImpl.newKey(decode(base64EncodedKey))

  private def createPublicKey(base64EncodedKey: String) = new RSAPublicKeyImpl(decode(base64EncodedKey))
}

object Base64EncodingService {

  import org.apache.commons.codec.binary.Base64

  def encode(input: Array[Byte]): String = Base64.encodeBase64String(input)

  def decode(input: String): Array[Byte] = Base64.decodeBase64(input)
}

object EncryptionService {
  def encrypt(content: String, publicKey: PublicKey): (String, String) = {
    val (encryptedContent, secretKey) = AesEncryptionService.encryptWithAES(content)
    (encryptedContent, RsaEncryptionService.encrypt(AesEncryptionService.aesKeyAsBase64EncodedString(secretKey), publicKey))
  }

  def decrypt(content: String, aesKey: String, privateKey: PrivateKey): String = {
    val decryptedAesKey = RsaEncryptionService.decrypt(aesKey, privateKey)
    AesEncryptionService.decryptWithAES(content, AesEncryptionService.aesKeyFromBase64EncodedString(decryptedAesKey))
  }
}

object RsaEncryptionService {

  Security.addProvider(new BouncyCastleProvider)

  def encrypt(content: String, publicKey: PublicKey): String = {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    encode(cipher.doFinal(content.getBytes))
  }

  def decrypt(encryptedContent: String, privateKey: PrivateKey): String = {
    try {
      val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC")
      cipher.init(Cipher.DECRYPT_MODE, privateKey)
      new String(cipher.doFinal(decode(encryptedContent)))
    } catch {
      case ex: BadPaddingException => throw new DecryptionFailedException("Most likely caused by incorrect Private key", ex)
    }
  }
}

object AesEncryptionService {

  private val kg = KeyGenerator.getInstance("AES")
  private val iVspec = new IvParameterSpec(new Array[Byte](16))

  def aesKeyAsBase64EncodedString(key: SecretKey) = Base64EncodingService.encode(key.getEncoded)

  def aesKeyFromBase64EncodedString(key: String) = new SecretKeySpec(Base64EncodingService.decode(key), "AES")

  def encryptWithAES(content: String): (String, SecretKey) = {
    val cipher = Cipher.getInstance("AES")
    val aesKey = generateKey

    cipher.init(Cipher.ENCRYPT_MODE, aesKey)
    (encode(cipher.doFinal(content.getBytes)), aesKey)
  }

  def decryptWithAES(encryptedContent: String, key: SecretKey): String = {
    val cipher = Cipher.getInstance("AES")

    try {
      cipher.init(Cipher.DECRYPT_MODE, key, iVspec)
      new String(cipher.doFinal(decode(encryptedContent)))
    } catch {
      case ex: BadPaddingException => throw new DecryptionFailedException("Most likely caused by incorrect Private key", ex)
    }
  }

  private def generateKey = {
    kg.init(new SecureRandom())
    kg.generateKey()
  }
}

class DecryptionFailedException(message: String, ex: Throwable) extends Exception(message, ex)