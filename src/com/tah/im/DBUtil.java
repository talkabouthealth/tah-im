package com.tah.im;

import java.net.UnknownHostException;
import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DB.WriteConcern;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
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
		
		if (talkerDBObject == null) {
			//check new IM Accounts storage - array "im_accounts"
			DBObject imAccountDBObject = BasicDBObjectBuilder.start()
				.add("uname", imUsername)
				.add("service", imService)
				.get();
			query = new BasicDBObject("im_accounts", imAccountDBObject);
			talkerDBObject = talkersColl.findOne(query);
		}
		
		UserInfo userInfo = new UserInfo();
		userInfo.parseDBInfo(talkerDBObject);
		
		//it's possible that we don't have such IM username in db,
		//so we return UserInfo only with IM data - to display in ONLINE lists
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
	public static int createTopic(String talkerId, String topicName) {
		//we try to insert topic 5 times
		return createTopic(talkerId, topicName, 5);
	}
	
	/**
	 * Tries to insert topic 'count' times (in case of duplicate key error on 'tid' field)
	 * Returns -1 in case of failure
	 */
	//TODO: move similar code in one jar ?
	private static int createTopic(String talkerId, String topicName, int count) {
		if (count == 0) {
			return -1;
		}
		
		DBCollection topicsColl = getDB().getCollection("topics");
		
		//get last tid
		DBCursor topicsCursor = 
			topicsColl.find(null, new BasicDBObject("tid", "")).sort(new BasicDBObject("tid", -1)).limit(1);
		int tid = topicsCursor.hasNext() ? ((Integer)topicsCursor.next().get("tid")) + 1 : 1;
		
		Date now = new Date();
		DBRef talkerRef = new DBRef(DBUtil.getDB(), "talkers", new ObjectId(talkerId));
		DBObject topicObject = BasicDBObjectBuilder.start()
			.add("uid", talkerRef)
			.add("tid", tid)
			.add("topic", topicName)
			.add("cr_date", now)
			.add("disp_date", now)
			.get();

		//Only with STRICT WriteConcern we receive exception on duplicate key
		topicsColl.setWriteConcern(WriteConcern.STRICT);
		try {
			topicsColl.save(topicObject);
		}
		catch (MongoException me) {
			//E11000 duplicate key error index
			if (me.getCode() == 11000) {
				System.err.println("Duplicate key error while saving topic");
				return createTopic(talkerId, topicName, --count);
			}
			me.printStackTrace();
		}
		return (Integer)topicObject.get("tid");
	}
	
	public static DBObject getTopicById(String topicId) {
		DBCollection topicsColl = DBUtil.getDB().getCollection("topics");
		
		DBObject query = null;
		try {
			query = new BasicDBObject("_id", new ObjectId(topicId));
		}
		catch(IllegalArgumentException iae) {
			//bad topicId - return no topic 
			return null;
		}
		DBObject topicDBObject = topicsColl.findOne(query);
		
		return topicDBObject;
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
	
	public static void main(String[] args) {
//		long tid = DBUtil.createTopic("4c2cb43160adf3055c97d061", "Hello World Topic!!!!");
//		System.out.println(tid);
		
		System.out.println(getUserByIm("YahooIM", "kan_kangaroo14"));
	}
}
