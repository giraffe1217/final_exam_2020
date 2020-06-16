<%--
  Created by IntelliJ IDEA.
  User: ASUS
  Date: 2020/6/16
  Time: 21:34
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
        String Account = ControlRedis.GetUserName();
        String bookName = request.getParameter("bookName");
        String bookNum = request.getParameter("bookNum");

        //书籍类型分为  politics  economics  literature  science  sport  military
        String bookType = request.getParameter("bookType");

        String price = request.getParameter("price");
        String ISBN = request.getParameter("ISBN");
        String substract = request.getParameter("substract");
        String sellingType = request.getParameter("sellingType");
        String remainingNum = request.getParameter("remainingNum");

        // 只有全部填写完成才能通过
        if (Account.equals("") || bookName.equals("") || bookNum.equals("") || bookType.equals("") || price.equals("")
                || ISBN.equals("") || substract.equals("") || sellingType.equals("") || remainingNum.equals(""))
        {
            response.sendRedirect("Add.jsp");
        }

        //(用户名，书名，编号，类型，价格，ISBN，摘要，出售种类，剩余数量)
        boolean flag = ControlRedis.StoreOneGood(Account,bookName,bookNum,bookType,price,ISBN,substract,sellingType,remainingNum);

        if(flag)
        {
            response.sendRedirect("manage.jsp");
        }
        else{
            response.sendRedirect("Add.jsp");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
%>
</body>
</html>
