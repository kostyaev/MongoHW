package mephi.cybern223;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountDAO {

    private final DBCollection accountsCollection;

    public AccountDAO(final DB blogDatabase) {
        accountsCollection = blogDatabase.getCollection("accounts");
    }

    public void createTestAccounts() {
        accountsCollection.drop();
        if(accountsCollection.find().size() > 0)
            return ;
        addAccount("Соколов Никита Андреевич", "4310 342123", 30000L, 30000L, false, "1000 2000 3000 4001");
        addAccount("Макаров Иван Васильевич", "4310 342124", 30000L, 30000L, false, "1000 2000 3000 4002");
        addAccount("Ворожцов Андрей Петрович", "4310 342125", 30000L, 30000L, false, "1000 2000 3000 4003");
        addAccount("Касаткин Михаил Алексеевич", "4310 342126", 30000L, 30000L, false, "1000 2000 3000 4004");
        addAccount("Блинов Владимир Андреевич", "4310 342127", 30000L, 30000L, false, "1000 2000 3000 4005");
    }

    public boolean addAccount(String fullname, String passport, Long balance, Long limit, Boolean isBlocked, String cardNumber) {
        BasicDBObject account = new BasicDBObject("fullname", fullname)
                .append("passport", passport)
                .append("balance", balance)
                .append("limit", limit)
                .append("isBlocked", isBlocked)
                .append("cardNumber", cardNumber)
                .append("transactions", new ArrayList<DBObject>());
        try {
            accountsCollection.insert(account);
            return true;
        } catch (MongoException.DuplicateKey e) {
            System.out.println("Account already in use: " + account);
            return false;
        }
    }

    public boolean takeMoney(String accountId, Long amount) {
        System.out.println("take money, amount: " + amount);
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(accountId));
        DBObject account = accountsCollection.findOne(query);
        System.out.println("take money from account: " + account);
        Long result = (Long)account.get("balance") - amount;
        BasicDBObject transaction = new BasicDBObject("date", getDate())
                .append("amount", amount);
        if ( result < -1000 || (boolean) account.get("isBlocked")) {
            transaction.append("operation", "Попытка снятия со счета, не хватает средств");
            accountsCollection.update(query, new BasicDBObject("$push", new BasicDBObject("transactions", transaction)));
            return false;
        }
        if (result <= 0)
            accountsCollection.update(query, new BasicDBObject("$set", new BasicDBObject("isBlocked", true)));

        transaction.append("operation", "Снятие со счета");
        accountsCollection.update(query, new BasicDBObject("$push", new BasicDBObject("transactions", transaction)));
        accountsCollection.update(query, new BasicDBObject("$set", new BasicDBObject("balance", result)));

        return true;
    }

    public void putMoney(String accountId, Long amount) {
        System.out.println("put money, amount: " + amount);
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(accountId));
        DBObject account = accountsCollection.findOne(query);
        Long result = (Long)account.get("balance") + amount;
        BasicDBObject transaction = new BasicDBObject("date", getDate())
                .append("operation", "Пополнение счета")
                .append("amount", amount);
        if (result > 0)
            accountsCollection.update(query, new BasicDBObject("$set", new BasicDBObject("isBlocked", false)));
        accountsCollection.update(query, new BasicDBObject("$set", new BasicDBObject("balance", result)));
        accountsCollection.update(query, new BasicDBObject("$push", new BasicDBObject("transactions", transaction)));
    }

    public List<DBObject> getAllAccounts() {
        return accountsCollection.find().sort(new BasicDBObject("_id", -1)).toArray();
    }

    public boolean deleteAccount() {
        return false;
    }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date now = new Date();
        return sdf.format(now);
    }

}
