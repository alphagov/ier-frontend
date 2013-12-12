package uk.gov.gds.ier.security

import java.security._
import javax.crypto.{SecretKey, BadPaddingException, Cipher, KeyGenerator}
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}
import org.bouncycastle.jce.provider.BouncyCastleProvider
import sun.security.rsa.{RSAPublicKeyImpl, RSAPrivateCrtKeyImpl}
import uk.gov.gds.ier.config.Config
import com.google.inject.Inject

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
