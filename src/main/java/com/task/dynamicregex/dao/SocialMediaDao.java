package com.task.dynamicregex.dao;

import com.task.dynamicregex.entities.SocialMedia;
import com.task.dynamicregex.utils.PostgreSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SocialMediaDao {

    public List<SocialMedia> findAll() throws SQLException, ClassNotFoundException {
        List<SocialMedia> socialMediaList = new ArrayList<>();
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "SELECT * FROM social_media";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        SocialMedia socialMedia = new SocialMedia(
                                rs.getString("id"),
                                rs.getString("name"));

                        socialMediaList.add(socialMedia);
                    }
                }
            }
        }

        return socialMediaList;
    }

    public int save(SocialMedia socialMedia) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = PostgreSQLConnection.createConnection()) {
            String query = "INSERT INTO social_media(id, name) VALUES(?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, socialMedia.id());
                ps.setString(2, socialMedia.name());

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
