package com.bl.emppayroll;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import com.bl.emppayroll.service.EmployeePayrollFileIOService;

public class EmployeePayrollFileIOServiceTest {

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchNumberOfEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmployees = { new EmployeePayrollData(1, "Akash Roy", 1000000.0),
				new EmployeePayrollData(2, "Sanjay Reddy", 600000.0),
				new EmployeePayrollData(3, "Aditi Banerjee", 6500000.0) };
		EmployeePayrollFileIOService payrollServiceObject = new EmployeePayrollFileIOService();
		payrollServiceObject.writeData(Arrays.asList(arrayOfEmployees));
		Assert.assertEquals(3, payrollServiceObject.countEntries());
		payrollServiceObject.printEmployeePayrolls();
	}
}