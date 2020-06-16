<%--
  Created by IntelliJ IDEA.
  User: ASUS
  Date: 2020/6/17
  Time: 0:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.lang.*" %>
<%@ page import="test.ControlRedis" %>
<html>
<head>
    <title>loading...</title>
</head>
<body>
<%
    try {
        String bookNum = request.getParameter("id");
        String userName = ControlRedis.GetUserName();

        ControlRedis.DeleteAllGood(userName,bookNum);
        response.sendRedirect("manage.jsp");
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
</body>
</html>
