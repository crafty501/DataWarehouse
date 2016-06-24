import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import data.DB2ConnectionManager;

/**
 * Implement the ETL process as a Java application that extracts data from both data sources and
 * loads them into a destination schema that you also have to define; the destination schema in the data
 * warehouse is to be realised as a star schema.
 * @author callya
 *
 */
public class ETL extends DB2ConnectionManager {

	/**
	 * Diese Methode sollte benutzt werden um Anfragen an die Datenbank zu senden 
	 * Beispiel für eine Anfrage; 'SELECT * FROM Table tabellen_name'
	 * @param S
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	private ResultSet SendQuery(String S, boolean result) throws SQLException {

		try {

			Statement stm = this.con.createStatement();

			if (result) {
				if (stm.execute(S)) {
					// return stm.getResultSet();
					return stm.executeQuery(S);
				} else {
					return null;
				}
			} else {
				stm.execute(S);
				return null;
			}

		} catch (SQLException e) {
			throw e;

		}
	}
}
