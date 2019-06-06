package eu.arima.hbasephoenixthinclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HbaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HbaseController.class);

    private static final String VARCHAR_TABLE = "DEMO_TABLE";

    private static final int LIMIT = 1000;

    private final String url;

    public HbaseController(@Value("${url}") String url) {
        this.url = url;
    }

    @ResponseBody
    @GetMapping("/create")
    public String create() throws Exception {

        Class.forName("org.apache.phoenix.queryserver.client.Driver");

        LOGGER.info("Connecting to {}", this.url);

        try (Connection conn = DriverManager.getConnection(this.url)) {
            conn.setAutoCommit(false);
    
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("drop table if exists " + VARCHAR_TABLE);
                stmt.execute("create table " + VARCHAR_TABLE + " (id integer not null primary key, textvalue varchar(50)  )");
            }
            
            conn.commit();

            LOGGER.info("New table {} created", VARCHAR_TABLE);
    
            try (PreparedStatement stmt = conn.prepareStatement("UPSERT INTO " + VARCHAR_TABLE + " VALUES(?, ?)")) {
                for (int i = 0; i < LIMIT; i ++) {
                    stmt.setInt(1, i);
                    stmt.setString(2, "VALUE_" + i);
                    stmt.execute();
                }
            }

            conn.commit();

            LOGGER.info("{} rows inserted", LIMIT);
        }

        return "Tables created and populated";
        
    }

    @ResponseBody
    @GetMapping("/read/{id}")
    public String read(@PathVariable long id) throws Exception {

        LOGGER.info("Trying to read row with id {}", id);

        if (id > LIMIT) {
            throw new IllegalArgumentException("id bigger than limit (" + LIMIT + ")");
        }

        try (Connection conn = DriverManager.getConnection(this.url)) {
            conn.setAutoCommit(true);
            
            String sqlStmt = "SELECT textvalue FROM " + VARCHAR_TABLE + " WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sqlStmt);
            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();

            String value = null;

            while (resultSet.next()) {
                value = resultSet.getString("textvalue");
            }

            resultSet.close();
            stmt.close();

            LOGGER.info("Read value: {}", value);

            return value;
        }

    }
    
}