/*
 * Copyright 2013,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.integrationtest.sampleproject.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.unitils.core.UnitilsException;
import org.unitils.integrationtest.sampleproject.dao.PersonDao;
import org.unitils.integrationtest.sampleproject.model.Person;
import org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils;

public class JdbcPersonDao implements PersonDao {

	private DataSource dataSource;
	
	public JdbcPersonDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Person findById(Long id) {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			st = conn.prepareStatement("select id, name from person where id = ?");
			st.setLong(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				return new Person(rs.getLong("id"), rs.getString("name"));
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new UnitilsException(e);
		} finally {
			DbUtils.closeQuietly(conn, st, rs);
		}
	}

	public void persist(Person person) {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = dataSource.getConnection();
			st = conn.prepareStatement("insert into person(id, name) values (?, ?)");
			st.setLong(1, person.getId());
			st.setString(2, person.getName());
			st.execute();
		} catch (SQLException e) {
			throw new UnitilsException(e);
		} finally {
			DbUtils.closeQuietly(conn, st, null);
		}
	}

}
