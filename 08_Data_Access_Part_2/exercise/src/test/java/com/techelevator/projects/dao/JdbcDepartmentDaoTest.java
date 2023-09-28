package com.techelevator.projects.dao;

import com.techelevator.projects.exception.DaoException;
import com.techelevator.projects.model.Department;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JdbcDepartmentDaoTest extends BaseDaoTest {

    private static final Department DEPARTMENT_1 = new Department(1, "Department 1");
    private static final Department DEPARTMENT_2 = new Department(2, "Department 2");

    private JdbcDepartmentDao sut;
    private JdbcDepartmentDao invalidConnectionDao;

    @Before
    public void setup() {
        sut = new JdbcDepartmentDao(dataSource);
        invalidConnectionDao = new JdbcDepartmentDao(invalidDataSource);
    }

    @Test
    public void created_department_has_expected_values_when_retrieved() {
        Department newDepartment = new Department();
        newDepartment.setName("New Department Test");

        Department createdDepartment = sut.createDepartment(newDepartment);
        Assert.assertNotNull("createDepartment should return the newly created department", createdDepartment);
        Assert.assertTrue("createDepartment did not return an object with ID set", createdDepartment.getId() > 0);
        Assert.assertEquals("createDepartment did not return a department with the correct name", newDepartment.getName(), createdDepartment.getName());

        // verify value was saved to database, retrieve it and compare values
        Department retrievedDepartment = getDepartmentByIdForTestVerification(createdDepartment.getId());
        Assert.assertNotNull("createDepartment failed to create department in database", retrievedDepartment);
        assertDepartmentsMatch("createDepartment did not return the newly inserted record", createdDepartment, retrievedDepartment);
    }

    @Test
    public void createDepartment_throws_dao_exception_for_data_integrity_violation() {
        Department newDepartment = new Department();
        newDepartment.setName(DEPARTMENT_1.getName()); // non-unique name

        String methodName = "createDepartment";
        try {
            sut.createDepartment(newDepartment);
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
    public void updated_department_has_expected_name_when_retrieved() {
        Department existingDepartment = new Department();
        existingDepartment.setId(DEPARTMENT_2.getId());
        existingDepartment.setName("Test Updated Project Name");

        Department updatedDepartment = sut.updateDepartment(existingDepartment);
        Assert.assertNotNull("updateDepartment should return the updated department", updatedDepartment);
        Assert.assertEquals("updateDepartment did not return an object with the same ID", existingDepartment.getId(), updatedDepartment.getId());
        Assert.assertEquals("updateDepartment did not return a department with the correct name", existingDepartment.getName(), updatedDepartment.getName());

        // verify value was saved to database, retrieve it and compare values
        Department retrievedDepartment = getDepartmentByIdForTestVerification(DEPARTMENT_2.getId());
        assertDepartmentsMatch("updateDepartment failed to change department in database", updatedDepartment, retrievedDepartment);
    }

    @Test
    public void updateDepartment_throws_dao_exception_for_data_integrity_violation() {
        Department existingDepartment = new Department();
        existingDepartment.setId(DEPARTMENT_2.getId());
        existingDepartment.setName(DEPARTMENT_1.getName()); // non-unique name

        String methodName = "updateDepartment";
        try {
            sut.updateDepartment(existingDepartment);
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
    public void deleted_department_can_no_longer_be_retrieved() {
        int recordsAffected = sut.deleteDepartmentById(DEPARTMENT_1.getId());
        Assert.assertEquals("deleteDepartmentById did not return the correct number of rows affected", 1, recordsAffected);
        Department retrievedDepartment = getDepartmentByIdForTestVerification(DEPARTMENT_1.getId());
        Assert.assertNull("department was not successfully deleted from database", retrievedDepartment);
    }

    @Test
    public void deleteDepartmentById_returns_zero_for_invalid_department_id() {
        int recordsAffected = sut.deleteDepartmentById(999); // non-existent department_id
        Assert.assertEquals("deleteDepartmentById did not return the correct number of rows affected", 0, recordsAffected);
    }

    @Test
    public void department_dao_get_methods_throw_dao_exception_for_invalid_connection() {
        String methodName = "";

        methodName = "getDepartmentById";
        try {
            invalidConnectionDao.getDepartmentById(1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "getDepartments";
        try {
            invalidConnectionDao.getDepartments();
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
    public void department_dao_insert_update_delete_methods_throw_dao_exception_for_invalid_connection() {
        String methodName = "";

        methodName = "createDepartment";
        try {
            invalidConnectionDao.createDepartment(DEPARTMENT_1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "updateDepartment";
        try {
            invalidConnectionDao.updateDepartment(DEPARTMENT_1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "deleteDepartmentById";
        try {
            invalidConnectionDao.deleteDepartmentById(1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }
    }

    private void assertDepartmentsMatch(String message, Department expected, Department actual) {
        Assert.assertEquals(message, expected.getId(), actual.getId());
        Assert.assertEquals(message, expected.getName(), actual.getName());
    }

    // test-specific implementation of getDepartmentById to be independent of DAO class
    private Department getDepartmentByIdForTestVerification(int id) {
        Department department = null;
        String sql = "SELECT department_id, name FROM department WHERE department_id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            Department mappedDepartment = new Department();
            mappedDepartment.setId(results.getInt("department_id"));
            mappedDepartment.setName(results.getString("name"));
            department = mappedDepartment;
        }
        return department;
    }
}
