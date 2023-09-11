package com.andreseptian.dao;

import com.andreseptian.entities.ArtifactCategory;
import com.andreseptian.entities.SocialMedia;
import com.andreseptian.entities.SocmedRegex;
import com.andreseptian.utils.PostgreSQLConnection;
import javafx.scene.control.CheckBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SocmedRegexDao {

    public List<SocmedRegex> findAll(String socialMediaId) throws SQLException, ClassNotFoundException {
        List<SocmedRegex> socmedRegexList = new ArrayList<>();
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "SELECT sr.id, sr.field, sr.regex, ac.id AS ac_id, ac.name AS ac_name, sm.id AS sm_id, sm.name AS sm_name FROM socmed_regex sr JOIN artifact_category ac on ac.id = sr.artifact_category_id JOIN social_media sm on sm.id = ac.socmed_id WHERE socmed_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, socialMediaId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        SocialMedia socialMedia = new SocialMedia(
                                rs.getString("sm_id"),
                                rs.getString("sm_name"));

                        ArtifactCategory artifactCategory = new ArtifactCategory(
                                rs.getString("ac_id"),
                                rs.getString("ac_name"),
                                socialMedia);

                        SocmedRegex socmedRegex = new SocmedRegex(
                                rs.getString("id"),
                                rs.getString("field"),
                                rs.getString("regex"),
                                artifactCategory,
                                new CheckBox());

                        socmedRegexList.add(socmedRegex);
                    }
                }
            }
        }

        return socmedRegexList;
    }

    public int save(SocmedRegex socmedRegex) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "INSERT INTO socmed_regex(id, field, regex, artifact_category_id) VALUES(?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, socmedRegex.getId());
                ps.setString(2, socmedRegex.getField());
                ps.setString(3, socmedRegex.getRegex());
                ps.setString(4, socmedRegex.getArtifactCategory().getId());

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

    public int update(SocmedRegex socmedRegex) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "UPDATE socmed_regex SET field = ?, regex = ?, artifact_category_id = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, socmedRegex.getField());
                ps.setString(2, socmedRegex.getRegex());
                ps.setString(3, socmedRegex.getArtifactCategory().getId());
                ps.setString(4, socmedRegex.getId());

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

    public int delete(SocmedRegex socmedRegex) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "DELETE FROM socmed_regex WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, socmedRegex.getId());

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

    public int getSocmedRegexCount(ArtifactCategory artifactCategory) {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "SELECT COUNT(*) FROM socmed_regex WHERE artifact_category_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, artifactCategory.getId());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result = rs.getInt("count");
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}
