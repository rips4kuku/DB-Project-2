create or replace trigger size_add
after insert on enrollments
for each row
begin
	update classes set class_size = class_size + 1 where classid=:new.classid;
end;
/

create or replace trigger size_sub
after delete on enrollments
for each row
begin
	update classes set class_size = class_size - 1 where classid=:old.classid;
end;
/

create or replace trigger drop_all_class
before delete on students
for each row
begin
	delete from enrollments where sid=:old.sid;
end;
/

create or replace trigger enroll_class
after insert on enrollments
for each row
declare
	tmp_logid number(4);
	tmp_who varchar2(10);
	tmp_time date;
	tmp_table_name varchar2(20);
	tmp_operation varchar2(6);
	tmp_key_value varchar2(14);
begin
	select user into tmp_who from dual;
	tmp_time := SYSDATE;
	tmp_table_name := 'enrollments';
	tmp_operation := 'insert';
	tmp_key_value := :new.sid||','||:new.classid;
	insert into logs values(log_seq.nextval, tmp_who, tmp_time, tmp_table_name, tmp_operation, tmp_key_value);
end;
/

create or replace trigger drop_class
after delete on enrollments
for each row
declare
	tmp_logid number(4);
	tmp_who varchar2(10);
	tmp_time date;
	tmp_table_name varchar2(20);
	tmp_operation varchar2(6);
	tmp_key_value varchar2(14);
begin
	select user into tmp_who from dual;
	tmp_time := SYSDATE;
	tmp_table_name := 'enrollments';
	tmp_operation := 'delete';
	tmp_key_value := :old.sid||','||:old.classid;
	insert into logs values(log_seq.nextval, tmp_who, tmp_time, tmp_table_name, tmp_operation, tmp_key_value);
end;
/

create or replace trigger add_student
after insert on students
for each row
declare
	tmp_logid number(4);
	tmp_who varchar2(10);
	tmp_time date;
	tmp_table_name varchar2(20);
	tmp_operation varchar2(6);
	tmp_key_value varchar2(14);
begin
	select user into tmp_who from dual;
	tmp_time := SYSDATE;
	tmp_table_name := 'students';
	tmp_operation := 'insert';
	tmp_key_value := :new.sid;
	insert into logs values(log_seq.nextval, tmp_who, tmp_time, tmp_table_name, tmp_operation, tmp_key_value);
end;
/

create or replace trigger delete_student
after delete on students
for each row
declare
	tmp_logid number(4);
	tmp_who varchar2(10);
	tmp_time date;
	tmp_table_name varchar2(20);
	tmp_operation varchar2(6);
	tmp_key_value varchar2(14);
begin
	select user into tmp_who from dual;
	tmp_time := SYSDATE;
	tmp_table_name := 'students';
	tmp_operation := 'delete';
	tmp_key_value := :old.sid;
	insert into logs values(log_seq.nextval, tmp_who, tmp_time, tmp_table_name, tmp_operation, tmp_key_value);
end;
/
show errors