package com.starfireaviation.groundschool.service;

import com.starfireaviation.groundschool.model.entity.QuestionACS;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class QuestionACSService extends BaseService {

    private final Map<Long, QuestionACS> cache = new HashMap<>();

    @Autowired
    private CourseService courseService;

    public List<QuestionACS> getAll() {
        return new ArrayList<>(cache.values());
    }

    @PostConstruct
    public void loadData() {
        final String query = "SELECT ID, QuestionID, ACSID FROM QuestionsACS";
        for (final String course : courseService.getCourseList()) {
            try (Connection sqlLiteConn = getSQLLiteConnection(course);
                 PreparedStatement ps = sqlLiteConn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final QuestionACS questionACS = new QuestionACS();
                    questionACS.setId(rs.getLong(1));
                    questionACS.setQuestionId(rs.getLong(2));
                    questionACS.setAcsId(rs.getLong(3));
                    cache.put(questionACS.getId(), questionACS);
                }
            } catch (SQLException e) {
                log.error("Error: {}", e.getMessage());
            }
        }
    }
}
