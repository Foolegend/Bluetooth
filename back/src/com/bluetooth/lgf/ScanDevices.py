from bluetooth import *

target_name = "My Device"
target_address = None

nearby_devices = discover_devices()
print(nearby_devices)
for addr, name in nearby_devices:
    print("  %s - %s" % (addr, name))