/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.auth;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import javax.naming.InitialContext;

/**
 * A login service for connecting to SQL based databases via JDBC
 *
 * @author rbair
 */
public class JDBCLoginService extends LoginService {
    /**
     * The connection to the database
     */
    private Connection conn;
    /**
     * If used, defines the JNDI context from which to get a connection to
     * the data base
     */
    private String jndiContext;
    /**
     * When using the DriverManager to connect to the database, this specifies
     * any additional properties to use when connecting.
     */
    private Properties properties;
    
    /**
     * Create a new JDBCLoginService and initializes it to connect to a
     * database using the given params.
     * @param driver
     * @param url
     */
    public JDBCLoginService(String driver, String url) {
        super(url);
        try {
            Class.forName(driver);
        } catch (Exception e) {
            System.err.println("WARN: The driver passed to the " +
                    "JDBCLoginService constructor could not be loaded. " +
                    "This may be due to the driver not being on the classpath");
            e.printStackTrace();
        }
        this.setUrl(url);
    }
    
    /**
     * Create a new JDBCLoginService and initializes it to connect to a
     * database using the given params.
     * @param driver
     * @param url
     * @param props
     */
    public JDBCLoginService(String driver, String url, Properties props) {
        super(url);
        try {
            Class.forName(driver);
        } catch (Exception e) {
            System.err.println("WARN: The driver passed to the " +
                    "JDBCLoginService constructor could not be loaded. " +
                    "This may be due to the driver not being on the classpath");
            e.printStackTrace();
        }
        this.setUrl(url);
        this.setProperties(props);
    }
    
    /**
     * Create a new JDBCLoginService and initializes it to connect to a
     * database using the given params.
     * @param jndiContext
     */
    public JDBCLoginService(String jndiContext) {
        super(jndiContext);
        this.jndiContext = jndiContext;
    }
    
    /**
     * @return the JDBC connection url
     */
    public String getUrl() {
        return getServer();
    }

    /**
     * @param url set the JDBC connection url
     */
    public void setUrl(String url) {
        setServer(url);
    }

    /**
     * @return JDBC connection properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @param properties miscellaneous JDBC properties to use when connecting
     *        to the database via the JDBC driver
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
    public Connection getConnection() {
        return conn;
    }
    
    /**
     * Attempts to get a JDBC Connection from a JNDI javax.sql.DataSource, using
     * that connection for interacting with the database.
     * @throws Exception
     */
    private void connectByJNDI(String userName, char[] password) throws Exception {
        InitialContext ctx = new InitialContext();
        javax.sql.DataSource ds = (javax.sql.DataSource)ctx.lookup(jndiContext);
        conn = ds.getConnection(userName, new String(password));
        conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
    }
    
    /**
     * Attempts to get a JDBC Connection from a DriverManager. If properties
     * is not null, it tries to connect with those properties. If that fails,
     * it then attempts to connect with a user name and password. If that fails,
     * it attempts to connect without any credentials at all.
     * <p>
     * If, on the other hand, properties is null, it first attempts to connect
     * with a username and password. Failing that, it tries to connect without
     * any credentials at all.
     * @throws Exception
     */
    private void connectByDriverManager(String userName, char[] password) throws Exception {
        if (getProperties() != null) {
            try {
                conn = DriverManager.getConnection(getUrl(), getProperties());
                conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (Exception e) {
                try {
                    conn = DriverManager.getConnection(getUrl(), userName, new String(password));
                    conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                } catch (Exception ex) {
                    conn = DriverManager.getConnection(getUrl());
                    conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                }
            }
        } else {
            try {
                conn = DriverManager.getConnection(getUrl(), userName, new String(password));
            } catch (Exception e) {
                e.printStackTrace();
                //try to connect without using the userName and password
                conn = DriverManager.getConnection(getUrl());

            }
        }
    }

    /**
     * @param name	user name
     * @param password	user password
     * @param server Must be either a valid JDBC URL for the type of JDBC driver you are using,
     * or must be a valid JNDIContext from which to get the database connection
     */
    public boolean authenticate(String name, char[] password, String server) throws IOException {
        //try to form a connection. If it works, conn will not be null
        //if the jndiContext is not null, then try to get the DataSource to use
        //from jndi
        if (jndiContext != null) {
            try {
                connectByJNDI(name, password);
            } catch (Exception e) {
                try {
                    connectByDriverManager(name, password);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //login failed
                    return false;
                }
            }
        } else {
            try {
                connectByDriverManager(name, password);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
