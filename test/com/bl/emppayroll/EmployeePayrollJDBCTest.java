package com.bl.emppayroll;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.bl.emppayroll.EmployeePayrollService.IOService;

public class EmployeePayrollJDBCTest {
	EmployeePayrollService empPayRollService;
	List<EmployeePayrollData> empPayrollList;

	@Before
	public void initialize() {
		empPayRollService = new EmployeePayrollService();
		empPayrollList = empPayRollService.readEmployeePayrollData(IOService.DB_IO);
	}

	@Test
	public void givenEmpPayrollDB_WhenRetrieved_ShouldMatchEmpCount() {
		assertEquals(5, empPayrollList.size());
	}

	@Test
	public void givenNewSalary_WhenUpdated_ShouldSyncWithDB() {
		empPayRollService.updateEmployeeSalary("Terisa", 3000000.0);
		boolean isSynced = empPayRollService.isEmpPayrollSyncedWithDB("Terisa");
		assertTrue(isSynced);
	}

}
