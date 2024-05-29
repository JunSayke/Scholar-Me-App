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
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tbluseraccount ("
                    + "userid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "username VARCHAR(255) NOT NULL UNIQUE,"
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
                    + "profilepic VARCHAR(255),"
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
                    + "title VARCHAR(255) NOT NULL,"
                    + "description TEXT NOT NULL,"
                    + "thumbnail VARCHAR(255),"
                    + "views INT NOT NULL DEFAULT 0,"
                    + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcourselesson ("
                    + "courselessonid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "courseid INT NOT NULL,"
                    + "title VARCHAR(255) NOT NULL,"
                    + "lessonno INT NOT NULL,"
                    + "description TEXT NOT NULL,"
                    + "content TEXT NOT NULL,"
                    + "duration INT NOT NULL,"
                    + "islocked BOOLEAN NOT NULL DEFAULT 0,"
                    + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (courseid) REFERENCES tblcourse(courseid) ON DELETE CASCADE" +
                    ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcourselearner ("
                    + "courselearnerid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "courseid INT NOT NULL,"
                    + "userid INT NOT NULL,"
                    + "dateenrolled TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (courseid) REFERENCES tblcourse(courseid) ON DELETE CASCADE,"
                    + "FOREIGN KEY (userid) REFERENCES tbluseraccount(userid) ON DELETE CASCADE,"
                    + "UNIQUE (courseid, userid)"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tbluserfavorite ("
                    + "favoriteid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "userid INT NOT NULL,"
                    + "courseid INT NOT NULL,"
                    + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (userid) REFERENCES tbluseraccount(userid) ON DELETE CASCADE,"
                    + "FOREIGN KEY (courseid) REFERENCES tblcourse(courseid) ON DELETE CASCADE,"
                    + "UNIQUE (userid, courseid)"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblflashcardset ("
                    + "flashcardsetid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "userid INT NOT NULL,"
                    + "title VARCHAR(255) NOT NULL,"
                    + "description TEXT NOT NULL,"
                    + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (userid) REFERENCES tbluseraccount(userid) ON DELETE CASCADE"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblflashcard ("
                    + "flashcardid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "userid INT NOT NULL,"
                    + "question TEXT NOT NULL,"
                    + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (userid) REFERENCES tbluseraccount(userid) ON DELETE CASCADE"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblflashcardsetflashcard ("
                    + "flashcardsetflashcardid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "flashcardsetid INT NOT NULL,"
                    + "flashcardid INT NOT NULL,"
                    + "FOREIGN KEY (flashcardsetid) REFERENCES tblflashcardset(flashcardsetid) ON DELETE CASCADE,"
                    + "FOREIGN KEY (flashcardid) REFERENCES tblflashcard(flashcardid) ON DELETE CASCADE"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblflashcardchoice ("
                    + "flashcardchoiceid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "flashcardid INT NOT NULL,"
                    + "choice TEXT NOT NULL,"
                    + "isanswer BOOLEAN NOT NULL DEFAULT 0,"
                    + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (flashcardid) REFERENCES tblflashcard(flashcardid) ON DELETE CASCADE"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblnotification ("
                    + "notificationid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "userid INT NOT NULL,"
                    + "title VARCHAR(255) NOT NULL,"
                    + "message TEXT NOT NULL,"
                    + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (userid) REFERENCES tbluseraccount(userid) ON DELETE CASCADE"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcategory ("
                    + "categoryid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "category VARCHAR(255) NOT NULL"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcoursecategory ("
                    + "coursecategoryid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "courseid INT NOT NULL,"
                    + "categoryid INT NOT NULL,"
                    + "FOREIGN KEY (courseid) REFERENCES tblcourse(courseid) ON DELETE CASCADE,"
                    + "FOREIGN KEY (categoryid) REFERENCES tblcategory(categoryid) ON DELETE CASCADE"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblflashcardsetcategory ("
                    + "flashcardsetcategoryid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "flashcardsetid INT NOT NULL,"
                    + "categoryid INT NOT NULL,"
                    + "FOREIGN KEY (flashcardsetid) REFERENCES tblflashcardset(flashcardsetid) ON DELETE CASCADE,"
                    + "FOREIGN KEY (categoryid) REFERENCES tblcategory(categoryid) ON DELETE CASCADE"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcomment ("
                    + "commentid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "userid INT NOT NULL,"
                    + "comment TEXT NOT NULL,"
                    + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tbldiscussioncomment ("
                    + "discussioncommentid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "commentid INT NOT NULL,"
                    + "FOREIGN KEY (commentid) REFERENCES tblcomment(commentid) ON DELETE CASCADE"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcoursecomment ("
                    + "coursecommentid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "courseid INT NOT NULL,"
                    + "commentid INT NOT NULL,"
                    + "FOREIGN KEY (courseid) REFERENCES tblcourse(courseid) ON DELETE CASCADE,"
                    + "FOREIGN KEY (commentid) REFERENCES tblcomment(commentid) ON DELETE CASCADE"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblcourselessoncomment ("
                    + "courselessoncommentid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "courselessonid INT NOT NULL,"
                    + "commentid INT NOT NULL,"
                    + "FOREIGN KEY (courselessonid) REFERENCES tblcourselesson(courselessonid) ON DELETE CASCADE,"
                    + "FOREIGN KEY (commentid) REFERENCES tblcomment(commentid) ON DELETE CASCADE"
                    + ")"
            );
            stmt.addBatch("CREATE TABLE IF NOT EXISTS tblreply ("
                    + "replyid INT PRIMARY KEY AUTO_INCREMENT,"
                    + "commentid INT NOT NULL,"
                    + "userid INT NOT NULL,"
                    + "reply TEXT NOT NULL,"
                    + "dateadded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "dateupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ")"
            );
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Start the server
        Route.launch(6969);
    }
}


