import java.sql.*; 
import java.util.Scanner;
import oracle.jdbc.*;
import java.io.*;
import java.awt.*;
import java.math.*;
import oracle.jdbc.pool.OracleDataSource;

public class mydemo1 
{
	public static void main (String args []) throws SQLException
	{
		//Connection to Oracle server
		OracleDataSource ds = new oracle.jdbc.pool.OracleDataSource();
		ds.setURL("jdbc:oracle:thin:@grouchoIII.cc.binghamton.edu:1521:ACAD111");
		Connection conn = ds.getConnection("gchen21", "qwe1234A");

		//Query
		Statement stmt = conn.createStatement (); 

		//Save result
		ResultSet rset = stmt.executeQuery ("select table_name from user_tables");

		while(true)
		{
			System.out.println();
			System.out.println("-------------MENU--------------");
			System.out.println("1.Display all the tables");
			System.out.println("2.Add a Student");
			System.out.println("3.Delete a Student");
			System.out.println("4.Display all courses taken by a student");
			System.out.println("5.Display the prerequisite courses for a given course");
			System.out.println("6.Display all students of a class");
			System.out.println("7.Enroll a Student into a class");
			System.out.println("8.Drop a class for a Student");
			System.out.println("0.Quit");
			Scanner in = new Scanner(System.in);
			System.out.println();
			System.out.printf("Enter Your Choice:");
			int num = Integer.parseInt(in.next());

			//1.Display all the tables
			if(num == 1)
			{
				while(true)
				{
					try
					{
						rset = stmt.executeQuery ("select table_name from user_tables");

						//Print
						System.out.println();
						System.out.println("TABLES:");
						int count=0;

						//it is good to automatic print the table name
						//but what if there are more tables 
						//and what if the table names are not show as the order for "no"
						/*while (rset.next()) 
						{
							count++; 
							String tab_name = rset.getString("TABLE_NAME");
							System.out.println(count+ "." +tab_name);
						}*/
						System.out.println("1.STUDENTS");
						System.out.println("2.COURSES");
						System.out.println("3.PREREQUISITES");
						System.out.println("4.CLASSES");
						System.out.println("5.ENROLLMENTS");
						System.out.println("6.LOGS");
						System.out.println("0.Return to the upper level");

						System.out.println();
						System.out.printf("Enter your choice to display a table:");
						int no = Integer.parseInt(in.next());
						if(no == 1)
						{
							//Call the show_students procedure with two parameters
							OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.show_students(?)}");     
							//set type OracleTypes.CURSOR for parameter 1 to get the output table result
							cs.registerOutParameter(1, OracleTypes.CURSOR);
							cs.executeQuery();
							//get the output table result
							OracleResultSet rs = (OracleResultSet)cs.getObject(1);
							System.out.println();
							String sid,firstname,lastname,status,email;
							float gpa;
							System.out.format("%1s %13s %12s %11s %11s %15s","SID","FIRSTNAME","LASTNAME","STATUS","GPA","EMAIL");
							System.out.println();
							System.out.println("-----------------------------------------------------------------------------------");
							while (rs.next())
							{
								sid =  rs.getString ("sid");
								firstname = rs.getString ("firstname");
								lastname = rs.getString ("lastname");
								status = rs.getString ("status");
								gpa = rs.getFloat ("gpa");
								email = rs.getString ("email");
								System.out.format("%1s %11s %12s %12s %11.2f %20s",sid,firstname,lastname,status,gpa,email);
								System.out.println();
							}
							rs.close();
							cs.close();
						}
						else if(no == 2)
						{
							//Call the show_courses procedure with two parameters
							OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.show_courses(?)}");
							//set type OracleTypes.CURSOR for parameter 1 to get the output table result
							cs.registerOutParameter(1, OracleTypes.CURSOR);
							cs.executeQuery();
							//get the output table result
							OracleResultSet rs = (OracleResultSet)cs.getObject(1);
							System.out.println();
							String dept_code,title;
							int course_no;
							System.out.format("%1s %13s %12s","DEPT_CODE","COURSE#","TITLE");
							System.out.println();
							System.out.println("------------------------------------------------------------------------");
							while (rs.next ())
							{
								dept_code =  rs.getString ("dept_code");
								course_no = rs.getInt ("course#");
								title = rs.getString ("title");
								System.out.format("%1s %17d %23s",dept_code,course_no,title);
								System.out.println();
							}
							rs.close();
							cs.close();
						}
						else if(no == 3)
						{
							//Call the show_prerequisites procedure with two parameters
							OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.show_prerequisites(?)}");
							//set type OracleTypes.CURSOR for parameter 1 to get the output table result
							cs.registerOutParameter(1, OracleTypes.CURSOR);
							cs.executeQuery();
							//get the output table result
							OracleResultSet rs = (OracleResultSet)cs.getObject(1);
							System.out.println();
							String dept_code,pre_dept_code;
							int course_no,pre_course_no;
							System.out.format("%1s %12s %18s %15s","DEPT_CODE","COURSE#","PRE_DEPT_CODE","PRE_COURSE#");
							System.out.println();
							System.out.println("--------------------------------------------------------------------------------");
							while (rs.next ())
							{
								dept_code =  rs.getString ("dept_code");
								course_no = rs.getInt ("course#");
								pre_dept_code = rs.getString ("pre_dept_code");
								pre_course_no = rs.getInt ("pre_course#");
								System.out.format("%1s %16d %15s %15d",dept_code,course_no,pre_dept_code,pre_course_no);
								System.out.println();
							}
							rs.close();
							cs.close();
						}
						else if(no == 4)
						{
							//Call the show_classes procedure with two parameters
							OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.show_classes(?)}");
							//set type OracleTypes.CURSOR for parameter 1 to get the output table result
							cs.registerOutParameter(1, OracleTypes.CURSOR);
							cs.executeQuery();
							//get the output table result
							OracleResultSet rs = (OracleResultSet)cs.getObject(1);
							System.out.println();
							String classid,dept_code,semester;
							int course_no,year,class_size,limit,sect_no;

							System.out.format("%1s %13s %13s %13s %12s %14s %12s %15s","CLASSID","DEPT_CODE","COURSE#","SECT#","YEAR","SEMESTER","LIMIT","CLASS_SIZE");
							System.out.println();
							System.out.println("----------------------------------------------------------------------------------------------------------------------");
							while (rs.next ())
							{
								classid = rs.getString ("classid");
								dept_code =  rs.getString ("dept_code");
								course_no = rs.getInt ("course#");
								sect_no = rs.getInt ("sect#");
								year = rs.getInt ("year");
								semester = rs.getString ("semester");
								limit = rs.getInt ("limit");
								class_size = rs.getInt ("class_size");
								System.out.format("%1s %11s %14d %14d %14d %13s %12d %12d",classid,dept_code,course_no,sect_no,year,semester,limit,class_size);
								System.out.println();
							}
							rs.close();
							cs.close();
						}
						else if(no == 5)
						{
							//Call the show_enrollments procedure with two parameters
							OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.show_enrollments(?)}");
							//set type OracleTypes.CURSOR for parameter 1 to get the output table result
							cs.registerOutParameter(1, OracleTypes.CURSOR);
							cs.executeQuery();
							//get the output table result
							OracleResultSet rs = (OracleResultSet)cs.getObject(1);
							System.out.println();
							String sid,classid,lgrade;
							System.out.format("%1s %14s %14s","SID","CLASSID","LGRADE");
							System.out.println();
							System.out.println("---------------------------------------------------------------------");
							while (rs.next ())
							{
								sid =  rs.getString ("sid");
								classid = rs.getString ("classid");
								lgrade = rs.getString ("lgrade");
								System.out.format("%1s %12s %13s",sid,classid,lgrade);
								System.out.println();
							}
							rs.close();
							cs.close();
						}
						else if(no == 6)
						{
							//Call the show_logs procedure with two parameters
							OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.show_logs(?)}");
							//set type OracleTypes.CURSOR for parameter 1 to get the output table result
							cs.registerOutParameter(1, OracleTypes.CURSOR);
							cs.executeQuery();
							//get the output table result
							OracleResultSet rs = (OracleResultSet)cs.getObject(1);
							System.out.println();
							String who,table_name,operation,key_value;
							int logid;
							String time;
							System.out.format("%1s %13s %13s %16s %16s %16s","LOGID","WHO","TIME","TABLE_NAME","OPERATION","KEY_VALUE");
							System.out.println();
							System.out.println("--------------------------------------------------------------------------------------------------");
							while (rs.next ())
							{
								logid = rs.getInt ("logid");
								who =  rs.getString ("who");
								time = rs.getDate("time").toString();
								table_name = rs.getString ("table_name");
								operation = rs.getString ("operation");
								key_value = rs.getString ("key_value");
								System.out.format("%1d %11s %14s %14s %14s %13s",logid,who,time,table_name,operation,key_value);
								System.out.println();
							}
							rs.close();
							cs.close();
						}
						//return to the upper level
						else if(no == 0)
						{
							break;
						}
						else
						{
							System.out.println();
							System.out.println("Invalid option!!");
						}
					} 
					catch (SQLException ex) 
					{ 
						System.out.println (ex);
					}
					catch (Exception e) 
					{
						System.out.println (e);
					}
				}
			}
			//2.Add a Student
			else if(num == 2)
			{
				System.out.println();
				System.out.println("PLEASE ENTER STUDENT DETAIL:");
				System.out.println("----------------------------");
				System.out.printf("Enter SID:");
				String sid = in.next();
				System.out.printf("Enter FIRSTNAME:");
				String firstname = in.next();
				System.out.printf("Enter LASTNAME:");
				String lastname = in.next();
				System.out.printf("Enter STATUS:");   
				String status = in.next();
				System.out.printf("Enter GPA:");
				float gpa = Float.parseFloat(in.next());
				System.out.printf("Enter EMAIL:");
				String email = in.next();
				System.out.println();

				//Call the add_students procedure with 6 parameters
				OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.add_students(?,?,?,?,?,?)}");
				//set the 6 in parameters   
				cs.setString(1, sid);
				cs.setString(2, firstname);
				cs.setString(3, lastname);
				cs.setString(4, status);
				cs.setFloat(5, gpa);
				cs.setString(6, email); 
				//execute the store procedure
				cs.executeUpdate();
				System.out.println("Student is succesfully added!!");
				cs.close();
			}

			//3.Delete a Student
			else if(num == 3)
			{
				System.out.println();
				System.out.println("PLEASE ENTER STUDENT SID:");
				String sid = in.next();
				//Call the add_students procedure with 2 parameters
				OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.deletestu(?,?)}");
				//set the sid as a in parameter
				cs.setString(1, sid);
				//register the out parameter to know if sid if valid
				cs.registerOutParameter(2, java.sql.Types.INTEGER);
				cs.executeUpdate();
				int res = cs.getInt(2);
				if(res == 1)
				{
					System.out.println("The sid is invalid.");
				}
				else
				{
					System.out.println("Student is succesfully deleted!!");
				}
			}

			//4.Display all courses taken by a student
			else if(num == 4)
			{
				System.out.println();
				System.out.println("PLEASE ENTER STUDENT SID:");
				String sid = in.next();
				//Call the add_students procedure with 3 parameters
				OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.check_classes(?,?,?)}");
				//set the sid as a in parameter
				cs.setString(1, sid);
				//get the student detail
				cs.registerOutParameter(2, OracleTypes.CURSOR);
				//get the classes detail
				cs.registerOutParameter(3, OracleTypes.CURSOR);
				cs.executeQuery();
				
				//print the student detail
				OracleResultSet rs1 = (OracleResultSet)cs.getObject(2);
				System.out.println();
				int count=0;
				while (rs1.next ())
				{
					String sid_s = rs1.getString ("sid");
					String fir_f =  rs1.getString ("firstname");
					System.out.format("sid: %1s, firstname: %10s", sid_s, fir_f);
					System.out.println();
					count++;
				}
				//when there is no entry for the given sid
				if(count == 0)
				{
					System.out.format("The sid is invalid.");
					System.out.println();
					//thus, we do not need to get the classes info
					continue;
				}
				rs1.close();

				//print the classes detail taken by the student
				OracleResultSet rs2 = (OracleResultSet)cs.getObject(3);
				System.out.println();
				String dept_c;
				int course_n;
				count=0;
				System.out.format("classes the student has taken are");
				System.out.println();
				System.out.println("--------------------------------------");
				while (rs2.next ())
				{
					dept_c = rs2.getString ("dept_code");
					course_n =  rs2.getInt ("course#");
					System.out.format("%1s %10d", dept_c, course_n);
					System.out.println();
					count++;
				}
				//there is no record in enrollments table which means The student has not taken any course
				if(count == 0)
				{
					System.out.format("The student has not taken any course.");
					System.out.println();
				}
				rs2.close();
				cs.close();
			}

			else if(num == 5)
			{
				System.out.println();
				System.out.println("PLEASE ENTER COURSE DETAIL:");
				System.out.println("----------------------------");
				System.out.printf("Enter DEPT_CODE:");
				String dept_code = in.next();
				System.out.println("Enter COURSE#:");
				int course_no = Integer.parseInt(in.next());
				//Call the check_prereq procedure with 3 parameters
				OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.check_prereq(?,?,?)}");

				//set 2 in parameter, dept_code and course_no
				cs.setString(1, dept_code);
				cs.setInt(2, course_no);
				//register a OracleTypes.CURSOR type to get the result
				cs.registerOutParameter(3, OracleTypes.CURSOR);
				cs.executeQuery();
				OracleResultSet rs = (OracleResultSet)cs.getObject(3);
				while (rs.next ())
				{
					String dept_c = rs.getString ("pre_dept_code");
					int cour_n =  rs.getInt ("pre_course#");
					System.out.format("dept_code: %1s, course#: %5d", dept_c, cour_n);
					System.out.println();
					//recursively call pre_r for each record to find multiple level result
					//here, cs and rs and deemed as input as we only need to initialize the OracleCallableStatement
					//and OracleResultSet for one time
					pre_r(dept_c, cour_n, cs, rs);
				}

				rs.close();
				cs.close();                                
			}

			else if(num == 6)
			{
				System.out.println();
				System.out.println("PLEASE ENTER CLASSID:");
				String classid = in.next();
				//Call the class_info procedure with 3 parameters
				OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.class_info(?,?,?)}");
				cs.setString(1, classid);
				//register 2 OracleTypes.CURSOR type to get the result
				cs.registerOutParameter(2, OracleTypes.CURSOR);
				cs.registerOutParameter(3, OracleTypes.CURSOR);
				cs.executeQuery();
				//get the sql result of class
				OracleResultSet rs1 = (OracleResultSet)cs.getObject(2);
				System.out.println();
				int count=0;
				while (rs1.next ())
				{
					String cid_c = rs1.getString ("classid");
					String title =  rs1.getString ("title");
					System.out.format("classid : %1s, firstname: %10s", cid_c, title);
					System.out.println();
					count++;
				}
				//if there is no record, it means that the cid is invalid
				if(count == 0)
				{
					System.out.format("The cid is invalid.");
					System.out.println();
					//then there is no need to continue
					continue;
				}
				rs1.close();

				//get the sql result of which students have taken that class
				OracleResultSet rs2 = (OracleResultSet)cs.getObject(3);
				System.out.println();
				String sid_s, fir_f;
				count=0;
				System.out.format("classes the student has taken are");
				System.out.println();
				System.out.println("--------------------------------------");
				while (rs2.next ())
				{
					sid_s = rs2.getString ("sid");
					fir_f =  rs2.getString ("firstname");
					System.out.format("sid: %1s, firstname: %10s", sid_s, fir_f);
					System.out.println();
					count++;
				}
				//if there is no record, it means that the No student is enrolled in the class
				if(count == 0)
				{
					System.out.format("No student is enrolled in the class.");
					System.out.println();
				}
				rs2.close();
				cs.close();
			}

			else if(num == 7)
			{
				System.out.println();
				System.out.println("PLEASE ENTER SID:");
				String sid = in.next();
				System.out.println("PLEASE ENTER CLASSID:");
				String classid = in.next();
				//Call the enroll procedure with 7 parameters
				OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.enroll(?,?,?,?,?,?,?)}");
				//set sid and classid as input from user
				cs.setString(1, sid);
				cs.setString(2, classid);
				//register 5 INTEGER as output to tell java the result of SQL procedure
				cs.registerOutParameter(3, java.sql.Types.INTEGER);
				cs.registerOutParameter(4, java.sql.Types.INTEGER);
				cs.registerOutParameter(5, java.sql.Types.INTEGER);
				cs.registerOutParameter(6, java.sql.Types.INTEGER);
				cs.registerOutParameter(7, java.sql.Types.INTEGER);
				cs.executeUpdate();
				int res = cs.getInt(3);
				int res_pre = cs.getInt(4);
				int res_over = cs.getInt(5);
				int res_4 = cs.getInt(6);
				int res_size = cs.getInt(7);
				if(res == 1)
				{
					System.out.format("The sid is invalid.");
					System.out.println();
				}
				else if(res == 2)
				{
					System.out.format("The classid is invalid.");
					System.out.println();
				}
				else if(res == 3)
				{
					System.out.format("The student is already in the class.");
					System.out.println();
				}
				else if(res == 8)
				{
					System.out.format("Successfully enroll a student into a class!");
					System.out.println();
				}
				if(res_size == 1)
				{
					System.out.format("The class is closed.");
					System.out.println();
				}
				if(res_over == 1)
				{
					System.out.format("You are overloaded.");
					System.out.println();
				}
				if(res_4 == 1)
				{
					System.out.format("Students cannot be enrolled in more than four classes in the same semester.");
					System.out.println();
				}
				if(res_pre == 1)
				{
					System.out.format("Prerequisite courses have not been completed.");
					System.out.println();
				}
				cs.close();
			}
			else if(num == 8)
			{
				System.out.println();
				System.out.println("PLEASE ENTER SID:");
				String sid = in.next();
				System.out.println("PLEASE ENTER CLASSID:");
				String classid = in.next();
				//Call the dropclass procedure with 6 parameters
				OracleCallableStatement cs = (OracleCallableStatement)conn.prepareCall("{call proj2.dropclass(?,?,?,?,?,?)}");
				//set sid and classid as input from user
				cs.setString(1, sid);
				cs.setString(2, classid);
				//register 3 INTEGER as output to tell java the result of SQL procedure
				cs.registerOutParameter(3, java.sql.Types.INTEGER);
				cs.registerOutParameter(4, java.sql.Types.INTEGER);
				cs.registerOutParameter(5, java.sql.Types.INTEGER);
				cs.registerOutParameter(6, java.sql.Types.INTEGER);
				cs.executeUpdate();
				int res = cs.getInt(3);
				int res_pre = cs.getInt(4);
				int res_stu = cs.getInt(5);
				int res_cla = cs.getInt(6);
				if(res == 1)
				{
					System.out.format("The sid is invalid.");
					System.out.println();
				}
				else if(res == 2)
				{
					System.out.format("The classid is invalid.");
					System.out.println();
				}
				else if(res == 3)
				{
					System.out.format("The student is not enrolled in the class.");
					System.out.println();
				}
				else if(res == 5)
				{
					System.out.format("Successfully drop a class for a student!");
					System.out.println();
				}
				if(res_pre == 1)
				{
					System.out.format("The drop is not permitted because another class uses it as a prerequisite.");
					System.out.println();
				}
				//parallel with other INTEGER parameter to tell if This student is not enrolled in any classes
				if(res_stu == 1)
				{
					System.out.format("This student is not enrolled in any classes.");
					System.out.println();
				}
				//parallel with other INTEGER parameter to tell if The class now has no students
				if(res_cla == 1)
				{
					System.out.format("The class now has no students.");
					System.out.println();
				}
				cs.close();
			}
			else if(num == 0)
			{
				//close the result set, statement, and the connection
				break;
			}
			else
			{
				System.out.println("Invalid Choice!!");
			}
		}
		rset.close();
		stmt.close();
		conn.close();
	}
	
	static void pre_r(String dept_code, int course_no, OracleCallableStatement cs, OracleResultSet rs) throws SQLException
	{
		try
		{
			//set the in parameter dept_code and dept_code from upper level result
			cs.setString(1, dept_code);
			cs.setInt(2, course_no);
			cs.registerOutParameter(3, OracleTypes.CURSOR);
			cs.executeQuery();
			rs = (OracleResultSet)cs.getObject(3);
			int count=0;
			while (rs.next ())
			{
				String dept_c = rs.getString ("pre_dept_code");
				int cour_n =  rs.getInt ("pre_course#");
				System.out.format("dept_code: %1s, course#: %5d", dept_c, cour_n);
				System.out.println();
				//recursively call the function to query the deeper result
				pre_r(dept_c, cour_n, cs, rs);
				count++;
			}
			//if there is no result from this level, we can return
			if(count == 0)
			{
				return;
			}
		}
		catch (SQLException ex) 
		{ 
			System.out.println (ex);
		}
		catch (Exception e) 
		{
			System.out.println (e);
		}
	}
}