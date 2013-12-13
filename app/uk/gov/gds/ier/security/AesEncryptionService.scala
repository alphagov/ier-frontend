package uk.gov.gds.ier.security

import java.security._
import javax.crypto.{SecretKey, BadPaddingException, Cipher, KeyGenerator}
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}
import org.bouncycastle.jce.provider.BouncyCastleProvider
import sun.security.rsa.{RSAPublicKeyImpl, RSAPrivateCrtKeyImpl}
import uk.gov.gds.ier.config.Config
import com.google.inject.Inject

class AesEncryptionService @Inject ()(base64EncodingService:Base64EncodingService) {

  private val kg = KeyGenerator.getInstance("AES")
  private val iVspec = new IvParameterSpec(new Array[Byte](16))

  def aesKeyAsBase64EncodedString(key: SecretKey) = base64EncodingService.encode(key.getEncoded)

  def aesKeyFromBase64EncodedString(key: String) = new SecretKeySpec(base64EncodingService.decode(key), "AES")

  def encryptWithAES(content: String): (String, SecretKey) = {
    val cipher = Cipher.getInstance("AES")
    val aesKey = generateKey

    cipher.init(Cipher.ENCRYPT_MODE, aesKey)
    (base64EncodingService.encode(cipher.doFinal(content.getBytes)), aesKey)
  }

  def decryptWithAES(encryptedContent: String, key: SecretKey): String = {
    val cipher = Cipher.getInstance("AES")

    try {
      cipher.init(Cipher.DECRYPT_MODE, key, iVspec)
      new String(cipher.doFinal(base64EncodingService.decode(encryptedContent)))
    } catch {
      case ex: BadPaddingException => throw new DecryptionFailedException("Most likely caused by incorrect Private key", ex)
    }
  }

  private def generateKey = {
    kg.init(new SecureRandom())
    kg.generateKey()
  }
}
