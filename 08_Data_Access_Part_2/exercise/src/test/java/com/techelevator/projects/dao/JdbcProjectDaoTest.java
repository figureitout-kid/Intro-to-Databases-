package com.techelevator.projects.dao;

import com.techelevator.projects.exception.DaoException;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcProjectDaoTest extends BaseDaoTest {
    private static final Employee EMPLOYEE_2 =
        new Employee(2, 2, "First2", "Last2",
                LocalDate.parse("1982-02-01"), LocalDate.parse("2002-02-03"));
    private static final Employee EMPLOYEE_3 =
        new Employee(3, 1, "First3", "Last3",
                LocalDate.parse("1983-03-01"), LocalDate.parse("2003-03-04"));

    private static final Project PROJECT_1 =
            new Project(1, "Project 1", LocalDate.parse("2000-01-02"), LocalDate.parse("2000-12-31"));
    private static final Project PROJECT_2 =
            new Project(2, "Project 2", null, null);

    private JdbcProjectDao sut;
    private JdbcProjectDao invalidConnectionDao;

    @Before
    public void setup() {
        sut = new JdbcProjectDao(dataSource);
        invalidConnectionDao = new JdbcProjectDao(invalidDataSource);
    }

    @Test
    public void created_project_has_expected_values_when_retrieved() {
        Project newProject = new Project();
        newProject.setName("Project Ultima");
        newProject.setFromDate(LocalDate.of(2023, 02, 01));
        newProject.setToDate(LocalDate.of(2023, 04, 01));

        Project createdProject = sut.createProject(newProject);
        Assert.assertNotNull("createProject should return the newly created project", createdProject);
        Assert.assertTrue("createProject did not return an object with ID set", createdProject.getId() > 0);
        Assert.assertEquals("createProject did not return a project with the correct name", newProject.getName(), createdProject.getName());
        Assert.assertEquals("createProject did not return a project with the correct fromDate", newProject.getFromDate(), createdProject.getFromDate());
        Assert.assertEquals("createProject did not return a project with the correct toDate", newProject.getToDate(), createdProject.getToDate());

        // verify value was saved to database, retrieve it and compare values
        Project retrievedProject = getProjectByIdForTestVerification(createdProject.getId());
        Assert.assertNotNull("createProject failed to create project in database", retrievedProject);
        assertProjectsMatch("createProject did not return the newly inserted record", createdProject, retrievedProject);
    }

    @Test
    public void createProject_throws_dao_exception_for_data_integrity_violation() {
        Project newProject = new Project();
        newProject.setName(PROJECT_1.getName()); // non-unique name

        String methodName = "createProject";
        try {
            sut.createProject(newProject);
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
    public void employee_added_to_project_is_in_list_of_employees_for_project() {
        // Get list of employees before link
        int preLinkEmployeeCount = getProjectEmployeesForTestVerification(1).size();

        sut.linkProjectEmployee(1, 3);
        List<Employee> projectEmployees = getProjectEmployeesForTestVerification(1);
        int postLinkEmployeeCount = projectEmployees.size();

        Assert.assertEquals("linkProjectEmployee didn't increase number of a project's employees", preLinkEmployeeCount + 1, postLinkEmployeeCount);
        assertProjectEmployeesMatch("linkProjectEmployee did not add correct employee to the project", EMPLOYEE_3, projectEmployees.get(1));
    }

    @Test
    public void employee_removed_from_project_is_not_in_list_of_employees_for_project() {
        // Get list of employees before link
        int preLinkEmployeeCount = getProjectEmployeesForTestVerification(2).size();

        sut.unlinkProjectEmployee(2, 3);
        List<Employee> projectEmployees = getProjectEmployeesForTestVerification(2);
        int postLinkEmployeeCount = projectEmployees.size();

        Assert.assertEquals("linkProjectEmployee didn't decrease number of a project's employees", preLinkEmployeeCount - 1, postLinkEmployeeCount);
        assertProjectEmployeesMatch("unlinkProjectEmployee did not remove correct employee to the project", EMPLOYEE_2, projectEmployees.get(0));
    }
    
    @Test
    public void updated_project_has_expected_values_when_retrieved() {
        Project existingProject = new Project();
        existingProject.setId(PROJECT_2.getId());
        existingProject.setName("Test Project Update");
        existingProject.setFromDate(LocalDate.parse("2003-02-21"));
        existingProject.setToDate(LocalDate.parse("2023-02-21"));

        Project updatedProject = sut.updateProject(existingProject);
        Assert.assertNotNull("updateProject should return the updated project", updatedProject);
        Assert.assertEquals("updateProject did not return an object with the same ID", existingProject.getId(), updatedProject.getId());
        Assert.assertEquals("updateProject failed to assign new name to project", "Test Project Update", existingProject.getName());
        Assert.assertEquals("updateProject failed to assign new from date to employee", LocalDate.parse("2003-02-21"), existingProject.getFromDate());
        Assert.assertEquals("updateProject failed to assign new to date to employee", LocalDate.parse("2023-02-21"), existingProject.getToDate());

        // verify value was saved to database, retrieve it and compare values
        Project retrievedProject = getProjectByIdForTestVerification(PROJECT_2.getId());
        assertProjectsMatch("updateProject failed to change project in database", updatedProject, retrievedProject);
    }

    @Test
    public void updateProject_throws_dao_exception_for_data_integrity_violation() {
        Project existingProject = new Project();
        existingProject.setId(PROJECT_2.getId());
        existingProject.setName(PROJECT_1.getName()); // non-unique name

        String methodName = "updateProject";
        try {
            sut.updateProject(existingProject);
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
    public void deleted_project_cant_be_retrieved() {
        int recordsAffected = sut.deleteProjectById(PROJECT_1.getId());
        Assert.assertEquals("deleteProjectById did not return the correct number of rows affected", 1, recordsAffected);
        Project retrievedProject = getProjectByIdForTestVerification(PROJECT_1.getId());
        Assert.assertNull("project was not successfully deleted from database", retrievedProject);
    }

    @Test
    public void deleteProjectById_returns_zero_for_invalid_project_id() {
        int recordsAffected = sut.deleteProjectById(999); // non-existent project_id
        Assert.assertEquals("deleteProjectById did not return the correct number of rows affected", 0, recordsAffected);
    }

    @Test
    public void project_dao_get_methods_throw_dao_exception_for_invalid_connection() {
        String methodName = "";

        methodName = "getProjectById";
        try {
            invalidConnectionDao.getProjectById(1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "getProjects";
        try {
            invalidConnectionDao.getProjects();
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
    public void project_dao_insert_update_delete_methods_throw_dao_exception_for_invalid_connection() {
        String methodName = "";

        methodName = "createProject";
        try {
            invalidConnectionDao.createProject(PROJECT_1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "linkProjectEmployee";
        try {
            invalidConnectionDao.linkProjectEmployee(1,1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "unlinkProjectEmployee";
        try {
            invalidConnectionDao.unlinkProjectEmployee(1,1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "updateProject";
        try {
            invalidConnectionDao.updateProject(PROJECT_1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }

        methodName = "deleteProjectById";
        try {
            invalidConnectionDao.deleteProjectById(1);
            Assert.fail(didNotThrowAnyExceptionForCannotGetConnection(methodName));
        } catch (DaoException e) {
            if (e.getMessage().equalsIgnoreCase(methodName + NOT_IMPLEMENTED_LONG_SUFFIX)) {
                Assert.fail(threwNotImplementedException(methodName));
            }
        } catch (Exception e) {
            Assert.fail(didNotThrowDaoExceptionForCannotGetConnection(methodName));
        }
    }

    private void assertProjectsMatch(String message, Project expected, Project actual) {
        Assert.assertEquals(message, expected.getId(), actual.getId());
        Assert.assertEquals(message, expected.getName(), actual.getName());
        Assert.assertEquals(message, expected.getFromDate(), actual.getFromDate());
        Assert.assertEquals(message, expected.getToDate(), actual.getToDate());
    }

    public static void assertProjectEmployeesMatch(String message, Employee expected, Employee actual) {
        Assert.assertEquals(message, expected.getId(), actual.getId());
        Assert.assertEquals(message, expected.getDepartmentId(), actual.getDepartmentId());
        Assert.assertEquals(message, expected.getFirstName(), actual.getFirstName());
        Assert.assertEquals(message, expected.getLastName(), actual.getLastName());
        Assert.assertEquals(message, expected.getBirthDate(), actual.getBirthDate());
        Assert.assertEquals(message, expected.getHireDate(), actual.getHireDate());
    }

    // test-specific implementation of getProjectById to be independent of DAO class
    private Project getProjectByIdForTestVerification(int id) {
        Project project = null;
        String sql = "SELECT project_id, name, from_date, to_date FROM project WHERE project_id=?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            Project mappedProject = new Project();
            mappedProject.setId(results.getInt("project_id"));
            mappedProject.setName(results.getString("name"));
            if(results.getDate("from_date") != null) {
                mappedProject.setFromDate(results.getDate("from_date").toLocalDate());
            }
            if(results.getDate("to_date") != null) {
                mappedProject.setToDate(results.getDate("to_date").toLocalDate());
            }
            project = mappedProject;
        }
        return project;
    }

    private List<Employee> getProjectEmployeesForTestVerification(int id) {
        List<Employee> projectEmployees = new ArrayList<>();
        String sql = "SELECT e.employee_id, e.department_id, e.first_name, e.last_name, e.birth_date, e.hire_date FROM employee e "+
                "JOIN project_employee pe ON e.employee_id = pe.employee_id "+
                "WHERE pe.project_id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            while (results.next()) {
                Employee mappedEmployee = new Employee();
                mappedEmployee.setId(results.getInt("employee_id"));
                mappedEmployee.setDepartmentId(results.getInt("department_id"));
                mappedEmployee.setFirstName(results.getString("first_name"));
                mappedEmployee.setLastName(results.getString("last_name"));
                mappedEmployee.setBirthDate(results.getDate("birth_date").toLocalDate());
                mappedEmployee.setHireDate(results.getDate("hire_date").toLocalDate());
                projectEmployees.add(mappedEmployee);
            }
        return projectEmployees;
    }
}
