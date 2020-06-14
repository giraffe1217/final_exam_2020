<%--
  Created by IntelliJ IDEA.
  User: ASUS
  Date: 2019/12/5
  Time: 21:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="java.lang.*" %>
<%@ page import="test.ControlRedis" %>
<%@ page import="redis.clients.jedis.JedisPool" %>
<%@ page import="redis.clients.jedis.Jedis" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>choosePanel</title>
</head>
<body>
<%
    try {
        String value = ControlRedis.SayHelloWorld();
        out.print(value);
    }
    catch (Exception e) {
        e.printStackTrace();
        out.print(e);
    }
%>
</body>
</html>


