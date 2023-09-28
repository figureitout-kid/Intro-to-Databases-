package com.techelevator.projects.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

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

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, projectId);
		if (results.next()) {
			project = mapRowToProject(results);
		}

		return project;
	}

	@Override
	public List<Project> getProjects() {
		List<Project> allProjects = new ArrayList<>();
		String sql = PROJECT_SELECT;

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			Project projectResult = mapRowToProject(results);
			allProjects.add(projectResult);
		}

		return allProjects;
	}

	@Override
	public Project createProject(Project newProject) {
		throw new DaoException("createProject() not implemented");
	}
	
	@Override
	public void linkProjectEmployee(int projectId, int employeeId) {
		throw new DaoException("linkProjectEmployee() not implemented");
	}

	@Override
	public void unlinkProjectEmployee(int projectId, int employeeId) {
		throw new DaoException("unlinkProjectEmployee() not implemented");
	}

	@Override
	public Project updateProject(Project project) {
		throw new DaoException("updateProject() not implemented");
	}
	@Override
	public int deleteProjectById(int projectId) {
		throw new DaoException("deleteProjectById() not implemented");
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
