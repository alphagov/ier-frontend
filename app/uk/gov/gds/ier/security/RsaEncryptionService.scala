package uk.gov.gds.ier.security

import java.security._
import javax.crypto.{SecretKey, BadPaddingException, Cipher, KeyGenerator}
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}
import org.bouncycastle.jce.provider.BouncyCastleProvider
import sun.security.rsa.{RSAPublicKeyImpl, RSAPrivateCrtKeyImpl}
import uk.gov.gds.ier.config.Config
import com.google.inject.Inject

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
