module guiced.swagger.ui.tests {
    requires com.guicedee.swaggerui;
    requires com.guicedee.client;
    requires com.guicedee.guicedinjection;
    requires org.junit.jupiter.api;
    requires java.net.http;

    opens com.guicedee.servlets.swaggerui.tests to com.google.guice,com.fasterxml.jackson.databind,org.junit.platform.commons;
}