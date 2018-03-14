package com.vas.mirrordir.models;

import java.util.StringTokenizer;

/**
 *
 * @author Vinícius
 */
public class Address {

    private String ip;
    private int port;

    public Address(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Address(String respPASV) {
        StringTokenizer st = new StringTokenizer(respPASV);
        st.nextToken("(");
        String Address = st.nextToken(",").substring(1) + "."
                + st.nextToken(",") + "."
                + st.nextToken(",") + "."
                + st.nextToken(",");
        int value1 = Integer.parseInt(st.nextToken(","));
        int value2 = Integer.parseInt(st.nextToken(")").substring(1));
        int portAddress = value1 * 256 + value2;

        this.ip = Address;
        this.port = portAddress;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
