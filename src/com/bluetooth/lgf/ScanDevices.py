from bluetooth import *

target_name = "My Device"
target_address = None

nearby_devices = discover_devices()
print(nearby_devices)
for bdaddr in nearby_devices:
    print(nearby_devices)
    print(nearby_devices.__sizeof__())
    print(lookup_name(bdaddr))

if target_address is not None:
    print("found target bluetooth device with address ", target_address)
else:
    print("could not find target bluetooth device nearby")