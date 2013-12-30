package czd.lib.encode;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Rijndael {
	public static String encrypt(String plainText, byte[] key) {
		try
		{
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = plainText.getBytes();
			int plaintTextLength = dataBytes.length;
			if (plaintTextLength % blockSize != 0)
			{
				plaintTextLength = plaintTextLength + (blockSize - (plaintTextLength % blockSize));
			}
			byte[] plainTextByte = new byte[plaintTextLength];
			System.arraycopy(dataBytes, 0, plainTextByte, 0, dataBytes.length);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
			return Base64.encode(cipher.doFinal(plainTextByte)) + String.format("%06d", plainText.length());
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		} catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		} catch (InvalidKeyException e)
		{
			e.printStackTrace();
		} catch (IllegalBlockSizeException e)
		{
			e.printStackTrace();
		} catch (BadPaddingException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public static String decrypt(String cipherText, byte[] key) {
		int plainTextLength = Integer.parseInt(cipherText.substring(cipherText.length() - 6));
		cipherText = cipherText.substring(0, cipherText.length() - 6);
		try
		{
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
			return new String(cipher.doFinal(Base64.decode(cipherText.getBytes()))).substring(0, plainTextLength);
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		} catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		} catch (InvalidKeyException e)
		{
			e.printStackTrace();
		} catch (IllegalBlockSizeException e)
		{
			e.printStackTrace();
		} catch (BadPaddingException e)
		{
			e.printStackTrace();
		}
		return "";
	}
}
