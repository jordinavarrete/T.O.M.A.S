package com.puchdemont.tomas;

import java.util.List;

public class Airport {
    private AirportMetadata airportMetadata;
    private List<Flight> flights;

    // Getters and setters
    public AirportMetadata getAirportMetadata() {
        return airportMetadata;
    }

    public void setAirportMetadata(AirportMetadata airportMetadata) {
        this.airportMetadata = airportMetadata;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    // Nested class
    public static class AirportMetadata {
        private String ICAO;
        private String IATA;
        private String presentName;
        private String lastUpdateTimestamp;
        private String startUpdateTimestamp;

        // Getters and setters
        public String getICAO() { return ICAO; }
        public void setICAO(String ICAO) { this.ICAO = ICAO; }

        public String getIATA() { return IATA; }
        public void setIATA(String IATA) { this.IATA = IATA; }

        public String getPresentName() { return presentName; }
        public void setPresentName(String presentName) { this.presentName = presentName; }

        public String getLastUpdateTimestamp() { return lastUpdateTimestamp; }
        public void setLastUpdateTimestamp(String lastUpdateTimestamp) { this.lastUpdateTimestamp = lastUpdateTimestamp; }

        public String getStartUpdateTimestamp() { return startUpdateTimestamp; }
        public void setStartUpdateTimestamp(String startUpdateTimestamp) { this.startUpdateTimestamp = startUpdateTimestamp; }
    }
}
