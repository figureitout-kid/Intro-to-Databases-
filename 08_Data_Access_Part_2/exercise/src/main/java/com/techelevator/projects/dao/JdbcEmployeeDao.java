package com.techelevator.projects.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.exception.DaoException;
import com.techelevator.projects.model.Employee;

public class JdbcEmployeeDao implements EmployeeDao {

	private final String EMPLOYEE_SELECT = "SELECT e.employee_id, e.department_id, e.first_name, e.last_name, " +
				"e.birth_date, e.hire_date FROM employee e ";

	private final JdbcTemplate jdbcTemplate;

	public JdbcEmployeeDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Employee getEmployeeById(int id) {
		Employee employee = null;
		String sql = EMPLOYEE_SELECT +
				" WHERE e.employee_id=?";
	try {
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		if (results.next()) {
			employee = mapRowToEmployee(results);
		}
	} catch (CannotGetJdbcConnectionException e) {
		throw new DaoException ("Unable to connect to server or database", e);
	} catch (DataIntegrityViolationException e) {
		throw new DaoException ("Data integrity violation", e);
	}
		return employee;
	}

	@Override
	public List<Employee> getEmployees() {
		List<Employee> allEmployees = new ArrayList<>();
		String sql = EMPLOYEE_SELECT;
	try {
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
	} catch (CannotGetJdbcConnectionException e) {
		throw new DaoException ("Unable to connect to server or database", e);
	} catch (DataIntegrityViolationException e) {
		throw new DaoException ("Data integrity violation", e);
	}

		return allEmployees;
	}

	@Override
	public List<Employee> getEmployeesByFirstNameLastName(String firstName, String lastName) {
		List<Employee> allEmployees = new ArrayList<>();
		String sql = EMPLOYEE_SELECT +
				" WHERE e.first_name ILIKE ? AND e.last_name ILIKE ?";
	try {
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, "%" + firstName + "%", "%" + lastName + "%");
		while (results.next()) {
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
	} catch (CannotGetJdbcConnectionException e) {
		throw new DaoException ("Unable to connect to server or database", e);
	} catch (DataIntegrityViolationException e) {
		throw new DaoException ("Data integrity violation", e);
	}
		return allEmployees;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(int projectId) {
		List<Employee> allEmployees = new ArrayList<>();
		String sql =  EMPLOYEE_SELECT +
				"JOIN project_employee pe ON e.employee_id = pe.employee_id " +
				"WHERE pe.project_id = ?";
		try {
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, projectId);
		while (results.next()) {
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
		return allEmployees;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		List<Employee> allEmployees = new ArrayList<>();
		String sql = EMPLOYEE_SELECT +
				" WHERE e.employee_id NOT IN (SELECT DISTINCT employee_id FROM project_employee)";
		try {
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
		return allEmployees;
	}

	@Override
	public Employee createEmployee(Employee employee) {
		Employee newEmployee = null;
		String sql = "INSERT INTO employee (department_id, first_name, last_name, birth_date, hire_date) " +
				"VALUES (?, ?, ?, ?, ?) RETURNING employee_id;" ;
		try {
			int newEmployeeId = jdbcTemplate.queryForObject(sql, int.class, employee.getDepartmentId(), employee.getFirstName(), employee.getLastName(), employee.getBirthDate(), employee.getHireDate());

			newEmployee = getEmployeeById(newEmployeeId);

		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
		return newEmployee;
	}

	
	@Override
	public Employee updateEmployee(Employee employee) {
		String sql = "UPDATE employee SET department_id = ? , first_name = ?, last_name = ?, birth_date = ?, hire_date = ?  " +
				"WHERE employee_id = ?;" ;
		try {
			int numRows = jdbcTemplate.update(sql, employee.getDepartmentId(), employee.getFirstName(), employee.getLastName(), employee.getBirthDate(), employee.getHireDate(), employee.getId());

			if (numRows== 0) {
				throw new DaoException("Zero rows affected, expected at least one");
			}
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
		return getEmployeeById(employee.getId());
	}

	@Override
	public int deleteEmployeeById(int id) {
		int numRows = 0;
		String deleteProjectEmployeeSql = "DELETE from project_employee WHERE employee_id = ?;";
		String deleteEmployeeSql = "DELETE FROM employee WHERE employee_id = ?;";

		try {
			jdbcTemplate.update(deleteProjectEmployeeSql, id);
			numRows = jdbcTemplate.update(deleteEmployeeSql, id);
			return numRows;
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
	}

	@Override
	public int deleteEmployeesByDepartmentId(int departmentId) {
		int numRows = 0;
		String deleteProjectEmployeeSql = "DELETE from project_employee WHERE employee_id IN (SELECT employee_id FROM employee WHERE department_id = ?);";
		String deleteEmployeeSql = "DELETE FROM employee WHERE department_id = ?;";

		try {
			jdbcTemplate.update(deleteProjectEmployeeSql, departmentId);
			numRows = jdbcTemplate.update(deleteEmployeeSql, departmentId);
			return numRows;
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
	}

	private Employee mapRowToEmployee(SqlRowSet result) {
		Employee employee = new Employee();
		employee.setId(result.getInt("employee_id"));
		employee.setDepartmentId(result.getInt("department_id"));
		employee.setFirstName(result.getString("first_name"));
		employee.setLastName(result.getString("last_name"));
		employee.setBirthDate(result.getDate("birth_date").toLocalDate());
		employee.setHireDate(result.getDate("hire_date").toLocalDate());
		return employee;
	}
}
