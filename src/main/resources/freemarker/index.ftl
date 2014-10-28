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
          <th>Действия</th>
    </tr>
</thead>

<#list accounts as account>
    <#list account.transactions as transaction>
         <p>
            ${transaction.date}: Операция: ${transaction.operation}, сумма: ${transaction.amount}
         </p>
    </#list>
    <tr>
        <td> ${account._id} </td>
        <td> ${account.fullname} </td>
        <td> ${account.passport} </td>
        <td> ${account.balance} </td>
        <td> ${account.limit} </td>
        <td> ${account.isBlocked?string} </td>
        <td> ${account.cardNumber} </td>


        <td>
            <form method="post" action="/put">
                <input type="hidden" name="id" value="${account._id}">
                <input type="number" name="amount" min="1" max="50000">
                <input type="submit" value="Пополнить счет">
            </form>

             <form method="post" action="/take">
                 <input type="hidden" name="id" value="${account._id}">
                 <input type="number" name="amount" min="1" max="50000">
                 <input type="submit" value="Снять со счета">
             </form>


        </td>

    </tr>

</#list>


</table>

<div id="wrapper" style="max-width: 600px; padding: 8px;">
    <form class="form-horizontal" role="form" method="post" action="/createAccount">
        <div class="form-group">
            <label for="fullname" class="col-sm-2 control-label">ФИО</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="fullname" placeholder="ФИО" name="fullname">
            </div>
        </div>
        <div class="form-group">
            <label for="passport" class="col-sm-2 control-label">Номер Пасспорта</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="passport" name="passport" placeholder="Номер пасспорта">
            </div>
        </div>
        <div class="form-group">
            <label for="cardNumber" class="col-sm-2 control-label">Номер Карты</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="cardNumber" name="cardNumber" placeholder="1000 0000 0000 0000">
            </div>
        </div>
        <div class="form-group">
            <label for="balance" class="col-sm-2 control-label">Баланс</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="balance" name="balance" placeholder="30000">
            </div>
        </div>
        <div class="form-group">
            <label for="limit" class="col-sm-2 control-label">Лимит</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="limit" name="limit" placeholder="50000">
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button type="submit" class="btn btn-default">Добавить</button>
            </div>
        </div>
    </form>
</div>


</body>
</html>

