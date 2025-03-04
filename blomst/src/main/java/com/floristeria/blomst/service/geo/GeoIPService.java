package com.floristeria.blomst.service.geo;

import com.floristeria.blomst.exception.ApiException;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeoIPService {

    private DatabaseReader databaseReader;

    @PostConstruct
    public void init() {
        try (InputStream databaseStream = getClass().getClassLoader().getResourceAsStream("GeoLite2-City.mmdb")) {
            if (databaseStream == null) {
                throw new RuntimeException("Archivo GeoLite2-City.mmdb no encontrado en resources/");
            }
            databaseReader = new DatabaseReader.Builder(databaseStream).build();
        } catch (IOException e) {
            throw new ApiException("Error al cargar la base de datos GeoLite2-City");
        }
    }

    /**
     * Obtiene la geolocalización de una IP.
     *
     * @param ipAddress Dirección IP a buscar.
     * @return Un mapa con país, región, ciudad y zona horaria.
     */
    public Map<String, String> getGeolocationFromIp(String ipAddress) {
        Map<String, String> geoData = new HashMap<>();

        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            CityResponse response = databaseReader.city(ip);

            Country country = response.getCountry();
            Subdivision subdivision = response.getMostSpecificSubdivision();
            City city = response.getCity();

            geoData.put("country", country.getName());
            geoData.put("region", subdivision.getName());
            geoData.put("city", city.getName());
            geoData.put("timezone", response.getLocation().getTimeZone());

        } catch (IOException | GeoIp2Exception e) {
            geoData.put("country", "Desconocido");
            geoData.put("region", "Desconocido");
            geoData.put("city", "Desconocido");
            geoData.put("timezone", "Desconocido");
        }

        return geoData;
    }
}
