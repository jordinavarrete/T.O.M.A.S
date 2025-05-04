package com.puchdemont.tomas;

public class SampleJson {
    public static String json = "{\n" +
            "  \"airportMetadata\": {\n" +
            "    \"icao\": \"LEBL\",\n" +
            "    \"iata\": \"BCN\",\n" +
            "    \"presentName\": \"Aeroport El Prat-Josep Tarradellas\",\n" +
            "    \"lastUpdateTimestamp\": \"1714166400\",\n" +
            "    \"startUpdateTimestamp\": \"1714080000\"\n" +
            "  },\n" +
            "  \"flights\": [\n" +
            "    {\n" +
            "      \"type\": \"departure\",\n" +
            "      \"programmedArriveTimestamp\": \"1714188000\",\n" +
            "      \"programmedDepartTimestamp\": \"1714185000\",\n" +
            "      \"icao\": \"LEBL\",\n" +
            "      \"iata\": \"BCN\",\n" +
            "      \"presentCityName\": \"Madrid\",\n" +
            "      \"destinationCity\": \"Madrid\",\n" +
            "      \"destinationTerminal\": \"T4\",\n" +
            "      \"location\": {\n" +
            "        \"terminal\": \"T1\",\n" +
            "        \"gate\": \"B14\"\n" +
            "      },\n" +
            "      \"actualArriveTimestamp\": \"1714188600\",\n" +
            "      \"actualDepartTimestamp\": \"1714185300\",\n" +
            "      \"status\": \"on-time\",\n" +
            "      \"code\": [\n" +
            "        {\n" +
            "          \"companyName\": \"Iberia\",\n" +
            "          \"flightNumber\": \"IB123\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"companyName\": \"British Airways\",\n" +
            "          \"flightNumber\": \"BA123\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"departure\",\n" +
            "      \"programmedArriveTimestamp\": \"1714189800\",\n" +
            "      \"programmedDepartTimestamp\": \"1714186800\",\n" +
            "      \"icao\": \"LEBL\",\n" +
            "      \"iata\": \"BCN\",\n" +
            "      \"presentCityName\": \"Paris\",\n" +
            "      \"destinationCity\": \"Paris\",\n" +
            "      \"destinationTerminal\": \"T2\",\n" +
            "      \"location\": {\n" +
            "        \"terminal\": \"T2\",\n" +
            "        \"gate\": \"C32\"\n" +
            "      },\n" +
            "      \"actualArriveTimestamp\": \"1714190400\",\n" +
            "      \"actualDepartTimestamp\": \"1714187100\",\n" +
            "      \"status\": \"delayed\",\n" +
            "      \"code\": [\n" +
            "        {\n" +
            "          \"companyName\": \"Vueling\",\n" +
            "          \"flightNumber\": \"VY987\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"companyName\": \"Air Europa\",\n" +
            "          \"flightNumber\": \"UX987\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"departure\",\n" +
            "      \"programmedArriveTimestamp\": \"1714193400\",\n" +
            "      \"programmedDepartTimestamp\": \"1714190400\",\n" +
            "      \"icao\": \"LEBL\",\n" +
            "      \"iata\": \"BCN\",\n" +
            "      \"presentCityName\": \"Rome\",\n" +
            "      \"destinationCity\": \"Rome\",\n" +
            "      \"destinationTerminal\": \"T1\",\n" +
            "      \"location\": {\n" +
            "        \"terminal\": \"T1\",\n" +
            "        \"gate\": \"A20\"\n" +
            "      },\n" +
            "      \"actualArriveTimestamp\": \"1714194000\",\n" +
            "      \"actualDepartTimestamp\": \"1714190700\",\n" +
            "      \"status\": \"on-time\",\n" +
            "      \"code\": [\n" +
            "        {\n" +
            "          \"companyName\": \"Ryanair\",\n" +
            "          \"flightNumber\": \"R7A77\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"departure\",\n" +
            "      \"programmedArriveTimestamp\": \"1714195200\",\n" +
            "      \"programmedDepartTimestamp\": \"1714192200\",\n" +
            "      \"icao\": \"LEBL\",\n" +
            "      \"iata\": \"BCN\",\n" +
            "      \"presentCityName\": \"Paris\",\n" +
            "      \"destinationCity\": \"Paris\",\n" +
            "      \"destinationTerminal\": \"T2\",\n" +
            "      \"location\": {\n" +
            "        \"terminal\": \"T2\",\n" +
            "        \"gate\": \"C45\"\n" +
            "      },\n" +
            "      \"actualArriveTimestamp\": \"1714195800\",\n" +
            "      \"actualDepartTimestamp\": \"1714192500\",\n" +
            "      \"status\": \"on-time\",\n" +
            "      \"code\": [\n" +
            "        {\n" +
            "          \"companyName\": \"Air France\",\n" +
            "          \"flightNumber\": \"AF1203\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"departure\",\n" +
            "      \"programmedArriveTimestamp\": \"1714197600\",\n" +
            "      \"programmedDepartTimestamp\": \"1714194600\",\n" +
            "      \"icao\": \"LEBL\",\n" +
            "      \"iata\": \"BCN\",\n" +
            "      \"presentCityName\": \"Amsterdam\",\n" +
            "      \"destinationCity\": \"Amsterdam\",\n" +
            "      \"destinationTerminal\": \"T3\",\n" +
            "      \"location\": {\n" +
            "        \"terminal\": \"T1\",\n" +
            "        \"gate\": \"A5\"\n" +
            "      },\n" +
            "      \"actualArriveTimestamp\": \"1714198200\",\n" +
            "      \"actualDepartTimestamp\": \"1714194900\",\n" +
            "      \"status\": \"delayed\",\n" +
            "      \"code\": [\n" +
            "        {\n" +
            "          \"companyName\": \"KLM Royal Dutch Airlines\",\n" +
            "          \"flightNumber\": \"KL875\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"companyName\": \"Delta Airlines\",\n" +
            "          \"flightNumber\": \"DL875\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";
}
