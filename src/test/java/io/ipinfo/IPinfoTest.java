package io.ipinfo;

import io.ipinfo.api.IPinfo;
import io.ipinfo.api.errors.ErrorResponseException;
import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.ASNResponse;
import io.ipinfo.api.model.IPResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class IPinfoTest {
    @Test
    public void testGoogleDNS() {
        IPinfo ii = new IPinfo.Builder()
                .setToken(System.getenv("IPINFO_TOKEN"))
                .build();

        try {
            IPResponse response = ii.lookupIP("8.8.8.8");
            assertAll("8.8.8.8",
                    () -> assertEquals(response.getIp(), "8.8.8.8"),
                    () -> assertEquals(response.getHostname(), "dns.google"),
                    () -> assertTrue(response.getAnycast()),
                    () -> assertEquals(response.getCity(), "Mountain View"),
                    () -> assertEquals(response.getRegion(), "California"),
                    () -> assertEquals(response.getCountryCode(), "US"),
                    () -> assertEquals(response.getCountryName(), "United States"),
                    () -> assertEquals(response.getTimezone(), "America/Los_Angeles"),
                    () -> assertFalse(response.getPrivacy().getProxy()),
                    () -> assertFalse(response.getPrivacy().getHosting()),
                    () -> assertFalse(response.getPrivacy().getVpn()),
                    () -> assertFalse(response.getPrivacy().getTor()),
                    () -> assertEquals(response.getDomains().getDomains().size(), 5)
            );
        } catch (RateLimitedException e) {
            fail(e);
        }
    }

    @Test
    public void testGetMap() {
        IPinfo ii = new IPinfo.Builder()
                .setToken(System.getenv("IPINFO_TOKEN"))
                .build();

        try {
            String mapUrl = ii.getMap(Arrays.asList("1.1.1.1", "2.2.2.2", "8.8.8.8"));
        } catch (RateLimitedException e) {
            fail(e);
        }
    }

    @Test
    public void testGetBatch() {
        IPinfo ii = new IPinfo.Builder()
                .setToken(System.getenv("IPINFO_TOKEN"))
                .build();

        try {
            List<String> urls = new ArrayList(10);
            urls.add("AS123");
            urls.add("8.8.8.8");
            urls.add("9.9.9.9/hostname");
            ConcurrentHashMap<String, Object> result = ii.getBatch(urls);

            assertAll("keys exist",
                    () -> assertTrue(result.containsKey("AS123")),
                    () -> assertTrue(result.containsKey("8.8.8.8")),
                    () -> assertTrue(result.containsKey("9.9.9.9/hostname"))
            );

            ASNResponse asnResp = (ASNResponse)result.get("AS123");
            assertAll("AS123",
                    () -> assertEquals(asnResp.getAsn(), "AS123"),
                    () -> assertEquals(asnResp.getName(), "Air Force Systems Networking"),
                    () -> assertEquals(asnResp.getCountryCode(), "US"),
                    () -> assertEquals(asnResp.getCountryName(), "United States"),
                    () -> assertEquals(asnResp.getAllocated(), "1987-08-24"),
                    () -> assertEquals(asnResp.getRegistry(), "arin"),
                    () -> assertEquals(asnResp.getDomain(), "af.mil"),
                    () -> assertEquals(asnResp.getNumIps(), new Integer(0)),
                    () -> assertEquals(asnResp.getType(), "inactive")
            );

            IPResponse ipResp = (IPResponse)result.get("8.8.8.8");
            assertAll("8.8.8.8",
                    () -> assertEquals(ipResp.getIp(), "8.8.8.8"),
                    () -> assertEquals(ipResp.getHostname(), "dns.google"),
                    () -> assertTrue(ipResp.getAnycast()),
                    () -> assertEquals(ipResp.getCity(), "Mountain View"),
                    () -> assertEquals(ipResp.getRegion(), "California"),
                    () -> assertEquals(ipResp.getCountryCode(), "US"),
                    () -> assertEquals(ipResp.getCountryName(), "United States"),
                    () -> assertEquals(ipResp.getTimezone(), "America/Los_Angeles"),
                    () -> assertFalse(ipResp.getPrivacy().getProxy()),
                    () -> assertFalse(ipResp.getPrivacy().getHosting()),
                    () -> assertFalse(ipResp.getPrivacy().getVpn()),
                    () -> assertFalse(ipResp.getPrivacy().getTor()),
                    () -> assertEquals(ipResp.getDomains().getDomains().size(), 5)
            );

            String hostname = (String)result.get("9.9.9.9/hostname");
            assertEquals(hostname, "dns9.quad9.net");
        } catch (RateLimitedException e) {
            fail(e);
        }
    }

    @Test
    public void testGetBatchIps() {
        IPinfo ii = new IPinfo.Builder()
                .setToken(System.getenv("IPINFO_TOKEN"))
                .build();

        try {
            List<String> ips = new ArrayList(10);
            ips.add("1.1.1.1");
            ips.add("8.8.8.8");
            ips.add("9.9.9.9");
            ConcurrentHashMap<String, IPResponse> result = ii.getBatchIps(ips);

            assertAll("keys exist",
                    () -> assertTrue(result.containsKey("1.1.1.1")),
                    () -> assertTrue(result.containsKey("8.8.8.8")),
                    () -> assertTrue(result.containsKey("9.9.9.9"))
            );

            IPResponse res1 = result.get("1.1.1.1");
            assertAll("1.1.1.1",
                    () -> assertEquals(res1.getIp(), "1.1.1.1"),
                    () -> assertEquals(res1.getHostname(), "one.one.one.one"),
                    () -> assertTrue(res1.getAnycast()),
                    () -> assertEquals(res1.getCity(), "Miami"),
                    () -> assertEquals(res1.getRegion(), "Florida"),
                    () -> assertEquals(res1.getCountryCode(), "US"),
                    () -> assertEquals(res1.getCountryName(), "United States"),
                    () -> assertEquals(res1.getTimezone(), "America/New_York"),
                    () -> assertFalse(res1.getPrivacy().getProxy()),
                    () -> assertTrue(res1.getPrivacy().getHosting()),
                    () -> assertFalse(res1.getPrivacy().getVpn()),
                    () -> assertFalse(res1.getPrivacy().getTor()),
                    () -> assertEquals(res1.getDomains().getDomains().size(), 5)
            );

            IPResponse res2 = result.get("8.8.8.8");
            assertAll("8.8.8.8",
                    () -> assertEquals(res2.getIp(), "8.8.8.8"),
                    () -> assertEquals(res2.getHostname(), "dns.google"),
                    () -> assertTrue(res2.getAnycast()),
                    () -> assertEquals(res2.getCity(), "Mountain View"),
                    () -> assertEquals(res2.getRegion(), "California"),
                    () -> assertEquals(res2.getCountryCode(), "US"),
                    () -> assertEquals(res2.getCountryName(), "United States"),
                    () -> assertEquals(res2.getTimezone(), "America/Los_Angeles"),
                    () -> assertFalse(res2.getPrivacy().getProxy()),
                    () -> assertFalse(res2.getPrivacy().getHosting()),
                    () -> assertFalse(res2.getPrivacy().getVpn()),
                    () -> assertFalse(res2.getPrivacy().getTor()),
                    () -> assertEquals(res2.getDomains().getDomains().size(), 5)
            );

            IPResponse res3 = result.get("9.9.9.9");
            assertAll("9.9.9.9",
                    () -> assertEquals(res3.getIp(), "9.9.9.9"),
                    () -> assertEquals(res3.getHostname(), "dns9.quad9.net"),
                    () -> assertTrue(res3.getAnycast()),
                    () -> assertEquals(res3.getCity(), "Ashburn"),
                    () -> assertEquals(res3.getRegion(), "Virginia"),
                    () -> assertEquals(res3.getCountryCode(), "US"),
                    () -> assertEquals(res3.getCountryName(), "United States"),
                    () -> assertEquals(res3.getTimezone(), "America/New_York"),
                    () -> assertFalse(res3.getPrivacy().getProxy()),
                    () -> assertFalse(res3.getPrivacy().getHosting()),
                    () -> assertFalse(res3.getPrivacy().getVpn()),
                    () -> assertFalse(res3.getPrivacy().getTor()),
                    () -> assertEquals(res3.getDomains().getDomains().size(), 5)
            );
        } catch (RateLimitedException e) {
            fail(e);
        }
    }

    @Test
    public void testGetBatchAsns() {
        IPinfo ii = new IPinfo.Builder()
                .setToken(System.getenv("IPINFO_TOKEN"))
                .build();

        try {
            List<String> asns = new ArrayList(10);
            asns.add("AS123");
            asns.add("AS321");
            ConcurrentHashMap<String, ASNResponse> result = ii.getBatchAsns(asns);

            assertAll("keys exist",
                    () -> assertTrue(result.containsKey("AS123")),
                    () -> assertTrue(result.containsKey("AS321"))
            );

            ASNResponse res1 = result.get("AS123");
            assertAll("AS123",
                    () -> assertEquals(res1.getAsn(), "AS123"),
                    () -> assertEquals(res1.getName(), "Air Force Systems Networking"),
                    () -> assertEquals(res1.getCountryCode(), "US"),
                    () -> assertEquals(res1.getCountryName(), "United States"),
                    () -> assertEquals(res1.getAllocated(), "1987-08-24"),
                    () -> assertEquals(res1.getRegistry(), "arin"),
                    () -> assertEquals(res1.getDomain(), "af.mil"),
                    () -> assertEquals(res1.getNumIps(), new Integer(0)),
                    () -> assertEquals(res1.getType(), "inactive")
            );

            ASNResponse res2 = result.get("AS321");
            assertAll("AS321",
                    () -> assertEquals(res2.getAsn(), "AS321"),
                    () -> assertEquals(res2.getName(), "DoD Network Information Center"),
                    () -> assertEquals(res2.getCountryCode(), "US"),
                    () -> assertEquals(res2.getCountryName(), "United States"),
                    () -> assertEquals(res2.getAllocated(), "1989-06-30"),
                    () -> assertEquals(res2.getRegistry(), "arin"),
                    () -> assertEquals(res2.getDomain(), "mail.mil"),
                    () -> assertEquals(res2.getNumIps(), new Integer(66048)),
                    () -> assertEquals(res2.getType(), "isp")
            );
        } catch (RateLimitedException e) {
            fail(e);
        }
    }
}
