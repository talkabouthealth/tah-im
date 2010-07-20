package com.tah.im;

import java.net.UnknownHostException;
import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class DBUtil {
	
private static Mongo mongo;
	
	static {
		try {
			mongo = new Mongo("localhost", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	public static DB getDB() {
		//boolean auth = db.authenticate(myUserName, myPassword);
		return mongo.getDB("tahdb");
	}
	
	/* ---- Talkers ---- */
	public static UserInfo getUserByIm(String imService, String imUsername) {
		DBCollection talkersColl = getDB().getCollection("talkers");
		
		DBObject query = new BasicDBObject();
		query.put("im", imService);
		query.put("imUsername", imUsername);
		DBObject talkerDBObject = talkersColl.findOne(query);
		
		UserInfo userInfo = new UserInfo(imService, imUsername);
		if (talkerDBObject != null) {
			userInfo.setUid(talkerDBObject.get("_id").toString());
			userInfo.setUname((String)talkerDBObject.get("uname"));
			userInfo.setEmail((String)talkerDBObject.get("email"));
			userInfo.setGender((String)talkerDBObject.get("gender"));
		}
		
		return userInfo;
	}
	
	/* ---- Notifications ----- */
	public static void saveNotification(String uid, String topicId, int sc) {
		DBCollection notificationsColl = getDB().getCollection("notifications");
		
		DBRef talkerRef = new DBRef(getDB(), "talkers", new ObjectId(uid));
		DBRef topicRef = new DBRef(getDB(), "topics", new ObjectId(topicId));
		DBObject notificationDBObject = BasicDBObjectBuilder.start()
			.add("uid", talkerRef)
			.add("topic_id", topicRef)
			.add("time", new Date())
			.add("sc", sc)
			.get();
		
		notificationsColl.save(notificationDBObject);
	}
}
