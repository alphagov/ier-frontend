package uk.gov.gds.ier.security

import java.security._
import javax.crypto.{SecretKey, BadPaddingException, Cipher, KeyGenerator}
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}
import org.bouncycastle.jce.provider.BouncyCastleProvider
import sun.security.rsa.{RSAPublicKeyImpl, RSAPrivateCrtKeyImpl}
import uk.gov.gds.ier.config.Config
import com.google.inject.Inject

class EncryptionKeys @Inject() (base64EncodingService:Base64EncodingService) {

  val config = new Config
  private def cookiesRsaPublicKey = config.cookiesRsaPublicKey
  private def cookiesRsaPrivateKey = config.cookiesRsaPrivateKey

  lazy val cookies = createKeyPair(cookiesRsaPublicKey, cookiesRsaPrivateKey)

  def createKeyPair(publicKey: String, privateKey: String) = new KeyPair(createPublicKey(publicKey), createPrivateKey(privateKey))

  private def createPrivateKey(base64EncodedKey: String) = RSAPrivateCrtKeyImpl.newKey(base64EncodingService.decode(base64EncodedKey))
  private def createPublicKey(base64EncodedKey: String) = new RSAPublicKeyImpl(base64EncodingService.decode(base64EncodedKey))
}

class Base64EncodingService {

  import org.apache.commons.codec.binary.Base64

  def encode(input: Array[Byte]): String = Base64.encodeBase64String(input)
  def decode(input: String): Array[Byte] = Base64.decodeBase64(input)
}

class EncryptionService @Inject ()(aesEncryptionService:AesEncryptionService, rsaEncryptionService: RsaEncryptionService) {
  def encrypt(content: String, publicKey: PublicKey): (String, String) = {
    val (encryptedContent, secretKey) = aesEncryptionService.encryptWithAES(content)
    (encryptedContent, rsaEncryptionService.encrypt(aesEncryptionService.aesKeyAsBase64EncodedString(secretKey), publicKey))
  }

  def decrypt(content: String, aesKey: String, privateKey: PrivateKey): String = {
    val decryptedAesKey = rsaEncryptionService.decrypt(aesKey, privateKey)
    aesEncryptionService.decryptWithAES(content, aesEncryptionService.aesKeyFromBase64EncodedString(decryptedAesKey))
  }
}

class RsaEncryptionService @Inject() (base64EncodingService:Base64EncodingService){

  Security.addProvider(new BouncyCastleProvider)

  def encrypt(content: String, publicKey: PublicKey): String = {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    base64EncodingService.encode(cipher.doFinal(content.getBytes))
  }

  def decrypt(encryptedContent: String, privateKey: PrivateKey): String = {
    try {
      val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC")
      cipher.init(Cipher.DECRYPT_MODE, privateKey)
      new String(cipher.doFinal(base64EncodingService.decode(encryptedContent)))
    } catch {
      case ex: BadPaddingException => throw new DecryptionFailedException("Most likely caused by incorrect Private key", ex)
    }
  }
}

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

class DecryptionFailedException(message: String, ex: Throwable) extends Exception(message, ex)