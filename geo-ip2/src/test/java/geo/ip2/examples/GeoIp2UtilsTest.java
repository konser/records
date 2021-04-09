package geo.ip2.examples;

import com.maxmind.geoip2.model.CountryResponse;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author no-today
 * @date 2020/12/04 2:39 PM
 */
class GeoIp2UtilsTest {

    @Test
    void getCountry() throws Exception {
        loadIpAddress().parallelStream()
                .forEach(e -> {
                    long startTime = System.currentTimeMillis();
                    CountryResponse country = GeoIp2Utils.getCountry(e);
                    System.out.println((System.currentTimeMillis() - startTime) + " " + country.getCountry().getIsoCode() + " " + country.getCountry().getNames().get("zh-CN"));
                });
    }

    private List<String> loadIpAddress() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("ip-address.txt").getPath()));

        List<String> lines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
}