package com.surveyform.surveyform;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SurveyController {

    String jdbcurl = "jdbc:mysql://127.0.0.1:3306/formsurvey";
    int loggedinUserName;
    int formId;

    @GetMapping("/")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String submit, @RequestParam("username") String username,
            @RequestParam("password") String password) throws Exception {
        System.out.println(username + " " + password + " " + submit);
        if (submit.equals("Signup")) {
            return "signup";
        }

        Connection connection;
        ResultSet resultSet;
        try {
            connection = DriverManager.getConnection(jdbcurl, "ThamizhanM3", "ThamizhanM3@");
            String sql = "SELECT userName, password, userType, idUser FROM user WHERE userName = ?";
            PreparedStatement pStatement = connection.prepareStatement(sql);
            pStatement.setString(1, username);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                String dbUserName = resultSet.getString("userName");
                String dbPassword = resultSet.getString("password");
                String dbUserType = resultSet.getString("userType");
                int dbUserId = resultSet.getInt("idUser");
                if (dbPassword.equals(password)) {
                    if (dbUserType.equals("Creator")) {
                        this.loggedinUserName = dbUserId;
                        return "creator";
                    }
                    this.loggedinUserName = dbUserId;
                    return "user";
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        if (username.equals("ThamizhanM3") && password.equals("ThamizhanM3@")) {
            return "survey";
        }
        return "login";
    }

    @PostMapping("/signup")
    public String getSignupPage(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
            @RequestParam("userType") String userType, @RequestParam("userName") String userName,
            @RequestParam("password") String password, @RequestParam String submit) throws Exception {
        Connection connection;
        try {
            connection = DriverManager.getConnection(jdbcurl, "ThamizhanM3", "ThamizhanM3@");

            int newId = 1;

            String lasIdSql = "SELECT MAX(idUser) AS lastId FROM user";
            PreparedStatement idGet = connection.prepareStatement(lasIdSql);
            ResultSet idSet = idGet.executeQuery(lasIdSql);
            if (idSet.next()) {
                if (idSet.getString("lastId") != null) {
                    newId = Integer.parseInt(idSet.getString("lastId")) + 1;
                }
            }

            String sql = "INSERT INTO user (idUser, firstName, lastName, userName, userType, password) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement pStatement = connection.prepareStatement(sql);
            pStatement.setString(1, String.valueOf(newId));
            pStatement.setString(2, firstName);
            pStatement.setString(3, lastName);
            pStatement.setString(4, userName);
            pStatement.setString(5, userType);
            pStatement.setString(6, password);
            pStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
        return "login";
    }

    @GetMapping("/qn")
    public String questionPage() {
        return "questionCreate";
    }

    @PostMapping("/question")
    public String getQuestion(@RequestParam("question") String question,
            @RequestParam("questionType") String questionType, @RequestParam("count") String count) {

        Connection connection;

        try {
            connection = DriverManager.getConnection(jdbcurl, "ThamizhanM3", "ThamizhanM3@");

            int newId = 1;

            String lasIdSql = "SELECT MAX(idsurveryquestions) AS lastId FROM surveryquestions";
            PreparedStatement idGet = connection.prepareStatement(lasIdSql);
            ResultSet idSet = idGet.executeQuery(lasIdSql);
            if (idSet.next()) {
                if (idSet.getString("lastId") != null) {
                    System.out.println("l");
                    newId = Integer.parseInt(idSet.getString("lastId")) + 1;
                }
            }
            System.out.println(newId);

            String sql = "INSERT INTO surveryquestions (idsurveryquestions, surveyFormId, question, answerType, count) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement pStatement = connection.prepareStatement(sql);

            pStatement.setString(1, String.valueOf(newId));
            pStatement.setInt(2, this.formId);
            pStatement.setString(3, question);
            pStatement.setString(4, questionType);
            pStatement.setInt(5, Integer.parseInt(count));
            pStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(question + " " + questionType + " " + count);
        return "formCreate";
    }

    @GetMapping("/frm")
    public String form() {
        return "formCreate";
    }

    @PostMapping("/form")
    public String createForm(@RequestParam("formName") String formName, @RequestParam String submit) {
        Connection connection;
        System.out.println(submit);
        try {
            if (submit.equals("Submit")) {
                connection = DriverManager.getConnection(jdbcurl, "ThamizhanM3", "ThamizhanM3@");

                int newId = 1;

                String lasIdSql = "SELECT MAX(idSurveryForms) AS lastId FROM surveryforms";
                PreparedStatement idGet = connection.prepareStatement(lasIdSql);
                ResultSet idSet = idGet.executeQuery(lasIdSql);
                if (idSet.next()) {
                    if (idSet.getString("lastId") != null) {
                        newId = Integer.parseInt(idSet.getString("lastId")) + 1;
                    }
                }

                String sql = "INSERT INTO surveryforms (idSurveryForms, formName, userId) VALUES(?, ?, ?)";
                PreparedStatement pStatement = connection.prepareStatement(sql);
                System.out.println(newId);
                pStatement.setInt(1, newId);
                pStatement.setString(2, formName);
                pStatement.setInt(3, this.loggedinUserName);
                pStatement.executeUpdate();
                return "creator";
            }

            connection = DriverManager.getConnection(jdbcurl, "ThamizhanM3", "ThamizhanM3@");

            int newId = 1;

            String lasIdSql = "SELECT MAX(idSurveryForms) AS lastId FROM surveryforms";
            PreparedStatement idGet = connection.prepareStatement(lasIdSql);
            ResultSet idSet = idGet.executeQuery(lasIdSql);
            if (idSet.next()) {
                if (idSet.getString("lastId") != null) {
                    newId = Integer.parseInt(idSet.getString("lastId")) + 1;
                }
            }
            this.formId = newId;
            return "questionCreate";
        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }

    @PostMapping("/creator")
    public String goFromCreator(@RequestParam String submit) {
        System.out.println(submit);
        if (submit.equals("create")) {
            return "formCreate";
        }
        return "";
    }
}
