package uk.gov.gds.ier.security

import com.google.inject.Inject

class EncryptionService @Inject ()(aesEncryptionService:AesEncryptionService) {
  def encrypt(content: String): String = {
    aesEncryptionService.encryptWithAES(content)
  }

  def decrypt(content: String): String = {
    aesEncryptionService.decryptWithAES(content)
  }
}
