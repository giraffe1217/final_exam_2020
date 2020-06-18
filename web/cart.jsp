<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ page import="java.lang.*" %>
<%@ page import="test.ControlRedis" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<html lang="zh-CN">
<head>

    <script src="jquery-3.5.1.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>叮当淘，一个专卖各类书籍的网站</title>
    <link rel="stylesheet" href="./bootstrap-4.5.0-dist/css/bootstrap.min.css">
    <style>
        #form
        {
            text-align:center;
        }
        body
        {
            background-image:url('book.jpg');
            background-color:#cccccc;
            height:100%;

            width:100%;

            overflow-x: auto;
            overflow-y: auto;

            background-size:cover;
        }
        h1{
            text-align:center;
        }
        .form-bg{
            background: #00b4ef;
        }
        .form-horizontal{
            background: #fff;
            padding-bottom: 40px;
            border-radius: 15px;
            text-align: center;
        }
        .form-horizontal .heading{
            display: block;
            font-size: 35px;
            font-weight: 700;
            padding: 35px 0;
            border-bottom: 1px solid #f0f0f0;
            margin-bottom: 30px;
        }
        .form-horizontal .form-group{
            padding: 0 40px;
            margin: 0 0 25px 0;
            position: relative;
        }
        .form-horizontal .form-control{
            background: #f0f0f0;
            border: none;
            border-radius: 20px;
            box-shadow: none;
            padding: 0 20px 0 45px;
            height: 40px;
            transition: all 0.3s ease 0s;
        }
        .form-horizontal .form-control:focus{
            background: #e0e0e0;
            box-shadow: none;
            outline: 0 none;
        }
        .form-horizontal .form-group i{
            position: absolute;
            top: 12px;
            left: 60px;
            font-size: 17px;
            color: #c8c8c9;
            transition : all 0.5s ease 0s;
        }
        .form-horizontal .form-control:focus + i{
            color: #00b5ef;
        }
        .form-horizontal .fa-question-circle{
            display: inline-block;
            position: absolute;
            top: 12px;
            right: 60px;
            font-size: 20px;
            color: #808081;
            transition: all 0.5s ease 0s;
        }
        .form-horizontal .fa-question-circle:hover{
            color: #000;
        }
        .form-horizontal .main-checkbox{
            float: left;
            width: 20px;
            height: 20px;
            background: #11a3fc;
            border-radius: 50%;
            position: relative;
            margin: 5px 0 0 5px;
            border: 1px solid #11a3fc;
        }
        .form-horizontal .main-checkbox label{
            width: 20px;
            height: 20px;
            position: absolute;
            top: 0;
            left: 0;
            cursor: pointer;
        }
        .form-horizontal .main-checkbox label:after{
            content: "";
            width: 10px;
            height: 5px;
            position: absolute;
            top: 5px;
            left: 4px;
            border: 3px solid #fff;
            border-top: none;
            border-right: none;
            background: transparent;
            opacity: 0;
            -webkit-transform: rotate(-45deg);
            transform: rotate(-45deg);
        }
        .form-horizontal .main-checkbox input[type=checkbox]{
            visibility: hidden;
        }
        .form-horizontal .main-checkbox input[type=checkbox]:checked + label:after{
            opacity: 1;
        }
        .form-horizontal .text{
            float: left;
            margin-left: 7px;
            line-height: 20px;
            padding-top: 5px;
            text-transform: capitalize;
        }
        .form-horizontal .btn{
            float: right;
            font-size: 14px;
            color: #fff;
            background: #00b4ef;
            border-radius: 30px;
            padding: 10px 25px;
            border: none;
            text-transform: capitalize;
            transition: all 0.5s ease 0s;
        }
        @media only screen and (max-width: 1080px){
            .form-horizontal .form-group{
                padding: 0 25px;
            }
            .form-horizontal .form-group i{
                left: 40px;
            }
            .form-horizontal .btn{
                padding: 10px 20px;
            }
        }
    </style>
</head>
<body>

<div class="form-horizontal" style="margin-bottom:0; opacity:0.7;">
    <button style="float:left;" class="btn btn-default" onclick="window.location='Home.jsp';">返回</button>
    <form class="form-horizontal" action="CheckOut.jsp" method="post" >
        <h1>购物车</h1>
        <button type="submit" style="float:right;" class="btn btn-default" onclick="showDialog()">结算</button>
        <div>
            <table class="table" border="1" align="center" width="500">
                <tr align="center">
                    <th>书名</th>
                    <th>编号</th>
                    <th>类型</th>
                    <th>价格</th>
                    <th>ISBN</th>
                    <th>摘要</th>
                    <th>出售种类</th>
                    <th>数量</th>
                </tr>
                    <%
            String userName = ControlRedis.GetUserName();
            Set<Map<String,String>> books = ControlRedis.ShowAllGoodsByUser("C"+userName);
            for (Map<String,String> map : books) {
                    %>
                    <tr align="center">
                        <td><%=map.get("bookName") %></td>
                        <td><%=map.get("bookNum") %></td>
                        <td><%=map.get("bookType") %></td>
                        <td><%=map.get("price") %></td>
                        <td><%=map.get("ISBN") %></td>
                        <td><%=map.get("summary") %></td>
                        <td><%=map.get("sellType") %></td>
                        <td><input type="text" name="BuyNum" value="<%=map.get("BuyNum") %>"></td>
                    </tr>

                    <%
            }
        %>
            </table>
        </div>


    </form>
</div>
<script>
    function showDialog(){
        alert("支付成功！");
    }
</script>
</body>
</html>
