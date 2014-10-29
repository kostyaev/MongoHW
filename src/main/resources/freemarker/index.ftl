<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">

    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css">

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
    <title>Restaurant accounts</title>
</head>
<body>

<h3 style="text-align: center;">
    Информация о счетах клиентов ресторана
</h3>

<div id="wrapper" style="max-width: 1200px; padding: 8px; margin-top: 40px; margin-bottom: 80px;">
    <form class="form-horizontal" role="form" method="post" action="/createAccount">
            <div class="col-md-2">
                <label for="fullname">ФИО</label>
                <input type="text" class="form-control" id="fullname" placeholder="ФИО" name="fullname">
            </div>
            <div class="col-md-2">
                <label for="passport">Номер паспорта</label>
                <input type="text" class="form-control" id="passport" name="passport" placeholder="Номер пасспорта">
            </div>
            <div class="col-md-2">

                <label for="cardNumber">Номер Карты</label>
                <input type="text" class="form-control" id="cardNumber" name="cardNumber" placeholder="1000 0000 0000 0000">
            </div>
             <div class="col-md-2">
                <label for="balance">Баланс</label>
                <input type="text" class="form-control" id="balance" name="balance" value="30000">
            </div>

            <div class="col-md-2">
                <label for="limit">Лимит</label>
                <input type="text" class="form-control" id="limit" name="limit" value="50000">
            </div>

            <div class="col-md-2">
                <label for="submit"> </label>
                <input type="submit" class="form-control btn btn-default" value="Добавить">
            </div>
    </form>
</div>


<table class="table table-hover">
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
    <tr <#if account.isBlocked> class="danger" <#else> class="success" </#if> >
        <td> ${account._id} </td>
        <td> ${account.fullname} </td>
        <td> ${account.passport} </td>
        <td> ${account.balance} </td>
        <td> ${account.limit} </td>
        <td> ${account.isBlocked?string} </td>
        <td> ${account.cardNumber} </td>

        <td>
            <form method="post" action="/put" style="padding: 2px;">
                <input type="hidden" name="id" value="${account._id}">
                <input type="number" name="amount" min="1" max="50000">
                <input type="submit" value="Пополнить счет">
            </form>

            <form method="post" action="/take" style="padding: 2px;">
                <input type="hidden" name="id" value="${account._id}">
                <input type="number" name="amount" min="1" max="50000">
                <input type="submit" value="Снять со счета">
            </form>

            <div class="span1" style="padding: 2px;">
                <button class="btn btn-success" data-toggle="collapse" data-target="#transaction${account._id}">Транзакции по счету</button>
            </div>
        </td>
    </tr>
    <tr id="transaction${account._id}" class="collapse">
        <td colspan="8">
            <div>
            <#if account.transactions?size = 0 >
                По данному счету отсутсвуют транзакции.
            </#if>
            <#list account.transactions as transaction>
                <p>
                ${transaction.date}: Операция: ${transaction.operation}, сумма: ${transaction.amount}
                </p>
            </#list>
            </div>
        </td>
    </tr>

</#list>

</table>

</body>
</html>

