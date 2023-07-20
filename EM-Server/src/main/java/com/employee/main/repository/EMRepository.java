package com.employee.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.main.model.EMModel;

@Repository
public interface EMRepository extends JpaRepository<EMModel, String> {
	EMModel findFirstByEmpId(String empId);
	EMModel findFirstByEmail(String email);
	EMModel findFirstByEmpIdAndPswd(String empId, String pswd);
//	@Query("SELECT e FROM Employee e WHERE e.emp_id = :empId AND e.pswd = :pswd AND e.is_admin = 1")
	EMModel findFirstByEmpIdAndPswdAndIsAdmin(String empId, String pswd,int IsAdmin);
}
