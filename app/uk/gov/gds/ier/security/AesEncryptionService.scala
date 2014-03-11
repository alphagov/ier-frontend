package uk.gov.gds.ier.security

import javax.crypto.{BadPaddingException, Cipher}
import javax.crypto.spec.SecretKeySpec
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config

class AesEncryptionService @Inject ()(base64EncodingService:Base64EncodingService, config:Config) {

    private def aesKeyFromBase64EncodedString(key: String) =
      new SecretKeySpec(base64EncodingService.decode(key), "AES")

    def encryptWithAES(content: String): String = {
      val encipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
      encipher.init(Cipher.ENCRYPT_MODE, aesKeyFromBase64EncodedString(config.cookiesAesKey))
      base64EncodingService.encode(encipher.doFinal(content.getBytes))
    }

    def decryptWithAES(content: String): String = {
      val encipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
      encipher.init(Cipher.DECRYPT_MODE, aesKeyFromBase64EncodedString(config.cookiesAesKey))
      new String (encipher.doFinal(base64EncodingService.decode(content)))
    }
}
