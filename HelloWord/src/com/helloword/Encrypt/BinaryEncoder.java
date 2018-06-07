package com.helloword.Encrypt;

public abstract interface BinaryEncoder extends Encoder {
	public abstract byte[] encode(byte[] paramArrayOfByte);
}
