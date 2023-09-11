package com.andreseptian.dao;

import com.andreseptian.entities.CaseIdentity;
import com.andreseptian.utils.PostgreSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CaseIdentityDao {

    public int save(CaseIdentity caseIdentity) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "INSERT INTO case_identity(id, investigators_name, handled_case, case_description) VALUES(?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, String.valueOf(caseIdentity.getId()));
                ps.setString(2, caseIdentity.getInvestigatorsName());
                ps.setString(3, caseIdentity.getHandledCase());
                ps.setString(4, caseIdentity.getCaseDescription());

                if (ps.executeUpdate() != 0) {
                    connection.commit();
                    result = 1;
                } else {
                    connection.rollback();
                }
            }
        }

        return result;
    }

    public int update(CaseIdentity caseIdentity) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "UPDATE case_identity SET investigators_name = ?, handled_case = ?, case_description = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, caseIdentity.getInvestigatorsName());
                ps.setString(2, caseIdentity.getHandledCase());
                ps.setString(3, caseIdentity.getCaseDescription());
                ps.setString(4, String.valueOf(caseIdentity.getId()));

                if (ps.executeUpdate() != 0) {
                    connection.commit();
                    result = 1;
                } else {
                    connection.rollback();
                }
            }
        }

        return result;
    }

}
