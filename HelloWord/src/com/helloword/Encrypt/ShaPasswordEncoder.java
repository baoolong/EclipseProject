package com.helloword.Encrypt;

public class ShaPasswordEncoder extends MessageDigestPasswordEncoder {
	public ShaPasswordEncoder() {
		this(1);
	}

	public ShaPasswordEncoder(int strength) {
		super("SHA-" + strength);
	}
	public static void main(String[] args) {
		
	}
}
