package com.task.dynamicregex.dao;

import com.task.dynamicregex.entities.ArtifactCategory;
import com.task.dynamicregex.entities.SocialMedia;
import com.task.dynamicregex.utils.PostgreSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtifactCategoryDao {

    public List<ArtifactCategory> findAll(String socialMediaId) throws SQLException, ClassNotFoundException {
        List<ArtifactCategory> artifactCategoryList = new ArrayList<>();
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "SELECT ac.id, ac.name, sm.id AS sm_id, sm.name AS sm_name FROM artifact_category ac JOIN social_media sm on sm.id = ac.socmed_id WHERE socmed_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, socialMediaId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        SocialMedia socialMedia = new SocialMedia(
                                rs.getString("sm_id"),
                                rs.getString("sm_name"));

                        ArtifactCategory artifactCategory = new ArtifactCategory(
                                rs.getString("id"),
                                rs.getString("name"),
                                socialMedia);

                        artifactCategoryList.add(artifactCategory);
                    }
                }
            }
        }

        return artifactCategoryList;
    }

    public int save(ArtifactCategory category) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "INSERT INTO artifact_category(id, name, socmed_id) VALUES(?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, category.getId());
                ps.setString(2, category.getName());
                ps.setString(3, category.getSocialMedia().id());

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

    public int update(ArtifactCategory category) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "UPDATE artifact_category SET name = ?, socmed_id = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, category.getName());
                ps.setString(2, category.getSocialMedia().id());
                ps.setString(3, category.getId());

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

    public int delete(ArtifactCategory category) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "DELETE FROM artifact_category WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, category.getId());

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
