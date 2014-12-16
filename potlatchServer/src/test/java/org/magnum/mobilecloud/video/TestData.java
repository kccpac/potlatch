package org.magnum.mobilecloud.video;

import java.net.URL;
import java.util.HashSet;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potlatch.server.repository.Gift;
//import com.potlatch.server.repository.Video;

/**
 * This is a utility class to aid in the construction of
 * Video objects with random names, urls, and durations.
 * The class also provides a facility to convert objects
 * into JSON using Jackson, which is the format that the
 * VideoSvc controller is going to expect data in for
 * integration testing.
 * 
 * @author jules
 *
 */
public class TestData {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Construct and return a Video object with a
	 * rnadom name, url, and duration.
	 * 
	 * @return
	 */
/*	public static Video randomVideo() {
		// Information about the video
		// Construct a random identifier using Java's UUID class
		String id = UUID.randomUUID().toString();
		String title = "Video-"+id;
		String url = "http://coursera.org/some/video-"+id;
		long duration = 60 * (int)Math.rint(Math.random() * 60) * 1000; // random time up to 1hr
		return new Video(title, url, duration, 0);
	}
*/
	public static Gift randomGift() {
		// Information about the video
		// Construct a random identifier using Java's UUID class
		URL url = null;
		Gift gift = null;
		try {
			String id = UUID.randomUUID().toString();			
			long ownerId = 0;
			String title = "image-"+id;
			String description="";
			url = new URL("http://coursera.org/some/image-"+id);			
			gift = new Gift(ownerId, title, description, "JPG");
		}
		catch(Exception e) {
			
		}
		return gift;
	}
	
	/**
	 *  Convert an object to JSON using Jackson's ObjectMapper
	 *  
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static String toJson(Object o) throws Exception{
		return objectMapper.writeValueAsString(o);
	}
}
