package geo.ip2.examples;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

/**
 * @author no-today
 * @date 2020/12/04 1:53 PM
 */
public class GeoIp2Utils {

    private static final String PATH = "./GeoLite2-Country.mmdb";
    private static DatabaseReader databaseReader;

    static {
        try {
            InputStream dbResource = GeoIp2Utils.class.getClassLoader().getResourceAsStream(PATH);
            if (dbResource == null) {
                System.out.println("[GeoIp2]: Db file not found: " + PATH);
            } else {
                databaseReader = new DatabaseReader.Builder(dbResource).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseReader getDatabaseReader() {
        return databaseReader;
    }

    public static CountryResponse getCountry(String ipAddress) {
        try {
            return databaseReader.country(InetAddress.getByName(ipAddress));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
