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
import static spark.Spark.post;
import static spark.Spark.setPort;

/**
 * Данный класс является стартовой точкой, в нем происходит запуск веб приложения.
 */
public class RestaurantController {

    private final Configuration cfg;
    private final AccountDAO accountDAO;

    /**
     *
     * @param args аргументы командной строки, принимает в качестве аргумента адрес MongoDB,
     *             если адрес не указаан, используется адрес по-умолчанию: mongodb://localhost
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            new RestaurantController("mongodb://localhost");
        }
        else {
            new RestaurantController(args[0]);
        }
    }

    /**
     * Конструктор класса RestaurantController, запускает веб-сервер Spark на 8082 порту, инициализирует обработчики
     * GET/POST запросов
     * @param mongoURIString адрес MongoDB
     * @throws IOException
     */
    public RestaurantController(String mongoURIString) throws IOException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));
        final DB blogDatabase = mongoClient.getDB("restaurant");
        accountDAO = new AccountDAO(blogDatabase);
        cfg = createFreemarkerConfiguration();
        setPort(8082);
        initializeRoutes();
        accountDAO.createTestAccounts();
    }

    /**
     * Абстрактный класс, переопределяет метод handle, позволяя использовать библиотеке Spark шаблонизатор Freemarker
     */
    abstract class FreemarkerBasedRoute extends Route {
        final Template template;

        /**
         *
         * @param path Путь HTTP запроса
         * @param templateName Имя шаблона (шаблона хранятся в папке resources/freemarker
         * @throws IOException
         */
        protected FreemarkerBasedRoute(final String path, final String templateName) throws IOException {
            super(path);
            template = cfg.getTemplate(templateName);
        }

        /**
         * Обработчик запроса использующий метод doHandle, который реализуется позже для каждого отдельного пути
         * @param request HTTP запрос
         * @param response HTTP ответ
         * @return
         */
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

        /**
         * Абстрактный метод, в котором должен быть реализован обработчик HTTP запроса
         * @param request HTTP запрос
         * @param response HTTP ответ
         * @param writer Объект, выполняющий вывод результата
         * @throws IOException
         * @throws TemplateException
         */
        protected abstract void doHandle(final Request request, final Response response, final Writer writer)
                throws IOException, TemplateException;

    }

    /**
     * Инициализация обработчиков HTTP запросов
     * @throws IOException
     */
    private void initializeRoutes() throws IOException {
        // Главная страница
        get(new FreemarkerBasedRoute("/", "index.ftl") {
            @Override
            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                HashMap<String, Object> root = new HashMap<>();
                root.put("what", "restaurant");
                root.put("accounts", accountDAO.getAllAccounts());
                template.process(root, writer);
            }
        });

        // Пополнение счета
        post(new FreemarkerBasedRoute("/put", "index.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String accountId = request.queryParams("id");
                String amount = request.queryParams("amount");
                accountDAO.putMoney(accountId, Long.parseLong(amount));
                response.redirect("/");

            }
        });

        // Снятие со счета
        post(new FreemarkerBasedRoute("/take", "index.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String accountId = request.queryParams("id");
                String amount = request.queryParams("amount");
                accountDAO.takeMoney(accountId, Long.parseLong(amount));
                response.redirect("/");

            }
        });

        // Добавление счета
        post(new FreemarkerBasedRoute("/createAccount", "index.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String fullname = request.queryParams("fullname");
                String passport = request.queryParams("passport");
                String cardNumber = request.queryParams("cardNumber");
                String balance = request.queryParams("balance");
                String limit = request.queryParams("limit");
                accountDAO.addAccount(fullname, passport, Long.parseLong(balance), Long.parseLong(limit), false, cardNumber);
                response.redirect("/");

            }
        });
    }


    /**
     * Вспомогательный метод для конфигурации библиотеки Freemarker, задает путь до html шаблонов
     * @return Конфигурация Freemarker
     */
    private Configuration createFreemarkerConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(RestaurantController.class, "/freemarker");
        return retVal;
    }
}
