package com.helloword.Encrypt;


public abstract interface PasswordEncoder {
	public abstract String encodePassword(String paramString, Object paramObject);
			
	public abstract boolean isPasswordValid(String paramString1,
                                            String paramString2, Object paramObject);
}
