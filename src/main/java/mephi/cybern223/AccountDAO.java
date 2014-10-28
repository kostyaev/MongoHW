package mephi.cybern223;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

import java.util.List;

public class AccountDAO {

    private final DBCollection accountsCollection;

    public AccountDAO(final DB blogDatabase) {
        accountsCollection = blogDatabase.getCollection("accounts");
    }


    public void createTestAccounts() {
        if(accountsCollection.find().size() > 0)
            return ;
        addAccount("Napoleon", "4310 342123", 30000L, 30000L, false, "1000 2000 3000 4001");
        addAccount("Alexander", "4310 342124", 30000L, 30000L, false, "1000 2000 3000 4002");
        addAccount("Lenin", "4310 342125", 30000L, 30000L, false, "1000 2000 3000 4003");
        addAccount("Stalin", "4310 342126", 30000L, 30000L, false, "1000 2000 3000 4004");
        addAccount("Hitler", "4310 342127", 30000L, 30000L, false, "1000 2000 3000 4005");
    }

    public boolean addAccount(String fullname, String passport, Long balance, Long limit, boolean isBlocked, String cardNumber) {
        BasicDBObject account = new BasicDBObject("fullname", fullname)
                .append("passport", passport)
                .append("balance", balance)
                .append("limit", limit)
                .append("isBlocked", isBlocked)
                .append("cardNumber", cardNumber);
        try {
            accountsCollection.insert(account);
            return true;
        } catch (MongoException.DuplicateKey e) {
            System.out.println("Account already in use: " + account);
            return false;
        }
    }

    public List<DBObject> getAllAccounts() {
        return accountsCollection.find().toArray();
    }

    public boolean deleteAccount() {
        return false;
    }


}