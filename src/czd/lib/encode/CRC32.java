package czd.lib.encode;

/**
 * 
 * @author Chen.Zhidong
 *         2012-8-29
 * 
 */
public class CRC32 {
	public static long encode(byte[] source) {
		java.util.zip.CRC32 crc = new java.util.zip.CRC32();
		crc.update(source);
		return crc.getValue();
	}
}
