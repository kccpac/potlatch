/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package com.potlatchClient;

import com.potlatchClient.oauth.SecuredRestBuilder;
import com.potlatchClient.server.PotlatchSvcApi;
import com.potlatchClient.unsafe.EasyHttpClient;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.content.Context;
import android.content.Intent;

public class PotlatchSvc {

	public static final String CLIENT_ID = "mobile";

	private static PotlatchSvcApi videoSvc_;

	public static synchronized PotlatchSvcApi getOrShowLogin(Context ctx) {
		if (videoSvc_ != null) {
			return videoSvc_;
		} else {
			Intent i = new Intent(ctx, LoginScreenActivity.class);
			ctx.startActivity(i);
			return null;
		}
	}

	public static synchronized PotlatchSvcApi init(String server, String user,
			String pass) {

		videoSvc_ = new SecuredRestBuilder()
				.setLoginEndpoint(server + PotlatchSvcApi.TOKEN_PATH)
				.setUsername(user)
				.setPassword(pass)
				.setClientId(CLIENT_ID)
				.setClient(
						new ApacheClient(new EasyHttpClient()))
				.setEndpoint(server).setLogLevel(LogLevel.FULL).build()
				.create(PotlatchSvcApi.class);

		return videoSvc_;
	}
}
