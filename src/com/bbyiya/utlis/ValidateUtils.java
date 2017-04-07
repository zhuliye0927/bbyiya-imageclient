package com.bbyiya.utlis;

import com.bbyiya.enums.UserIdentityEnums;


/**
 * 验证公用类
 * @author Administrator
 *
 */
public class ValidateUtils {

	/**
	 * 用户身份验证
	 * @param userIdentity 用户登录的身份标识
	 * @param type 需要验证的用户身份Identity
	 * @return
	 */
	public static boolean isIdentity(Long userIdentity,UserIdentityEnums type){
		userIdentity=userIdentity==null?0:userIdentity;
		long b=ObjectUtil.parseLong(type.toString());
		return b==(userIdentity&b);
	}
}
