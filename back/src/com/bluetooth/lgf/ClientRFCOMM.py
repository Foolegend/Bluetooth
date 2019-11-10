from bluetooth import *

bd_addr = "CC:3D:82:4B:27:37"

port = 1

sock = BluetoothSocket(RFCOMM)
sock.connect((bd_addr, port))

sock.send("hello!!")

sock.close()
