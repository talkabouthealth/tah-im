package com.tah.im;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public static boolean isEmailExist(String email) {
		DBObject talkerDBObject = DBUtil.getTalkerByEmail(email);
		
		if (talkerDBObject == null) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public static DBObject getTalkerByEmail(String email) {
		DBCollection talkersColl = getDB().getCollection("talkers");
		
		DBObject query = new BasicDBObject("email", email);
		DBObject talkerDBObject = talkersColl.findOne(query);
		
		return talkerDBObject;
	}

	public static String getIMType(String uid) {
		DBCollection talkersColl = getDB().getCollection("talkers");
		
		DBObject query = new BasicDBObject("_id", new ObjectId(uid));
		DBObject talkerDBObject = talkersColl.findOne(query, new BasicDBObject("im", ""));
		
		if (talkerDBObject == null) {
			return null;
		}
		else {
			return (String)talkerDBObject.get("im");
		}
	}
	
	public static List<Map<String, String>> loadTalkers() {
		DBCollection talkersColl = getDB().getCollection("talkers");
		
		//TODO: sort by last notification!
		List<DBObject> talkersDBList = talkersColl.find().toArray();
		
		List<Map<String, String>> talkersInfoList = new ArrayList<Map<String,String>>();
		for (DBObject talkerDBObject : talkersDBList) {
			Map<String, String> talkerInfoMap = new HashMap<String, String>();
			
			talkerInfoMap.put("id", talkerDBObject.get("_id").toString());
			talkerInfoMap.put("uname", talkerDBObject.get("uname").toString());
			talkerInfoMap.put("email", talkerDBObject.get("email").toString());
			
			talkersInfoList.add(talkerInfoMap);
		}
		
		return talkersInfoList;
	}
	
	
	
	/* ---- Topics ---- */
	
	public static List<Map<String, String>> loadTopics(boolean withNotifications) {
		DBCollection topicsColl = getDB().getCollection("topics");
		
		List<DBObject> topicsDBList = topicsColl.find().sort(new BasicDBObject("cr_date", 1)).toArray();
		
		List<Map<String, String>> topicsInfoList = new ArrayList<Map<String,String>>();
		for (DBObject topicDBObject : topicsDBList) {
			Map<String, String> topicInfoMap = new HashMap<String, String>();
			
			//noti_history.noti_time is null
			int numOfNotifications = getNotiNumByTopic(topicDBObject.get("_id").toString());
			if (withNotifications && numOfNotifications == 0) {
				continue;
			}
			else if (!withNotifications && numOfNotifications > 0) {
				continue;
			}
			
			//convert data to map
			DBRef talkerRef = (DBRef)topicDBObject.get("uid");
			DBObject talkerDBObject = talkerRef.fetch();
			
			topicInfoMap.put("topicId", topicDBObject.get("_id").toString());
			topicInfoMap.put("topic", topicDBObject.get("topic").toString());
			topicInfoMap.put("cr_date", topicDBObject.get("cr_date").toString());
			
			topicInfoMap.put("uid", talkerDBObject.get("_id").toString());
			topicInfoMap.put("uname", talkerDBObject.get("uname").toString());
			topicInfoMap.put("gender", talkerDBObject.get("gender").toString());
			
			topicsInfoList.add(topicInfoMap);
		}
		
		return topicsInfoList;
	}
	
	public static String getLastTopicId() {
		DBCollection topicsColl = getDB().getCollection("topics");
		
		DBObject topicDBObject = topicsColl.find().sort(new BasicDBObject("cr_date", -1)).next();
		
		if (topicDBObject == null) {
			return null;
		}
		else {
			return topicDBObject.get("_id").toString();
		}
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

	/**
	 * Returns number of notifications for last 24 hours
	 */
	public static int getNumOfNotifications(String uid) {
		DBCollection notificationsColl = getDB().getCollection("notifications");
		
		Calendar oneDayBeforeDate = Calendar.getInstance();
		oneDayBeforeDate.add(Calendar.DAY_OF_MONTH, -1);
		
		DBRef talkerRef = new DBRef(getDB(), "talkers", new ObjectId(uid));
		DBObject query = BasicDBObjectBuilder.start()
			.add("uid", talkerRef)
			.add("time", new BasicDBObject("$gt", oneDayBeforeDate.getTime()))
			.get();
		
		return notificationsColl.find(query).count();
	}
	
	public static Date getLastNotification(String uid) {
		DBCollection notificationsColl = getDB().getCollection("notifications");
		
		DBRef talkerRef = new DBRef(getDB(), "talkers", new ObjectId(uid));
		DBObject query = BasicDBObjectBuilder.start()
			.add("uid", talkerRef)
			.get();
		
		List<DBObject> notificationsDBList =
			notificationsColl.find(query).sort(new BasicDBObject("time", -1)).limit(1).toArray();
		if (!notificationsDBList.isEmpty()) {
			return (Date)notificationsDBList.get(0).get("time");
		}
		else {
			return null;
		}
	}
	
	public static int getNotiNumByTopic(String topicId) {
		DBCollection notificationsColl = getDB().getCollection("notifications");
		
		DBRef topicRef = new DBRef(getDB(), "topics", new ObjectId(topicId));
		DBObject query = BasicDBObjectBuilder.start()
			.add("topic_id", topicRef)
			.get();
		
		int numOfNotifications = notificationsColl.find(query).count();
		return numOfNotifications;
	}
	
}
