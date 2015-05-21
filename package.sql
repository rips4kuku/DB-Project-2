create or replace package proj2 as

procedure show_students(s_dbuser OUT SYS_REFCURSOR);
procedure show_courses(co_dbuser OUT SYS_REFCURSOR);
procedure show_prerequisites(p_dbuser OUT SYS_REFCURSOR);
procedure show_classes(cl_dbuser OUT SYS_REFCURSOR);
procedure show_enrollments(e_dbuser OUT SYS_REFCURSOR);
procedure show_logs(l_dbuser OUT SYS_REFCURSOR);
procedure add_students(s_id IN students.sid%TYPE,s_firstname IN students.firstname%TYPE,s_lastname IN students.lastname%TYPE,s_status IN students.status%TYPE,s_gpa IN students.gpa%TYPE,s_email IN students.email%TYPE);


procedure check_classes(check_sid in Students.sid%type, ch_stu OUT SYS_REFCURSOR, ch_class OUT SYS_REFCURSOR);
procedure check_prereq(check_dept in prerequisites.dept_code%type, check_co# in prerequisites.course#%type, ch_pre OUT SYS_REFCURSOR);
procedure class_info(cid in classes.classid%type, ch_class OUT SYS_REFCURSOR, ch_enroll OUT SYS_REFCURSOR);

function cmp_prereq(sid_t in enrollments.sid%type, check_dept in prerequisites.dept_code%type, check_co# in prerequisites.course#%type)return boolean;
procedure enroll(sid_t in enrollments.sid%type, cid_t in enrollments.classid%type, op OUT number, op_pre OUT number, op_over OUT number, op_4 OUT number, op_size OUT number);

function cmp_prereq1(sid_t in enrollments.sid%type, check_pre_dept in prerequisites.pre_dept_code%type, check_pre_co# in prerequisites.pre_course#%type)return boolean;
procedure dropclass(sid_t in enrollments.sid%type, cid_t in enrollments.classid%type, op OUT number, op_pre OUT number, stuend OUT number, claend OUT number);

procedure deletestu(sid_t in students.sid%type, op OUT number);

end;
/

show errors