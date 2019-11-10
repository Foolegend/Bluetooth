from bluetooth import *

nearby_devices = discover_devices(lookup_names=True)
print(nearby_devices)
for addr, name in nearby_devices:
    print("  %s - %s" % (addr, name))

server_sock = BluetoothSocket(RFCOMM)
port = 1
server_sock.bind(("CC:3D:82:4B:27:37", port))
server_sock.listen(1)
print(server_sock.getsockname());
client_sock, address = server_sock.accept()
print("Accepted connection from ", address)

data = client_sock.recv(1024)
print("received [%s]" % data)

client_sock.close()
server_sock.close()
