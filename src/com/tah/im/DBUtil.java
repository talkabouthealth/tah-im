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
		query.put("im_uname", imUsername);
		DBObject talkerDBObject = talkersColl.findOne(query);
		
		UserInfo userInfo = new UserInfo();
		userInfo.parseDBInfo(talkerDBObject);
		
		//it's possible that we don't have such IM username in db,
		//so we return UserInfo only with IM data
		userInfo.setImService(imService);
		userInfo.setImUsername(imUsername);
		
		return userInfo;
	}
	
	public static UserInfo getUserById(String talkerId) {
		DBCollection talkersColl = getDB().getCollection("talkers");
		
		DBObject query = new BasicDBObject("_id", new ObjectId(talkerId));
		DBObject talkerDBObject = talkersColl.findOne(query);
		
		UserInfo userInfo = new UserInfo();
		userInfo.parseDBInfo(talkerDBObject);
		
		return userInfo;
	}
	
	/* ------ Topics ------ */
	public static String createTopic(String talkerId, String topicName) {
		DBCollection topicsColl = getDB().getCollection("topics");
		
		Date now = new Date();
		
		DBRef talkerRef = new DBRef(DBUtil.getDB(), "talkers", new ObjectId(talkerId));
		DBObject topicObject = BasicDBObjectBuilder.start()
			.add("uid", talkerRef)
			.add("topic", topicName)
			.add("cr_date", now)
			.add("disp_date", now)
			.get();

		topicsColl.save(topicObject);
		return topicObject.get("_id").toString();
	}
	
	
	/* ---- Notifications ----- */
	public static void saveNotification(String uid, String topicId) {
		DBCollection notificationsColl = getDB().getCollection("notifications");
		
		DBRef talkerRef = new DBRef(getDB(), "talkers", new ObjectId(uid));
		DBRef topicRef = new DBRef(getDB(), "topics", new ObjectId(topicId));
		DBObject notificationDBObject = BasicDBObjectBuilder.start()
			.add("uid", talkerRef)
			.add("topic_id", topicRef)
			.add("time", new Date())
			.get();
		
		notificationsColl.save(notificationDBObject);
	}
}
