package mephi.cybern223;


import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import static spark.Spark.get;
import static spark.Spark.setPort;

/**
 * This class encapsulates the controllers for the web application. It delegates all interaction with MongoDB
 * to Data Access Objects (DAOs).
 * <p/>
 * It is also the entry point into the web application.
 */
public class RestaurantController {

    private final Configuration cfg;
    private final AccountDAO accountDAO;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            new RestaurantController("mongodb://localhost");
        }
        else {
            new RestaurantController(args[0]);
        }
    }

    public RestaurantController(String mongoURIString) throws IOException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));
        final DB blogDatabase = mongoClient.getDB("restaurant");
        accountDAO = new AccountDAO(blogDatabase);
        cfg = createFreemarkerConfiguration();
        setPort(8082);
        initializeRoutes();
        accountDAO.createTestAccounts();
    }

    abstract class FreemarkerBasedRoute extends Route {
        final Template template;


        protected FreemarkerBasedRoute(final String path, final String templateName) throws IOException {
            super(path);
            template = cfg.getTemplate(templateName);
        }

        @Override
        public Object handle(Request request, Response response) {
            StringWriter writer = new StringWriter();
            try {
                doHandle(request, response, writer);
            } catch (Exception e) {
                e.printStackTrace();
                response.redirect("/internal_error");
            }
            return writer;
        }

        protected abstract void doHandle(final Request request, final Response response, final Writer writer)
                throws IOException, TemplateException;

    }

    private void initializeRoutes() throws IOException {
        // this is the blog home page
        get(new FreemarkerBasedRoute("/", "index.ftl") {
            @Override
            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                HashMap<String, Object> root = new HashMap<>();

                root.put("what", "restaurant");
                root.put("accounts", accountDAO.getAllAccounts());

                template.process(root, writer);
            }
        });

    }



    private Configuration createFreemarkerConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(RestaurantController.class, "/freemarker");
        return retVal;
    }
}
