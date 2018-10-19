package com.unotech.application;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class AppOracleImpl {
	
	private Connection connection = null;
	private AppMongoImpl appMongoImpl= new AppMongoImpl();

	public AppOracleImpl() {
		try {
			FileReader reader = new FileReader(
					"/tmp/Bumblebee/config.properties");
			Properties p = new Properties();
			p.load(reader);
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@"
							+ p.getProperty("oracle.worksite.server.ip") + ":"
							+ p.getProperty("oracle.worksite.port") + ":"
							+ p.getProperty("oracle.worksite.database.name"),
					p.getProperty("oracle.worksite.user.name"),
					p.getProperty("oracle.worksite.password"));
			System.out.println("------------connection created----------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getWorkspaceStructure(String workspace) {
		try {
			System.out.println("---inside getWorkspaceStructure--------"+workspace);
			/*
			 * select name,sid from wks_workspace w where w.name = 'ROG1';
			 * 
			 * select sid,p_name,t.workspace_rsid from doc_document t where
			 * t.workspace_rsid ='20401'--6024 and p_datasource='project' order
			 * by p_name;
			 * 
			 * select
			 * P.P_PARENT_RSID,D.SID,D.P_NAME,d.p_datasource,p.p_workspace_rsid
			 * from P_REL_REL_DOCUMENT_DOCUMENT P LEFT OUTER JOIN DOC_DOCUMENT D
			 * ON P.P_CHILD_RSID = D.SID WHERE P.P_PARENT_RSID='141219' and
			 * d.p_datasource='documentFolder' order by D.P_NAME;
			 * 
			 * select distinct p_datasource from doc_document;
			 */

			String sql = "select sid,p_name,workspace_rsid from doc_document where workspace_rsid =(select sid from wks_workspace w where trim(w.name) = trim(?)) and p_datasource='project' order by p_name";
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, workspace);
			
			String workspaceId = appMongoImpl.searchWorkspaceId(workspace);
			
			ResultSet rs = prepareStatement.executeQuery();
			while (rs.next()) {
				String sid = rs.getInt("sid") + "";
				String name = rs.getString("p_name");
//				System.out.println("workspace:- "+name);
				String folderId = appMongoImpl.addFolderIn(workspaceId, name);
				getFolders(sid,folderId);
			}
			rs.close();
			prepareStatement.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void getFolders(String worksiteId,String folderId) {
		PreparedStatement prepareStatement;
		ResultSet rs = null;
		String sql = "select D.SID,D.P_NAME from P_REL_REL_DOCUMENT_DOCUMENT P LEFT OUTER JOIN DOC_DOCUMENT D ON P.P_CHILD_RSID = D.SID WHERE P.P_PARENT_RSID=? and d.p_datasource='documentFolder' order by D.P_NAME";
		try {
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, worksiteId);
			rs = prepareStatement.executeQuery();
			while (rs.next()) {
				String sid = rs.getInt(1) + "";
				String name = rs.getString(2);
				//String workspaceId = appMongoImpl.searchWorkspaceId(name);
				if(true){
					String newFolderId = appMongoImpl.addFolderIn(folderId, name);
					System.out.println("Folder created :-"+newFolderId);
					getFolders(sid, newFolderId);
				}else{
					System.out.println("Folder alredy present :-"+name);
				}
			}
			rs.close();
			prepareStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
	}
}