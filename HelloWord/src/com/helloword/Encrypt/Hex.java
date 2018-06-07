package com.helloword.Encrypt;
public class Hex implements BinaryEncoder, BinaryDecoder{
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	public static byte[] decodeHex(char[] data){
		
		int len = data.length;
		if ((len & 0x1) != 0) {
			try {
				throw new Exception("Odd number of characters.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		byte[] out = new byte[len >> 1];
		int i = 0; for (int j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f |= toDigit(data[j], j);
			j++;
			out[i] = ((byte)(f & 0xFF));
		}
		return out;
	}
	protected static int toDigit(char ch, int index){
		int digit = Character.digit(ch, 16);
		if (digit == -1) {
			try {
				throw new Exception("Illegal hexadecimal charcter " + ch + " at index " + index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return digit;
	}
	public static char[] encodeHex(byte[] data){
		int l = data.length;
		char[] out = new char[l << 1];
		int i = 0; for (int j = 0; i < l; i++) {
			out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
			out[(j++)] = DIGITS[(0xF & data[i])];
		}
		return out;
	}
	public byte[] decode(byte[] array){
		return decodeHex(new String(array).toCharArray());
	}
	public Object decode(Object object){
		try{
			char[] charArray = (object instanceof String) ? ((String)object).toCharArray() : (char[])object;
			return decodeHex(charArray);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return object;
	}
	public byte[] encode(byte[] array){
		return new String(encodeHex(array)).getBytes();
	}
	public Object encode(Object object){
		try{
			byte[] byteArray = (object instanceof String) ? ((String)object).getBytes() : (byte[])object;
			return encodeHex(byteArray);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return object;
	}
}
