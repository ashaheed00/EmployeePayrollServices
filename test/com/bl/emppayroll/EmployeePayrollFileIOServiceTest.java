package com.bl.emppayroll;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class EmployeePayrollFileIOServiceTest {

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchNumberOfEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmployees = { new EmployeePayrollData(1, "Aditya Verma", 800000.0),
				new EmployeePayrollData(2, "Akhil Singh", 850000.0),
				new EmployeePayrollData(3, "Anamika Bhatt", 900000.0) };
		EmployeePayrollFileIOService payrollServiceObject = new EmployeePayrollFileIOService();
		payrollServiceObject.writeData(Arrays.asList(arrayOfEmployees));
		Assert.assertEquals(3, payrollServiceObject.countEntries());
	}
}