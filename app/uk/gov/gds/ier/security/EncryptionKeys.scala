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
