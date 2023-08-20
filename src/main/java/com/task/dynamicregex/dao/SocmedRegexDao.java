package com.task.dynamicregex.dao;

import com.task.dynamicregex.entities.ArtifactCategory;
import com.task.dynamicregex.entities.SocialMedia;
import com.task.dynamicregex.entities.SocmedRegex;
import com.task.dynamicregex.utils.PostgreSQLConnection;
import javafx.scene.control.CheckBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SocmedRegexDao {

    public List<SocmedRegex> findSocmedRegex(String socialMediaId) throws SQLException, ClassNotFoundException {
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

    public List<ArtifactCategory> findArtifactCategory(String socialMediaId) throws SQLException, ClassNotFoundException {
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

    public int save(SocmedRegex socmedRegex) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "INSERT INTO socmed_regex(id, field, regex, artifact_category_id) VALUES(?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, socmedRegex.getId());
                ps.setString(2, socmedRegex.getField());
                ps.setString(3, socmedRegex.getRegex());
                ps.setString(4, socmedRegex.getArtifactCategory().id());

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

    public int saveCategory(ArtifactCategory category) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "INSERT INTO artifact_category(id, name, socmed_id) VALUES(?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, category.id());
                ps.setString(2, category.name());
                ps.setString(3, category.socialMedia().id());

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
