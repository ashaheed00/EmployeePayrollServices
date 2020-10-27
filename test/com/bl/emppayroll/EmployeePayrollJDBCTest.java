package com.bl.emppayroll;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.bl.emppayroll.EmployeePayrollService.IOService;

public class EmployeePayrollJDBCTest {

	@Test
	public void givenEmpPayrollDB_WhenRetrieved_ShouldMatchEmpCount() {
		EmployeePayrollService empPayRollService = new EmployeePayrollService();
		List<EmployeePayrollData> empPayrollList = empPayRollService.readEmployeePayrollData(IOService.DB_IO);
		assertEquals(5, empPayrollList.size());
		System.out.println(empPayrollList);
	}

}
