package com.techelevator.projects.dao;

import com.techelevator.projects.exception.DaoException;
import com.techelevator.projects.model.Employee;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDate;

public class JdbcEmployeeDaoTest extends BaseDaoTest {

    private static final Employee EMPLOYEE_1 =
        new Employee(1, 1, "First1", "Last1",
                LocalDate.parse("1981-01-01"), LocalDate.parse("2001-01-02"));
    private static final Employee EMPLOYEE_2 =
        new Employee(2, 2, "First2", "Last2",
                LocalDate.parse("1982-02-01"), LocalDate.parse("2002-02-03"));

    private JdbcEmployeeDao sut;
    private JdbcEmployeeDao invalidConnectionDao;

    @Before
    public void setup() {
        sut = new JdbcEmployeeDao(dataSource);
        invalidConnectionDao = new JdbcEmployeeDao(invalidDataSource);
    }

    @Test
    public void created_employee_has_expected_values_when_retrieved() {
        Employee newEmployee = new Employee();
        newEmployee.setDepartmentId(1);
        newEmployee.setFirstName("Test");
        newEmployee.setLastName("Testerson");
        newEmployee.setBirthDate(LocalDate.parse("2021-02-21"));
        newEmployee.setHireDate(LocalDate.parse("2022-02-21"));

		Employee createdEmployee = sut.createEmployee(newEmployee);

        Assert.assertNotNull("createEmployee should return the newly created employee", createdEmployee);
        Assert.assertTrue("createEmployee did not return an employee with ID set", createdEmployee.getId() > 0);
        Assert.assertEquals("createEmployee did not return an employee with the correct departmentId", 1, newEmployee.getDepartmentId());
        Assert.assertEquals("createEmployee did not return an employee with the correct firstName", "Test", newEmployee.getFirstName());
        Assert.assertEquals("createEmployee did not return an employee with the correct lastName", "Testerson", newEmployee.getLastName());
        Assert.assertEquals("createEmployee did not return an employee with the correct birthDate", LocalDate.parse("2021-02-21"), newEmployee.getBirthDate());
        Assert.assertEquals("createEmployee did not return an employee with the correct hireDate", LocalDate.parse("2022-02-21"), newEmployee.getHireDate());

        // verify value was saved to database, retrieve it and compare values
        Employee retrievedEmployee = getEmployeeByIdForTestVerification(createdEmployee.getId());
        Assert.assertNotNull("createEmployee failed to create employee in database", retrievedEmployee);
        assertEmployeesMatch("createEmployee did not return the newly inserted record", createdEmployee, retrievedEmployee);
    }

    @Test
    public void createEmployee_throws_dao_exception_for_data_integrity_violation() {
        Employee newEmployee = new Employee();
        newEmployee.setDepartmentId(999); // non-existent department_id
        newEmployee.setFirstName("Test");
        newEmployee.setLastName("Testerson");
        newEmployee.setBirthDate(LocalDate.parse("2021-02-21"));
        newEmployee.setHireDate(LocalDate.parse("2022-02-21"));

        String methodName = "createEmployee";
        try {
            sut.createEmployee(newEmployee);
            Assert.fail(didNotThrowAnyExceptionForDataIntegrity(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForDataIntegrity(methodName));
        }
    }

    @Test
    public void updated_employee_has_expected_values_when_retrieved() {
        Employee existingEmployee = new Employee();
        existingEmployee.setId(EMPLOYEE_2.getId());
        existingEmployee.setDepartmentId(1);
        existingEmployee.setFirstName("TestUpdate");
        existingEmployee.setLastName("UpdateTesterson");
        existingEmployee.setBirthDate(LocalDate.parse("2003-02-21"));
        existingEmployee.setHireDate(LocalDate.parse("2023-02-21"));

        Employee updatedEmployee = sut.updateEmployee(existingEmployee);

        Assert.assertNotNull("updateEmployee should return the updated employee", updatedEmployee);
        Assert.assertEquals("updateEmployee did not return an object with the same ID", existingEmployee.getId(), updatedEmployee.getId());
        Assert.assertEquals("updateEmployee failed to assign new departmentId to employee", 1, existingEmployee.getDepartmentId());
        Assert.assertEquals("updateEmployee failed to assign new firstName to employee", "TestUpdate", updatedEmployee.getFirstName());
        Assert.assertEquals("updateEmployee failed to assign new lastName to employee", "UpdateTesterson", updatedEmployee.getLastName());
        Assert.assertEquals("updateEmployee failed to assign new birthDate to employee", LocalDate.parse("2003-02-21"), updatedEmployee.getBirthDate());
        Assert.assertEquals("updateEmployee failed to assign new hireDate to employee", LocalDate.parse("2023-02-21"), updatedEmployee.getHireDate());

        // verify value was saved to database, retrieve it and compare values
        Employee retrievedEmployee = getEmployeeByIdForTestVerification(EMPLOYEE_2.getId());
        assertEmployeesMatch("updateEmployee failed to change employee in database", updatedEmployee, retrievedEmployee);
    }

    @Test
    public void updateEmployee_throws_dao_exception_for_data_integrity_violation() {
        Employee newEmployee = new Employee();
        newEmployee.setDepartmentId(999); // non-existent department_id
        newEmployee.setFirstName("Test");
        newEmployee.setLastName("Testerson");
        newEmployee.setBirthDate(LocalDate.parse("2021-02-21"));
        newEmployee.setHireDate(LocalDate.parse("2022-02-21"));

        String methodName = "createEmployee";
        try {
            sut.createEmployee(newEmployee);
            Assert.fail(didNotThrowAnyExceptionForDataIntegrity(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForDataIntegrity(methodName));
        }
    }

    @Test
    public void deleted_employee_can_no_longer_be_retrieved() {
        int recordsAffected = sut.deleteEmployeeById(EMPLOYEE_1.getId());
        Assert.assertEquals("deleteEmployeeById did not return the correct number of rows affected", 1, recordsAffected);
        Employee retrievedEmployee = getEmployeeByIdForTestVerification(EMPLOYEE_1.getId());
        Assert.assertNull("employee was not successfully deleted from database", retrievedEmployee);
    } 

    @Test
    public void deleted_employee_by_department_can_no_longer_be_retrieved() {
        int recordsAffected = sut.deleteEmployeesByDepartmentId(EMPLOYEE_1.getDepartmentId());
        Assert.assertEquals("deleteEmployeesByDepartmentId did not return the correct number of rows affected", 3, recordsAffected);
        Employee retrievedEmployee = getEmployeeByIdForTestVerification(EMPLOYEE_1.getId());
        Assert.assertNull("employee was not successfully deleted from database", retrievedEmployee);
    }

    @Test
    public void deleteEmployeeById_returns_zero_for_invalid_employee_id() {
        int recordsAffected = sut.deleteEmployeeById(999); // non-existent employee_id
        Assert.assertEquals("deleteEmployeeById did not return the correct number of rows affected", 0, recordsAffected);
    }

    @Test
    public void deleteEmployeesByDepartmentId_returns_zero_for_invalid_department_id() {
        int recordsAffected = sut.deleteEmployeesByDepartmentId(999); // non-existent department_id
        Assert.assertEquals("deleteEmployeesByDepartmentId did not return the correct number of rows affected", 0, recordsAffected);
    }

    @Test
    public void employee_dao_get_methods_throw_dao_exception_for_invalid_connection() {
        String methodName = "";

        methodName = "getEmployeeById";
        try {
            invalidConnectionDao.getEmployeeById(1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "getEmployees";
        try {
            invalidConnectionDao.getEmployees();
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "getEmployeesByFirstNameLastName";
        try {
            invalidConnectionDao.getEmployeesByFirstNameLastName("First1", "Last1");
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "getEmployeesByProjectId";
        try {
            invalidConnectionDao.getEmployeesByProjectId(1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "getEmployeesWithoutProjects";
        try {
            invalidConnectionDao.getEmployeesWithoutProjects();
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }
    }

    @Test
    public void employee_dao_insert_update_delete_methods_throw_dao_exception_for_invalid_connection() {
        String methodName = "";

        methodName = "createEmployee";
        try {
            invalidConnectionDao.createEmployee(EMPLOYEE_1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "updateEmployee";
        try {
            invalidConnectionDao.updateEmployee(EMPLOYEE_1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "deleteEmployeeById";
        try {
            invalidConnectionDao.deleteEmployeeById(1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "deleteEmployeesByDepartmentId";
        try {
            invalidConnectionDao.deleteEmployeesByDepartmentId(1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }
    }

    public static void assertEmployeesMatch(String message, Employee expected, Employee actual) {
        Assert.assertEquals(message, expected.getId(), actual.getId());
        Assert.assertEquals(message, expected.getDepartmentId(), actual.getDepartmentId());
        Assert.assertEquals(message, expected.getFirstName(), actual.getFirstName());
        Assert.assertEquals(message, expected.getLastName(), actual.getLastName());
        Assert.assertEquals(message, expected.getBirthDate(), actual.getBirthDate());
        Assert.assertEquals(message, expected.getHireDate(), actual.getHireDate());
    }

    // test-specific implementation of getEmployeeById to be independent of DAO class
    private Employee getEmployeeByIdForTestVerification(int id) {
        Employee employee = null;
        String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, hire_date " +
                     "FROM employee WHERE employee_id=?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            Employee mappedEmployee = new Employee();
            mappedEmployee.setId(results.getInt("employee_id"));
            mappedEmployee.setDepartmentId(results.getInt("department_id"));
            mappedEmployee.setFirstName(results.getString("first_name"));
            mappedEmployee.setLastName(results.getString("last_name"));
            mappedEmployee.setBirthDate(results.getDate("birth_date").toLocalDate());
            mappedEmployee.setHireDate(results.getDate("hire_date").toLocalDate());
            employee = mappedEmployee;
        }
        return employee;
    }
}
