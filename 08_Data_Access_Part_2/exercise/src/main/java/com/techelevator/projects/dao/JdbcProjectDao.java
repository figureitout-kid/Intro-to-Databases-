package com.techelevator.projects.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.techelevator.projects.model.Department;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.exception.DaoException;
import com.techelevator.projects.model.Project;

public class JdbcProjectDao implements ProjectDao {

	private final String PROJECT_SELECT = "SELECT p.project_id, p.name, p.from_date, p.to_date FROM project p";

	private final JdbcTemplate jdbcTemplate;

	public JdbcProjectDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Project getProjectById(int projectId) {
		Project project = null;
		String sql = PROJECT_SELECT +
				" WHERE p.project_id=?";
		try {
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, projectId);
		if (results.next()) {
			project = mapRowToProject(results);
		}
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
		return project;
	}

	@Override
	public List<Project> getProjects() {
		List<Project> allProjects = new ArrayList<>();
		String sql = PROJECT_SELECT;
		try {
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			Project projectResult = mapRowToProject(results);
			allProjects.add(projectResult);
		}
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
		return allProjects;
	}

	@Override
	public Project createProject(Project newProject) {

		String sql = "INSERT INTO project (name, from_date, to_date) " +
				"VALUES (?, ?, ?) RETURNING project_id;" ;
		try {
			int newProjectId = jdbcTemplate.queryForObject(sql, int.class, newProject.getName(), newProject.getFromDate(), newProject.getToDate());

			newProject = getProjectById(newProjectId);

		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
		return newProject;
	}
	
	@Override
	public void linkProjectEmployee(int projectId, int employeeId) {
		String sql = "INSERT INTO project_employee (employee_id, project_id) VALUES (?, ?);";

		try {
			jdbcTemplate.update(sql, employeeId, projectId);
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
	}


	@Override
	public void unlinkProjectEmployee(int projectId, int employeeId) {
		String sql = "DELETE FROM project_employee WHERE employee_id = ? AND project_id = ?;";

		try {
			jdbcTemplate.update(sql, employeeId, projectId);
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
	}

	@Override
	public Project updateProject(Project project) {
		String sql = "UPDATE project SET name = ? , from_date = ?, to_date = ?  " +
				"WHERE project_id = ?;" ;
		try {
			int numRows = jdbcTemplate.update(sql, project.getName(), project.getFromDate(), project.getToDate(), project.getId());

			if (numRows== 0) {
				throw new DaoException("Zero rows affected, expected at least one");
			}
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
		return getProjectById(project.getId());
	}

	@Override
	public int deleteProjectById(int projectId) {
		String deleteProjectEmployeeSql = "DELETE FROM project_employee WHERE project_id = ?; ";
		String deleteProjectSql = "DELETE FROM project WHERE project_id = ?;";

		try {
			jdbcTemplate.update(deleteProjectEmployeeSql, projectId);
			return jdbcTemplate.update(deleteProjectSql, projectId);
		} catch (CannotGetJdbcConnectionException e) {
			throw new DaoException ("Unable to connect to server or database", e);
		} catch (DataIntegrityViolationException e) {
			throw new DaoException ("Data integrity violation", e);
		}
	}
	
	private Project mapRowToProject(SqlRowSet results) {
		Project project = new Project();
		project.setId(results.getInt("project_id"));
		project.setName(results.getString("name"));
		if (results.getDate("from_date") != null) {
			project.setFromDate(results.getDate("from_date").toLocalDate());
		}
		if (results.getDate("to_date") != null) {
			project.setToDate(results.getDate("to_date").toLocalDate());
		}
		return project;
	}

}
