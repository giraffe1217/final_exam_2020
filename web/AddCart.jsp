<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.lang.*" %>
<%@ page import="test.ControlRedis" %>
<html>
<head>
    <title>loading...</title>
</head>
<body>
<%
    try{
        String bookName = request.getParameter("bookName");
        String bookNum = request.getParameter("bookNum");
        String bookType = request.getParameter("bookType");
        String price = request.getParameter("price");
        String ISBN = request.getParameter("ISBN");
        String summary = request.getParameter("summary");
        String sellType = request.getParameter("sellType");
        String BuyNum = request.getParameter("BuyNum");
        String remainNum = request.getParameter("remainNum");
        String Account = ControlRedis.GetUserName();
        boolean flag = ControlRedis.AddShoppingCart(Account,bookName,bookNum,bookType,price,ISBN,summary,sellType,BuyNum,remainNum);
        response.sendRedirect("Home.jsp");
    }catch (Exception e) {
        e.printStackTrace();
        System.out.println(e);
    }

%>
</body>
</html>
