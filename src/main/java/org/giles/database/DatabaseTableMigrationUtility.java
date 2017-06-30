package org.giles.database;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;

public class DatabaseTableMigrationUtility {

   public static void main(String args[]) {
      try {
         final String TABLE_NAME = "profile";
         Class.forName("oracle.jdbc.driver.OracleDriver");
         String username = "username";
         String password = "password";
         String sourceDBName = "xe";
         String sourceDBHost = "localhost";
         String destinationDBName = "xe";
         String destinationHostName = "localhost";
         Connection sourceDBConnection = DriverManager.getConnection(String.format("jdbc:oracle:thin:@%s:1521:%s", sourceDBHost, sourceDBName), username, password);
         Connection destinationDBConnection = DriverManager.getConnection(String.format("jdbc:oracle:thin:@%s:1521:%s", destinationHostName, destinationDBName), username, password);

         ResultSet resultSetFromSourceDB = sourceDBConnection.createStatement().executeQuery(String.format("select * from %s", TABLE_NAME));
         PrintWriter writer = new PrintWriter(new FileWriter("c:/result.csv"));
         while (resultSetFromSourceDB.next()) {
            final StringBuilder builder = new StringBuilder();
            Clob myClob = resultSetFromSourceDB.getClob("voters");
            String data = myClob.getSubString(1, (int) myClob.length());
            builder.append(resultSetFromSourceDB.getObject("Profile_name")).append(",");
            builder.append(resultSetFromSourceDB.getObject("description")).append(",");
            builder.append(data.replaceAll("[\n\r]","")).append("\n");
            writer.write(builder.toString());


            //copy table data into another DB
           /* String insertSql = String.format("insert into %s values(?,?,?)", TABLE_NAME);
            final PreparedStatement statement = destinationDBConnection.prepareStatement(insertSql);
            statement.setString(1, resultSetFromSourceDB.getObject("Profile_name").toString());
            statement.setString(2, resultSetFromSourceDB.getObject("description").toString());
            statement.setClob(3, myClob);
            statement.executeUpdate();*/
         }
         writer.close();
         sourceDBConnection.close();
         destinationDBConnection.close();
      }
      catch (Exception e) {
         System.out.println(e.getMessage());
      }
   }
}
