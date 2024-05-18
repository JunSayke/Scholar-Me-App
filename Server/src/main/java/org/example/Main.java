package org.example;

import org.example.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        MySQLConnection.createDatabase("dbscholarme");

        // Create necessary tables
        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(false);
            try {
                stmt.addBatch("CREATE TABLE IF NOT EXISTS tbluseraccount ("
                        + "userid INT PRIMARY KEY AUTO_INCREMENT,"
                        + "email VARCHAR(255) NOT NULL UNIQUE,"
                        + "password VARCHAR(255) NOT NULL,"
                        + "role ENUM('admin', 'creator', 'user') NOT NULL DEFAULT 'user',"
                        + "status ENUM('active', 'inactive') NOT NULL DEFAULT 'active',"
                        + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                        + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                        + ")"
                );
                stmt.addBatch("CREATE TABLE IF NOT EXISTS tbluserprofile ("
                        + "acctid INT PRIMARY KEY AUTO_INCREMENT,"
                        + "firstname VARCHAR(255) NOT NULL,"
                        + "lastname VARCHAR(255) NOT NULL,"
                        + "phonenumber VARCHAR(11) NOT NULL,"
                        + "profilepic VARCHAR(255) NOT NULL,"
                        + "FOREIGN KEY (acctid) REFERENCES tbluseraccount(userid) ON DELETE CASCADE"
                        + ")"
                );
                stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcreatorapplicant ("
                        + "creatorapplicantid INT PRIMARY KEY AUTO_INCREMENT,"
                        + "userid INT NOT NULL,"
                        + "status ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',"
                        + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                        + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                        + "FOREIGN KEY (userid) REFERENCES tbluseraccount(userid) ON DELETE CASCADE"
                        + ")"
               );
               stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcourse ("
                       + "courseid INT PRIMARY KEY AUTO_INCREMENT,"
                       + "author INT NOT NULL,"
                       + "coursename VARCHAR(255) NOT NULL,"
                       + "description TEXT NOT NULL,"
                       + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                       + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                       ")"
               );
               stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcoursechapter ("
                       + "coursechapterid INT PRIMARY KEY AUTO_INCREMENT,"
                       + "courseid INT NOT NULL,"
                       + "chaptername VARCHAR(255) NOT NULL,"
                       + "description TEXT NOT NULL,"
                       + "content VARCHAR(255) NOT NULL,"
                       + "duration INT NOT NULL,"
                       + "islocked BOOLEAN NOT NULL DEFAULT 0,"
                       + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                       + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                       + "FOREIGN KEY (courseid) REFERENCES tblcourse(courseid) ON DELETE CASCADE" +
                       ")"
               );
               stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcourseenrollment ("
                       + "enrollmentid INT PRIMARY KEY AUTO_INCREMENT,"
                       + "courseid INT NOT NULL,"
                       + "userid INT NOT NULL,"
                       + "dateenrolled TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                       + "FOREIGN KEY (courseid) REFERENCES tblcourse(courseid) ON DELETE CASCADE,"
                       + "FOREIGN KEY (userid) REFERENCES tbluseraccount(userid) ON DELETE CASCADE" +
                       ")"
                );
               // TODO: Add more tables here
                stmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
//                e.printStackTrace();
                conn.rollback();
                throw new RuntimeException("Failed to create tables");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        // Start the server
        Route.launch(6969);
    }
 }