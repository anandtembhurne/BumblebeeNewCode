package com.unotech.application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SimpleTimeZone;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class AppMongoImpl {

	// done
	public long totalWorkspace() {
		// db.getCollection('default').find({"ecm:primaryType" :
		// "Workspace"}).count()
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(p.getProperty("link"),
					new Integer(p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			MongoCollection<Document> data = db.getCollection("default");
			BasicDBObject query = new BasicDBObject("ecm:primaryType",
					"Workspace");
			long cursorDoc = data.count(query);
			mongoClient.close();
			reader.close();
			return cursorDoc;
		} catch (Exception e) {
			System.out.println(e);
		}
		return -1;
	}

	// done
	public boolean filesDeletedByUsers() {
		FileWriter fileWriter = null;
		// db.getCollection('default').find({ "ecm:lifeCycleState" :
		// "deleted","ecm:primaryType":"File"
		// },{"ecm:name":1,"dss:sizeTrash":2,"ecm:primaryType"
		// :3,"dc:lastContributor":4})
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(p.getProperty("link"),
					new Integer(p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			fileWriter = new FileWriter(p.getProperty("file.path")
					+ "filesDeletedByUsers.csv");
			fileWriter
					.append("Name , File Type , Size of Trash, Deleted By \n");
			MongoCollection<Document> data = db.getCollection("default");
			// boolean collectionFirst = true;
			BasicDBObject query = new BasicDBObject("ecm:lifeCycleState",
					"deleted");
			query.append("ecm:primaryType", "File");
			MongoCursor<Document> cursorDoc = data.find(query).iterator();
			while (cursorDoc.hasNext()) {
				Document collectionElement = cursorDoc.next();
				boolean first = true;
				Set<String> keySet = new HashSet<String>();
				keySet.add("ecm:name");
				keySet.add("dss:sizeTrash");
				keySet.add("ecm:primaryType");
				keySet.add("dc:lastContributor");
				/*
				 * if (collectionFirst) { for (String key: keySet) if (first) {
				 * fileWriter.append(""+key); first = !first; } else{
				 * fileWriter.append("," + key); }
				 * 
				 * collectionFirst = !collectionFirst; fileWriter.append("\n");
				 * }
				 */
				first = true;
				for (String key : keySet)
					if (first) {
						fileWriter.append("" + collectionElement.get(key));
						first = !first;
					} else {
						fileWriter.append("," + collectionElement.get(key));
					}

				fileWriter.append("\n");
			}
			fileWriter.flush();
			fileWriter.close();
			cursorDoc.close();
			mongoClient.close();
			reader.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	// done
	public boolean trashBinSizeOfWorkspace() {
		FileWriter fileWriter = null;
		// db.getCollection('default').find({ "ecm:primaryType":"Workspace"
		// },{"ecm:name":1,"dss:sizeTrash":2})
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(p.getProperty("link"),
					new Integer(p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			fileWriter = new FileWriter(p.getProperty("file.path")
					+ "trashBinSizeOfWorkspace.csv");
			fileWriter.append("Workspace, Size of Trash in Bytes \n");
			MongoCollection<Document> data = db.getCollection("default");
			// boolean collectionFirst = true;
			BasicDBObject query = new BasicDBObject("ecm:primaryType",
					"Workspace");
			MongoCursor<Document> cursorDoc = data.find(query).iterator();
			while (cursorDoc.hasNext()) {
				Document collectionElement = cursorDoc.next();
				boolean first = true;
				Set<String> keySet = new HashSet<String>();
				keySet.add("ecm:name");
				keySet.add("dss:sizeTrash");
				/*
				 * if (collectionFirst) { for (String key: keySet) if (first) {
				 * fileWriter.append(""+key); first = !first; } else{
				 * fileWriter.append("," + key); } collectionFirst =
				 * !collectionFirst; fileWriter.append("\n"); }
				 */
				first = true;
				for (String key : keySet)
					if (first) {
						if (collectionElement.get(key) != null) {
							fileWriter.append("" + collectionElement.get(key));
							first = !first;
						}
					} else {
						if (collectionElement.get(key) != null) {
							fileWriter.append("," + collectionElement.get(key));
						}
					}
				fileWriter.append("\n");
			}
			fileWriter.flush();
			fileWriter.close();
			cursorDoc.close();
			mongoClient.close();
			reader.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	// done
	public boolean maximumSizeOccupiedByWorkspace() {
		FileWriter fileWriter = null;
		// db.getCollection('default').find({ "ecm:primaryType":"Workspace"
		// },{"ecm:name":1,"dss:maxSize":2,"dss:totalSize":3})
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(p.getProperty("link"),
					new Integer(p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			fileWriter = new FileWriter(p.getProperty("file.path")
					+ "maximumSizeOccupiedByWorkspace.csv");
			fileWriter
					.append("Workspace, Max Size in Bytes, Total size in Bytes \n");
			MongoCollection<Document> data = db.getCollection("default");
			// boolean collectionFirst = true;
			BasicDBObject query = new BasicDBObject("ecm:primaryType",
					"Workspace");
			MongoCursor<Document> cursorDoc = data.find(query).iterator();
			while (cursorDoc.hasNext()) {
				Document collectionElement = cursorDoc.next();
				boolean first = true;
				Set<String> keySet = new HashSet<String>();
				keySet.add("ecm:name");
				keySet.add("dss:maxSize");
				keySet.add("dss:totalSize");
				/*
				 * if (collectionFirst) { for (String key: keySet) if (first) {
				 * fileWriter.append(""+key); first = !first; } else{
				 * fileWriter.append("," + key); } collectionFirst =
				 * !collectionFirst; fileWriter.append("\n"); }
				 */
				first = true;
				for (String key : keySet)
					if (first) {
						if (collectionElement.get(key) != null) {
							fileWriter.append("" + collectionElement.get(key));
							first = !first;
						}
					} else {
						fileWriter.append(",");
						if (collectionElement.get(key) != null) {
							fileWriter.append("" + collectionElement.get(key));
						}
					}
				fileWriter.append("\n");
			}
			fileWriter.flush();
			fileWriter.close();
			cursorDoc.close();
			mongoClient.close();
			reader.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	// done
	public int sizeOfMongodb() {
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(p.getProperty("link"),
					new Integer(p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			Document stats = db.runCommand(new Document("dbstats", 1));
			/*
			 * for (Map.Entry<String, Object> set : stats.entrySet()) {
			 * 
			 * System.out.format("%s: %s%n", set.getKey(), set.getValue()); }
			 */
			int sizeInBytes = stats.getInteger("dataSize");
			mongoClient.close();
			reader.close();
			return sizeInBytes;
		} catch (Exception e) {
			System.out.println(e);
		}
		return -1;
	}

	// done
	public boolean particularTimeWorkspace(String dateFrom, String dateTo)
			throws ParseException {
		FileWriter fileWriter = null;

		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		format.setCalendar(new GregorianCalendar(new SimpleTimeZone(+5, "GMT")));
		Date df = format.parse(dateFrom + "T00:00:00.000Z");
		Date dt = format.parse(dateTo + "T23:59:59.000Z");
		// db.getCollection('default').find({ "ecm:primaryType":"Workspace"
		// ,"dc:created":{$gte:ISODate("2017-10-07T0:0:0.0Z")
		// ,$lt:ISODate("2017-11-08T23:59:59.0Z") }
		// },{"dc:title":1,"dss:totalSize":2 })
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(p.getProperty("link"),
					new Integer(p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			fileWriter = new FileWriter(p.getProperty("file.path")
					+ "particularDateWorkspace.csv");
			fileWriter.append("Workspace , Total Size \n");
			MongoCollection<Document> data = db.getCollection("default");
			// boolean collectionFirst = true;
			BasicDBObject query = new BasicDBObject();
			query.append("ecm:primaryType", "Workspace");
			query.append("dc:created",
					new BasicDBObject("$gte", df).append("$lt", dt));
			MongoCursor<Document> cursorDoc = data.find(query).iterator();
			while (cursorDoc.hasNext()) {
				Document collectionElement = cursorDoc.next();
				boolean first = true;
				Set<String> keySet = new HashSet<String>();
				keySet.add("dc:title");
				keySet.add("dss:totalSize");
				/*
				 * if (collectionFirst) { for (String key: keySet) if (first) {
				 * fileWriter.append(""+key); first = !first; } else{
				 * fileWriter.append("," + key); }
				 * 
				 * collectionFirst = !collectionFirst; fileWriter.append("\n");
				 * }
				 */
				first = true;
				for (String key : keySet)
					if (first) {
						if (collectionElement.get(key) != null) {
							fileWriter.append("" + collectionElement.get(key));
						} else {
							fileWriter.append(" ");
						}
						first = !first;
					} else {
						if (collectionElement.get(key) != null) {
							fileWriter.append("," + collectionElement.get(key));
						} else {
							fileWriter.append(",  ");
						}
					}
				fileWriter.append("\n");
			}
			fileWriter.flush();
			fileWriter.close();
			cursorDoc.close();
			mongoClient.close();
			reader.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	// done
	public boolean workspaceDetails1(String type) {
		FileWriter printWriter = null;
		// db.getCollection('default').find({ "ecm:primaryType":"Workspace"
		// },{"ecm:name":1,"ecm:acp":2,"dcs:descendantsCount":3})
		
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(p.getProperty("link"),
					new Integer(p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			printWriter = new FileWriter(p.getProperty("file.path")
					+ "workspaceDetails.csv");
			printWriter
					.append("index,Workspace, no. of Documents, user, permission \n");
			MongoCollection<Document> data = db.getCollection("default");
			BasicDBObject query = new BasicDBObject("ecm:primaryType",
					type);//"Workspace"
//			BasicDBObject query1 = new BasicDBObject("ecm:primaryType",
//					"Workspace");//
			MongoCursor<Document> cursorDoc = data.find(query).iterator();
			int index = 1;
			while (cursorDoc.hasNext()) {
				Document collectionElement = cursorDoc.next();
				System.out.println("Documents ...======== > "+collectionElement);
				JSONObject jsonObj = new JSONObject(
						JSON.serialize(collectionElement));
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
				collectionElement.get("ecm:name").toString();
				
				printWriter.append(",");
				if (collectionElement.get("ecm:acp") != null) {
					boolean first = true;
					JSONArray jsonArray = jsonObj.getJSONArray("ecm:acp")
							.getJSONObject(0).getJSONArray("acl");
					for (int i = 0; i < jsonArray.length(); i++) {
						if (first) {
							first = !first;
							if (i + 1 == jsonArray.length()) {
								printWriter.append(jsonArray.getJSONObject(i)
										.get("user").toString()
										+ ",");
								printWriter.append(jsonArray.getJSONObject(i)
										.get("perm").toString());
								break;
							} else {
								printWriter.append(jsonArray.getJSONObject(i)
										.get("user").toString()
										+ ",");
								printWriter.append(jsonArray.getJSONObject(i)
										.get("perm").toString()
										+ "\n");
							}
						} else {
							if (i + 1 == jsonArray.length()) {
								printWriter.append(" , , ,"
										+ jsonArray.getJSONObject(i)
												.get("user").toString() + ",");
								printWriter.append(jsonArray.getJSONObject(i)
										.get("perm").toString());
								break;
							} else {
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
				System.out.println();
				printWriter.flush();
			}
			//printWriter.flush();
			printWriter.close();
			mongoClient.close();
			reader.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	public boolean permissionOnWorkspace(String workspaceId, String path,
			String permission) {
		// db.getCollection('default').update({ "ecm:name" : "manoj" },{ $push:
		// { "ecm:acp.0.acl": {"creator" : "Administrator","perm" :
		// "Read","grant" : true,"user" : "test5"} } })
		// db.getCollection('default').update({ "ecm:name" : "manoj" },{ $push:
		// { "ecm:racl": "test5" } })
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(p.getProperty("link"),
					new Integer(p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			MongoCollection<Document> collection = db.getCollection("default");
			BasicDBObject searchQuery = new BasicDBObject("ecm:id", workspaceId);

			// FindIterable<Document> find = collection.find(searchQuery);

			Path pathToFile = Paths.get(path);
			System.out.println(pathToFile);
			BufferedReader br = Files.newBufferedReader(pathToFile);
			int row = 1;
			String line;
			while ((line = br.readLine()) != null) {
				if (row == 1) {
					row++;
					continue;
				}
				String[] split = line.split(",");
				String inputName = split[0];
				BasicDBObject acl = new BasicDBObject("creator",
						"Administrator");
				acl.append("perm", permission);
				acl.append("grant", true);
				acl.append("user", inputName);
				BasicDBObject updateQuery = new BasicDBObject("$push",
						new BasicDBObject("ecm:acp.0.acl", acl));
				collection.updateOne(searchQuery, updateQuery);

				BasicDBObject name = new BasicDBObject("$push",
						new BasicDBObject("ecm:racl", inputName));
				collection.updateOne(searchQuery, name);
			}

			br.close();
			reader.close();
			mongoClient.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean permissionOnWorkspaceOfIDS(String workspaceId, String ids,
			String permission) {
		// db.getCollection('default').update({ "ecm:name" : "manoj" },{ $push:
		// { "ecm:acp.0.acl": {"creator" : "Administrator","perm" :
		// "Read","grant" : true,"user" : "test5"} } })
		// db.getCollection('default').update({ "ecm:name" : "manoj" },{ $push:
		// { "ecm:racl": "test5" } })
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(p.getProperty("link"),
					new Integer(p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			MongoCollection<Document> collection = db.getCollection("default");
			BasicDBObject searchQuery = new BasicDBObject("ecm:id", workspaceId);

			String[] split = ids.split(",");
			for (int i = 0; i < split.length; i++) {

				String inputName = split[i];
				BasicDBObject acl = new BasicDBObject("creator",
						"Administrator");
				acl.append("perm", permission);
				acl.append("grant", true);
				acl.append("user", inputName);
				BasicDBObject updateQuery = new BasicDBObject("$push",
						new BasicDBObject("ecm:acp.0.acl", acl));
				collection.updateOne(searchQuery, updateQuery);

				BasicDBObject name = new BasicDBObject("$push",
						new BasicDBObject("ecm:racl", inputName));
				collection.updateOne(searchQuery, name);
			}

			mongoClient.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String searchWorkspaceId(String docName) {

		// db.getCollection('default').find({ "ecm:name"
		// :"FolderCreationTest"},{"ecm:id":1})
		String uid = null;
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(
					p.getProperty("mongo.server.ip"), new Integer(
							p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			MongoCollection<Document> collection = db.getCollection("default");
			BasicDBObject searchQuery = new BasicDBObject("ecm:name", docName);
			MongoCursor<Document> cursorDoc = collection.find(searchQuery)
					.iterator();
			if (!cursorDoc.hasNext()) {
				reader.close();
				mongoClient.close();
				return uid;
			}

			Document next = cursorDoc.next();
			System.out.println(next);
			uid = next.getString("ecm:id");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return uid;
	}

	public boolean addFolder(String parentDocId, String path) {
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);

			Path pathToFile = Paths.get(path);
			BufferedReader br = Files.newBufferedReader(pathToFile);
			int row = 1;
			String line;
			while ((line = br.readLine()) != null) {
				if (row == 1) {
					row++;
					continue;
				}
				URL url = new URL("http://" + p.getProperty("server.ip") + ":"
						+ p.getProperty("server.port") + "/"
						+ p.getProperty("server.name") + "/api/v1/id/"
						+ parentDocId);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setDoOutput(true);
				conn.setRequestProperty("Authorization",
						"Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y");
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				String[] split = line.split(",");
				String docName = split[0];
				String input = "{\"entity-type\": \"document\",\"name\":\""
						+ docName
						+ "\",\"type\": \"Folder\",\"properties\": {\"dc:title\": \""
						+ docName
						+ "\",\"common:icon\": \"/icons/folder.gif\",\"common:icon-expanded\": null,\"common:size\": null}}";

				OutputStream os = conn.getOutputStream();
				os.write(input.getBytes());
				os.flush();
				if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br1 = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));

				String output;
				System.out.println("Output from Server .... \n");
				while ((output = br1.readLine()) != null) {
					System.out.println(output);
				}
				JSONObject stringToValue = (JSONObject) JSONObject
						.stringToValue(output);
				stringToValue.get("uid");
				conn.disconnect();
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String addFolderIn(String parentDocId, String docName) {
		String uid = null;
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			URL url = new URL("http://" + p.getProperty("server.ip") + ":"
					+ p.getProperty("server.port") + "/"
					+ p.getProperty("server.name") + "/api/v1/id/"
					+ parentDocId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization",
					"Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			String input = "{\"entity-type\": \"document\",\"name\":\""
					+ docName
					+ "\",\"type\": \"Folder\",\"properties\": {\"dc:title\": \""
					+ docName
					+ "\",\"common:icon\": \"/icons/folder.gif\",\"common:icon-expanded\": null,\"common:size\": null}}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br1 = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br1.readLine()) != null) {
				System.out.println(output);
				JSONObject stringToValue = new JSONObject(output);
				uid = (String) stringToValue.get("uid");
			}

			conn.disconnect();

			return uid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uid;
	}

	public boolean removePermissionOnWorkspace(String docId, String path) {
		// /db.default.update( {"ecm:name":"test"}, {$pull: {"ecm:acp.0.acl":
		// {"user":"test1","perm":"Read"}}}, {multi:true} )
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(
					p.getProperty("mongo.server.ip"), new Integer(
							p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			MongoCollection<Document> collection = db.getCollection("default");
			BasicDBObject searchQuery = new BasicDBObject("ecm:id", docId);
			MongoCursor<Document> cursorDoc = collection.find(searchQuery)
					.iterator();
			if (!cursorDoc.hasNext()) {
				reader.close();
				mongoClient.close();
				return false;
			}
			Document collectionElement = cursorDoc.next();
			/*
			 * JSONObject jsonObj = new JSONObject(
			 * JSON.serialize(collectionElement)); JSONArray jsonArray =
			 * jsonObj.getJSONArray("ecm:racl"); String list =
			 * jsonArray.toString();
			 */
			// System.out.println(list.contains("153148"));
			Path pathToFile = Paths.get(path);
			BufferedReader br = Files.newBufferedReader(pathToFile);
			int row = 1;
			String line;
			while ((line = br.readLine()) != null) {
				if (row == 1) {
					row++;
					continue;
				}
				String[] split = line.split(",");
				String inputName = split[0];
				String permission = split[1];
				/*
				 * if (list.contains(inputName)) { continue; }
				 */
				BasicDBObject acl = new BasicDBObject("creator",
						"Administrator");
				acl.append("perm", permission); // "Read"
				acl.append("grant", true);
				acl.append("user", inputName);
				BasicDBObject updateQuery = new BasicDBObject("$pull",
						new BasicDBObject("ecm:acp.0.acl", acl));
				collection.updateOne(searchQuery, updateQuery);

				/*
				 * BasicDBObject name = new BasicDBObject("$push", new
				 * BasicDBObject("ecm:racl", inputName));
				 * collection.updateOne(searchQuery, name);
				 */
			}

			br.close();
			reader.close();
			mongoClient.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	public boolean removePermissionOnWorkspaceByCreator(String docId,
			String path, String creator) {
		// TODO Auto-generated method stub
		// /db.default.update( {"ecm:name":"test"}, {$pull: {"ecm:acp.0.acl":
		// {"user":"test1","perm":"Read"}}}, {multi:true} )
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			MongoClient mongoClient = new MongoClient(
					p.getProperty("mongo.server.ip"), new Integer(
							p.getProperty("mongo.db.port")));
			MongoDatabase db = mongoClient.getDatabase(p
					.getProperty("mongo.database.name"));
			MongoCollection<Document> collection = db.getCollection("default");
			BasicDBObject searchQuery = new BasicDBObject("ecm:id", docId);
			MongoCursor<Document> cursorDoc = collection.find(searchQuery)
					.iterator();
			if (!cursorDoc.hasNext()) {
				reader.close();
				mongoClient.close();
				return false;
			}
			Document collectionElement = cursorDoc.next();
			/*
			 * JSONObject jsonObj = new JSONObject(
			 * JSON.serialize(collectionElement)); JSONArray jsonArray =
			 * jsonObj.getJSONArray("ecm:racl"); String list =
			 * jsonArray.toString();
			 */
			// System.out.println(list.contains("153148"));
			Path pathToFile = Paths.get(path);
			BufferedReader br = Files.newBufferedReader(pathToFile);
			int row = 1;
			String line;
			while ((line = br.readLine()) != null) {
				if (row == 1) {
					row++;
					continue;
				}
				String[] split = line.split(",");
				String inputName = split[0];
				String permission = split[1];
				/*
				 * if (list.contains(inputName)) { continue; }
				 */
				BasicDBObject acl = new BasicDBObject("creator",creator);
				acl.append("perm", permission); // "Read"
				acl.append("grant", true);
				acl.append("user", inputName);
				BasicDBObject updateQuery = new BasicDBObject("$pull",
						new BasicDBObject("ecm:acp.0.acl", acl));
				collection.updateOne(searchQuery, updateQuery);

				/*
				 * BasicDBObject name = new BasicDBObject("$push", new
				 * BasicDBObject("ecm:racl", inputName));
				 * collection.updateOne(searchQuery, name);
				 */
			}

			br.close();
			reader.close();
			mongoClient.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	
}

/*
 * public boolean specificDetails() { FileWriter fileWriter = null;
 * 
 * try { MongoClient mongoClient = new MongoClient("localhost", 27017);
 * MongoDatabase db = mongoClient.getDatabase("default"); fileWriter = new
 * FileWriter("/home/network/Desktop/specificDetails.csv");
 * //fileWriter.append("adad, aasd, adasdsa, sdad \n czxcz, cc");
 * MongoCollection<Document> data = db.getCollection("default");
 * 
 * boolean collectionFirst = true; BasicDBObject query = new
 * BasicDBObject("ecm:primaryType","Workspace"); MongoCursor<Document> cursorDoc
 * = data.find(query).iterator(); while (cursorDoc.hasNext()) {
 * 
 * Document collectionElement = cursorDoc.next(); boolean first = true; Set <
 * String > keySet = collectionElement.keySet(); if (collectionFirst) { for
 * (String key: keySet) if (first) { System.out.print(key);
 * fileWriter.append(""+key); first = !first; } else{ System.out.print("," +
 * key); fileWriter.append("," + key); }
 * 
 * collectionFirst = !collectionFirst; System.out.println("");
 * fileWriter.append("\n"); } first = true; for (String key: keySet) if (first)
 * { System.out.print(collectionElement.get(key));
 * fileWriter.append(""+collectionElement.get(key)); first = !first; } else{
 * System.out.print("," + collectionElement.get(key)); fileWriter.append("," +
 * collectionElement.get(key)); }
 * 
 * System.out.println(""); fileWriter.append("\n"); }
 * 
 * System.out.println("CSV file was created successfully !!!");
 * fileWriter.flush(); fileWriter.close(); cursorDoc.close();
 * mongoClient.close(); return true; } catch (Exception e) {
 * System.out.println(e); } return false; }
 */

/*
 * public boolean allDetails() { FileWriter fileWriter = null;
 * 
 * try { MongoClient mongoClient = new MongoClient("localhost", 27017);
 * MongoDatabase db = mongoClient.getDatabase("nuxeo"); fileWriter = new
 * FileWriter("/home/network/Desktop/allDetails.csv");
 * //fileWriter.append("adad, aasd, adasdsa, sdad \n czxcz, cc");
 * MongoCollection<Document> data = db.getCollection("default");
 * 
 * boolean collectionFirst = true; MongoCursor<Document> cursorDoc =
 * data.find().iterator(); while (cursorDoc.hasNext()) {
 * 
 * Document collectionElement = cursorDoc.next(); boolean first = true; Set <
 * String > keySet = collectionElement.keySet(); if (collectionFirst) { for
 * (String key: keySet) if (first) { System.out.print(key);
 * fileWriter.append(""+key); first = !first; } else{ System.out.print("," +
 * key); fileWriter.append("," + key); }
 * 
 * collectionFirst = !collectionFirst; System.out.println("");
 * fileWriter.append("\n"); } first = true; for (String key: keySet) if (first)
 * { System.out.print(collectionElement.get(key));
 * fileWriter.append(""+collectionElement.get(key)); first = !first; } else{
 * System.out.print("," + collectionElement.get(key)); fileWriter.append("," +
 * collectionElement.get(key)); }
 * 
 * System.out.println(""); fileWriter.append("\n"); }
 * 
 * System.out.println("CSV file was created successfully !!!");
 * fileWriter.flush(); fileWriter.close(); cursorDoc.close();
 * mongoClient.close(); return true; } catch (Exception e) {
 * System.out.println(e); } return false; }
 */

/*
 * public boolean loginSuccess() throws JSONException { FileWriter fileWriter =
 * null; String url="http://localhost:9200/nuxeo-audit/_search?pretty&size=500";
 * String data =
 * "{\"_source\":[\"eventDate\",\"principalName\"],\"query\":{\"term\":{\"eventId\":\"loginSuccess\"}}}"
 * ; String[] command = {"curl", "-H", "Accept:application/json" , url, "-d" ,
 * data} ; ProcessBuilder process = new ProcessBuilder(command); Process p; try
 * { p = process.start(); fileWriter = new
 * FileWriter("/home/network/Desktop/loginDetails.csv"); BufferedReader reader =
 * new BufferedReader(new InputStreamReader(p.getInputStream())); StringBuilder
 * builder = new StringBuilder(); String line = null; while ( (line =
 * reader.readLine()) != null) { builder.append(line);
 * builder.append(System.getProperty("line.separator")); } JSONObject jsonObj =
 * new JSONObject(builder.toString()); JSONArray jsonArray = new
 * JSONArray(jsonObj.getJSONObject("hits").getJSONArray("hits").toString()) ;
 * fileWriter.append("principalName,eventDate\n"); for(int i=0 ; i<
 * jsonArray.length(); i++){
 * fileWriter.append(jsonArray.getJSONObject(i).getJSONObject
 * ("_source").get("principalName").toString()); fileWriter.append(",");
 * fileWriter
 * .append(jsonArray.getJSONObject(i).getJSONObject("_source").get("eventDate"
 * ).toString()); fileWriter.append("\n"); } reader.close(); fileWriter.flush();
 * fileWriter.close(); return true; } catch (IOException e) {
 * System.out.print("error"); e.printStackTrace(); } return false; }
 */

/*
 * public boolean uploadsAndDownloads() throws JSONException { FileWriter
 * fileWriter = null; String
 * url="http://localhost:9200/nuxeo-audit/_search?pretty&size=500"; String data
 * = "{\"query\":{\"term\":{\"docType\":\"File\"}}}" ; String[] command =
 * {"curl", "-H", "Accept:application/json" , url, "-d" , data} ; ProcessBuilder
 * process = new ProcessBuilder(command); Process p; try { p = process.start();
 * fileWriter = new FileWriter("/home/network/Desktop/fileDownloadDetails.csv");
 * BufferedReader reader = new BufferedReader(new
 * InputStreamReader(p.getInputStream())); StringBuilder builder = new
 * StringBuilder(); String line = null; while ( (line = reader.readLine()) !=
 * null) { builder.append(line);
 * builder.append(System.getProperty("line.separator")); } JSONObject jsonObj =
 * new JSONObject(builder.toString()); JSONArray jsonArray = new
 * JSONArray(jsonObj.getJSONObject("hits").getJSONArray("hits").toString()) ;
 * fileWriter
 * .append("principalName,comment,docPath,docType,eventId,eventDate\n"); for(int
 * i=0 ; i< jsonArray.length(); i++){ for(int
 * j=0;j<jsonArray.getJSONObject(i).getJSONObject("_source").length();j++){
 * fileWriter
 * .append(jsonArray.getJSONObject(i).getJSONObject("_source").get("principalName"
 * ).toString() ); fileWriter.append(",");
 * fileWriter.append(jsonArray.getJSONObject
 * (i).getJSONObject("_source").get("comment").toString() );
 * fileWriter.append(",");
 * fileWriter.append(jsonArray.getJSONObject(i).getJSONObject
 * ("_source").get("docPath").toString() ); fileWriter.append(",");
 * fileWriter.append
 * (jsonArray.getJSONObject(i).getJSONObject("_source").get("docType"
 * ).toString() ); fileWriter.append(",");
 * fileWriter.append(jsonArray.getJSONObject
 * (i).getJSONObject("_source").get("eventId").toString() );
 * fileWriter.append(",");
 * fileWriter.append(jsonArray.getJSONObject(i).getJSONObject
 * ("_source").get("eventDate").toString() ); fileWriter.append(",");
 * //fileWriter
 * .append(jsonArray.getJSONObject(i).getJSONObject("_source").get("extended"
 * ).toString() ); /*
 * if(jsonArray.getJSONObject(i).getJSONObject("_source").get(
 * "extended").toString() == "{}" ){ fileWriter.append("\n"); continue; } else{
 * fileWriter
 * .append(jsonArray.getJSONObject(i).getJSONObject("_source").getJSONObject
 * ("extended").get("blobFilename").toString() ); fileWriter.append(",");
 * fileWriter
 * .append(jsonArray.getJSONObject(i).getJSONObject("_source").getJSONObject
 * ("extended").get("downloadReason").toString() ); } fileWriter.append("\n"); }
 * } reader.close(); fileWriter.flush(); fileWriter.close(); return true;
 * 
 * } catch (IOException e) { System.out.print("error"); e.printStackTrace(); }
 * return false; }
 */
