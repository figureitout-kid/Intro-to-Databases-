package com.techelevator.projects.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.exception.DaoException;
import com.techelevator.projects.model.Department;

public class JdbcDepartmentDao implements DepartmentDao {

	private final String DEPARTMENT_SELECT = "SELECT d.department_id, d.name FROM department d ";
	
	private final JdbcTemplate jdbcTemplate;

	public JdbcDepartmentDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Department getDepartmentById(int id) {
		Department department = null;
		String sql = DEPARTMENT_SELECT +
				" WHERE d.department_id=?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		if (results.next()) {
			department = mapRowToDepartment(results);
		}

		return department;
	}

	@Override
	public List<Department> getDepartments() {
		List<Department> departments = new ArrayList<>();
		String sql = DEPARTMENT_SELECT;

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			departments.add(mapRowToDepartment(results));
		}
		
		return departments;
	}

	@Override
	public Department createDepartment(Department department) {
		throw new DaoException("createDepartment() not implemented");
	}

	@Override
	public Department updateDepartment(Department department) {
		throw new DaoException("updateDepartment() not implemented");
	}

	@Override
	public int deleteDepartmentById(int id) {
		throw new DaoException("updateDepartment() not implemented");
	}

	private Department mapRowToDepartment(SqlRowSet results) {
		Department department = new Department();
		department.setId(results.getInt("department_id"));
		department.setName(results.getString("name"));
		return department;
	}

}
