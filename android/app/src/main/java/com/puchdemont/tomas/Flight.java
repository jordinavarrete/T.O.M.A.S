package com.puchdemont.tomas;

import java.io.Serializable;
import java.util.List;

public class Flight implements Serializable {
    private String type;
    private String programmedArriveTimestamp;
    private String programmedDepartTimestamp;

    // Airport codes
    private String ICAO;
    private String IATA;
    private String destinationCity;
    private String destinationTerminal;
    private String presentCityName;
    private Location location;
    private String actualArriveTimestamp;
    private String actualDepartTimestamp;
    private String status;
    private List<FlightCode> code;

    // Getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getProgrammedArriveTimestamp() { return programmedArriveTimestamp; }
    public void setProgrammedArriveTimestamp(String programmedArriveTimestamp) { this.programmedArriveTimestamp = programmedArriveTimestamp; }

    public String getProgrammedDepartTimestamp() { return programmedDepartTimestamp; }
    public void setProgrammedDepartTimestamp(String programmedDepartTimestamp) { this.programmedDepartTimestamp = programmedDepartTimestamp; }

    public String getICAO() { return ICAO; }
    public void setICAO(String ICAO) { this.ICAO = ICAO; }

    public String getIATA() { return IATA; }
    public void setIATA(String IATA) { this.IATA = IATA; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public String getDestinationTerminal() { return destinationTerminal; }
    public void setDestinationTerminal(String destinationCity) { this.destinationTerminal = destinationTerminal; }

    public String getPresentCityName() { return presentCityName; }
    public void setPresentCityName(String presentCityName) { this.presentCityName = presentCityName; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public String getActualArriveTimestamp() { return actualArriveTimestamp; }
    public void setActualArriveTimestamp(String actualArriveTimestamp) { this.actualArriveTimestamp = actualArriveTimestamp; }
    public String getActualDepartTimestamp() { return actualDepartTimestamp; }
    public void setActualDepartTimestamp(String actualDepartTimestamp) { this.actualDepartTimestamp = actualDepartTimestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<FlightCode> getCode() { return code; }
    public void setCode(List<FlightCode> code) { this.code = code; }

    // Nested class for location
    public static class Location implements Serializable {
        private String terminal;
        private String gate;

        public Location(String depTerminal, String depGate)
        {
            terminal = depTerminal;
            gate = depGate;
        }

        public Location()
        {}

        public String getTerminal() { return terminal; }
        public void setTerminal(String terminal) { this.terminal = terminal; }

        public String getGate() { return gate; }
        public void setGate(String gate) { this.gate = gate; }
    }

    // Nested class for code
    public static class FlightCode implements Serializable {
        private String companyName;
        private String flightNumber;

        public FlightCode(String airline, String number)
        {
            companyName = airline;
            flightNumber = number;
        }

        public FlightCode() {}

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    }
}
