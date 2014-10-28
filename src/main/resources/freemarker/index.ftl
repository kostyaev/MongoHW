<!DOCTYPE html>
<html>
<head>
    <title>Restaurant accounts</title>
</head>
<body>

<h3>
    Here is a list of all accounts in the database
</h3>
<ul>

<#list accounts as account>
    <li> ${account.fullname} </li>
</#list>

</ul>

</body>
</html>

