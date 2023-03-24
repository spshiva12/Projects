package com.ojas.timesheet.repo;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.ojas.timesheet.entity.DSR;
import com.ojas.timesheet.entity.Employee;

@Repository
@EnableJpaRepositories
public interface DSRRepo extends JpaRepository<DSR, Long> {

	@Query(value = "select d from dsr d where d.project = ?1 and year(d.dsr_date) = ?2 and month(d.dsr_date) = ?3", nativeQuery = true)
	public List<DSR> findByProjectAndMonth(String projectName, String year, String month);

	@Query(value = "select d from DSR d where d.empName=?1")
	public List<DSR> getName(String empName);

	@Query(value = "select * from dsr d where d.project = ?1 and d.emp_name=?2 and year(d.dsr_date) = ?3 and month(d.dsr_date) = ?4 order by d.dsr_date asc", nativeQuery = true)
	public List<DSR> fetchTechDetails(String projectName, String ename, String year, String month);

	@Query(value = "select distinct(d.emp_name) from dsr d where d.project = ?1 and year(d.dsr_date) = ?2 and month(d.dsr_date) = ?3 order by emp_name", nativeQuery = true)
	public List<String> fetchEmpNameByProject(String projectName, String year, String month);

	@Query(value = "select snid,dsr_date,dsr_report,emp_name,project,submit_date,visibility,sum(if(cast((substring_index(d.hrs_worked,':',1)) as signed)>?5,?5,cast((substring_index(d.hrs_worked,':',1)) as signed))) as hrs_worked from dsr d where d.project = ?1 and d.emp_name=?2 and year(d.dsr_date) = ?3 and month(d.dsr_date) = ?4", nativeQuery = true)
	public List<DSR> fetchEmpProjectSummaryDetailsOpt(String projectName, String ename, String year, String month,int hrs);
	
	@Query(value = "select snid,dsr_date,dsr_report,emp_name,project,submit_date,visibility,sum(if(cast((substring_index(d.hrs_worked,':',1)) as signed)>?3,?3,cast((substring_index(d.hrs_worked,':',1)) as signed))) as hrs_worked from dsr d where d.project = ?1 and d.emp_name=?2 and date_format((d.dsr_date),'%d/%m/%Y') in ?4", nativeQuery = true)
	public List<DSR> fetchEmpProjectSummaryDetails(String projectName, String ename,int hrs,List<String> refinedDateString);

	@Query(value = "select * from dsr d where d.project = ?2 and d.emp_name=?1 and d.dsr_date = ?3", nativeQuery = true)
	public DSR checkRecordExists(String empName, String project, Date getdSRDate);

	@Modifying
	@Transactional
	@Query(value = "update dsr set dsr_report=?4,hrs_worked=?5,submit_date=now() where project = ?2 and emp_name=?1 and dsr_date = ?3", nativeQuery = true)
	public void updateRecord(String empName, String project, Date getdSRDate, String getdSRReport, String hrsWorked);

	@Query(value = "select distinct(emp_name) from dsr", nativeQuery = true)
	public List<String> getAllEmpName();

	@Query(value = "select distinct(project) from dsr", nativeQuery = true)
	public List<String> getAllProjectName();

}
