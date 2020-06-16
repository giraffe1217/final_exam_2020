<%--
  Created by IntelliJ IDEA.
  User: ASUS
  Date: 2020/6/16
  Time: 16:37
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
        // TODO 测试方便  每一次开始都清空 redis  日后删掉
        ControlRedis.ClearJedis();

        String Account = request.getParameter("Account");
        String Password = request.getParameter("Password");

        //radiobutton 值
        String flag = request.getParameter("UserClass");

        // 1 => customer  2=> seller
        switch (flag)
        {
            //这里我没有考虑注册时的用户名重复 懒得写了
            case "1":
                ControlRedis.CustomerSignIn(Account,Password);
                break;
            case "2":
                ControlRedis.SellerSignIn(Account,Password);
                break;
        }

        response.sendRedirect("index.jsp");
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
</body>
</html>
