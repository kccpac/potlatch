/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.potlatch.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLEngineResult.Status;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.RequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.potlatch.server.auth.User;
import com.potlatch.server.client.PotlatchStatus;
import com.potlatch.server.client.PotlatchSvcApi;
import com.potlatch.server.client.PotlatchStatus.PotlatchState;
import com.potlatch.server.repository.Gift;
import com.potlatch.server.repository.GiftRepository;
import com.potlatch.server.repository.UserEmotion;
import com.potlatch.server.repository.UserEmotionRepository;
import com.potlatch.server.repository.UserInfo;
import com.potlatch.server.repository.UserInfoRepository;
import com.potlatch.server.repository.emotionType;
import com.potlatch.server.repository.touchCount;

import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedOutput;

@Controller
public class PotlatchSvcController {
	
	
	@Autowired
	GiftRepository gifts;
	
	@Autowired
	UserInfoRepository userInfos;
	
	@Autowired
	UserEmotionRepository uRepro;
	

	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH + "/UserId", method=RequestMethod.GET)
	public @ResponseBody long getUserId(
			@RequestParam(value="Username") String username
			) {		
		UserInfo info = userInfos.findByUsername(username);
		if (info != null)
		{
			return info.getId();
		}
		
		return -1;
	}

	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Gift addGift(
			@RequestBody Gift v) {
		// TODO Auto-generated method stub
		gifts.save(v);
		long giftId = v.getId();	

		String file = v.getTitle() + "." + v.getGiftType();
		v.setSUrl(getDataUrl(giftId, file, "data"));
		v.setTUrl(getDataUrl(giftId, file, "thumbnail"));
		gifts.save(v);
		
		UserEmotion user = new UserEmotion(v.getOwnerId(), v.getId());
		uRepro.save(user);

		return v;
	}
	
	@RequestMapping(value=PotlatchSvcApi.POTLATCH_DATA_PATH, method=RequestMethod.POST)
	public @ResponseBody PotlatchStatus setGiftData (
			@PathVariable(value=PotlatchSvcApi.OWNER_ID_PARAMETER) String ownerId,
			@PathVariable(value=PotlatchSvcApi.GIFT_ID_PARAMETER) String giftId,
			@RequestParam(value=PotlatchSvcApi.THUMBNAIL_PARAMETER) MultipartFile giftThumbnail,
			@RequestParam(value=PotlatchSvcApi.DATA_PARAMETER) MultipartFile  giftData,
			HttpServletResponse response
			) {
		// TODO Auto-generated method stub

		int ret = HttpServletResponse.SC_NOT_FOUND;
		long id =Long.parseLong(giftId);
		Gift gift = gifts.findOne(id);

		if (gift != null)
		{
			GiftFileManager manager = null;
			try {
				manager = GiftFileManager.get();
				manager.saveData(new URL(gift.getSUrl()), giftData.getInputStream());
				manager.saveData(new URL(gift.getTUrl()), giftThumbnail.getInputStream());
				
				ret = HttpServletResponse.SC_OK;				
			} 
			
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		PotlatchStatus status = new PotlatchStatus(PotlatchState.READY);
		response.setStatus(ret);
		return status ;
	}
	
	@Streaming
	@RequestMapping(value=PotlatchSvcApi.POTLATCH_QUERYDATA_PATH, method=RequestMethod.GET)
	public @ResponseBody Response getData(
			@PathVariable(value=PotlatchSvcApi.GIFT_ID_PARAMETER) String giftId,
			@PathVariable(value=PotlatchSvcApi.GIFT_CAT_PARAMETER) String category,
		HttpServletResponse response) {
		// TODO Auto-generated method stub
		List<Header>  hlist = new ArrayList<Header>();

		TypedFile tFile = null;
		File file = null;
		int ret = HttpServletResponse.SC_NOT_FOUND;

		try 
		{
			GiftFileManager manager = GiftFileManager.get();
			long id =Long.parseLong(giftId);
			Gift gift = gifts.findOne(id);
	
			URL url = null;
			if (category.equals("data") == true)
			{
				url = new URL(gift.getSUrl());
			}
			else if (category.equals("thumbnail") == true)
			{
				url = new URL(gift.getTUrl());
			}
			
			if (url != null)
			{
				file = (File) new File(url.getFile());
				tFile = new TypedFile("image/ipeg", file);	

				long size = manager.copyData(url, (OutputStream) response.getOutputStream());
				ret = HttpServletResponse.SC_OK;
			}
		}
		
		catch(Exception e)
		{
			
		}
		
		Response resp = new Response(new String(), ret, new String(), hlist, tFile);
		response.setStatus(ret);

		return resp;
	}

	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH+"/emotion",method=RequestMethod.POST)
	public Void eMotionByGift(
			@RequestParam(value="id") long id,
			@RequestParam(value="eMotionType") emotionType type,
			HttpServletResponse response,
			Principal principal) 
	{
		int status = HttpServletResponse.SC_NOT_FOUND;
		Gift gift = gifts.findOne(id);
		String username = (principal != null) ? principal.getName(): null;		

		if (gift != null && username!=null )
		{		
			if (gift.getTouchBy().contains(username) == true)
			{
				status = HttpServletResponse.SC_BAD_REQUEST;			
			}
			else
			{
				status = HttpServletResponse.SC_OK;				
				gift.setEmotionCounter(type, username);
				gifts.save(gift);
			}
		}
		response.setStatus(status);
		return (Void) null;
	}

	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH+"/unEmotion", method=RequestMethod.POST)
	public Void UneMotionByGift(
			@RequestParam(value="id") long id,
			@RequestParam(value="eMotionType") emotionType type,
			HttpServletResponse response,
			Principal principal )
	{
		int status = HttpServletResponse.SC_NOT_FOUND;
		Gift gift = gifts.findOne(id);
		if (gift != null && principal != null)
		{		
			if (gift.getEmotionCounter(type) <= 0)
			{
				status = HttpServletResponse.SC_BAD_REQUEST;			
			}
			else
			{
				status = HttpServletResponse.SC_OK;
				gift.setUnEmotionCounter(type, principal.getName());
				gifts.save(gift);
			}
		}
		response.setStatus(status);
		return (Void) null;
	}
	
	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH+"/getUsers",method=RequestMethod.GET)
	public @ResponseBody Collection<String> getUsersWhoEmotionByGift(
			@RequestParam(value="id") long id,
			@RequestParam(value="eMotionType") emotionType type,
			HttpServletResponse response
			)
	{
		int status = HttpServletResponse.SC_NOT_FOUND;
		Gift gift = gifts.findOne(id);
		Collection<String> vlist = null ;
		if (gift != null)
		{
			vlist = gift.getTouchBy();
			status = HttpServletResponse.SC_OK;
		}
		response.setStatus(status);
		return vlist;
	}
	
	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH+"/getEmotionCount", method=RequestMethod.GET)
	public @ResponseBody Collection <touchCount> getEmotionCountList(
			@RequestParam(value="eMotionType") emotionType type,
			HttpServletResponse response)
	{
		Iterator <Gift> it = Lists.newArrayList(gifts.findAll()).iterator();
		Collection<touchCount> tc = new ArrayList<touchCount>();

		while (it.hasNext())
		{
			Gift g = it.next();
			int count = g.getEmotionCounter(type);
			tc.add(new touchCount(g.getId(), g.getTitle(), count));			
		}		
		return tc;
	}
	
	
	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection <Gift> getGiftList(
			@RequestParam(value="eMotionType") emotionType filterType,
			HttpServletResponse response)
	{
		if (filterType == emotionType.EMOTION_NONE)
			return Lists.newArrayList(gifts.findAll());
		
		Iterator <Gift> it =  Lists.newArrayList(gifts.findAll()).iterator();
		Collection<Gift> tc = new ArrayList<Gift>();
		while (it.hasNext())
		{
			Gift g = it.next();
			if (g.getEmotionCounter(filterType) == 0)
				tc.add(g);			
		}
		
		return tc;
	}
	
	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH+"/getUser", method=RequestMethod.GET)
	public  @ResponseBody UserEmotion queryUserData(
			@RequestParam(value="userId") long userId,
			@RequestParam(value="giftId") long giftId)
	{
		Collection <UserEmotion> users = uRepro.findByUserId(userId);
		
		Iterator <UserEmotion> it = users.iterator();
		
		UserEmotion user = null;
		while(it.hasNext())
		{
			user = it.next();
			if (giftId == user.getGiftId())
			{
				break;
			}
			user = null;
		}
		
		if (user == null)
		{
			user = new UserEmotion(userId, giftId);
			uRepro.save(user);
		}
		
		return user;
	}
	
	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH+"/setUser", method=RequestMethod.POST)
	public @ResponseBody boolean setUserEmotion(
			@RequestParam(value="userEmotion") UserEmotion user)
	{		
		UserEmotion u = uRepro.findByUserIdAndGiftId(user.getUserId(), user.getGiftId());
		if (u == null)
			return false;
		int emotion[] = u.getEmotion();
		for (int i=0; i< emotion.length; i++)
		{
			emotionType etype = emotionType.getType(i);
			u.setEmotion(etype, user.getEmotion(etype) == 1);			
		}
		uRepro.save(u);
		return true;
	}


	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH + "/giftById", method=RequestMethod.GET)
	public @ResponseBody Gift findGiftById(
			@RequestParam(value="giftId") String giftId){
		Gift v = gifts.findOne(Long.parseLong(giftId));
		return v;
	}
	
	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH + "/giftByTitle", method=RequestMethod.GET)
	public @ResponseBody Gift findGiftByTitle(
			@RequestParam(value="title") String title){
		Gift v = gifts.findByTitle(title);
		return v;
	}
	
	@RequestMapping(value=PotlatchSvcApi.POTLATCH_SVC_PATH + "/giftListByOwnerId", method=RequestMethod.GET)
	public @ResponseBody Collection<Gift> getGiftListByOwnerId(
			@RequestParam(value=PotlatchSvcApi.OWNER_ID_PARAMETER) String ownerId) {
		return Lists.newArrayList(gifts.findByOwnerId(Long.parseLong(ownerId)));
	}

	private String getDataUrl(long ownerId, String filename, String type){
		String url = null;

    	url = getUrlBaseForLocalServer() + 
    			PotlatchSvcApi.POTLATCH_SVC_PATH + "/" + type + "/" + ownerId + "/" + filename;
        return url;
    }
    
 	private String getUrlBaseForLocalServer() {
 	   HttpServletRequest request = 
 	       ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
 	   String base = 
 	      "http://"+request.getServerName() 
 	      + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
 	   return base;
 	}

}
