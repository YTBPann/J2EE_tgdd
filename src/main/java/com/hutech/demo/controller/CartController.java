<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Giỏ hàng</title>

<style>
body{
font-family:Arial;
background:#ffffff;
margin:0;
}

.container{
max-width:900px;
margin:auto;
padding:20px;
}

table{
width:100%;
border-collapse:collapse;
}

th,td{
border-bottom:1px solid #ddd;
padding:10px;
text-align:left;
}

.btn{
padding:6px 10px;
border:none;
border-radius:4px;
cursor:pointer;
}

.btn-remove{
background:#e53935;
color:white;
}

.btn-back{
background:#333;
color:white;
text-decoration:none;
padding:8px 12px;
display:inline-block;
margin-bottom:20px;
}

.total{
margin-top:20px;
font-size:18px;
font-weight:bold;
}
</style>

</head>

<body>

<div class="container">

<h2>🛒 Giỏ hàng</h2>

<a th:href="@{/products}" class="btn-back">← Tiếp tục mua hàng</a>

<div th:if="${cartItems.isEmpty()}">

<p>Giỏ hàng trống</p>

</div>

<div th:if="${!cartItems.isEmpty()}">

<table>

<thead>
<tr>
<th>Sản phẩm</th>
<th>Giá</th>
<th>Số lượng</th>
<th>Thành tiền</th>
<th></th>
</tr>
</thead>

<tbody>

<tr th:each="item : ${cartItems}">

<td th:text="${item.product.name}"></td>

<td th:text="${item.product.price}"></td>

<td th:text="${item.quantity}"></td>

<td th:text="${item.lineTotal}"></td>

<td>

<form th:action="@{'/cart/remove/' + ${item.product.id}}" method="post">

<button class="btn btn-remove">
Xóa
</button>

</form>

</td>

</tr>

</tbody>

</table>

<div class="total">

Tổng tiền: <span th:text="${grandTotal}"></span>

</div>

</div>

</div>

</body>
</html>