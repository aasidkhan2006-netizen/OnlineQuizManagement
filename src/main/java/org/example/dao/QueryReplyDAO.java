package org.example.dao;

import org.example.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QueryReplyDAO {

    // Add admin reply
    public boolean addAdminReply(int queryId, String message) {

        String sql = "INSERT INTO query_replies " +
                "(query_id, sender_role, message) " +
                "VALUES (?, 'ADMIN', ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, queryId);
            ps.setString(2, message);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all replies for a particular query
    public List<String> getReplies(int queryId) {

        List<String> replies = new ArrayList<>();

        String sql = "SELECT sender_role, message, replied_at " +
                "FROM query_replies " +
                "WHERE query_id = ? ORDER BY reply_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, queryId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    replies.add(
                            rs.getString("sender_role") + ": " +
                                    rs.getString("message") + "\n" +
                                    "Date: " + rs.getTimestamp("replied_at")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return replies;
    }
}