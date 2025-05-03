#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>
#include <bluetooth/sdp.h>
#include <bluetooth/sdp_lib.h>

// UUID must match the one used in Android app
#define SERVICE_UUID "969255c0-200a-11e0-ac64-c80d250c9a66"
#define RFCOMM_CHANNEL 1

// Register an SDP record for the service
void register_service(uint8_t channel) {
    uint8_t svc_uuid_int[] = {
        0x96, 0x92, 0x55, 0xc0,
        0x20, 0x0a,
        0x11, 0xe0,
        0xac, 0x64,
        0xc8, 0x0d, 0x25, 0x0c, 0x9a, 0x66
    };

    uuid_t svc_uuid;
    sdp_record_t *record = sdp_record_alloc();
    sdp_list_t *l2cap_list = 0, *rfcomm_list = 0, *root_list = 0;
    sdp_list_t *proto_list = 0, *access_proto_list = 0;
    sdp_session_t *session = 0;

    sdp_uuid128_create(&svc_uuid, svc_uuid_int);
    sdp_set_service_id(record, svc_uuid);

    // Make service publicly browsable
    uuid_t root_uuid;
    sdp_uuid16_create(&root_uuid, PUBLIC_BROWSE_GROUP);
    root_list = sdp_list_append(0, &root_uuid);
    sdp_set_browse_groups(record, root_list);

    // Set L2CAP info
    uuid_t l2cap_uuid;
    sdp_uuid16_create(&l2cap_uuid, L2CAP_UUID);
    l2cap_list = sdp_list_append(0, &l2cap_uuid);
    proto_list = sdp_list_append(0, l2cap_list);

    // Set RFCOMM info
    uuid_t rfcomm_uuid;
    sdp_uuid16_create(&rfcomm_uuid, RFCOMM_UUID);
    uint8_t rfcomm_channel = channel;
    sdp_data_t *channel_data = sdp_data_alloc(SDP_UINT8, &rfcomm_channel);
    rfcomm_list = sdp_list_append(0, &rfcomm_uuid);
    sdp_list_append(rfcomm_list, channel_data);
    sdp_list_append(proto_list, rfcomm_list);

    // Attach protocol information to service record
    access_proto_list = sdp_list_append(0, proto_list);
    sdp_set_access_protos(record, access_proto_list);

    // Set the service name, provider, and description
    sdp_set_info_attr(record, "Bluetooth RFCOMM Server", "LinuxHost", "RFCOMM with SDP registration");

    // Connect to local SDP server and register record
    session = sdp_connect(BDADDR_ANY, BDADDR_LOCAL, SDP_RETRY_IF_BUSY);
    if (!session) {
        perror("sdp_connect");
        exit(EXIT_FAILURE);
    }

    if (sdp_record_register(session, record, 0) < 0) {
        perror("sdp_record_register");
        exit(EXIT_FAILURE);
    }

    // NOTE: Keep session and record alive while server is running
    printf("Service registered with SDP using UUID %s\n", SERVICE_UUID);
}

int main() {
    struct sockaddr_rc loc_addr = { 0 }, rem_addr = { 0 };
    char buf[1024] = { 0 };
    int server_sock, client_sock;
    socklen_t opt = sizeof(rem_addr);

    // Create RFCOMM socket
    server_sock = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
    if (server_sock < 0) {
        perror("socket");
        exit(EXIT_FAILURE);
    }

    // Bind socket to local Bluetooth adapter on specified channel
    loc_addr.rc_family = AF_BLUETOOTH;
    loc_addr.rc_bdaddr = *BDADDR_ANY;
    loc_addr.rc_channel = RFCOMM_CHANNEL;
    if (bind(server_sock, (struct sockaddr *)&loc_addr, sizeof(loc_addr)) < 0) {
        perror("bind");
        close(server_sock);
        exit(EXIT_FAILURE);
    }

    // Print the server's Bluetooth MAC address
    char server_mac[18] = { 0 };
    ba2str(&loc_addr.rc_bdaddr, server_mac);  // Convert to human-readable MAC address format
    printf("Server is running on Bluetooth MAC address: %s\n", server_mac);

    // Register service with SDP
    register_service(RFCOMM_CHANNEL);

    // Listen for connections
    listen(server_sock, 1);
    printf("Waiting for incoming RFCOMM connection on channel %d...\n", RFCOMM_CHANNEL);

    // Accept client
    client_sock = accept(server_sock, (struct sockaddr *)&rem_addr, &opt);
    if (client_sock < 0) {
        perror("accept");
        close(server_sock);
        exit(EXIT_FAILURE);
    }

    char client_addr[18] = { 0 };
    ba2str(&rem_addr.rc_bdaddr, client_addr);
    printf("Accepted connection from %s\n", client_addr);

    // Read data
    int bytes_read = read(client_sock, buf, sizeof(buf));
    if (bytes_read > 0) {
        printf("Received [%d bytes]: %s\n", bytes_read, buf);
    } else {
        perror("read");
    }

    // Clean up
    close(client_sock);
    close(server_sock);
    printf("Connection closed.\n");

    return 0;
}

