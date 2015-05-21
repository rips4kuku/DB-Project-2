set serveroutput on;
create or replace package body proj2 as

procedure show_students(s_dbuser OUT SYS_REFCURSOR)
is
begin
	OPEN s_dbuser FOR
	SELECT * FROM students;
end;

procedure show_courses(co_dbuser OUT SYS_REFCURSOR)
is
begin
	OPEN co_dbuser FOR
	SELECT * FROM courses;
end;

procedure show_prerequisites(p_dbuser OUT SYS_REFCURSOR)
is
begin
	OPEN p_dbuser FOR
	SELECT * FROM prerequisites;

end;

procedure show_classes(cl_dbuser OUT SYS_REFCURSOR)
is
begin
	OPEN cl_dbuser FOR
	SELECT * FROM classes;
end;

procedure show_enrollments(e_dbuser OUT SYS_REFCURSOR)
is
begin
	OPEN e_dbuser FOR
	SELECT * FROM enrollments;
end;

procedure show_logs(l_dbuser OUT SYS_REFCURSOR)
is
begin
	OPEN l_dbuser FOR
	SELECT * FROM logs;
end;

procedure add_students(s_id IN students.sid%TYPE,
					s_firstname IN students.firstname%TYPE,
					s_lastname IN students.lastname%TYPE,
					s_status IN students.status%TYPE,
					s_gpa IN students.gpa%TYPE,
					s_email IN students.email%TYPE)
is
begin
	INSERT INTO students VALUES (s_id, s_firstname,s_lastname,s_status,s_gpa,s_email);
	COMMIT;
end add_students;

procedure check_classes(check_sid in Students.sid%type, ch_stu OUT SYS_REFCURSOR, ch_class OUT SYS_REFCURSOR)
is
begin
	OPEN ch_stu FOR
	select sid, firstname from Students where sid=check_sid;
	/*query for all classes that is taken by the student with input sid*/
	OPEN ch_class FOR
	select dept_code, course# from classes where classid in (select classid from enrollments where sid=check_sid);
end;

procedure check_prereq(check_dept in prerequisites.dept_code%type, check_co# in prerequisites.course#%type, ch_pre OUT SYS_REFCURSOR)
is
begin
	OPEN ch_pre FOR
	select pre_dept_code, pre_course# from prerequisites where dept_code=check_dept and course#=check_co#;
end;

procedure class_info(cid in classes.classid%type, ch_class OUT SYS_REFCURSOR, ch_enroll OUT SYS_REFCURSOR)
is
begin
	OPEN ch_class FOR
	select classid, title from courses co, classes cl where classid=cid and co.dept_code=cl.dept_code and co.course#=cl.course#;
	/*query for all students that take the class with input classid*/
	OPEN ch_enroll FOR
	select sid, firstname from students where sid in (select sid from enrollments where classid=cid);
end;

/*op is passed back to java to notify whether there is such student with the input sid*/
procedure deletestu(sid_t IN students.sid%TYPE, op OUT number)
is
	p_sid students.sid%type;
begin
	op := 0;
	/*if there is no such student, this would cause no_data_found exception*/
	select sid into p_sid from students where sid=sid_t;
	delete from students where sid=sid_t;
	COMMIT;
exception
	when no_data_found then
		op := 1;
end;

/*prerequisite check for procedure enroll*/
function cmp_prereq(sid_t in enrollments.sid%type, check_dept in prerequisites.dept_code%type, check_co# in prerequisites.course#%type)
return boolean
is
	/*list all prerequisite classes by providing the dept_code and course#*/
	cursor che_pre(pre_d prerequisites.dept_code%type, pre_c prerequisites.course#%type) is select pre_dept_code, pre_course# from prerequisites where dept_code=pre_d and course#=pre_c;
	/*list the grade for certain sid and classid*/
	cursor che_grade(tmp_s enrollments.sid%type, tmp_c enrollments.classid%type) is select lgrade from enrollments where sid=tmp_s and classid=tmp_c;
	/*list all classid by given dept_code and course#*/
	cursor che_cid(pre_d classes.dept_code%type, pre_c classes.course#%type) is select classid from classes where dept_code=pre_d and course#=pre_c;
	pre_dept prerequisites.dept_code%type;
	pre_co# prerequisites.course#%type;
	tmp_cid classes.classid%type;
	tmp_grade enrollments.lgrade%type;
	signal boolean;
begin
	/*for each prerequisite class*/
	for che_pre_rec in che_pre(check_dept, check_co#) loop
		/*we thought at first that the student has not take any prerequisite class*/
		signal := true;
		for che_cid_rec in che_cid(che_pre_rec.pre_dept_code, che_pre_rec.pre_course#) loop
			/*check the grade for the student taking that prerequisite class*/
			for che_grade_rec in che_grade(sid_t, tmp_cid) loop
				/*if such class has been taken, we can continue to test if it has been finished*/
				if(che_grade_rec.lgrade is  not null) then
					/*if find one prerequisite class has a grade which means the student has finished
					that class, then we can jump to the next dept_name and course#*/
					signal := false;
				end if;
			end loop;
			/*if one of the prerequisite classid of certain dept_code and course#
			has been taken by that student, we can jump to the next dept_name and course#*/
			if(not signal) then
				exit;
			end if;
		end loop;
		/*if none of the classid of certain dept_name and course# has been finished by the student
		we can be sure that this student cannot be enrolled*/
		if(signal) then
			return true;
		end if;
	end loop;
	/*no prerequisite class has not been taken by the student*/
	return false;
end;

procedure enroll(sid_t in enrollments.sid%type, cid_t in enrollments.classid%type, op OUT number,
				op_pre OUT number, op_over OUT number, op_4 OUT number, op_size OUT number)
is
	/*list all students data by providing sid to test if the sid is valid*/
	cursor che_sid(p_sid students.sid%type) is select * from students where sid=p_sid;
	/*list all classes data by providing classid to test if the classid is valid*/
	cursor che_cid(p_cid classes.classid%type) is select * from classes where classid=p_cid;
	cursor che_sid_cid(p_sid enrollments.sid%type) is select * from enrollments where sid=p_sid;
	/*list all enrollments data by providing sid and classid to test if a student has taken a specific class*/
	cursor che_enroll(p_sid enrollments.sid%type, p_cid enrollments.classid%type) is select * from enrollments where sid=p_sid and classid=p_cid;
	tmp_sem classes.semester%type;
	tmp_year classes.year%type;
	/*tmp_dept classes.dept_code%type;
	tmp_co# classes.course#%type;*/
	status boolean;
	no_class exception;
	no_student exception;
	already_enroll exception;
	/*count the number of class which has same year and semester with the enroll class*/
	count_t number(1):= 0;
	/*if flag == 1, there should be some problem happened, and we do not need to enroll this student to the class*/
	flag number(1) := 0;
begin
	op := 0;
	op_size := 0;
	op_pre := 0;
	op_over := 0;
	op_4 := 0;
/*check for valid classid*/
	status := false;
	for che_sid_rec in che_sid(sid_t) loop
		status := true;
	end loop;
	if(not status) then
		raise no_student;
	end if;
/*check for valid sid*/
	status := false;
	for che_cid_rec in che_cid(cid_t) loop
		status := true;
	end loop;
	if(not status) then
		raise no_class;
	end if;
/*check if already enrolled*/
	status := false;
	for che_enroll_rec in che_enroll(sid_t, cid_t) loop
		status := true;
	end loop;
	if(status) then
		raise already_enroll;
	end if;
/*check class size validation*/
	for che_cid_rec in che_cid(cid_t) loop
		if(che_cid_rec.class_size = che_cid_rec.limit) then
			op_size := 1;
			flag := 1;
		end if;
	end loop;
/*year and semester*/
	/*find out the year and semester of the class to be taken*/
	select year, semester into tmp_year, tmp_sem from classes where classid=cid_t;
	/*list all classid taken by a student*/
	for che_sid_cid_rec in che_sid_cid(sid_t) loop
		/*find the year and semester of each class*/
		for che_cid_rec in che_cid(che_sid_cid_rec.classid) loop
			if(tmp_year=che_cid_rec.year and tmp_sem=che_cid_rec.semester) then
				count_t := count_t + 1;
			end if;
		end loop;
	end loop;
	if(count_t=3) then
		op_over := 1;
		/*You are overloaded.*/
	end if;
	if(count_t=4) then
		flag := 1;
		op_4 := 1;
		/*Students cannot be enrolled in more than four classes in the same semester.*/
	end if;
/*check prerequisite validation*/
	status := false;
	for che_cid_rec1 in che_cid(cid_t) loop
		status := cmp_prereq(sid_t, che_cid_rec1.dept_code, che_cid_rec1.course#);
		if(status) then
			flag := 1;
			op_pre := 1;
		end if;
	end loop;

/*update*/
	if(flag=0) then
		insert into enrollments values(sid_t, cid_t, null);
		op := 8;
	end if;
exception
	when no_student then
		op := 1;
	when no_class then
		op := 2;
	when already_enroll then
		op := 3;
end;

/*prerequisite check for dropclass*/
function cmp_prereq1(sid_t in enrollments.sid%type, check_pre_dept in prerequisites.pre_dept_code%type, check_pre_co# in prerequisites.pre_course#%type)
return boolean
is
	/*list all classes depending on the specific class by providing the pre_dept_code and pre_course#*/
	cursor che_next(pre_d in prerequisites.pre_dept_code%type, pre_c in prerequisites.pre_course#%type) is select dept_code, course# from prerequisites where pre_dept_code=pre_d and pre_course#=pre_c;
	/*list the enrollments content for certain sid and classid*/
	cursor che_enroll(sid_t1 in enrollments.sid%type, cid_t in enrollments.classid%type) is select * from enrollments where sid=sid_t1 and classid=cid_t;
	/*check the classid by providing dept_code and course#*/
	cursor che_clsid(tmp_dep in classes.dept_code%type, tmp_co# in classes.course#%type) is select classid from classes where dept_code=tmp_dep and course#=tmp_co#;
begin
	/*list all classes depending on the specific class*/
	for che_next_rec in che_next(check_pre_dept, check_pre_co#) loop
		/*check the classid by providing dept_code and course#*/
		for che_clsid_rec in che_clsid(che_next_rec.dept_code, che_next_rec.course#) loop
			/*check if a student has taken a specific class*/
			for che_enroll_rec in che_enroll(sid_t, che_clsid_rec.classid) loop
				return true;
			end loop;
		end loop;
	end loop;
	return false;
end;

procedure dropclass(sid_t in enrollments.sid%type, cid_t in enrollments.classid%type,
					op OUT number, op_pre OUT number, stuend OUT number, claend OUT number)
is
	/*list all students data by providing sid to test if the sid is valid*/
	cursor che_sid(p_sid students.sid%type) is select * from students where sid=p_sid;
	/*list all classes data by providing classid to test if the classid is valid*/
	cursor che_cid(p_cid classes.classid%type) is select * from classes where classid=p_cid;
	/*list all classid taken by a student by sid*/
	cursor che_sid_cid(p_sid enrollments.sid%type) is select * from enrollments where sid=p_sid;
	/*list all enrollments data by providing sid and classid to test if a student has taken a specific class*/
	cursor che_enroll(p_sid enrollments.sid%type, p_cid enrollments.classid%type) is select * from enrollments where sid=p_sid and classid=p_cid;
	tmp_dept classes.dept_code%type;
	tmp_co# classes.course#%type;
	tmp_size classes.class_size%type;
	status boolean;
	no_class exception;
	no_student exception;
	not_enroll exception;
	flag number(1) := 0;
begin
	op := 0;
	op_pre := 0;
	stuend := 0;
	claend := 0;
/*check for valid classid*/
	status := false;
	for che_sid_rec in che_sid(sid_t) loop
		status := true;
	end loop;
	if(not status) then
		raise no_student;
	end if;
/*check for valid sid*/
	status := false;
	for che_cid_rec in che_cid(cid_t) loop
		status := true;
	end loop;
	if(not status) then
		raise no_class;
	end if;
/*check if already enrolled*/
	status := false;
	for che_enroll_rec in che_enroll(sid_t, cid_t) loop
		status := true;
	end loop;
	if(not status) then
		raise not_enroll;
	end if;
	status := false;
/*check if this class is the prerequisite class of other classes*/
	/*select dept_code, course# into tmp_dept, tmp_co# from classes where classid=cid_t;*/
	for che_cid_rec1 in che_cid(cid_t) loop
		status := cmp_prereq1(sid_t, che_cid_rec1.dept_code, che_cid_rec1.course#);
		if(status) then
			op_pre := 1;
			flag := 1;
		end if;
	end loop;
/*delete*/
	if(flag=0) then
		delete from enrollments where sid=sid_t and classid=cid_t;
		op := 5;
		/*further check*/
		/*if This student is not enrolled in any classes*/
		for che_sid_cid_rec in che_sid_cid(sid_t) loop
			status := true;
		end loop;
		if(not status) then
			stuend := 1;
		end if;
		/*if The class now has no students*/
		select class_size into tmp_size from classes where classid=cid_t;
		if(tmp_size=0) then
			claend := 1;
		end if;
	end if;

exception
	when no_student then
		op := 1;
		/*dbms_output.put_line('The sid is invalid.');*/
	when no_class then
		op := 2;
		/*dbms_output.put_line('The classid is invalid.');*/
	when not_enroll then
		op := 3;
		/*dbms_output.put_line('The student is not enrolled in the class.');*/
end;

end proj2;
/
show errors