import java.io.*;
import java.sql.*;
import java.util.*;
import java.math.* ;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class CourseEnrollmentSys {
    
    // Global variable
    public static Connection conn = null;
    
    
    // Administrator Menu
    public static void adminMenu(){
        System.out.println("============================================");
        System.out.println("Administrator Menu");
        System.out.println("============================================");
        System.out.println("1. Create all tables in the database");
        System.out.println("2. Delete all tables in the database");
        System.out.println("3. Load data into the database");
        System.out.println("4. Show the information of the database");
        System.out.println("5. Show the ranking of courses");
        System.out.println("6. Exit");
        System.out.println("============================================");
    }
    
    // Student Menu
    public static void studentMenu(){
        System.out.println("============================================");
        System.out.println("Student Menu");
        System.out.println("============================================");
        System.out.println("1. Enroll into a course");
        System.out.println("2. Drop from a course");
        System.out.println("3. Show the academic report");
        System.out.println("4. Exit");
        System.out.println("============================================");
    }
    
    // Administrator operation number
    public static int choAdminNum(){
        int num = 0;
        boolean is_valid = false;
        System.out.println("Pass the choice (1-6): ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (!is_valid){
            try {
                num = Integer.parseInt(in.readLine());
            } catch(IOException ioExc) {
                is_valid = false;
            } catch(NumberFormatException nfExc) {
                is_valid = false;
            }
            if (!(num >= 1 && num <= 6)){
                is_valid = false;
            }
            if (num >= 1 && num <= 6){
                is_valid = true;
            }
            if (!is_valid){
                System.out.println("Invalid input!");
                System.out.println("Pass the choice (1-6): ");
            }
        }
        return num;
    }

    // Student operation number
    public static int choStuNum(){
        int num = 0;
        boolean is_valid = false;
        System.out.println("Pass the choice (1-4): ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (!is_valid){
            try {
                num = Integer.parseInt(in.readLine());
            } catch(IOException ioExc) {
                is_valid = false;
            } catch(NumberFormatException nfExc) {
                is_valid = false;
            }
            if (!(num >= 1 && num <= 4)){
                is_valid = false;
            }
            if (num >= 1 && num <= 4){
                is_valid = true;
            }
            if (!is_valid){
                System.out.println("Invalid input!");
                System.out.println("Pass the choice (1-4): ");
            }
        }
        return num;
    }
    
//================================================================================
    // create all table
    public static void createAllTables(){
        Statement stmt;
        boolean is_success = true;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE STUDENT "
                              + "(sid NUMBER(10) PRIMARY KEY, "
                              + "name CHAR(26) NOT NULL, "
                              + "major CHAR(101) NOT NULL)");
            stmt.executeUpdate("CREATE TABLE COURSE "
                               + "(code CHAR(9) PRIMARY KEY, "
                               + "cname CHAR(101) NOT NULL, "
                               + "credit NUMBER(1))");
            stmt.executeUpdate("CREATE TABLE CSECTION "
                               + "(code CHAR(9), "
                               + "cYear NUMBER(4), "
                               + "cTerm NUMBER(1), "
                               + "quota NUMBER(2), "
                               + "instructor CHAR(26) NOT NULL, "
                               + "PRIMARY KEY (code, cYear, cTerm), "
                               + "FOREIGN KEY (code) REFERENCES COURSE(code))");
            stmt.executeUpdate("CREATE TABLE LECTURE "
                               + "(code CHAR(9), "
                               + "LYear NUMBER(4), "
                               + "LTerm NUMBER(1), "
                               + "timeslot CHAR(4), "
                               + "PRIMARY KEY(code, LYear, LTerm, timeslot), "
                               + "FOREIGN KEY (code, LYear, LTerm) REFERENCES CSECTION(code, cYear, cTerm))");
            stmt.executeUpdate("CREATE TABLE ENROLL "
                               + "(sid INT, "
                               + "code CHAR(9), "
                               + "LYear NUMBER(4), "
                               + "LTerm NUMBER(1), "
                               + "grade CHAR(2), "
                               + "PRIMARY KEY (sid, code, LYear, LTerm), "
                               + "FOREIGN KEY (sid) REFERENCES STUDENT(sid), "
                               + "FOREIGN KEY (code,LYear,LTerm) REFERENCES CSECTION(code, cYear, cTerm))");
            stmt.executeUpdate("CREATE TABLE PREREQUISITE "
                               + "(course CHAR(9), "
                               + "precourse CHAR(9), "
                               + "PRIMARY KEY(course, precourse), "
                               + "FOREIGN KEY (course) REFERENCES COURSE(code), "
                               + "FOREIGN KEY (precourse) REFERENCES COURSE(code))");
            stmt.close();
        } catch (SQLException sqlExc) {
            System.out.println("ERROR: create tables fail...");
            is_success = false;
        }
        if (is_success){
            System.out.println("Create tables success...");
        }
    }
    
//================================================================================
    // delete all table
    public static void deleteAllTables(){
        Statement stmt;
        boolean is_success = true;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE LECTURE");
            stmt.executeUpdate("DROP TABLE ENROLL");
            stmt.executeUpdate("DROP TABLE PREREQUISITE");
            stmt.executeUpdate("DROP TABLE CSECTION");
            stmt.executeUpdate("DROP TABLE COURSE");
            stmt.executeUpdate("DROP TABLE STUDENT");
            stmt.close();
        } catch (SQLException sqlExc) {
            System.out.println("ERROR: delete tables fail...");
            is_success = false;
        }
        if (is_success){
            System.out.println("Delete tables success...");
        }
    }
    
//=================================================================================
    // load data into database
    public static void loadData(String filename){
        BufferedReader inFile = null;
        PreparedStatement pstmt_1 = null;
        PreparedStatement pstmt_2 = null;
        String[] instance;
        String currentLine;
        
        if (filename.equals("course.txt")){
            try {
                inFile = new BufferedReader(new FileReader(new File(filename)));
                pstmt_1 = conn.prepareStatement("INSERT INTO COURSE VALUES (?, ?, ?)");
                pstmt_2 = conn.prepareStatement("INSERT INTO PREREQUISITE VALUES (?, ?)");
                currentLine = inFile.readLine();
                while(currentLine != null){
                    instance = currentLine.split("\t");
                    //System.out.println(instance[0] + " " + instance[1] + " " + instance[2] + " " + instance[3]);
                    //System.out.println(instance.length);
                    pstmt_1.setString(1, instance[0]);
                    pstmt_1.setString(2, instance[1]);
                    pstmt_1.setInt(3, Integer.parseInt(instance[2]));
                    pstmt_1.executeUpdate();
                    currentLine = inFile.readLine();
                }
                inFile.close();
                
                inFile = new BufferedReader(new FileReader(new File(filename)));
                currentLine = inFile.readLine();
                while(currentLine != null){
                    instance = currentLine.split("\t");
                    String[] preCourse;
                    preCourse = instance[3].split(",");
                    for (int z = 0; z < preCourse.length; z++){
                        //System.out.println(preCourse[z]);
                        if (!preCourse[z].equals("null")){
                            pstmt_2.setString(1, instance[0]);
                            pstmt_2.setString(2, preCourse[z]);
                            pstmt_2.executeUpdate();
                        }
                    }
                    currentLine = inFile.readLine();
                }
                
                pstmt_1.close();
                pstmt_2.close();
                System.out.println("Load data from " + filename + " in to database success...");
            } catch (FileNotFoundException fnfExc) {
                System.out.println(filename + " not found!");
            } catch (SQLException sqlExc) {
                System.out.println("ERROR: SQL error!");
            } catch (IOException ioExc) {
                System.out.println("ERROR: I/O error!");
            }
        }
        else if (filename.equals("enrollment.txt")){
            try {
                inFile = new BufferedReader(new FileReader(new File(filename)));
                pstmt_1 = conn.prepareStatement("INSERT INTO ENROLL VALUES (?, ?, ?, ?, ?)");
                currentLine = inFile.readLine();
                while (currentLine != null){
                    instance = currentLine.split("\t");
                    //System.out.println(instance[4]);
                    pstmt_1.setInt(1, Integer.parseInt(instance[0]));
                    pstmt_1.setString(2, instance[1]);
                    pstmt_1.setInt(3, Integer.parseInt(instance[2]));
                    pstmt_1.setInt(4, Integer.parseInt(instance[3]));
                    if (!instance[4].equals("null")){
                        pstmt_1.setString(5, instance[4]);
                    }
                    else {
                        pstmt_1.setNull(5, 0);
                    }
                    pstmt_1.executeUpdate();
                    currentLine = inFile.readLine();
                }
                
                pstmt_1.close();
                System.out.println("Load data from " + filename + " in to database success...");
            } catch (FileNotFoundException fnfExc) {
                System.out.println(filename + " not found!");
            } catch (SQLException sqlExc) {
                System.out.println("ERROR: SQL error!");
            } catch (IOException ioExc) {
                System.out.println("ERROR: I/O error!");
            }
        }
        else if (filename.equals("section.txt")){
            try{
                inFile = new BufferedReader(new FileReader(new File(filename)));
                pstmt_1 = conn.prepareStatement("INSERT INTO CSECTION VALUES (?, ?, ?, ?, ?)");
                pstmt_2 = conn.prepareStatement("INSERT INTO LECTURE VALUES (?, ?, ?, ?)");
                currentLine = inFile.readLine();
                while (currentLine != null){
                    instance = currentLine.split("\t");
                    //System.out.println(instance[5]);
                    pstmt_1.setString(1, instance[0]);
                    pstmt_1.setInt(2, Integer.parseInt(instance[1]));
                    pstmt_1.setInt(3, Integer.parseInt(instance[2]));
                    pstmt_1.setInt(4, Integer.parseInt(instance[3]));
                    pstmt_1.setString(5, instance[4]);
                    pstmt_1.executeUpdate();
                    currentLine = inFile.readLine();
                }
                inFile.close();
                
                inFile = new BufferedReader(new FileReader(new File(filename)));
                currentLine = inFile.readLine();
                while (currentLine != null){
                    instance = currentLine.split("\t");
                    String[] lec;
                    lec = instance[5].split(",");
                    for (int z = 0; z < lec.length; z++){
                        //System.out.println(lec[z]);
                        if (!lec[z].equals("null")){
                            pstmt_2.setString(1, instance[0]);
                            pstmt_2.setInt(2, Integer.parseInt(instance[1]));
                            pstmt_2.setInt(3, Integer.parseInt(instance[2]));
                            pstmt_2.setString(4, lec[z]);
                            pstmt_2.executeUpdate();
                        }
                    }
                    currentLine = inFile.readLine();
                }
                
                pstmt_1.close();
                pstmt_2.close();
                System.out.println("Load data from " + filename + " in to database success...");
            } catch (FileNotFoundException fnfExc) {
                System.out.println(filename + " not found!");
            } catch (SQLException sqlExc) {
                System.out.println("ERROR: SQL error!");
            } catch (IOException ioExc) {
                System.out.println("ERROR: I/O error!");
            }
        }
        else if (filename.equals("student.txt")){
            try {
                inFile = new BufferedReader(new FileReader(new File(filename)));
                pstmt_1 = conn.prepareStatement("INSERT INTO STUDENT VALUES (?, ?, ?)");
                currentLine = inFile.readLine();
                while (currentLine != null){
                    instance = currentLine.split("\t");
                    //System.out.println(instance[2]);
                    pstmt_1.setInt(1, Integer.parseInt(instance[0]));
                    pstmt_1.setString(2, instance[1]);
                    pstmt_1.setString(3, instance[2]);
                    pstmt_1.executeUpdate();
                    currentLine = inFile.readLine();
                }
                
                pstmt_1.close();
                System.out.println("Load data from " + filename + " in to database success...");
            } catch (FileNotFoundException fnfExc) {
                System.out.println(filename + " not found!");
            } catch (SQLException sqlExc) {
                System.out.println("ERROR: SQL error!");
            } catch (IOException ioExc) {
                System.out.println("ERROR: I/O error!");
            }
        }
        try {
            inFile.close();
        } catch (IOException ioExc) {
            System.out.println("ERROR: can not close inFile!");
        } catch (NullPointerException npExc) {
            
        }
    }
    
    // show information of the database
    public static void showInfo(){
        Statement stmt;
        System.out.println("==========================================");
        System.out.println("Table Name\t\tNumber of Records");
        System.out.println("==========================================");
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM STUDENT");
            rs.next();
            int nor = rs.getInt(1);
            System.out.println(" STUDENT\t\t\t" + " " + nor);
            rs.close();
            stmt.close();
        } catch (SQLException sqlExc) {
            
        }
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM COURSE");
            rs.next();
            int nor = rs.getInt(1);
            System.out.println(" COURSE\t\t\t\t" + " " + nor);
            rs.close();
            stmt.close();
        } catch (SQLException sqlExc) {
            
        }
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM CSECTION");
            rs.next();
            int nor = rs.getInt(1);
            System.out.println(" CSECTION\t\t\t" + " " + nor);
            rs.close();
            stmt.close();
        } catch (SQLException sqlExc) {
            
        }
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM LECTURE");
            rs.next();
            int nor = rs.getInt(1);
            System.out.println(" LECTURE\t\t\t" + " " + nor);
            rs.close();
            stmt.close();
        } catch (SQLException sqlExc) {
            
        }
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ENROLL");
            rs.next();
            int nor = rs.getInt(1);
            System.out.println(" ENROLL\t\t\t\t" + " " + nor);
            rs.close();
            stmt.close();
        } catch (SQLException sqlExc) {
            
        }
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM PREREQUISITE");
            rs.next();
            int nor = rs.getInt(1);
            System.out.println(" PREREQUISITE\t\t\t" + " " + nor);
            rs.close();
            stmt.close();
        } catch (SQLException sqlExc) {
            
        }
        System.out.println("==========================================");
    }

//=================================================================================
    // Ranking of courses
    public static void rankOfCourse(){
        Statement stmt;
        int year = 0;
        int term = 0;
        boolean is_valid_year = false;
        boolean is_valid_term = false;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Which year?");
        while (!is_valid_year){
            try {
                year = Integer.parseInt(in.readLine());
                is_valid_year = true;
            } catch (IOException ioExc) {
                System.out.println("Error: I/O error!");
                System.out.println("Try again year:");
                is_valid_year = false;
            } catch(NumberFormatException nfExc) {
                System.out.println("Error: wrong input!");
                System.out.println("Try again year:");
                is_valid_year = false;
            }
        }
        System.out.println("Which term?");
        while (!is_valid_term){
            try {
                term = Integer.parseInt(in.readLine());
                if (term >=1 && term <= 3){
                    is_valid_term = true;
                }
                else {
                    System.out.println("Try again term(1,2,3):");
                    is_valid_term = false;
                }
            } catch (IOException ioExc) {
                System.out.println("Error: I/O error!");
                System.out.println("Try again term(1,2,3):");
                is_valid_term = false;
            } catch(NumberFormatException nfExc) {
                System.out.println("Error: wrong input!");
                System.out.println("Try again term(1,2,3):");
                is_valid_term = false;
            }
        }
        if (is_valid_year && is_valid_term){
            System.out.println("===================================================================");
            System.out.println("Rank\tCourse code\t\tName of Course");
            System.out.println("===================================================================");
            //System.out.println(" " + year + "\t\t" + term);
            ResultSet rs;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT RANK() OVER (ORDER BY R.er DESC) AS Rank, R.code, R.cname "
                                       + "FROM (SELECT CSN.code, CSN.cname, CAST(ET.enroll_total AS FLOAT)/CSN.quota AS er "
                                       + "FROM (SELECT CSEC.code, C.cname, CSEC.quota FROM (SELECT * FROM CSECTION WHERE cYear = " + year + " AND cTerm = " + term + ") CSEC, COURSE C WHERE CSEC.code = C.code) CSN, (SELECT ENR.code, COUNT(*) AS enroll_total FROM (SELECT * FROM ENROLL WHERE LYear = " + year + " AND LTerm = " + term +") ENR GROUP BY ENR.code) ET WHERE CSN.code = ET.code UNION "
                                       + "SELECT CSN.code, CSN.cname, 0 AS er FROM (SELECT CSEC.code, C.cname, CSEC.quota FROM (SELECT * FROM CSECTION WHERE cYear = " + year + " AND cTerm = " + term + ") CSEC, COURSE C WHERE CSEC.code = C.code) CSN WHERE CSN.code <> ALL(SELECT ET.code FROM (SELECT ENR.code, COUNT(*) AS enroll_total FROM (SELECT * FROM ENROLL WHERE LYear = " + year + " AND LTerm = " + term +") ENR GROUP BY ENR.code) ET)) R ORDER BY Rank, R.code ASC");
                int course_rank;
                String course_code;
                String course_name;
                while (rs.next()){
                    course_rank = Integer.parseInt(rs.getString(1));
                    course_code = (rs.getString(2)).trim();
                    course_name = (rs.getString(3)).trim();
                    System.out.println(" " + course_rank + "\t " + course_code + "\t " + course_name);
                }
                stmt.close();
                rs.close();
                System.out.println("===================================================================");
            } catch (SQLException sqlExc) {
                System.out.println("ERROR: SQL error!");
            } catch (NumberFormatException nfExc) {
                System.out.println("ERROR: Number Format error!");
            }
            /*try {
                stmt.close();
            } catch (SQLException sqlExc){
                System.out.println("ERROR: Can not close Statement!");
            }
            try {
                rs.close();
            } catch (SQLException sqlExc){
                System.out.println("ERROR: Can not close ResultSet!");
            }*/
        }
    }
    
//=================================================================================
    // check sid is valid
    public static boolean isExist(long sid){
        Statement stmt;
        ResultSet rs;
        boolean is_exist = false;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM STUDENT S WHERE S.sid = "
                                 + sid);
            while (rs.next()){
                is_exist = true;
            }
            stmt.close();
            rs.close();
        } catch (SQLException sqlExc) {
            System.out.println("ERROR: SQL error!");
        }
        return is_exist;
    }
    
//=================================================================================
    // enroll into a course
    public static void enrollIntoCourse(long sid){
        System.out.println("Which year:");
        int year = 0;
        int term = 0;
        String course_code;
        Calendar today = Calendar.getInstance();
        boolean is_valid_year = false;
        boolean is_valid_term = false;
        boolean is_valid_code = false;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (!is_valid_year){
            try {
                year = Integer.parseInt(in.readLine());
                if (year >= (today.get(today.YEAR))){
                    is_valid_year = true;
                }
                else {
                    System.out.println("The year has passed, please try another year:");
                    is_valid_year = false;
                }
            } catch (IOException ioExc){
                System.out.println("ERROR: wrong input, please try another year:");
                is_valid_year = false;
            } catch (NumberFormatException nfExc) {
                System.out.println("ERROR: wrong input, please try another year:");
                is_valid_year = false;
            }
        }
        System.out.println("Which term:");
        while (!is_valid_term){
            try {
                term = Integer.parseInt(in.readLine());
                if (term >= 1 && term <= 3){
                    int today_month = today.get(today.MONTH) + 1;
                    if (term == 1 && today_month <= 4){
                        is_valid_term = true;
                    }
                    else if (term == 2 && today_month <= 8){
                        is_valid_term = true;
                    }
                    else if (term == 3 && today_month <= 12){
                        is_valid_term = true;
                    }
                    else if (year > (today.get(today.YEAR))){
                        is_valid_term = true;
                    }
                    else {
                        System.out.println("The term has passed, please try another term:");
                        is_valid_term = false;
                    }
                }
                else {
                    System.out.println("ERROR: wrong input, please give a correct term:");
                    is_valid_term = false;
                }
            } catch (IOException ioExc){
                System.out.println("ERROR: wrong input, please try another term:");
                is_valid_term = false;
            } catch (NumberFormatException nfExc) {
                System.out.println("ERROR: wrong input, please try another term:");
                is_valid_term = false;
            }
        }
        //System.out.println(year + " " + term);
        Statement stmt;
        ResultSet rs;
        System.out.println("Course code:");
        while (!is_valid_code){
            try {
                boolean is_course_exit_period = false;
                course_code = in.readLine();
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM CSECTION CS WHERE CS.code = '"
                                       + course_code + "' AND CS.cYear = "
                                       + year + " AND CS.cTerm = "
                                       + term);
                while (rs.next()){
                    is_course_exit_period = true;
                }
                if (is_course_exit_period){
                    rs = stmt.executeQuery("SELECT (C.quota - ET.enroll_total) AS quota FROM (SELECT CS.quota FROM CSECTION CS WHERE CS.code = '" + course_code + "' AND CS.cYear = " + year + " AND CS.cTerm = " + term + ") C, (SELECT E.code, COUNT(*) AS enroll_total FROM ENROLL E WHERE E.code = '" + course_code + "' AND E.LYear = " + year + " AND E.LTerm = " + term + " GROUP BY E.code) ET");
                    boolean is_course_not_full = true;
                    while (rs.next()){
                        //System.out.println(Integer.parseInt(rs.getString(1)));
                        if (Integer.parseInt(rs.getString(1)) == 0){
                            is_course_not_full = false;
                            System.out.println(course_code + " is full, please try another course:");
                            is_valid_code = false;
                        }
                    }
                    if (is_course_not_full){
                        rs = stmt.executeQuery("SELECT * FROM ENROLL E WHERE E.sid = " + sid + " AND E.code = '" + course_code + "' ORDER BY E.LYear, E.LTerm DESC");
                        boolean is_you_pass = false;
                        while (rs.next()){
                            if (rs.getString(5) == null){
                                System.out.println("You have enrolled in the course, please try another course:");
                                is_you_pass = true;
                                is_valid_code = false;
                            }
                            else if (!(rs.getString(5).contains("F"))){
                                System.out.println("You have passed the course or enrolled in it, please try another course:");
                                is_you_pass = true;
                                is_valid_code = false;
                            }
                        }
                        if (!is_you_pass){
                            rs = stmt.executeQuery("SELECT C.credit FROM COURSE C WHERE C.code = '" + course_code + "'");
                            rs.next();
                            int course_credit = Integer.parseInt(rs.getString(1));
                            //System.out.println(course_credit);
                            rs = stmt.executeQuery("SELECT C.credit FROM ENROLL E, COURSE C WHERE E.sid = " + sid + " AND E.code = C.code AND E.LYear = " + year + " AND E.LTerm = " + term);
                            boolean is_credit_overflow = false;
                            int total_credit = 0;
                            while (rs.next()){
                                total_credit = total_credit + Integer.parseInt(rs.getString(1));
                            }
                            if ((course_credit + total_credit) > 21){
                                System.out.println("You will overflow the credit limit, please drop a course first!");
                                is_credit_overflow = true;
                                is_valid_code = true;
                            }
                            if (!is_credit_overflow){
                                rs = stmt.executeQuery("SELECT E.code FROM LECTURE L1, ENROLL E, LECTURE L2 WHERE E.sid = " + sid + " AND L1.code = '" + course_code + "' AND L2.code = E.code AND E.LYear = " + year + " AND L1.LYear = " + year + " AND L2.LYear = " + year + " AND E.LTerm = " + term + " AND L1.LTerm = " + term + " AND L2.LTerm = " + term + " AND L1.timeslot = L2.timeslot");
                                boolean is_not_time_collision = true;
                                while (rs.next()){
                                    System.out.println(course_code + " time collision with " + rs.getString(1) + ", try another course!");
                                    is_not_time_collision = false;
                                }
                                if (is_not_time_collision){
                                    int pass_pre = 0;
                                    int need_to_pass = 0;
                                    rs = stmt.executeQuery("SELECT P.course, COUNT(*) AS pre_total FROM PREREQUISITE P WHERE P.course = '" + course_code + "' GROUP BY P.course");
                                    while (rs.next()){
                                        need_to_pass = Integer.parseInt(rs.getString(2));
                                    }
                                    rs = stmt.executeQuery("SELECT * FROM PREREQUISITE P WHERE P.course = '" + course_code + "'");
                                    String[] pre_course_code;
                                    pre_course_code = new String[need_to_pass];
                                    for (int i = 0; i < need_to_pass; i++){
                                        rs.next();
                                        pre_course_code[i] = rs.getString(2);
                                    }
                                    for (int i = 0; i < need_to_pass; i++){
                                        rs = stmt.executeQuery("SELECT * FROM ENROLL E WHERE E.sid = " + sid + " AND E.code = '" + pre_course_code[i] + "' AND E.grade <> 'F' AND E.grade IS NOT NULL");
                                        if (rs.next()){
                                            pass_pre = pass_pre + 1;
                                        }
                                    }
                                    //System.out.println(need_to_pass);
                                    if (pass_pre == need_to_pass){
                                        rs = stmt.executeQuery("INSERT INTO ENROLL VALUES (" + sid + ", '" + course_code + "', " + year + ", " + term + ", null)");
                                        System.out.println("Enroll in " + course_code + " success...");
                                        is_valid_code = true;
                                    }
                                    else {
                                        System.out.println("You have not passed some prerequisite courses of " + course_code + ", try another course:");
                                        is_valid_code = false;
                                    }
                                }
                            }
                        }
                    }
                    
                }
                else {
                    System.out.println(course_code + " does not open in term " + term + " and year " + year + ", please try another year:");
                    is_valid_code = false;
                }
                rs.close();
                stmt.close();
            } catch (IOException ioExc){
                System.out.println("ERROR: wrong I/O, please try another course code:");
                is_valid_code = false;
            } catch (SQLException sqlExc){
                System.out.println("ERROR: wrong SQL, please try another course code:");
                is_valid_code = false;
            }
        }
    }
    
//=================================================================================
    // drop course
    public static void dropCourse(long sid){
        Statement stmt = null;
        int year = 0;
        int term = 0;
        String course_code;
        boolean is_valid_year = false;
        boolean is_valid_term = false;
        boolean is_valid_code = false;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Which year:");
        while (!is_valid_year){
            try {
                year = Integer.parseInt(in.readLine());
                is_valid_year = true;
            } catch (IOException ioExc){
                System.out.println("ERROR: wrong input, please try another year:");
                is_valid_year = false;
            } catch (NumberFormatException nfExc) {
                System.out.println("ERROR: wrong input, please try another year:");
                is_valid_year = false;
            }
        }
        System.out.println("Which term:");
        while (!is_valid_term){
            try {
                term = Integer.parseInt(in.readLine());
                if (term >= 1 && term <= 3){
                    is_valid_term = true;
                }
                else {
                    System.out.println("ERROR: wrong input, please give a correct term:");
                    is_valid_term = false;
                }
            } catch (IOException ioExc){
                System.out.println("ERROR: wrong input, please try another term:");
                is_valid_term = false;
            } catch (NumberFormatException nfExc) {
                System.out.println("ERROR: wrong input, please try another term:");
                is_valid_term = false;
            }
        }
        ResultSet rs;
        while (!is_valid_code){
            try{
                System.out.println("Which course:");
                course_code = in.readLine();
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM ENROLL E WHERE E.sid = "
                                       + sid + " AND E.code = '" + course_code + "' AND E.LYear = " + year + " AND E.LTerm = " + term + " AND E.grade IS NULL");
                while (rs.next()){
                    stmt.executeQuery("DELETE FROM ENROLL E WHERE E.sid = " + sid + " AND E.code = '" + course_code + "' AND E.LYear = " + year + " AND E.LTerm = " + term + " AND E.grade IS NULL");
                    System.out.println("Drop course " + course_code + " success...");
                    is_valid_code = true;
                }
                if (!is_valid_code){
                    System.out.println("You cannot drop the course now!");
                }
            } catch (IOException ioExc) {
                System.out.println("Wrong input!");
                is_valid_code = false;
            } catch (SQLException sqlExc) {
                System.out.println("ERROR: SQL error!");
                is_valid_code = false;
            } catch (NullPointerException npExc) {
                System.out.println("ERROR: Pointer error!");
                is_valid_code = false;
            }
        }
    }
    
//=================================================================================
    //
    public static void acaReport(long sid){
        int year = 0;
        int term = 0;
        double sum_c_p = 0.0;
        double sum_c = 0.0;
        double c_sum_c_p = 0.0;
        double c_sum_c = 0.0;
        Calendar today = Calendar.getInstance();
        boolean is_valid_year = false;
        boolean is_valid_term = false;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Which year:");
        while (!is_valid_year){
            try {
                year = Integer.parseInt(in.readLine());
                if (year <= (today.get(today.YEAR))){
                    is_valid_year = true;
                }
                else {
                    System.out.println("The year is in the future, please try another year:");
                    is_valid_year = false;
                }
            } catch (IOException ioExc){
                System.out.println("ERROR: wrong input, please try another year:");
                is_valid_year = false;
            } catch (NumberFormatException nfExc) {
                System.out.println("ERROR: wrong input, please try another year:");
                is_valid_year = false;
            }
        }
        System.out.println("Which term:");
        while (!is_valid_term){
            try {
                term = Integer.parseInt(in.readLine());
                if (term >= 1 && term <= 3){
                    if (year == today.get(today.YEAR)){
                        int today_month = today.get(today.MONTH) + 1;
                        if (term == 1 && today_month >= 1){
                            is_valid_term = true;
                        }
                        else if (term == 2 && today_month >= 5){
                            is_valid_term = true;
                        }
                        else {
                            System.out.println("The term has not finish, please try another term:");
                            is_valid_term = false;
                        }
                    }
                    else {
                        is_valid_term = true;
                    }
                }
                else {
                    System.out.println("ERROR: wrong input, please give a correct term:");
                    is_valid_term = false;
                }
            } catch (IOException ioExc){
                System.out.println("ERROR: wrong input, please try another term:");
                is_valid_term = false;
            } catch (NumberFormatException nfExc) {
                System.out.println("ERROR: wrong input, please try another term:");
                is_valid_term = false;
            }
        }
        Statement stmt;
        ResultSet rs;
        try {
            System.out.println("=======================================================================================================");
            System.out.println("Course Code\t\tCourse Name\t\t\t\tCredits\t\tGrade\t\tPoints");
            System.out.println("=======================================================================================================");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT C.code, C.cname, C.credit, E.grade FROM ENROLL E, COURSE C WHERE C.code = E.code AND E.sid = " + sid + " AND E.LYear = " + year + " AND E.LTerm = " + term + " ORDER BY E.code ASC");
            boolean is_today_course = true;
            while (rs.next()){
                if (rs.getString(4) == null){
                    String course_code = rs.getString(1).trim();
                    String course_name = (rs.getString(2)).trim();
                    int course_credit = rs.getInt(3);
                    System.out.println(" " + course_code + "\t" + course_name + "\t\t " + course_credit + "\t\t NULL\t\t NULL");
                }
                else {
                    String grade = rs.getString(4).trim();
                    //System.out.println(grade.length());
                    double point = 0.0;
                    if (grade.contains("A")){
                        point = 4.0;
                    }
                    else if (grade.contains("B")){
                        point = 3.0;
                    }
                    else if (grade.contains("C")){
                        point = 2.0;
                    }
                    else if (grade.contains("D")){
                        point = 1.0;
                    }
                    else if (grade.contains("F")){
                        point = 0.0;
                    }
                    String course_code = rs.getString(1).trim();
                    String course_name = (rs.getString(2)).trim();
                    int course_credit = rs.getInt(3);
                    //System.out.println(course_name.length());
                    sum_c_p = sum_c_p + (course_credit * point);
                    sum_c = sum_c + course_credit;
                    System.out.println(" " + course_code + "\t" + course_name + "\t\t " + course_credit + "\t\t " + grade + "\t\t " + point);
                    is_today_course = false;
                }
            }
            System.out.println("=======================================================================================================");
            if (is_today_course){
                System.out.println("Term GPA: NULL");
            }
            else {
                System.out.println("Term GPA:\t" + (sum_c_p / sum_c));
            }
            rs = stmt.executeQuery("SELECT C.code, C.cname, C.credit, E.grade FROM ENROLL E, COURSE C WHERE C.code = E.code AND E.sid = " + sid + " AND E.grade IS NOT NULL ORDER BY E.code ASC");
            while (rs.next()){
                String grade = rs.getString(4).trim();
                //System.out.println(grade.length());
                double point = 0.0;
                if (grade.contains("A")){
                    point = 4.0;
                }
                else if (grade.contains("B")){
                    point = 3.0;
                }
                else if (grade.contains("C")){
                    point = 2.0;
                }
                else if (grade.contains("D")){
                    point = 1.0;
                }
                else if (grade.contains("F")){
                    point = 0.0;
                }
                int course_credit = rs.getInt(3);
                c_sum_c_p = c_sum_c_p + (course_credit * point);
                c_sum_c = c_sum_c + course_credit;
            }
            double c_gpa = c_sum_c_p / c_sum_c;
            BigDecimal c_gpa_a = new BigDecimal(c_gpa);
            System.out.println("Cumulative GPA:\t" + c_gpa_a.setScale(1,2));
            System.out.println("=======================================================================================================");
        } catch (SQLException sqlExc) {
            System.out.println("ERROR: SQL error!");
        } catch (NullPointerException npExc) {
            System.out.println("ERROR: Pointer error!");
        }
    }
    
//=================================================================================
    // main function
    public static void main(String[] args){
        
        // load driver
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver is ready...");
        } catch(Exception x) {
            System.out.println("Unable to load the driver class!");
            System.exit(1);
        }
        
        // connection to jdbc
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12", "a034", "AnthonyJamieLucia");
            System.out.println("Connection is ready...");
        } catch(SQLException sqlExc) {
            System.out.println("Connection false!!!");
            System.exit(1);
        }
        
        System.out.println("Username: ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            boolean is_valid_user = false;
            while (!is_valid_user){
                String username = in.readLine();
                if (username.equals("admin")){
                    adminMenu();
                
                    int adminCho; // Administrator choice
                    // Administrator Choice
                    while ((adminCho = choAdminNum()) != 6){
                        switch (adminCho){
                            case 1:
                                createAllTables();
                                break;
                            case 2:
                                deleteAllTables();
                                break;
                            case 3:
                                loadData("course.txt");
                                loadData("student.txt");
                                loadData("section.txt");
                                loadData("enrollment.txt");
                                break;
                            case 4:
                                showInfo();
                                break;
                            case 5:
                                rankOfCourse();
                                break;
                        }
                    }
                    System.out.println("Exit success...");
                    is_valid_user = true;
                }
                else {
                    try {
                        long sid = Long.parseLong(username);
                        /*if (username > 10000000000 && username < 1200000000){
                            studentMenu();
                            is_valid_user = true;
                        }
                        else {
                            System.out.println("Wrong user id, please try again:");
                            is_valid_user = false;
                            //System.out.println(sid);
                        }*/
                        boolean is_valid_exist = false;
                        
                        if (isExist(sid)){
                            is_valid_exist = true;
                        }
                        
                        if (is_valid_exist){
                            studentMenu();
                        
                            int stuCho;
                            while ((stuCho = choStuNum()) != 4){
                                switch (stuCho){
                                    case 1:
                                        enrollIntoCourse(sid);
                                        break;
                                    case 2:
                                        dropCourse(sid);
                                        break;
                                    case 3:
                                        acaReport(sid);
                                        break;
                                }
                            }
                            System.out.println("Exit success...");
                            is_valid_user = true;
                        }
                        else {
                            System.out.println("ERROR: wrong user id, please try again:");
                            is_valid_user = false;
                        }
                    } catch (NumberFormatException nfExc) {
                        System.out.println("Wrong user id, please try again:");
                        is_valid_user = false;
                    }
                }
            }
            in.close();
        } catch(IOException ioExc) {
            System.out.println("Wrong input!!!");
        }
        try {
            conn.close();
        } catch (SQLException sqlExc) {
            System.out.println("Connection close false!!!");
            System.exit(1);
        }
    }
}

