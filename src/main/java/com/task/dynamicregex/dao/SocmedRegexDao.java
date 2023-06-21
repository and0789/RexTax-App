package com.task.dynamicregex.dao;

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

    public List<SocmedRegex> findBySocialMediaId(String socialMediaId) throws SQLException, ClassNotFoundException {
        List<SocmedRegex> socmedRegexList = new ArrayList<>();
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "SELECT sr.id, sr.field, sr.regex, sm.id AS sm_id, sm.name AS sm_name FROM socmed_regex sr JOIN social_media sm on sm.id = sr.socmed_id WHERE socmed_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, socialMediaId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        SocialMedia socialMedia = new SocialMedia(
                                rs.getString("sm_id"),
                                rs.getString("sm_name"));

                        SocmedRegex socmedRegex = new SocmedRegex(
                                rs.getString("id"),
                                rs.getString("field"),
                                rs.getString("regex"),
                                socialMedia,
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
            String query = "INSERT INTO socmed_regex(id, field, regex, socmed_id) VALUES(?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, socmedRegex.getId());
                ps.setString(2, socmedRegex.getField());
                ps.setString(3, socmedRegex.getRegex());
                ps.setString(4, socmedRegex.getSocialMedia().getId());

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
