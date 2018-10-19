package com.unotech.application;

import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;
/**
 * created by Sandesh Patil @UnotechSoftwares
 *
 */
public class App {
	public static void main(String[] args) throws IOException, ParseException {
		AppMongoImpl impl = new AppMongoImpl();
		AppPostgresImpl postgreImpl = new AppPostgresImpl();
		AppOracleImpl oracleImpl = new AppOracleImpl();
		int choice, status9;
		boolean status;
		Scanner sc = new Scanner(System.in);
		do {
			System.out.println("Options");
			System.out.println("3: Finding total Workspace size");
			System.out.println("4: LIST OF FILES DELETED BY USERS WITH SIZE");
			System.out.println("5: TRASH  BIN size of workspace");
			System.out.println("6: Maximum size/space occupied by Workspace");
			System.out.println("7: User's login details");
			System.out.println("8: Today's user logins");
			System.out.println("9: Size of mongodb");
			System.out
					.println("10: NAME OF USERS WITH NUMBER OF DOCUMENT AND SIZE IN WORKSPACE");
			System.out
					.println("11: TOTAL SPACE FOR PARTICULAR TIME SPAN/Period");
			System.out.println("12: CREATE FOLDER STRUCTURE IN WORKSPACE");
			System.out.println("13: REMOVE PERMISSION ON WORKSPACE");
			System.out.println("14: ASSIGN PERMISSION ON WORKSPACE (using text)");
			System.out.println("15: REMOVE PERMISSION ON WORKSPACE BY CREATOR");
			System.out.println("22: ASSIGN PERMISSION ON WORKSPACE");
			/*
			 * System.out.println(
			 * "8: document upload/download against the workspaces");
			 * System.out.println(" No. of unique users that logged in today");
			 */
			System.out.println("0: exit");
			System.out.println("enter the options");
			choice = sc.nextInt();
			switch (choice) {

			case 3:
				long status3 = impl.totalWorkspace();
				if (status3 == -1) {
					System.out.println("error");
				} else
					System.out.println("Total workspace :" + status3);
				break;
			case 4:
				status = impl.filesDeletedByUsers();
				if (status) {
					System.out.println("data is available in csv");
				} else
					System.out.println("error");
				break;
			case 5:
				status = impl.trashBinSizeOfWorkspace();
				if (status) {
					System.out.println("data is available in csv");
				} else
					System.out.println("error");
				break;
			case 6:
				status = impl.maximumSizeOccupiedByWorkspace();
				if (status) {
					System.out.println("data is available in csv");
				} else
					System.out.println("error");
				break;
			case 7:
				status = postgreImpl.loginDetails();
				if (status) {
					System.out.println("data is available in csv");
				} else
					System.out.println("error");
				break;
			case 8:
				status = postgreImpl.todaysloginDetails();
				if (status) {
					System.out.println("data is available in csv");
				} else
					System.out.println("error");
				break;
		case 9:
				status9 = impl.sizeOfMongodb();
				if (status9 == -1) {
					System.out.println("error");
				} else
					System.out.println("Size of mongodb :" + status9 + "Bytes");
				break;
			case 10:
				System.out.println("Enter ecm:primaryType: ");
				System.out.println("eg. Workspace, Folder, File, OrderedFolder, Note, Comment, HiddenFolder,Favorites , Collections, Collection");
				String type = sc.next();
				status = impl.workspaceDetails1(type);
//				status = impl.workspaceDetails1();
				
				
				if (status) {
					System.out.println("data is available in csv");
				} else {
					System.out.println("error");
				}
				break;
			case 11:
				System.out.println("Enter Date From:YYYY-MM-DD format ");
				String dateFrom = sc.next();
				System.out.println("Date till:YYYY-MM-DD format");
				String dateTo = sc.next();
				try {
					status = impl.particularTimeWorkspace(dateFrom, dateTo);

					if (status) {
						System.out.println("data is available in csv");
					} else
						System.out.println("error");
					break;
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
				break;
			case 12:
				System.out.println("\033[33m \t ENTER PARENT DOCUMENT NAME");
				String docName = sc.next();
				oracleImpl.getWorkspaceStructure(docName);
				// String parentDocId = impl.searchWorkspaceId(docName);
				// System.out.println("\033[33m \t ID : "+parentDocId);
				// System.out.println("\033[33m \t ENTER FOLDER NAME");
				// docName = sc.next();
				// impl.addFolderIn(parentDocId, docName);
				break;
			case 13:
				System.out.println("Enter Id of Workspace");
				String workspace1 = sc.next();
				System.out.println("Enter the path of csv file:");
				String path1 = sc.next();
				// System.out.println("Enter the permission from options (Read,Everything,ReadWrite):");
				// String permission = sc.next();
				status = impl.removePermissionOnWorkspace(workspace1, path1);
				if (status) {
					System.out.println("work is done");
				} else
					System.out.println("error");
				break;
				
				// permission by text
			case 14:System.out.println("Enter Id of Workspace");
			String workspace = sc.next();
			System.out.println("Enter the comm seprated IDs");
			String ids = sc.next();
			System.out.println("Enter the permission from options (Read,Everything,ReadWrite):");
			String permission = sc.next();
			status = impl.permissionOnWorkspaceOfIDS(workspace,ids,permission);
			if(status){
				System.out.println("work is done");
			}
			else System.out.println("error");
			break;
				
			case 15:
				System.out.println("Enter Id of Workspace");
				String workspace2 = sc.next();
				System.out.println("Enter the path of csv file options (Read,Everything,ReadWrite):");
				String path2 = sc.next();
				System.out.println("Enter the creator");
				String creator = sc.next();
				status = impl.removePermissionOnWorkspaceByCreator(workspace2, path2, creator);
				if (status) {
					System.out.println("work is done");
				} else
					System.out.println("error");
				break;
				

			case 22:
				System.out.println("Enter Id of Workspace");
				 workspace = sc.next();
				System.out.println("Enter the path of csv file:");
				String path = sc.next();
				System.out
						.println("Enter the permission from options (Read,Everything,ReadWrite):");
				 permission = sc.next();
				status = impl
						.permissionOnWorkspace(workspace, path, permission);
				if (status) {
					System.out.println("work is done");
				} else
					System.out.println("error");
				break;

		
			case 0:
				break;

			
			default:
				System.out.println("please enter correct option");
				break;
			}

		} while (choice != 0);
		sc.close();
		System.out.println("Thank you!");
	}
}

/*
 * case 1: System.out.println("hello1"); status = impl.allDetails(); if(status){
 * System.out.println("data is available in csv"); } else
 * System.out.println("error"); break; case 2: System.out.println("hello2");
 * status = impl.specificDetails(); if(status){
 * System.out.println("data is available in csv"); } else
 * System.out.println("error"); break;
 * 
 * case 8: System.out.println("hello8"); status = impl.uploadsAndDownloads();
 * if(status){ System.out.println("data is available in csv"); } else
 * System.out.println("error"); break;
 */