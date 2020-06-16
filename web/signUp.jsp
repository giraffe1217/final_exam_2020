<%--
  Created by IntelliJ IDEA.
  User: ASUS
  Date: 2020/6/16
  Time: 17:59
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
        String Account = request.getParameter("Account");
        String Password = request.getParameter("Password");

        //radiobutton 值
        String flag = request.getParameter("UserClass");
        String userType = "";

        // 1 => customer  2=> seller
        switch (flag)
        {
            case "1":
                userType = "customer";
                break;
            case "2":
                userType = "seller";
                break;
        }

        // 判断账户是否存在
        if (ControlRedis.UserExist(Account,userType)){

            // 判断账户与密码是否匹配
            if (ControlRedis.IsRightPassword(Account,Password,userType))
            {
                ControlRedis.RemeberUserName(Account);

                if (userType.equals("customer"))
                {
                    response.sendRedirect("Home.jsp");
                }
                else if (userType.equals("seller")){
                    response.sendRedirect("manage.jsp");
                }
            }
        }

        response.sendRedirect("index.jsp");
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
</body>
</html>
