<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.lang.*" %>
<%@ page import="test.ControlRedis" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<html>
<head>
    <title>loading</title>
</head>
<body>
<%
    String userName = ControlRedis.GetUserName();
    Set<Map<String,String>> books;
    books = ControlRedis.ShowAllGoodsByUser("C"+userName);
    for (Map<String,String> map : books) {
        String ISBN=map.get("ISBN");
        String bookNum = map.get("bookNum");
        String BuyNum = map.get("BuyNum");
        String remainNum = map.get("remainNum");
        Integer currentNum = Integer.parseInt(remainNum)-Integer.parseInt(BuyNum);
        String Sell=ControlRedis.GetGoodOwnerUserName(ISBN);
        boolean flag = ControlRedis.ChangeOneGoodNum(Sell,bookNum,Integer.toString(currentNum));
    }
    boolean fflag = ControlRedis.FlushCart(userName);
    response.sendRedirect("cart.jsp");
%>
</body>
</html>
