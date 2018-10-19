package com.unotech.application;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;


public class PermissionTree {
	
	 private static  MongoDatabase db;
	 private static MongoClient mongoClient;
	private static FileReader reader;
	Properties p = new Properties();
	 
	public PermissionTree() {
		try {
			reader = new FileReader("/tmp/Bumblebee/config.properties");
			p.load(reader);
			mongoClient = new MongoClient(p.getProperty("mongo.server.ip"), new Integer(p.getProperty("mongo.db.port")));
			 db = mongoClient.getDatabase(p.getProperty("mongo.database.name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean getPermissions(String workspaceId) {
		try {
			MongoCollection<Document> collection = db.getCollection("default");
			BasicDBObject searchQuery = new BasicDBObject("ecm:parentId", workspaceId);
			MongoCursor<Document> cursorDoc = collection.find(searchQuery).iterator();
			/*if (!cursorDoc.hasNext()) {
				reader.close();
				mongoClient.close();
				return false;
			}*/
			
			
			
//			
			 System.out.println("workspaceId=====>"+workspaceId);
			while(cursorDoc.hasNext()){
				Document collectionElement = cursorDoc.next();
			System.out.println("Documents ===="+collectionElement);
				
				String name = collectionElement.getString("ecm:name");
				String id = collectionElement.getString("ecm:id");
				System.out.println("name  = "+name+"\t======>id = "+id);
				
				
				boolean permissions = getPermissions(id);
				System.out.println("Status === "+permissions);
				
			}

			
			return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return false;
	}

	private MongoCursor<Document> getDocumentList(String workspaceId) {
		// TODO Auto-generated method stub
		MongoCollection<Document> collection = db.getCollection("default");
		BasicDBObject searchQuery = new BasicDBObject("ecm:parentId", workspaceId);
		MongoCursor<Document> cursorDoc = collection.find(searchQuery).iterator();
		return cursorDoc;
		
		
	}


	private long getDocumentCount(String workspaceId) {
		// TODO Auto-generated method stub
		MongoCollection<Document> collection = db.getCollection("default");
		BasicDBObject searchQuery = new BasicDBObject("ecm:parentId", workspaceId);
		long count = collection.count(searchQuery);
		return count;
		
	}


	public static void connectionClose() {
		try {
			if (reader != null) {
				reader.close();
			}
			if (mongoClient != null) {
				mongoClient.close();
			}
		} catch (Exception e2) {
			System.out.println(e2);
		}
	}
	
	public boolean workspaceDetails1() {
		FileWriter printWriter = null;
		// db.getCollection('default').find({ "ecm:primaryType":"Workspace"
		// },{"ecm:name":1,"ecm:acp":2,"dcs:descendantsCount":3})
		try {
/*			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(p.getProperty("link"),
					new Integer(p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
*/
			printWriter = new FileWriter(p.getProperty("file.path")
					+ "workspaceDetails.csv");
			printWriter
					.append("index,Workspace, no. of Documents, user, permission \n");
			MongoCollection<Document> data = db.getCollection("default");
			BasicDBObject query = new BasicDBObject("ecm:primaryType",
					"Workspace");
			MongoCursor<Document> cursorDoc = data.find(query).iterator();
			int index = 1;
			while (cursorDoc.hasNext()) {
				Document collectionElement = cursorDoc.next();
				JSONObject jsonObj = new JSONObject(
						JSON.serialize(collectionElement));
				System.out.println("collections ===="+jsonObj);
				printWriter.append(index + "");
				index++;
				printWriter.append(",");
				printWriter
						.append(collectionElement.get("ecm:name").toString());
				printWriter.append(",");
				if (collectionElement.get("dcs:descendantsCount") != null) {
					printWriter.append(collectionElement.get(
							"dcs:descendantsCount").toString());
				}
				printWriter.append(",");
				if (collectionElement.get("ecm:acp") != null) {
					boolean first = true;
					JSONArray jsonArray = jsonObj.getJSONArray("ecm:acp")
							.getJSONObject(0).getJSONArray("acl");
					for (int i = 0; i < jsonArray.length(); i++) {
						if (first) {
							first = !first;
							if (i + 1 == jsonArray.length()) {
								System.out.println("Inside first true....");
								printWriter.append(jsonArray.getJSONObject(i)
										.get("user").toString()
										+ ",");
								System.out.println(" =================");
								printWriter.append(jsonArray.getJSONObject(i)
										.get("perm").toString());
								System.out.println("***************************");
								break;
							} else {
								System.out.println("inside first else .......");
								printWriter.append(jsonArray.getJSONObject(i)
										.get("user").toString()
										+ ",");
								printWriter.append(jsonArray.getJSONObject(i)
										.get("perm").toString()
										+ "\n");
								System.out.println("*************************** ===");
							}
						} else {
							if (i + 1 == jsonArray.length()) {
								printWriter.append(" , , ,"
										+ jsonArray.getJSONObject(i)
												.get("user").toString() + ",");
								printWriter.append(jsonArray.getJSONObject(i)
										.get("perm").toString());
								System.out.println("*************************** ===1111111");
								break;
							} else {
								System.out.println("inside  else  second ........");
								printWriter.append(" , , ,"
										+ jsonArray.getJSONObject(i)
												.get("user").toString() + ",");
								printWriter.append(jsonArray.getJSONObject(i)
										.get("perm").toString()
										+ "\n");
							}
						}
					}
				}
				printWriter.append("\n");
			}
			printWriter.flush();
			printWriter.close();
/*			mongoClient.close();
			reader.close();
*/			return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}
}
