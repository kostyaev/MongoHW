<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
    <title>Restaurant accounts</title>
</head>
<body>

<h3>
    Here is a list of all accounts in the database
</h3>


<table class="table table-striped">
<thead>
    <tr>
          <th>Номер счета</th>
          <th>ФИО</th>
          <th>Паспорт</th>
          <th>Баланс</th>
          <th>Лимит</th>
          <th>Заблокирован</th>
          <th>Номер карты</th>
    </tr>
</thead>

<#list accounts as account>
    <tr>
        <td> ${account._id} </td>
        <td> ${account.fullname} </td>
        <td> ${account.passport} </td>
        <td> ${account.balance} </td>
        <td> ${account.limit} </td>
        <td> ${account.isBlocked?string} </td>
        <td> ${account.cardNumber} </td>
    </tr>

</#list>


</table>


</body>
</html>

